/*

################################################################################
Migration script to create a new curation status for unpublishing studies,
create a new UNPUBLISH_REASON table  and create a new column in HOUSEKEEPING
to crossreference the unpublish reason.

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Dani Welter
date:    June 4th 2015
version: 1.9.9.054 (pre 2.0)
################################################################################
*/

--------------------------------------------------------
-- INSERT INTO CURATION_STATUS
--------------------------------------------------------

INSERT INTO CURATION_STATUS(ID, SEQNBR, STATUS) VALUES(65, 15, 'Unpublished from catalog');


--------------------------------------------------------
-- CREATE UNPUBLISH_REASON TABLE
--------------------------------------------------------

  CREATE TABLE "UNPUBLISH_REASON" (
      "ID" NUMBER(19,0),
      "REASON" VARCHAR2(255 CHAR));


--------------------------------------------------------
--  DDL for Index UNPUBLISH_REASON_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "UNPUBLISH_REASON_ID_PK" ON "UNPUBLISH_REASON" ("ID");

--------------------------------------------------------
--  Constraints for Table UNPUBLISH_REASON
--------------------------------------------------------
  ALTER TABLE "UNPUBLISH_REASON" ADD PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "UNPUBLISH_REASON" MODIFY ("ID" NOT NULL ENABLE);


--------------------------------------------------------
--  ALTER HOUSEKEEPING
--------------------------------------------------------

ALTER TABLE "HOUSEKEEPING" ADD "UNPUBLISH_REASON_ID" NUMBER(19,0);


--------------------------------------------------------
--  Ref Constraint for Table HOUSEKEEPING
--------------------------------------------------------
  ALTER TABLE "HOUSEKEEPING" ADD CONSTRAINT "HOUSEKEEPING_UNPUB_REAS_ID_FK" FOREIGN KEY ("UNPUBLISH_REASON_ID")
	  REFERENCES "UNPUBLISH_REASON" ("ID") ENABLE;


