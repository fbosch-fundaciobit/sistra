--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

--
-- Name: str_seqdjs; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('str_seqdjs', 1, false);


--
-- Name: str_seqdnv; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('str_seqdnv', 1, false);


--
-- Name: str_seqdoc; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('str_seqdoc', 1, false);


--
-- Name: str_seqdom; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('str_seqdom', 1, false);


--
-- Name: str_seqetn; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('str_seqetn', 1, false);


--
-- Name: str_seqifi; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('str_seqifi', 1, false);


--
-- Name: str_seqimp; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('str_seqimp', 1, false);


--
-- Name: str_seqmpl; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('str_seqmpl', 1, false);


--
-- Name: str_seqmtr; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('str_seqmtr', 1, false);


--
-- Name: str_seqorg; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('str_seqorg', 1, false);


--
-- Name: str_seqtnv; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('str_seqtnv', 1, false);


--
-- Name: str_seqtra; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('str_seqtra', 1, false);


--
-- Name: str_seqtrv; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('str_seqtrv', 1, false);


--
-- Data for Name: str_espniv; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: str_datjus; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: str_orgres; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO str_orgres VALUES (1, 'GE-Genérico');


--
-- Data for Name: str_tramit; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: str_traver; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: str_docum; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: str_docniv; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: str_domin; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO str_domin VALUES (1, 'GESACARBUA', 'Arbol unidadades administrativas', 'S', 'es.caib.sistra.db', 'select ''1'' codigo, null parent, ''Unidad Test'' descripcion from dual', 'L', 'N', NULL, NULL, NULL, 'N', 1);
INSERT INTO str_domin VALUES (2, 'GESACUADES', 'Descripción de unidad administrativa. Parametrizado por código unidad.', 'S', 'es.caib.sistra.db', 'SELECT  ''1'' CODIGO,''Unidad Test'' DESCRIPCION  FROM dual WHERE ''XX'' <> ?', 'L', 'N', NULL, NULL, NULL, 'N', 1);
INSERT INTO str_domin VALUES (3, 'GESACUNADM', 'Lista de unidades administrativas', 'S', 'es.caib.sistra.db', 'SELECT  ''1'' CODIGO,''Unidad Test'' DESCRIPCION  FROM dual', 'L', 'N', NULL, NULL, NULL, 'N', 1);
INSERT INTO str_domin VALUES (4, 'GERDSMODE', 'Lista de modelos de documentos del RDS', 'S', 'es.caib.redose.db', 'SELECT 	MOD_MODELO CODIGO,MOD_MODELO || '' - '' || MOD_NOMBRE DESCRIPCION FROM RDS_MODELO ORDER BY 1', 'L', 'N', NULL, NULL, NULL, 'N', 1);
INSERT INTO str_domin VALUES (5, 'GERDSVERS', 'Lista de versiones de un modelo de documento del RDS. Parametrizado por código modelo.', 'S', 'es.caib.redose.db', 'SELECT RDS_VERS.VER_VERSIO CODIGO, TO_CHAR(RDS_VERS.VER_VERSIO) DESCRIPCION FROM RDS_VERS,RDS_MODELO WHERE RDS_MODELO.MOD_CODIGO =  RDS_VERS.VER_CODMOD AND RDS_MODELO.MOD_MODELO = ? ORDER BY 1', 'L', 'N', NULL, NULL, NULL, 'N', 1);
INSERT INTO str_domin VALUES (6, 'GEFORMMODE', 'Lista de modelos de formularios', 'S', 'es.caib.rolforms.db', 'SELECT DISTINCT RFR_FORMUL.FOR_MODELO CODIGO,RFR_FORMUL.FOR_MODELO || '' - '' || RFR_TRAFOR.TRF_TITULO DESCRIPCION
FROM RFR_FORMUL,RFR_TRAFOR
WHERE RFR_TRAFOR.TRF_CODFOR = RFR_FORMUL.FOR_CODI AND RFR_TRAFOR.TRF_CODIDI = ''es''
ORDER BY 1', 'R', 'N', NULL, NULL, NULL, 'N', 1);
INSERT INTO str_domin VALUES (7, 'GEFORMMOVE', 'Arbol de formularios y versiones', 'S', 'es.caib.rolforms.db', 'SELECT RFR_FORMUL.FOR_MODELO MODELO,
	   RFR_FORMUL.FOR_MODELO || '' - '' || RFR_TRAFOR.TRF_TITULO DESCRIPCION,
	   RFR_FORMUL.FOR_VERSIO VERSION	   	   
FROM RFR_FORMUL,RFR_TRAFOR
WHERE RFR_TRAFOR.TRF_CODFOR = RFR_FORMUL.FOR_CODI AND RFR_TRAFOR.TRF_CODIDI = ''es''
ORDER BY 1,3', 'R', 'N', NULL, NULL, NULL, 'N', 1);
INSERT INTO str_domin VALUES (8, 'GEFORMVERS', 'Lista de versiones de un formulario. Parametrizado por código de formulario.', 'S', 'es.caib.rolforms.db', 'SELECT  RFR_FORMUL.FOR_VERSIO CODIGO, RFR_FORMUL.FOR_VERSIO DESCRIPCION 
FROM RFR_FORMUL 
WHERE RFR_FORMUL.FOR_MODELO = ?
ORDER BY 1', 'R', 'N', NULL, NULL, NULL, 'N', 1);
INSERT INTO str_domin VALUES (9, 'GEPAISES', 'Lista de paises', 'S', 'es.caib.sistra.db', 'SELECT TRIM( PAI_CODALF ) CODIGO, UPPER( PAI_DENCAS ) DESCRIPCION FROM STR_DPAIS WHERE PAI_VIGENC = ''S'' ORDER BY DESCRIPCION', 'L', 'N', NULL, NULL, NULL, 'N', 1);
INSERT INTO str_domin VALUES (10, 'GEPROVINCI', 'Lista de provincias', 'S', 'es.caib.sistra.db', 'SELECT PRO_CODIGO CODIGO, PRO_DENCAT DESCRIPCION FROM STR_DPROVI ORDER BY CODIGO', 'L', 'N', NULL, NULL, NULL, 'N', 1);
INSERT INTO str_domin VALUES (11, 'GEGMUNICI', 'Lista de municipios de una provincia. Parametrizado por código provincia.', 'S', 'es.caib.sistra.db', 'SELECT MUN_CODIGO CODIGO, MUN_DENOFI DESCRIPCION FROM STR_DMUNIC WHERE MUN_PROVIN = ? ORDER BY DESCRIPCION', 'L', 'N', NULL, NULL, NULL, 'N', 1);
INSERT INTO str_domin VALUES (12, 'GEMUNIDESC', 'Descripción de un municipio. Parametrizado por código provincia y por código municipio.', 'S', 'es.caib.sistra.db', 'SELECT MUN_CODIGO CODIGO, MUN_DENOFI DESCRIPCION FROM STR_DMUNIC WHERE MUN_PROVIN = ? AND MUN_CODIGO = ?', 'L', 'N', NULL, NULL, NULL, 'N', 1);


--
-- Data for Name: str_gesfrm; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO str_gesfrm VALUES ('forms', 'FORMS', '@sistra.url@', '@forms.server@/formfront/iniciTelematic.do', '/formfront/continuacioTelematic.do');


--
-- Data for Name: str_idioma; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO str_idioma VALUES ('ca', 2);
INSERT INTO str_idioma VALUES ('es', 1);


--
-- Data for Name: str_import; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: str_impfic; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: str_menpla; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: str_mentra; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: str_tradjs; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: str_tradnv; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: str_tradoc; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: str_traetn; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: str_trampl; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: str_tramtr; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: str_traniv; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: str_tratra; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- PostgreSQL database dump complete
--

