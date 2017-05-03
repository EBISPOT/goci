/*
################################################################################
Migration script to create ANCESTRAL_GROUP and ANCESTRY_ANCESTRAL_GROUP tables

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Dani Welter
date:   April 13th 2017
version: 2.2.0.026
################################################################################

#######################################
#  CREATE NEW TABLES AND CONSTRAINTS
#######################################
*/
--------------------------------------------------------
--  Create Table ANCESTRAL_GROUP
--------------------------------------------------------

  CREATE TABLE "ANCESTRAL_GROUP" (
     "ID" NUMBER(19,0),
     "ANCESTRAL_GROUP" VARCHAR2(255 CHAR));

--------------------------------------------------------
--  DDL for Index ANCESTRAL_GROUP_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "ANCESTRAL_GROUP_ID_PK" ON "ANCESTRAL_GROUP" ("ID");

--------------------------------------------------------
--  Constraints for Table ANCESTRAL_GROUP
--------------------------------------------------------
  ALTER TABLE "ANCESTRAL_GROUP" ADD PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "ANCESTRAL_GROUP" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
-- Create trigger on ANCESTRAL_GROUP
--------------------------------------------------------

    CREATE OR REPLACE TRIGGER ANCESTRAL_GROUP_TRG
    BEFORE INSERT ON "ANCESTRAL_GROUP"
    FOR EACH ROW
        BEGIN
            IF :NEW.ID IS NULL THEN
                SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
            END IF;
        END;
    /
    ALTER TRIGGER ANCESTRAL_GROUP_TRG ENABLE;

--------------------------------------------------------
-- Insert default values into ANCESTRAL_GROUP
--------------------------------------------------------
    INSERT INTO ANCESTRAL_GROUP (ANCESTRAL_GROUP) VALUES('European');
    INSERT INTO ANCESTRAL_GROUP (ANCESTRAL_GROUP) VALUES('Sub-Saharan African');
    INSERT INTO ANCESTRAL_GROUP (ANCESTRAL_GROUP) VALUES('African unspecified');
    INSERT INTO ANCESTRAL_GROUP (ANCESTRAL_GROUP) VALUES('South Asian');
    INSERT INTO ANCESTRAL_GROUP (ANCESTRAL_GROUP) VALUES('South East Asian');
    INSERT INTO ANCESTRAL_GROUP (ANCESTRAL_GROUP) VALUES('Central Asian');
    INSERT INTO ANCESTRAL_GROUP (ANCESTRAL_GROUP) VALUES('East Asian');
    INSERT INTO ANCESTRAL_GROUP (ANCESTRAL_GROUP) VALUES('Asian unspecified');
    INSERT INTO ANCESTRAL_GROUP (ANCESTRAL_GROUP) VALUES('African American or Afro-Caribbean');
    INSERT INTO ANCESTRAL_GROUP (ANCESTRAL_GROUP) VALUES('Greater Middle Eastern (Middle Eastern, North African or Persian)');
    INSERT INTO ANCESTRAL_GROUP (ANCESTRAL_GROUP) VALUES('Oceanian');
    INSERT INTO ANCESTRAL_GROUP (ANCESTRAL_GROUP) VALUES('Native American');
    INSERT INTO ANCESTRAL_GROUP (ANCESTRAL_GROUP) VALUES('Hispanic or Latin American');
    INSERT INTO ANCESTRAL_GROUP (ANCESTRAL_GROUP) VALUES('Aboriginal Australian');
    INSERT INTO ANCESTRAL_GROUP (ANCESTRAL_GROUP) VALUES('Circumpolar peoples');
    INSERT INTO ANCESTRAL_GROUP (ANCESTRAL_GROUP) VALUES('Other');
    INSERT INTO ANCESTRAL_GROUP (ANCESTRAL_GROUP) VALUES('NR');


--------------------------------------------------------
--  DDL for Table ANCESTRY_ANCESTRAL_GROUP
--------------------------------------------------------
  CREATE TABLE "ANCESTRY_ANCESTRAL_GROUP" (
      "ANCESTRY_ID" NUMBER(19,0),
      "ANCESTRAL_GROUP_ID" NUMBER(19,0));


--------------------------------------------------------
--  Constraints for Table ANCESTRY_ANCESTRAL_GROUP
--------------------------------------------------------
  ALTER TABLE "ANCESTRY_ANCESTRAL_GROUP" MODIFY ("ANCESTRAL_GROUP_ID" NOT NULL ENABLE);
  ALTER TABLE "ANCESTRY_ANCESTRAL_GROUP" MODIFY ("ANCESTRY_ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Ref Constraints for Table ANCESTRY_ANCESTRAL_GROUP
--------------------------------------------------------
  ALTER TABLE "ANCESTRY_ANCESTRAL_GROUP" ADD CONSTRAINT "ANCES_ANCGROUP_ID_FK" FOREIGN KEY ("ANCESTRAL_GROUP_ID")
	  REFERENCES "ANCESTRAL_GROUP" ("ID") ENABLE;
  ALTER TABLE "ANCESTRY_ANCESTRAL_GROUP" ADD CONSTRAINT "ANCES_ANCGROUP_ANCESTRY_ID_FK" FOREIGN KEY ("ANCESTRY_ID")
	  REFERENCES "ANCESTRY" ("ID") ENABLE;

