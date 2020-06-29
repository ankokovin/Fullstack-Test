DROP TABLE IF EXISTS Organization, Worker;

CREATE TABLE Organization (
    id  SERIAL NOT NULL PRIMARY KEY,
    org_name text NOT NULL UNIQUE,
    head_org_id integer REFERENCES Organization
);


CREATE TABLE Worker (
    id SERIAL NOT NULL PRIMARY KEY,
    worker_name text NOT NULL,
    org_id integer REFERENCES Organization,
    head_id integer REFERENCES Worker
);

CREATE INDEX ON Worker(worker_name);


CREATE OR REPLACE FUNCTION check_worker_head() RETURNS TRIGGER AS $worker_head$
    DECLARE
        cur_row Worker%ROWTYPE;
    BEGIN
        IF NEW.head_id IS NULL THEN
            RETURN NEW;
        END IF;
        IF NEW.id = NEW.head_id THEN
            RAISE EXCEPTION 'id must not be equal head_id. Set head_id to NULL instead.';
        END IF;
        IF (SELECT org_id FROM Worker WHERE id = NEW.head_id LIMIT 1) != NEW.org_id THEN
            RAISE EXCEPTION 'org_id in worker and head worker must be equal';
        END IF;
        IF TG_OP = 'INSERT' THEN
            RETURN NEW;
        END IF;
        cur_row := NEW;
        LOOP
            EXIT WHEN cur_row.head_id IS NULL;
            IF cur_row.head_id = NEW.id THEN
                RAISE EXCEPTION 'new head_id value caused a loop';
            END IF;
            SELECT * INTO cur_row FROM Worker WHERE id = cur_row.head_id;
        END LOOP;
        RETURN NEW;
    END;
$worker_head$ LANGUAGE plpgsql;
CREATE TRIGGER worker_head BEFORE INSERT OR UPDATE ON Worker
     FOR EACH ROW EXECUTE PROCEDURE check_worker_head();

CREATE OR REPLACE FUNCTION check_org_head() RETURNS TRIGGER AS $org_head$
    DECLARE
        cur_row Organization%ROWTYPE;
    BEGIN
        IF NEW.head_org_id IS NULL THEN
            RETURN NEW;
        END IF;
        cur_row := NEW;
        LOOP
            EXIT WHEN cur_row.head_org_id IS NULL;
            IF cur_row.head_org_id = NEW.id THEN
                RAISE EXCEPTION 'new head_org_id value caused a loop';
            END IF;
            SELECT * INTO cur_row FROM Organization WHERE id = cur_row.head_org_id;
        END LOOP;
        RETURN NEW;
    END;
$org_head$ LANGUAGE plpgsql;
CREATE TRIGGER org_head BEFORE UPDATE ON Organization
     FOR EACH ROW EXECUTE PROCEDURE check_org_head();