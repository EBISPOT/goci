/*

################################################################################
Migration script to create a User and Role table

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    February 11th 2015
version: 1.9.9.022 (pre 2.0)
################################################################################

*/

/*
#######################################
#  CREATE NEW TABLES AND CONSTRAINTS  #
#######################################
*/

--------------------------------------------------------
--  DDL for Table USER
--------------------------------------------------------
  CREATE TABLE "USER" (
     "ID" NUMBER(19,0),
     "EMAIL" VARCHAR2(255 CHAR),
     "PASSWORD_HASH" VARCHAR2(255 CHAR),
     "ROLE_ID" NUMBER(19,0));

--------------------------------------------------------
--  DDL for Table ROLE
--------------------------------------------------------
  CREATE TABLE "ROLE" (
      "ID" NUMBER(19,0),
      "ROLE" VARCHAR2(255 CHAR));

--------------------------------------------------------
--  DDL for Index USER_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "USER_ID_PK" ON "USER" ("ID");

--------------------------------------------------------
--  DDL for Index ROLE_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "ROLE_ID_PK" ON "ROLE" ("ID");

--------------------------------------------------------
--  Constraints for Table USER
--------------------------------------------------------
  ALTER TABLE "USER" ADD PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "USER" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table ROLE
--------------------------------------------------------
  ALTER TABLE "ROLE" ADD PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "ROLE" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Ref Constraints for Table USER
--------------------------------------------------------
  ALTER TABLE "USER" ADD CONSTRAINT "STUDY_ROLE_ID_FK" FOREIGN KEY ("ROLE_ID")
	  REFERENCES "ROLE" ("ID") ENABLE;


/*
###################################
#  CREATE SEQUENCES and TRIGGERS  #
###################################
*/

--------------------------------------------------------
-- Create trigger on USER
--------------------------------------------------------

CREATE OR REPLACE TRIGGER USER_TRG
BEFORE INSERT ON "USER"
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER USER_TRG ENABLE;

--------------------------------------------------------
-- Create trigger on ROLE
--------------------------------------------------------
CREATE OR REPLACE TRIGGER ROLE_TRG
BEFORE INSERT ON "ROLE"
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER ROLE_TRG ENABLE;

