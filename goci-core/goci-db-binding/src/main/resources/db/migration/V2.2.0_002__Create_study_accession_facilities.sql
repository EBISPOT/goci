/*
################################################################################
Update study table with column for accession IDs and create sequence and
trigger for accession IDs.

author: Dani Welter
date:    Aug 31st 2016
version: 2.1.2.047
################################################################################
*/
--------------------------------------------------------
-- Add accession field to study table
--------------------------------------------------------

ALTER TABLE study
	ADD accession_id VARCHAR2(12);

--------------------------------------------------------
-- Create the accession sequence
--------------------------------------------------------
CREATE SEQUENCE accession_seq
	MINVALUE 1
	MAXVALUE 999999
	START WITH 1
	INCREMENT BY 1
	NOCYCLE
	NOORDER
	CACHE 20;


--------------------------------------------------------
-- Create the trigger to generate accession IDs
--------------------------------------------------------
CREATE OR REPLACE TRIGGER STUDY_ACCESSION_TRG
AFTER UPDATE OF catalog_publish_date ON housekeeping
FOR EACH ROW
DECLARE
	h_id number;
	a_id VARCHAR2(12);
	pub_date DATE;
	existing_accession EXCEPTION;
BEGIN
	h_id = :NEW.id;
	pub_date = :NEW.catalog_publish_date;

	SELECT accession_id
	  INTO a_id
	  FROM study
	WHERE housekeeping_id = h_id;

	IF a_id IS NULL THEN
		UPDATE STUDY
		SET accession_id = 'GCST' || LPAD(accession_seq.NEXTVAL, 6, 0)
		WHERE housekeeping_id = h_id
	ELSEIF pub_date >= trunc(sysdate) THEN
		dbms_output.put_line('Warning, accession ID already generated today');
		RAISE existing_accession;
	END IF;
END;
/
ALTER TRIGGER STUDY_ACCESSION_TRG ENABLE;









