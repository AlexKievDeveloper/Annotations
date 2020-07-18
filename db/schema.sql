CREATE DATABASE alexander
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'ru_UA.UTF-8'
    LC_CTYPE = 'ru_UA.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

CREATE SCHEMA testschema
    AUTHORIZATION postgres;

CREATE TABLE testschema.entity
(
    identify integer NOT NULL,
    surname character varying COLLATE pg_catalog."default" NOT NULL,
    income double precision NOT NULL,
    CONSTRAINT entity_pk PRIMARY KEY (identify)
)

    TABLESPACE pg_default;

ALTER TABLE testschema.entity
    OWNER to postgres;

SELECT identify, surname, income
FROM testschema.entity;

INSERT INTO testschema.entity(
    identify, surname, income)
VALUES (?, ?, ?);

UPDATE testschema.entity
SET identify=?, surname=?, income=?
WHERE <condition>;

DELETE FROM testschema.entity
WHERE <condition>;