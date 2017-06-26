/*
################################################################################
Migration script to create GENOTYPING_TECHNOLOGY and STUDY_GENOTYPING_TECHNOLOGY
tables

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Dani Welter
date:   June 21st 2017
version: 2.2.0.033
################################################################################

#######################################
#  CREATE NEW TABLES AND CONSTRAINTS
#######################################
*/
--------------------------------------------------------
--  Create Table GENOTYPING_TECHNOLOGY
--------------------------------------------------------

  CREATE TABLE "GENOTYPING_TECHNOLOGY" (
     "ID" NUMBER(19,0),
     "GENOTYPING_TECHNOLOGY" VARCHAR2(255 CHAR));

--------------------------------------------------------
--  DDL for Index GENOTYPING_TECHNOLOGY_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "GENOTYPING_TECHNOLOGY_ID_PK" ON "GENOTYPING_TECHNOLOGY" ("ID");

--------------------------------------------------------
--  Constraints for Table GENOTYPING_TECHNOLOGY
--------------------------------------------------------
  ALTER TABLE "GENOTYPING_TECHNOLOGY" ADD PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "GENOTYPING_TECHNOLOGY" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
-- Create trigger on GENOTYPING_TECHNOLOGY
--------------------------------------------------------

    CREATE OR REPLACE TRIGGER GENOTYPING_TECHNOLOGY_TRG
    BEFORE INSERT ON "GENOTYPING_TECHNOLOGY"
    FOR EACH ROW
        BEGIN
            IF :NEW.ID IS NULL THEN
                SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
            END IF;
        END;
    /
    ALTER TRIGGER GENOTYPING_TECHNOLOGY_TRG ENABLE;

--------------------------------------------------------
-- Insert default values into GENOTYPING_TECHNOLOGY
--------------------------------------------------------
    INSERT INTO GENOTYPING_TECHNOLOGY (GENOTYPING_TECHNOLOGY) VALUES('Genome-wide sequencing');
    INSERT INTO GENOTYPING_TECHNOLOGY (GENOTYPING_TECHNOLOGY) VALUES('Exome-wide sequencing');
    INSERT INTO GENOTYPING_TECHNOLOGY (GENOTYPING_TECHNOLOGY) VALUES('Exome genotyping array');
    INSERT INTO GENOTYPING_TECHNOLOGY (GENOTYPING_TECHNOLOGY) VALUES('Genome-wide genotyping array');
    INSERT INTO GENOTYPING_TECHNOLOGY (GENOTYPING_TECHNOLOGY) VALUES('Targeted genotyping array');

--------------------------------------------------------
--  DDL for Table STUDY_GENOTYPING_TECHNOLOGY
--------------------------------------------------------
  CREATE TABLE "STUDY_GENOTYPING_TECHNOLOGY" (
      "STUDY_ID" NUMBER(19,0),
      "GENOTYPING_TECHNOLOGY_ID" NUMBER(19,0));


--------------------------------------------------------
--  Constraints for Table STUDY_GENOTYPING_TECHNOLOGY
--------------------------------------------------------
  ALTER TABLE "STUDY_GENOTYPING_TECHNOLOGY" MODIFY ("GENOTYPING_TECHNOLOGY_ID" NOT NULL ENABLE);
  ALTER TABLE "STUDY_GENOTYPING_TECHNOLOGY" MODIFY ("STUDY_ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Ref Constraints for Table STUDY_GENOTYPING_TECHNOLOGY
--------------------------------------------------------
  ALTER TABLE "STUDY_GENOTYPING_TECHNOLOGY" ADD CONSTRAINT "STUDY_GENOTYPTECH_ID_FK" FOREIGN KEY ("GENOTYPING_TECHNOLOGY_ID")
	  REFERENCES "GENOTYPING_TECHNOLOGY" ("ID") ENABLE;
  ALTER TABLE "STUDY_GENOTYPING_TECHNOLOGY" ADD CONSTRAINT "STUDY_GENOTYPETECH_STUDY_ID_FK" FOREIGN KEY ("STUDY_ID")
	  REFERENCES "STUDY" ("ID") ENABLE;

