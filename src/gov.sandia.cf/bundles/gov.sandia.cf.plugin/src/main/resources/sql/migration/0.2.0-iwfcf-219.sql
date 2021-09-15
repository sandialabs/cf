-----------------------------------------------
-- UPDATE field to add rich text content 
-----------------------------------------------
-- PCMM - Assessment
ALTER TABLE PCMMASSESSMENT ALTER COLUMN COMMENT LONGVARCHAR;

-- PCMM - Evidence
ALTER TABLE PCMMEVIDENCE ALTER COLUMN DESCRIPTION LONGVARCHAR;

-- PIRT - Quantity Of Interest
ALTER TABLE QOI ALTER COLUMN DESCRIPTION LONGVARCHAR;

-- PIRT - Phenomenon criterion
ALTER TABLE CRITERION ALTER COLUMN VALUE LONGVARCHAR;