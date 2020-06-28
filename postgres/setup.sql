DROP TABLE IF EXISTS Organization, Worker;

CREATE TABLE Organization (
    id  integer NOT NULL PRIMARY KEY,
    org_name text NOT NULL UNIQUE,
    head_org_id integer REFERENCES Organization
);

CREATE TABLE Worker (
    id integer NOT NULL PRIMARY KEY,
    worker_name text NOT NULL,
    org_id integer REFERENCES Organization,
    head_id integer REFERENCES Worker
);

-- TODO(#17):  postgres: проверка, что начальник состоит в той же организации

-- Источник: https://postgrespro.ru/docs/postgresql/9.6/plpgsql-trigger
-- CREATE OR REPLACE FUNCTION check_worker_head() RETURNS TRIGGER AS $worker_head$
--    BEGIN
--    END
-- $worker_head$ LANGUAGE plpgsql;
-- CREATE TRIGGER worker_head BEFORE INSERT OR UPDATE ON Worker
--    FOR EACH ROW EXECUTE PROCEDURE check_worker_head()

-- TODO(#20):  postgres: обработчик удаления работника
-- TODO(#21):  postgres: обработчик удаления организации

    
