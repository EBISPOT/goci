/*
################################################################################
Update the trigger for accession IDs with correct allocation and error message.

author: Dani Welter
date:    Sep 8th 2016
version: 2.2.0.003
################################################################################
*/

--------------------------------------------------------
-- Create or replace the trigger to generate accession IDs
--------------------------------------------------------
CREATE OR REPLACE TRIGGER STUDY_ACCESSION_TRG
AFTER UPDATE OF catalog_publish_date ON housekeeping
FOR EACH ROW
DECLARE
	h_id number;
	a_id VARCHAR2(12);
  old_pub_date DATE;
BEGIN
	h_id := :NEW.id;
	old_pub_date := :OLD.catalog_publish_date;

	SELECT accession_id
	  INTO a_id
	  FROM study
	WHERE housekeeping_id = h_id;

	IF a_id IS NULL THEN
		UPDATE STUDY
		SET accession_id = 'GCST' || LPAD(accession_seq.NEXTVAL, 6, 0)
		WHERE housekeeping_id = h_id;
	ELSIF old_pub_date IS NOT NULL THEN
		raise_application_error( -20001, 'Warning, update to publication date, accession ID may already exist');
  END IF;
END;
/
ALTER TRIGGER STUDY_ACCESSION_TRG ENABLE;









