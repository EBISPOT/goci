/*
################################################################################
Migration script to create new tables for ethnicity tracking

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author: Emma Hastings
date:    Aug 04th 2016
version: 2.1.2.038
################################################################################

#######################################
#  CREATE NEW JOIN TABLES AND CONSTRAINTS
#######################################
*/
--------------------------------------------------------
--  DDL for Table ETHNICITY_EVENT
--------------------------------------------------------
CREATE TABLE "ETHNICITY_EVENT" (
    "ETHNICITY_ID" NUMBER(19, 0),
    "EVENT_ID" NUMBER(19, 0)
);

--------------------------------------------------------
--  Constraints for Table ETHNICITY_EVENT
--------------------------------------------------------
ALTER TABLE "ETHNICITY_EVENT"
    MODIFY ("ETHNICITY_ID" NOT NULL ENABLE);
ALTER TABLE "ETHNICITY_EVENT"
    MODIFY ("EVENT_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Ref Constraints for Table ETHNICITY_EVENT
--------------------------------------------------------
ALTER TABLE "ETHNICITY_EVENT"
    ADD CONSTRAINT "ETH_ID_FK" FOREIGN KEY ("ETHNICITY_ID")
REFERENCES "ETHNICITY" ("ID") ENABLE;
ALTER TABLE "ETHNICITY_EVENT"
    ADD CONSTRAINT "ETH_EVENT_ID_FK" FOREIGN KEY ("EVENT_ID")
REFERENCES "EVENT" ("ID") ENABLE;

--------------------------------------------------------
--  DDL for Table DELETED_ETHNICITY
--------------------------------------------------------
CREATE TABLE "DELETED_ETHNICITY" (
    "ID"         NUMBER(19, 0),
    "STUDY_ID"         NUMBER(19, 0)
);

--------------------------------------------------------
--  DDL for Index DELETED_ETHNICITY_ID_PK
--------------------------------------------------------
CREATE UNIQUE INDEX "DELETED_ETH_ID_PK" ON "DELETED_ETHNICITY" ("ID");

--------------------------------------------------------
--  Constraints for DELETED_ETHNICITY
--------------------------------------------------------
ALTER TABLE "DELETED_ETHNICITY"
    ADD PRIMARY KEY ("ID") ENABLE;
ALTER TABLE "DELETED_ETHNICITY"
    MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
--  DDL for Table DELETED_ETHNICITY_EVENT
--------------------------------------------------------
CREATE TABLE "DELETED_ETHNICITY_EVENT" (
    "DELETED_ETHNICITY_ID" NUMBER(19, 0),
    "EVENT_ID" NUMBER(19, 0)
);

--------------------------------------------------------
--  Constraints for Table DELETED_ETHNICITY_EVENT
--------------------------------------------------------
ALTER TABLE "DELETED_ETHNICITY_EVENT"
    MODIFY ("DELETED_ETHNICITY_ID" NOT NULL ENABLE);
ALTER TABLE "DELETED_ETHNICITY_EVENT"
    MODIFY ("EVENT_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Ref Constraints for Table DELETED_ETHNICITY_EVENT
--------------------------------------------------------
ALTER TABLE "DELETED_ETHNICITY_EVENT"
    ADD CONSTRAINT "DETH_ID_FK" FOREIGN KEY ("DELETED_ETHNICITY_ID")
REFERENCES "DELETED_ETHNICITY" ("ID") ENABLE;
ALTER TABLE "DELETED_ETHNICITY_EVENT"
    ADD CONSTRAINT "DETH_EVENT_ID_FK" FOREIGN KEY ("EVENT_ID")
REFERENCES "EVENT" ("ID") ENABLE;