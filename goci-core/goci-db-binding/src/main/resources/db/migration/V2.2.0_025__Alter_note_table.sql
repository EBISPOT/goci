/*

################################################################################

Migration script to alter the table Note
The text_note has to be the same type of housekeeping.notes


author: Cinzia Malangone
date:    31 March 2017
version: 2.2.0.024
################################################################################
*/

  ALTER TABLE "NOTE" MODIFY "TEXT_NOTE" VARCHAR2(4000 BYTE);

--------------------------------------------------------
--  Note from housekeeping to note
--------------------------------------------------------
  INSERT INTO NOTE_SUBJECT(id,subject) VALUES (0,'Imported from previous system');
