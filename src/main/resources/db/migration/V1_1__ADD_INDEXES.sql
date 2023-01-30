-- CONCURRENTLY builds the index without locking out writes
CREATE INDEX CONCURRENTLY staff_email_idx ON staff USING gin (email gin_trgm_ops);

-- CONCURRENTLY builds the index without locking out writes
CREATE INDEX CONCURRENTLY staff_temp_email_idx ON staff_temp USING gin (email gin_trgm_ops);