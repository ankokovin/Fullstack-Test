CREATE EXTENSION IF NOT EXISTS pgtap;
\set ON_ERROR_ROLLBACK 1
\set ON_ERROR_STOP true
BEGIN;

SELECT plan(9);

PREPARE org_insert AS INSERT INTO Organization VALUES
    (1, 'headOrg1', NULL),
    (2, 'headOrg2', NULL),
    (3, 'Org_1_1', 1),
    (4, 'Org_1_2', 1);
SELECT lives_ok(
    'org_insert'
);

PREPARE org_update_self_update AS UPDATE Organization SET head_org_id = 3 WHERE id = 1;

SELECT throws_ok(
    'org_update_self_update'
);

PREPARE del_not_head_org AS DELETE FROM Organization WHERE id = 3;
SELECT lives_ok(
    'del_not_head_org'
);
SELECT results_eq(
    'SELECT * FROM Organization',
     $$VALUES ( 1, 'headOrg1', NULL), (2, 'headOrg2', NULL), (4, 'Org_1_2', 1)$$
);
PREPARE del_head_org AS DELETE FROM Organization WHERE id = 1;
SELECT throws_ok (
    'del_head_org'
);
SELECT results_eq(
    'SELECT * FROM Organization',
     $$VALUES ( 1, 'headOrg1', NULL), (2, 'headOrg2', NULL), (4, 'Org_1_2', 1)$$
);
INSERT INTO Worker VALUES (1, 'worker1', 4, NULL);
PREPARE del_org_with_worker AS DELETE FROM Organization WHERE id = 1;
SELECT throws_ok (
    'del_org_with_worker'
);
SELECT results_eq(
    'SELECT * FROM Organization',
     $$VALUES ( 1, 'headOrg1', NULL), (2, 'headOrg2', NULL), (4, 'Org_1_2', 1)$$
);
INSERT INTO Organization VALUES
    (5, 'Org_1_2_1', 4),
    (6, 'Org_1_2_1_1', 5),
    (7, 'Org_1_2_1_1_1', 6),
    (8, 'Org_1_2_1_1_1_1', 7);
PREPARE update_head_long_chain AS UPDATE Organization SET head_org_id = 8 WHERE id = 1;
SELECT throws_ok (
    'update_head_long_chain'
);
ROLLBACK;