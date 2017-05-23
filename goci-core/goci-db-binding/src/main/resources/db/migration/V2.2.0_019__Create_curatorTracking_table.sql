/*

################################################################################

Migration script to create the table curator_tracking

author: Cinzia Malangone
date:    23th Nov 2016
version: 2.2.0.019
################################################################################
*/

--------------------------------------------------------
--  CREATE TABLE, INDEX, FOREIGN KEY
--------------------------------------------------------


  CREATE TABLE "CURATOR_TRACKING"(
     "ID" NUMBER(19,0),
     "WEEK" NUMBER(19,0),
     "YEAR" NUMBER(19,0),
     "STUDY_ID" NUMBER(19,0) NOT NULL,
     "PUBMED_ID" VARCHAR2(255 CHAR),
     "CURATOR_NAME" VARCHAR2(255 CHAR) NOT NULL,
     "LEVEL_CURATION_DATE" DATE,
     "LEVEL_CURATION" VARCHAR2(30 CHAR) NOT NULL
     );

  ALTER TABLE "CURATOR_TRACKING" ADD PRIMARY KEY ("ID") ENABLE;

  ALTER TABLE "CURATOR_TRACKING" ADD CONSTRAINT "CURATOR_TRACKING_ID_FK" FOREIGN KEY ("STUDY_ID")
	  REFERENCES "STUDY" ("ID") ENABLE;




