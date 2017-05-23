/*

################################################################################

Migration script to create WEEKLY_TRACKING table

author: Cinzia Malangone
date:    23th Nov 2016
version: 2.2.0.018
################################################################################
*/

--------------------------------------------------------
--  CREATE TABLE, INDEX, FOREIGN KEY
--------------------------------------------------------


  CREATE TABLE "WEEKLY_TRACKING" (
     "ID" NUMBER(19,0),
     "WEEK" NUMBER(19,0),
     "YEAR" NUMBER(19,0),
     "STUDY_ID" NUMBER(19,0) NOT NULL,
     "PUBMED_ID" VARCHAR2(255 CHAR),
     "STATUS" VARCHAR2(255 CHAR),
     "EVENT_DATE" DATE
  );

  ALTER TABLE "WEEKLY_TRACKING" ADD PRIMARY KEY ("ID") ENABLE;

