/*
################################################################################
Update the trigger for accession IDs with additional checks.

author: Dani Welter
date:    Sep 21st 2016
version: 2.2.0.005
################################################################################
*/

--------------------------------------------------------
-- Create or replace the trigger to generate accession IDs
--------------------------------------------------------
CREATE OR REPLACE TRIGGER STUDY_ACCESSION_TRG
AFTER UPDATE OF is_published ON housekeeping
FOR EACH ROW
DECLARE
	h_id number;
	a_id VARCHAR2(12);
    old_pub_date DATE;
    old_published_flag number;
    new_published_flag number;
BEGIN
	h_id := :NEW.id;
	old_pub_date := :OLD.catalog_publish_date;
	old_published_flag := :OLD.is_published;
    new_published_flag := :NEW.is_published;

	SELECT accession_id
	  INTO a_id
	  FROM study
	WHERE housekeeping_id = h_id;


	IF old_published_flag = 0 AND new_published_flag = 1 THEN
	    IF old_pub_date IS NULL THEN
	        IF a_id IS NULL THEN
                UPDATE STUDY
                SET accession_id = 'GCST' || LPAD(accession_seq.NEXTVAL, 6, 0)
                WHERE housekeeping_id = h_id;
            ELSE
                raise_application_error( -20001, 'Warning, previously unpublished study that has already been accessioned!');
            END IF;
        ELSIF old_pub_date IS NOT NULL THEN
            IF a_id IS NULL THEN
                raise_application_error( -20002, 'Warning, study has a publish date but no accession!');
            END IF;
        END IF;
    ELSIF old_published_flag = 1 AND new_published_flag = 1 THEN
          raise_application_error( -20003, 'Warning, attempted update to published study without unpublishing first');
    ELSIF old_published_flag = 1 AND new_published_flag = 0 THEN
        IF a_id IS NULL THEN
            raise_application_error( -20004, 'Warning, study had a published flag but no accession!');
        END IF;
    END IF;
END;
/
ALTER TRIGGER STUDY_ACCESSION_TRG ENABLE;