#!/bin/bash

pg_prove --host localhost --dbname orgs_and_workers --port 5432 --username admin tests/*.sql
