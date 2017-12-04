SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

DROP DATABASE IF EXISTS piratedb;

CREATE DATABASE piratedb WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8';

ALTER DATABASE piratedb OWNER TO postgres;

\connect piratedb

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';

CREATE EXTENSION IF NOT EXISTS tablefunc WITH SCHEMA public;

COMMENT ON EXTENSION tablefunc IS 'functions that manipulate whole tables, including crosstab';

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

CREATE TABLE combexpenditureoverthreshold (
    departmentfamily character varying(50),
    entity character varying(50),
    date character varying(10),
    expensetype character varying(200) NOT NULL,
    expensearea character varying(200) NOT NULL,
    supplier character varying(200) NOT NULL,
    transactionnumber integer NOT NULL,
    apamount double precision
);

ALTER TABLE combexpenditureoverthreshold OWNER TO postgres;

ALTER TABLE ONLY combexpenditureoverthreshold
    ADD CONSTRAINT combexpenditureoverthreshold_pkey PRIMARY KEY (expensetype, expensearea, supplier, transactionnumber);

GRANT ALL ON SCHEMA public TO PUBLIC;

