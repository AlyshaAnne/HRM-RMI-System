--
-- PostgreSQL database dump
--

\restrict FrsHrTxWBcQ3EAnzTIHbrjMO3IoQvyaKjct95dA0WDmxOBio8twL9KpYK1IHpNG

-- Dumped from database version 18.2
-- Dumped by pg_dump version 18.1

-- Started on 2026-02-28 18:08:11

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 219 (class 1259 OID 16388)
-- Name: accounts; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.accounts (
    username character varying(50) NOT NULL,
    password character varying(255) NOT NULL,
    role character varying(30) NOT NULL,
    active boolean NOT NULL,
    full_name character varying(100) NOT NULL,
    employee_id character varying(20) NOT NULL
);


ALTER TABLE public.accounts OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16400)
-- Name: reset_requests; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.reset_requests (
    request_id integer NOT NULL,
    request_time timestamp without time zone NOT NULL,
    full_name character varying(100) NOT NULL,
    employee_id character varying(20) NOT NULL,
    status character varying(30) NOT NULL
);


ALTER TABLE public.reset_requests OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 16399)
-- Name: reset_requests_request_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.reset_requests_request_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.reset_requests_request_id_seq OWNER TO postgres;

--
-- TOC entry 5020 (class 0 OID 0)
-- Dependencies: 220
-- Name: reset_requests_request_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.reset_requests_request_id_seq OWNED BY public.reset_requests.request_id;


--
-- TOC entry 4860 (class 2604 OID 16403)
-- Name: reset_requests request_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reset_requests ALTER COLUMN request_id SET DEFAULT nextval('public.reset_requests_request_id_seq'::regclass);


--
-- TOC entry 5012 (class 0 OID 16388)
-- Dependencies: 219
-- Data for Name: accounts; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.accounts (username, password, role, active, full_name, employee_id) FROM stdin;
admin	admin123	HR	t	HR Admin	HR001
emp1	pass123	EMPLOYEE	t	John Tan	EMP001
\.


--
-- TOC entry 5014 (class 0 OID 16400)
-- Dependencies: 221
-- Data for Name: reset_requests; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.reset_requests (request_id, request_time, full_name, employee_id, status) FROM stdin;
6	2026-02-23 02:24:54.795945	John Tan	EMP001	APPROVED
5	2026-02-23 02:18:09.057021	John Tan	EMP001	REJECTED
4	2026-02-23 02:05:33.328929	John Tan	EMP001	REJECTED
3	2026-02-23 00:02:06.255966	Alysha Anne	E001	REJECTED
2	2026-02-23 00:01:55.393513	Alysha Anne	E001	REJECTED
7	2026-02-23 09:32:51.446756	John Tan	EMP001	REJECTED
\.


--
-- TOC entry 5021 (class 0 OID 0)
-- Dependencies: 220
-- Name: reset_requests_request_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.reset_requests_request_id_seq', 7, true);


--
-- TOC entry 4862 (class 2606 OID 16398)
-- Name: accounts accounts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.accounts
    ADD CONSTRAINT accounts_pkey PRIMARY KEY (username);


--
-- TOC entry 4864 (class 2606 OID 16410)
-- Name: reset_requests reset_requests_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reset_requests
    ADD CONSTRAINT reset_requests_pkey PRIMARY KEY (request_id);


-- Completed on 2026-02-28 18:08:11

--
-- PostgreSQL database dump complete
--

\unrestrict FrsHrTxWBcQ3EAnzTIHbrjMO3IoQvyaKjct95dA0WDmxOBio8twL9KpYK1IHpNG

