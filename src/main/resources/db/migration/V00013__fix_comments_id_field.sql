-- Drop the existing primary key constraint
ALTER TABLE comments DROP CONSTRAINT pk_comments;

-- Alter the column to use GENERATED ALWAYS AS IDENTITY
ALTER TABLE comments RENAME COLUMN id TO old_id;
ALTER TABLE comments ADD COLUMN id BIGINT GENERATED ALWAYS AS IDENTITY;
ALTER TABLE comments DROP COLUMN old_id;

-- Re-add the primary key constraint
ALTER TABLE comments ADD CONSTRAINT pk_comments PRIMARY KEY (id);