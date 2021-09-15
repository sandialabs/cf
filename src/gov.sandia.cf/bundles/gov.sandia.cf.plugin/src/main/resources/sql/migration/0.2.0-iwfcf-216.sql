------------------------------------------------
-- Migration of QoI to manage tag under qoi tree
------------------------------------------------

-- DROP procedure if exists 
DROP PROCEDURE IF EXISTS MIGRATE_QOI_TAG;

-- CREATE procedure to migrate qoi tag
CREATE PROCEDURE MIGRATE_QOI_TAG()
   MODIFIES SQL DATA
   BEGIN ATOMIC
         DECLARE ID_PARENT INT;
         DECLARE DATE_PARENT TIMESTAMP;
   BEGIN ATOMIC
	  for_label: 
		FOR SELECT ID, CREATION_DATE FROM QOI WHERE TAG_DATE IS NULL DO
			-- Set variables
		    SET ID_PARENT = ID;
		    SET DATE_PARENT = CREATION_DATE;

		    -- Update QoIs
		   	UPDATE QOI tagged SET tagged.PARENT_ID=ID_PARENT WHERE tagged.TAG_DATE IS NOT NULL AND tagged.CREATION_DATE=DATE_PARENT;
		END FOR for_label;
	 END;
END;
.;

-- Apply the procedure
CALL PUBLIC.MIGRATE_QOI_TAG();

-- DROP the procedure after apply
DROP PROCEDURE IF EXISTS MIGRATE_QOI_TAG;