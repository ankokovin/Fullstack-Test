\set ON_ERROR_ROLLBACK 1
\set ON_ERROR_STOP true
BEGIN;
-- Проверка схемы данных
SELECT plan(15);

-- Таблицы
SELECT has_table( 'worker' );
SELECT has_table( 'organization' );

-- Колонки

SELECT columns_are('worker', ARRAY[ 'id', 'worker_name', 'org_id', 'head_id' ]);

SELECT col_is_pk ( 'worker', 'id' );
SELECT col_type_is( 'worker', 'id', 'integer');

SELECT col_type_is( 'worker', 'worker_name', 'text');

SELECT col_type_is( 'worker', 'org_id', 'integer');
SELECT fk_ok( 'worker', 'org_id', 'organization', 'id');

SELECT col_type_is( 'worker', 'head_id', 'integer');
SELECT fk_ok( 'worker', 'head_id', 'worker', 'id');

SELECT columns_are('organization', ARRAY[ 'id', 'org_name', 'head_org_id']);

SELECT col_is_pk ('organization', 'id');
SELECT col_type_is('organization', 'id', 'integer');

SELECT col_type_is('organization', 'org_name', 'text');
SELECT col_is_unique('organization', 'org_name');

SELECT * FROM finish();
ROLLBACK;
