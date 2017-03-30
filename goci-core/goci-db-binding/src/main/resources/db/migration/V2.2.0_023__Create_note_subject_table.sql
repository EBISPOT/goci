/*

################################################################################

Migration script to create the table Note_Subject
This table stores the list of possible subject for the note


author: Cinzia Malangone
date:    27 March 2017
version: 2.2.0.023
################################################################################
*/

--------------------------------------------------------
--  CREATE TABLE, INDEX, FOREIGN KEY
--------------------------------------------------------
  CREATE TABLE "NOTE_SUBJECT" (
     "ID" NUMBER(19,0),
     "SUBJECT" VARCHAR2(255 CHAR) NOT NULL,
     "LAST_UPDATE_TIME" TIMESTAMP
     );

  ALTER TABLE "NOTE_SUBJECT" ADD PRIMARY KEY ("ID") ENABLE;

  INSERT INTO NOTE_SUBJECT(id,subject) VALUES (1,'System note');
  INSERT INTO NOTE_SUBJECT(id,subject) VALUES (2,'General');
  INSERT INTO NOTE_SUBJECT(id,subject) VALUES (3,'Other');
