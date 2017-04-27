/*
################################################################################
Migration script to create ANCESTRY_COUNTRY_OF_ORIGIN and
ANCESTRY_COUNTRY_OF_RECRUITMENT tables

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Dani Welter
date:   April 24th 2017
version: 2.2.0.028
################################################################################

#######################################
#  CREATE NEW TABLES AND CONSTRAINTS
#######################################
*/


--------------------------------------------------------
--  DDL for Table ANCESTRY_COUNTRY_OF_ORIGIN
--------------------------------------------------------
  CREATE TABLE "ANCESTRY_COUNTRY_OF_ORIGIN" (
      "ANCESTRY_ID" NUMBER(19,0),
      "COUNTRY_ID" NUMBER(19,0));


--------------------------------------------------------
--  Constraints for Table ANCESTRY_COUNTRY_OF_ORIGIN
--------------------------------------------------------
  ALTER TABLE "ANCESTRY_COUNTRY_OF_ORIGIN" MODIFY ("COUNTRY_ID" NOT NULL ENABLE);
  ALTER TABLE "ANCESTRY_COUNTRY_OF_ORIGIN" MODIFY ("ANCESTRY_ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Ref Constraints for Table ANCESTRY_COUNTRY_OF_ORIGIN
--------------------------------------------------------
  ALTER TABLE "ANCESTRY_COUNTRY_OF_ORIGIN" ADD CONSTRAINT "ANCES_COUNTRYOO_ID_FK" FOREIGN KEY ("COUNTRY_ID")
	  REFERENCES "COUNTRY" ("ID") ENABLE;
  ALTER TABLE "ANCESTRY_COUNTRY_OF_ORIGIN" ADD CONSTRAINT "ANCES_COUNTRYOO_ANCESTRY_ID_FK" FOREIGN KEY ("ANCESTRY_ID")
	  REFERENCES "ANCESTRY" ("ID") ENABLE;


--------------------------------------------------------
--  DDL for Table ANCESTRY_COUNTRY_OF_RECRUITMENT
--------------------------------------------------------
  CREATE TABLE "ANCESTRY_COUNTRY_RECRUITMENT" (
      "ANCESTRY_ID" NUMBER(19,0),
      "COUNTRY_ID" NUMBER(19,0));


--------------------------------------------------------
--  Constraints for Table ANCESTRY_COUNTRY_OF_RECRUITMENT
--------------------------------------------------------
  ALTER TABLE "ANCESTRY_COUNTRY_RECRUITMENT" MODIFY ("COUNTRY_ID" NOT NULL ENABLE);
  ALTER TABLE "ANCESTRY_COUNTRY_RECRUITMENT" MODIFY ("ANCESTRY_ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Ref Constraints for Table ANCESTRY_COUNTRY_OF_RECRUITMENT
--------------------------------------------------------
  ALTER TABLE "ANCESTRY_COUNTRY_RECRUITMENT" ADD CONSTRAINT "ANCES_COUNTRYOR_ID_FK" FOREIGN KEY ("COUNTRY_ID")
	  REFERENCES "COUNTRY" ("ID") ENABLE;
  ALTER TABLE "ANCESTRY_COUNTRY_RECRUITMENT" ADD CONSTRAINT "ANCES_COUNTRYOR_ANCESTRY_ID_FK" FOREIGN KEY ("ANCESTRY_ID")
	  REFERENCES "ANCESTRY" ("ID") ENABLE;


--------------------------------------------------------
-- Rename NAME in COUNTRY to COUNTRY_NAME
--------------------------------------------------------
   ALTER TABLE COUNTRY RENAME COLUMN NAME TO COUNTRY_NAME;

--------------------------------------------------------
-- Insert NR value into COUNTRY
--------------------------------------------------------
    INSERT INTO COUNTRY (ID, MAJOR_AREA, COUNTRY_NAME, REGION) VALUES(333, 'NR', 'NR', 'NR');


--------------------------------------------------------
-- Insert NR value into empty COUNTRY_OF_RECRUITMENT
-- and COUNTRY_OF_ORIGIN
--------------------------------------------------------
    UPDATE ANCESTRY SET COUNTRY_OF_ORIGIN = 'NR' WHERE COUNTRY_OF_ORIGIN = null;
    UPDATE ANCESTRY SET COUNTRY_OF_RECRUITMENT = 'NR' WHERE COUNTRY_OF_RECRUITMENT = null

