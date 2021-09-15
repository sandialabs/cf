-----------------------------------------------
-- Script with a missing comma at the end of line ID INTEGER IDENTITY PRIMARY KEY
-----------------------------------------------

CREATE TABLE IF NOT EXISTS MIGRATION_LOG
(
   ID INTEGER IDENTITY PRIMARY KEY
   DATABASE_VERSION VARCHAR (200) NULL,
   SCRIPT_NAME VARCHAR (1000) NULL,
   DATE_EXECUTION TIMESTAMP NULL,
   IS_ERROR BOOLEAN NULL,
   EXECUTION_LOG VARCHAR (40000) NULL
);

