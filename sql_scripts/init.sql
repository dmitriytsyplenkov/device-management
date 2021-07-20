-- CREATE DATABASE devices;

DROP TABLE IF EXISTS projects;
CREATE TABLE IF NOT EXISTS projects
(
    id   bigserial primary key,
    name varchar(128) NOT NULL
);

DROP TABLE IF EXISTS devices;
CREATE TABLE IF NOT EXISTS devices
(
    id         bigserial primary key,
    serial_number varchar(128) NOT NULL,
    project_id bigint       NOT NULL,
    FOREIGN KEY (project_id) REFERENCES projects (id)
);

DROP TABLE IF EXISTS events;
CREATE TABLE IF NOT EXISTS events
(
    id         bigserial primary key,
    device_id bigint NOT NULL,
    is_read boolean,
    type varchar(10),
    date timestamp,
    FOREIGN KEY (device_id) REFERENCES devices (id)
);

DO
$do$
    DECLARE
        device_id bigint := 1;
        event_id bigint := 1;
        even_type varchar[] := ARRAY['event','warning','error'];
    BEGIN
        FOR i IN 1..5 LOOP
                INSERT INTO projects (id,name) VALUES (i,concat('P',i));
                FOR j IN 1..10 LOOP
                        INSERT INTO devices (id, serial_number, project_id) VALUES (device_id,substring(concat(gen_random_uuid(),''),1,6),i);
                        FOR k in 1..3 LOOP

                                INSERT INTO events (id, date, device_id, is_read, type) VALUES (event_id,now(),device_id, random()>0.5, even_type[(floor(random()*3)+1)]);
                                event_id := event_id + 1;
                            END LOOP;
                        device_id := device_id + 1;
                    END LOOP;
            END LOOP;
    END
$do$;