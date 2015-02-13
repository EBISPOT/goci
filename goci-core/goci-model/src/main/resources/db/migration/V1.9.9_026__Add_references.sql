/*

################################################################################
Migration script to add missing referential constraints

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Tony Burdett
date:    February 13th 2015
version: 1.9.9.026 (pre 2.0)
################################################################################

*/


--------------------------------------------------------
--  Ref Constraints for Table ASSOCIATION_LOCUS
--------------------------------------------------------
DELETE FROM ASSOCIATION_LOCUS al WHERE al.LOCUS_ID NOT IN (SELECT ID FROM LOCUS);
DELETE FROM ASSOCIATION_LOCUS al WHERE al.ASSOCIATION_ID NOT IN (SELECT ID FROM ASSOCIATION);

ALTER TABLE "ASSOCIATION_LOCUS" ADD CONSTRAINT "ASSOCIATION_LOCUS_ASSOC_ID_FK" FOREIGN KEY ("ASSOCIATION_ID")
REFERENCES "ASSOCIATION" ("ID") ENABLE;
ALTER TABLE "ASSOCIATION_LOCUS" ADD CONSTRAINT "ASSOCIATION_LOCUS_LOCUS_ID_FK" FOREIGN KEY ("LOCUS_ID")
REFERENCES "LOCUS" ("ID") ENABLE;
