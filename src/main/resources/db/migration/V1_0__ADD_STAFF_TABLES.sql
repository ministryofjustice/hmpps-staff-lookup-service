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

drop table if exists staff_temp;

CREATE TABLE staff_temp(LIKE staff INCLUDING ALL);


