/*

################################################################################

Migration script to create the table Note
This table stores the public/private notes/comments.


author: Cinzia Malangone
date:    27 March 2017
version: 2.2.0.024
################################################################################
*/

--------------------------------------------------------
--  CREATE TABLE, INDEX, FOREIGN KEY
--------------------------------------------------------
  CREATE TABLE "NOTE" (
     "ID" NUMBER(19,0),
     "TEXT_NOTE" VARCHAR2(255 CHAR) NOT NULL,
     "STUDY_ID" NUMBER(19,0) NOT NULL,
     "NOTE_SUBJECT_ID" NUMBER(19,0) NOT NULL,
     "STATUS" NUMBER(1,0) NOT NULL,
     "CURATOR_ID" NUMBER(19,0) NOT NULL,
     "CONTENT_TYPE" VARCHAR2(150) NOT NULL,
     "GENERIC_ID" NUMBER(19,0) NOT NULL,
     "CREATED" TIMESTAMP,
     "UPDATED" TIMESTAMP
     );

  ALTER TABLE "NOTE" ADD PRIMARY KEY ("ID") ENABLE;

  ALTER TABLE "NOTE" ADD CONSTRAINT "NOTE_STUDY_ID_FK" FOREIGN KEY ("STUDY_ID")
	  REFERENCES "STUDY" ("ID") ENABLE;

  ALTER TABLE "NOTE" ADD CONSTRAINT "NOTE_SUBJECT_ID_FK" FOREIGN KEY ("NOTE_SUBJECT_ID")
	  REFERENCES "NOTE_SUBJECT" ("ID") ENABLE;

  ALTER TABLE "NOTE" ADD CONSTRAINT "NOTE_CURATOR_ID_FK" FOREIGN KEY ("CURATOR_ID")
	  REFERENCES "CURATOR" ("ID") ENABLE;

--------------------------------------------------------
--  DDL for Index EVENT_ID_PK
--------------------------------------------------------
  CREATE INDEX "STUDY_NOTE_ID_PK" ON "NOTE" ("STUDY_ID");

  CREATE INDEX "CURATOR_NOTE_ID_PK" ON "NOTE" ("CURATOR_ID");

  CREATE INDEX "SUBJECT_NOTE_ID_PK" ON "NOTE" ("NOTE_SUBJECT_ID");

  CREATE INDEX "CONTENT_TYPE_ID_IX" ON "NOTE" ("CONTENT_TYPE", "GENERIC_ID");