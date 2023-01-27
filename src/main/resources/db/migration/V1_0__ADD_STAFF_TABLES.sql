-- Install the pg_trgm extension
CREATE EXTENSION IF NOT EXISTS pg_trgm;


drop table if exists staff;

CREATE TABLE staff(
    id SERIAL PRIMARY KEY,
    first_name VARCHAR NOT NULL,
    last_name VARCHAR NOT NULL,
    job_title VARCHAR,
    email VARCHAR NOT NULL
);

-- CONCURRENTLY builds the index without locking out writes
CREATE INDEX CONCURRENTLY staff_email_idx ON staff USING gin (email gin_trgm_ops);

drop table if exists staff_temp;

CREATE TABLE staff_temp(
    id SERIAL PRIMARY KEY,
    first_name VARCHAR NOT NULL,
    last_name VARCHAR NOT NULL,
    job_title VARCHAR,
    email VARCHAR NOT NULL
);

-- CONCURRENTLY builds the index without locking out writes
CREATE INDEX CONCURRENTLY staff_temp_email_idx ON staff_temp USING gin (email gin_trgm_ops);


