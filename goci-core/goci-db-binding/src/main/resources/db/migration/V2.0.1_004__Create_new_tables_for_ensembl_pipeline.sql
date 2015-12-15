/*

################################################################################
Migration script to create a LOCATION and SNP_LOCATION

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    June 17th 2015
version: 2.0.1.004
################################################################################

#######################################
#  CREATE NEW TABLES AND CONSTRAINTS  #
#######################################
*/

--------------------------------------------------------
--  Create Table LOCATION
--------------------------------------------------------

  CREATE TABLE "LOCATION" (
     "ID" NUMBER(19,0),
     "REGION_ID" NUMBER(19,0),
     "CHROMOSOME_NAME" VARCHAR2(255 CHAR),
     "CHROMOSOME_POSITION" VARCHAR2(255 CHAR));

--------------------------------------------------------
--  DDL for Index LOCATION_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "LOCATION_ID_PK" ON "LOCATION" ("ID");

--------------------------------------------------------
--  Constraints for Table LOCATION
--------------------------------------------------------
  ALTER TABLE "LOCATION" ADD PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "LOCATION" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Ref Constraints for Table LOCATION
--------------------------------------------------------
  ALTER TABLE "LOCATION" ADD CONSTRAINT "REGION_ID_FK" FOREIGN KEY ("REGION_ID")
    REFERENCES "REGION" ("ID") ENABLE;

--------------------------------------------------------
-- Create trigger on LOCATION
--------------------------------------------------------

CREATE OR REPLACE TRIGGER LOCATION_TRG
BEFORE INSERT ON "LOCATION"
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER LOCATION_TRG ENABLE;


--------------------------------------------------------
--  Create Table SNP_LOCATION
--------------------------------------------------------
  CREATE TABLE "SNP_LOCATION" (
     "SNP_ID" NUMBER(19,0),
     "LOCATION_ID" NUMBER(19,0));

--------------------------------------------------------
--  Constraints for Table SNP_LOCATION
--------------------------------------------------------
ALTER TABLE "SNP_LOCATION" MODIFY ("LOCATION_ID" NOT NULL ENABLE);
ALTER TABLE "SNP_LOCATION" MODIFY ("SNP_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Ref Constraints for Table SNP_LOCATION
--------------------------------------------------------
ALTER TABLE "SNP_LOCATION" ADD CONSTRAINT "SNP_LOC_LOCATION_ID_FK" FOREIGN KEY ("LOCATION_ID")
REFERENCES "LOCATION" ("ID") ENABLE;
ALTER TABLE "SNP_LOCATION" ADD CONSTRAINT "SNP_LOC_SNP_ID_FK" FOREIGN KEY ("SNP_ID")
REFERENCES "SINGLE_NUCLEOTIDE_POLYMORPHISM" ("ID") ENABLE;
