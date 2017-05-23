/*
################################################################################
Migration script to create new tables required to track study deletion

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author: Emma Hastings
date:    May 31th 2016
version: 2.1.2.028
################################################################################
#######################################
#  CREATE NEW TABLE AND CONSTRAINTS  #
#######################################
*/
--------------------------------------------------------
--  DDL for Table DELETED_STUDY
--------------------------------------------------------
CREATE TABLE "DELETED_STUDY" (
    "ID"         NUMBER(19, 0),
    "PUBMED_ID" VARCHAR2(255 CHAR),
    "TITLE" VARCHAR2(4000 BYTE)
);

--------------------------------------------------------
--  DDL for Index DELETED_STUDY_ID_PK
--------------------------------------------------------
CREATE UNIQUE INDEX "DELETED_STUDY_ID_PK" ON "DELETED_STUDY" ("ID");

--------------------------------------------------------
--  Constraints for DELETED_STUDY
--------------------------------------------------------
ALTER TABLE "DELETED_STUDY"
    ADD PRIMARY KEY ("ID") ENABLE;
ALTER TABLE "DELETED_STUDY"
    MODIFY ("ID" NOT NULL ENABLE);

/*
#######################################
#  CREATE NEW JOIN TABLE AND CONSTRAINT
#######################################
*/
--------------------------------------------------------
--  DDL for Table DELETED_STUDY_EVENT
--------------------------------------------------------
CREATE TABLE "DELETED_STUDY_EVENT" (
    "DELETED_STUDY_ID" NUMBER(19, 0),
    "EVENT_ID" NUMBER(19, 0)
);

--------------------------------------------------------
--  Constraints for Table DELETED_STUDY_EVENT
--------------------------------------------------------
ALTER TABLE "DELETED_STUDY_EVENT"
    MODIFY ("DELETED_STUDY_ID" NOT NULL ENABLE);
ALTER TABLE "DELETED_STUDY_EVENT"
    MODIFY ("EVENT_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Ref Constraints for Table DELETED_STUDY_EVENT
--------------------------------------------------------
ALTER TABLE "DELETED_STUDY_EVENT"
    ADD CONSTRAINT "DSTU_ID_FK" FOREIGN KEY ("DELETED_STUDY_ID")
REFERENCES "DELETED_STUDY" ("ID") ENABLE;
ALTER TABLE "DELETED_STUDY_EVENT"
    ADD CONSTRAINT "EVENT_ID_FK" FOREIGN KEY ("EVENT_ID")
REFERENCES "EVENT" ("ID") ENABLE;