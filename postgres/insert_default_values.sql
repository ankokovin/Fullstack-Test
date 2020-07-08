CREATE OR REPLACE FUNCTION GenerateOrganization(count integer) RETURNS void AS $gen_org$
    DECLARE
        n integer;
        n_range integer;
        org_name text;
        root_probability CONSTANT NUMERIC := 0.2;
    BEGIN
        FOR i IN 1..count LOOP
            select count(*) into n_range from Organization;
            n_range := n_range + 1;
            n := floor(random()*n_range*(1+root_probability)-n_range*root_probability);
            org_name := CONCAT('Test', n_range);
            IF n <= 0 THEN 
                INSERT INTO Organization VALUES (n_range, org_name, NULL);
            ELSE
                INSERT INTO Organization VALUES (n_range, org_name, n);
            END IF;
            END LOOP;
    END;
$gen_org$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION GenerateWorkers(n integer) RETURNS void AS $gen_worker$
    DECLARE
        org_id_v integer;
        org_count integer;
        head_probability NUMERIC := 0.1;
        worker_cnt integer;
        head_id integer;
        head_id_n integer;
        worker_name text;
    BEGIN
        SELECT(Count(*)) INTO org_count FROM Organization;
        FOR i IN 1..n LOOP
            org_id_v := floor(random()*org_count)+1;
            SELECT(Count(*)) INTO worker_cnt FROM Worker WHERE Worker.org_id = org_id_v;
            worker_name := CONCAT('Test', i);
            IF worker_cnt > 0 AND random() < head_probability THEN
                head_id_n := floor(random()*worker_cnt);
                SELECT(Worker.id) INTO head_id  FROM Worker WHERE Worker.org_id = org_id_v OFFSET head_id_n LIMIT 1;
                INSERT INTO Worker VALUES (i, worker_name, org_id_v, head_id);
            ELSE
                INSERT INTO Worker VALUES (i, worker_name, org_id_v, NULL);
            END IF;
        END LOOP;
    END;
$gen_worker$ LANGUAGE plpgsql;

SELECT GenerateOrganization(20);

SELECT GenerateWorkers(200);


