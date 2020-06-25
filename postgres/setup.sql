-- TODO(#6): setup.sql: описать структуру базы данных
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


-- TODO(#18):  postgres: перенос объявления начальных данных в отдельный файл
-- TODO:  postgres: тестирование с pgTap
INSERT INTO Organization VALUES
    (1, 'headOrg1', NULL),
    (2, 'headOrg2', NULL),
    (3, 'Org_1_1', 1),
    (4, 'Org_1_2', 1);

INSERT INTO Worker VALUES
    (1, 'Worker1', 1, NULL),
    (2, 'Worker2', 1, 2),
    (3, 'Worker3', 2, NULL),
    (4, 'Worker4', 2, 3),
    (5, 'Worker5', 2, 4),
    (6, 'Worker6', 3, NULL),
    (7, 'Worker7', 4, NULL);
    
