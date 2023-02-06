drop table if exists build_status;

CREATE TABLE build_status(
    id SERIAL PRIMARY KEY,
    last_failed_build_date_time TIMESTAMP,
    last_successful_build_date_time TIMESTAMP
);

INSERT INTO build_status VALUES (1, NULL, NULL)