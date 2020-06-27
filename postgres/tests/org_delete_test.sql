\set ON_ERROR_ROLLBACK 1
\set ON_ERROR_STOP true
BEGIN;

SELECT plan(4);

PREPARE org_insert AS INSERT INTO Organization VALUES
    (1, 'headOrg1', NULL),
    (2, 'headOrg2', NULL),
    (3, 'Org_1_1', 1),
    (4, 'Org_1_2', 1);
SELECT lives_ok(
    'org_insert'
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
END;