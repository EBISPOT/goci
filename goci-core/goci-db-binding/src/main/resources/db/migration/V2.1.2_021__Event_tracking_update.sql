/*
################################################################################
Migration script to create new tables for event tracking

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author: Emma Hastings
date:    May 6th 2016
version: 2.1.2.021
################################################################################
#######################################
#  CREATE NEW TABLE AND CONSTRAINTS  #
#######################################
*/
--------------------------------------------------------
--  DDL for Table EVENT
--------------------------------------------------------
CREATE TABLE "EVENT" (
    "ID"         NUMBER(19, 0),
    "DATE"       DATE,
    "EVENT_TYPE" VARCHAR2(255 CHAR),
    "USER_ID"    NUMBER(19, 0)
);

--------------------------------------------------------
--  DDL for Index EVENT_ID_PK
--------------------------------------------------------
CREATE UNIQUE INDEX "EVENT_ID_PK" ON "EVENT" ("ID");

--------------------------------------------------------
--  Constraints for EVENT USER
--------------------------------------------------------
ALTER TABLE "EVENT"
    ADD PRIMARY KEY ("ID") ENABLE;
ALTER TABLE "EVENT"
    MODIFY ("ID" NOT NULL ENABLE);

/*
###################################
#  CREATE SEQUENCES and TRIGGERS  #
###################################
*/
--------------------------------------------------------
-- Create trigger on USER
--------------------------------------------------------

CREATE OR REPLACE TRIGGER EVENT_TRG
BEFORE INSERT ON "EVENT"
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
ALTER TRIGGER EVENT_TRG ENABLE;

/*
#######################################
#  CREATE NEW  JOIN TABLES AND CONSTRAINTS  #
#######################################
*/

--------------------------------------------------------
--  DDL for Table STUDY_EVENT
--------------------------------------------------------
CREATE TABLE "STUDY_EVENT" (
    "STUDY_ID" NUMBER(19, 0),
    "EVENT_ID" NUMBER(19, 0)
);

--------------------------------------------------------
--  Constraints for Table STUDY_EVENT
--------------------------------------------------------
ALTER TABLE "STUDY_EVENT"
    MODIFY ("STUDY_ID" NOT NULL ENABLE);
ALTER TABLE "STUDY_EVENT"
    MODIFY ("EVENT_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Ref Constraints for Table STUDY_EVENT
--------------------------------------------------------
ALTER TABLE "STUDY_EVENT"
    ADD CONSTRAINT "STUDY_ID_FK" FOREIGN KEY ("STUDY_ID")
REFERENCES "STUDY" ("ID") ENABLE;
ALTER TABLE "STUDY_EVENT"
    ADD CONSTRAINT "STU_EVENT_ID_FK" FOREIGN KEY ("EVENT_ID")
REFERENCES "EVENT" ("ID") ENABLE;

--------------------------------------------------------
--  DDL for Table ASSOCIATION_EVENT
--------------------------------------------------------
CREATE TABLE "ASSOCIATION_EVENT" (
    "ASSOCIATION_ID" NUMBER(19, 0),
    "EVENT_ID"       NUMBER(19, 0)
);

--------------------------------------------------------
--  Constraints for Table ASSOCIATION_EVENT
--------------------------------------------------------
ALTER TABLE "ASSOCIATION_EVENT"
    MODIFY ("ASSOCIATION_ID" NOT NULL ENABLE);
ALTER TABLE "ASSOCIATION_EVENT"
    MODIFY ("EVENT_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Ref Constraints for Table ASSOCIATION_EVENT
--------------------------------------------------------
ALTER TABLE "ASSOCIATION_EVENT"
    ADD CONSTRAINT "ASSOCIATION_ID_FK" FOREIGN KEY ("ASSOCIATION_ID")
REFERENCES "ASSOCIATION" ("ID") ENABLE;
ALTER TABLE "ASSOCIATION_EVENT"
    ADD CONSTRAINT "ASS_EVENT_ID_FK" FOREIGN KEY ("EVENT_ID")
REFERENCES "EVENT" ("ID") ENABLE;