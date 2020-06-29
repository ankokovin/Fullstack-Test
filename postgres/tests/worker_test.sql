CREATE EXTENSION IF NOT EXISTS pgtap;
\set ON_ERROR_ROLLBACK 1
\set ON_ERROR_STOP true
BEGIN;
SELECT plan(18);

INSERT INTO Organization VALUES
    (1, 'headOrg1', NULL),
    (2, 'headOrg2', NULL),
    (3, 'Org_1_1', 1),
    (4, 'Org_1_2', 1);

PREPARE insert_workers AS INSERT INTO Worker VALUES
    (1, 'Worker1', 1, NULL),
    (2, 'Worker2', 1, 1),
    (3, 'Worker3', 2, NULL),
    (4, 'Worker4', 2, 3),
    (5, 'Worker5', 2, 4),
    (6, 'Worker6', 3, NULL),
    (7, 'Worker7', 4, NULL);
-- 1
SELECT lives_ok(
    'insert_workers'
);

PREPARE delete_worker AS DELETE FROM Worker WHERE id = 6;

-- 2
SELECT lives_ok(
    'delete_worker'
);
-- 3
SELECT results_eq(
    'SELECT * FROM Worker',
     $$VALUES  (1, 'Worker1', 1, NULL),
    (2, 'Worker2', 1, 1),
    (3, 'Worker3', 2, NULL),
    (4, 'Worker4', 2, 3),
    (5, 'Worker5', 2, 4),
    (7, 'Worker7', 4, NULL)$$
);

PREPARE delete_head_worker AS DELETE FROM Worker WHERE id = 4;
-- 4
SELECT throws_ok(
    'delete_head_worker'
);
-- 5
SELECT results_eq(
    'SELECT * FROM Worker',
     $$VALUES  (1, 'Worker1', 1, NULL),
    (2, 'Worker2', 1, 1),
    (3, 'Worker3', 2, NULL),
    (4, 'Worker4', 2, 3),
    (5, 'Worker5', 2, 4),
    (7, 'Worker7', 4, NULL)$$
);

PREPARE insert_worker AS INSERT INTO Worker VALUES (8, 'Worker8', 1, 1);
-- 6
SELECT lives_ok(
    'insert_worker'
);
-- 7
SELECT results_eq(
    'SELECT * FROM Worker',
     $$VALUES  (1, 'Worker1', 1, NULL),
    (2, 'Worker2', 1, 1),
    (3, 'Worker3', 2, NULL),
    (4, 'Worker4', 2, 3),
    (5, 'Worker5', 2, 4),
    (7, 'Worker7', 4, NULL),
    (8, 'Worker8', 1, 1)$$
);

PREPARE insert_worker_wrong_org AS INSERT INTO Worker VALUES (9, 'Worker9', 1, 3);
-- 8
SELECT throws_ok('insert_worker_wrong_org');
-- 9
SELECT results_eq(
    'SELECT * FROM Worker',
     $$VALUES  (1, 'Worker1', 1, NULL),
    (2, 'Worker2', 1, 1),
    (3, 'Worker3', 2, NULL),
    (4, 'Worker4', 2, 3),
    (5, 'Worker5', 2, 4),
    (7, 'Worker7', 4, NULL),
    (8, 'Worker8', 1, 1)$$
);

PREPARE insert_worker_selfref AS INSERT INTO Worker VALUES (10, 'Worker10', 1, 9);
-- 10
SELECT throws_ok('insert_worker_selfref');
-- 11
SELECT results_eq(
    'SELECT * FROM Worker',
     $$VALUES  (1, 'Worker1', 1, NULL),
    (2, 'Worker2', 1, 1),
    (3, 'Worker3', 2, NULL),
    (4, 'Worker4', 2, 3),
    (5, 'Worker5', 2, 4),
    (7, 'Worker7', 4, NULL),
    (8, 'Worker8', 1, 1)$$
);

PREPARE update_worker AS
    UPDATE Worker
    SET (id, worker_name, org_id, head_id) = (8, 'Worker_new', 2, 3)
    WHERE id = 8;
-- 12
SELECT lives_ok(
    'update_worker'
);
-- 13
SELECT results_eq(
    'SELECT * FROM Worker',
     $$VALUES  (1, 'Worker1', 1, NULL),
    (2, 'Worker2', 1, 1),
    (3, 'Worker3', 2, NULL),
    (4, 'Worker4', 2, 3),
    (5, 'Worker5', 2, 4),
    (7, 'Worker7', 4, NULL),
    (8, 'Worker_new', 2, 3)$$
);

PREPARE update_worker_wrong_org AS
    UPDATE Worker
    SET (id, worker_name, org_id, head_id) =  (8, 'Worker9', 1, 3)
    WHERE id = 8;
-- 14
SELECT throws_ok('update_worker_wrong_org');
-- 15
SELECT results_eq(
    'SELECT * FROM Worker',
     $$VALUES  (1, 'Worker1', 1, NULL),
    (2, 'Worker2', 1, 1),
    (3, 'Worker3', 2, NULL),
    (4, 'Worker4', 2, 3),
    (5, 'Worker5', 2, 4),
    (7, 'Worker7', 4, NULL),
    (8, 'Worker_new', 2, 3)$$
);

PREPARE update_worker_selfref AS
    UPDATE Worker
    SET (id, worker_name, org_id, head_id) = (8, 'Worker10', 1, 8)
    WHERE id = 8;
-- 16
SELECT throws_ok('update_worker_selfref');
-- 17
SELECT results_eq(
    'SELECT * FROM Worker',
     $$VALUES  (1, 'Worker1', 1, NULL),
    (2, 'Worker2', 1, 1),
    (3, 'Worker3', 2, NULL),
    (4, 'Worker4', 2, 3),
    (5, 'Worker5', 2, 4),
    (7, 'Worker7', 4, NULL),
    (8, 'Worker_new', 2, 3)$$
);

PREPARE update_head_id_chain AS UPDATE Worker SET head_id = 5 WHERE id = 3;
-- 18
SELECT (throws_ok('update_head_id_chain'));
ROLLBACK;