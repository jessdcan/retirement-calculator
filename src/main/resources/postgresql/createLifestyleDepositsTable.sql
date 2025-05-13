CREATE TABLE staging.lifestyle_deposits (
    id SERIAL PRIMARY KEY,
    lifestyle_type VARCHAR(20) NOT NULL,  -- 'simple' or 'fancy'
    monthly_deposit NUMERIC(10, 2) NOT NULL,  -- amount to save monthly
    annual_expenses NUMERIC(12, 2) NOT NULL,  -- yearly expenses after retirement
    description VARCHAR(255),  -- optional lifestyle description
);