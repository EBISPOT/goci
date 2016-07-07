/*
################################################################################
Migration script to create new tables required to track association deletion

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author: Emma Hastings
date:    July 07th 2016
version: 2.1.2.035
################################################################################
#######################################
#  CREATE NEW TABLE AND CONSTRAINTS  #
#######################################
*/
--------------------------------------------------------
--  DDL for Table DELETED_ASSOCIATION
--------------------------------------------------------
CREATE TABLE "DELETED_ASSOCIATION" (
    "ID"         NUMBER(19, 0),
    "STUDY_ID"         NUMBER(19, 0)
);

--------------------------------------------------------
--  DDL for Index DELETED_ASSOCIATION_ID_PK
--------------------------------------------------------
CREATE UNIQUE INDEX "DELETED_ASSOCIATION_ID_PK" ON "DELETED_ASSOCIATION" ("ID");

--------------------------------------------------------
--  Constraints for DELETED_ASSOCIATION
--------------------------------------------------------
ALTER TABLE "DELETED_ASSOCIATION"
    ADD PRIMARY KEY ("ID") ENABLE;
ALTER TABLE "DELETED_ASSOCIATION"
    MODIFY ("ID" NOT NULL ENABLE);

/*
#######################################
#  CREATE NEW JOIN TABLE AND CONSTRAINT
#######################################
*/
--------------------------------------------------------
--  DDL for Table DELETED_ASSOCIATION_EVENT
--------------------------------------------------------
CREATE TABLE "DELETED_ASSOCIATION_EVENT" (
    "DELETED_ASSOCIATION_ID" NUMBER(19, 0),
    "EVENT_ID" NUMBER(19, 0)
);

--------------------------------------------------------
--  Constraints for Table DELETED_ASSOCIATION_EVENT
--------------------------------------------------------
ALTER TABLE "DELETED_ASSOCIATION_EVENT"
    MODIFY ("DELETED_ASSOCIATION_ID" NOT NULL ENABLE);
ALTER TABLE "DELETED_ASSOCIATION_EVENT"
    MODIFY ("EVENT_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Ref Constraints for Table DELETED_ASSOCIATION_EVENT
--------------------------------------------------------
ALTER TABLE "DELETED_ASSOCIATION_EVENT"
    ADD CONSTRAINT "DASS_ID_FK" FOREIGN KEY ("DELETED_ASSOCIATION_ID")
REFERENCES "DELETED_ASSOCIATION" ("ID") ENABLE;
ALTER TABLE "DELETED_ASSOCIATION_EVENT"
    ADD CONSTRAINT "DEL_ASS_EVENT_ID_FK" FOREIGN KEY ("EVENT_ID")
REFERENCES "EVENT" ("ID") ENABLE;