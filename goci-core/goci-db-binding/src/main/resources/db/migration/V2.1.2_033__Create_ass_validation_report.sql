/*
################################################################################
Create ASSOCIATION_VALIDATION_REPORT table

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).


author: Emma Hastings
date:    Jun 24th 2016
version: 2.1.2.033
################################################################################
*/
--------------------------------------------------------
-- Generate table
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Table ASSOCIATION_VALIDATION_REPORT
--------------------------------------------------------

CREATE TABLE ASSOCIATION_VALIDATION_REPORT (
    ID NUMBER(19,0),
    ASSOCIATION_ID NUMBER(19,0),
    WARNING VARCHAR2(255 CHAR),
    VALIDATED_FIELD VARCHAR2(255 CHAR));

--------------------------------------------------------
--  DDL for Index ASSOC_REP_V_ID_PK
--------------------------------------------------------

CREATE UNIQUE INDEX "ASSOC_REP_V_ID_PK" ON "ASSOCIATION_VALIDATION_REPORT" ("ID");

--------------------------------------------------------
--  Constraints for Table ASSOCIATION_VALIDATION_REPORT
--------------------------------------------------------

ALTER TABLE "ASSOCIATION_VALIDATION_REPORT"
    ADD PRIMARY KEY ("ID") ENABLE;
ALTER TABLE "ASSOCIATION_VALIDATION_REPORT"
    MODIFY ("ID" NOT NULL ENABLE);

ALTER TABLE "ASSOCIATION_VALIDATION_REPORT" MODIFY ("ASSOCIATION_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Ref Constraints for Table ASSOCIATION_VALIDATION_REPORT
--------------------------------------------------------
ALTER TABLE "ASSOCIATION_VALIDATION_REPORT" ADD CONSTRAINT "ASSOC_REP_V_ASSOC_ID_FK" FOREIGN KEY ("ASSOCIATION_ID")
REFERENCES "ASSOCIATION" ("ID") ENABLE;

--------------------------------------------------------
-- Create trigger
--------------------------------------------------------

CREATE OR REPLACE TRIGGER AVR_TRG
BEFORE INSERT ON "ASSOCIATION_VALIDATION_REPORT"
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL
        THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL
            INTO :NEW.ID
            FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER AVR_TRG ENABLE;
