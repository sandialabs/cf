-----------------------------------------------
-- Create first script for Migration Management
-----------------------------------------------
-- Create table MIGRATION_LOG
CREATE TABLE IF NOT EXISTS MIGRATION_LOG_BIS
(
   ID INTEGER IDENTITY PRIMARY KEY,
   DATABASE_VERSION VARCHAR (200) NULL,
   SCRIPT_NAME VARCHAR (1000) NULL,
   DATE_EXECUTION TIMESTAMP NULL,
   IS_ERROR BOOLEAN NULL,
   EXECUTION_LOG VARCHAR (40000) NULL
);

UPDATE MODEL SET VERSION='Executed_savepoint';