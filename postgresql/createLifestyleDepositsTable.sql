-- Initial SQL script to create the lifestyle_deposits table in the staging schema.
    --CREATE TABLE staging.lifestyle_deposits (
    --    id SERIAL PRIMARY KEY,
    --    lifestyle_type VARCHAR(20) NOT NULL,  -- 'simple' or 'fancy'
    --    monthly_deposit NUMERIC(10, 2) NOT NULL,  -- amount to save monthly
    --    annual_expenses NUMERIC(12, 2) NOT NULL,  -- yearly expenses after retirement
    --    description VARCHAR(255)  -- optional lifestyle description
    --);

-- pdAdmin generate table script

-- Table: staging.lifestyle_deposits

-- DROP TABLE IF EXISTS staging.lifestyle_deposits;

CREATE TABLE IF NOT EXISTS staging.lifestyle_deposits
(
    id integer NOT NULL DEFAULT nextval('staging.lifestyle_deposits_id_seq'::regclass),
    lifestyle_type character varying(20) COLLATE pg_catalog."default" NOT NULL,
    monthly_deposit numeric(10,2) NOT NULL,
    annual_expenses numeric(12,2) NOT NULL,
    description character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT lifestyle_deposits_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS staging.lifestyle_deposits
    OWNER to postgresuser;