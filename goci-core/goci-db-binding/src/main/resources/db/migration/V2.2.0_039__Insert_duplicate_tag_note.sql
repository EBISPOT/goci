/*

################################################################################

Migration script to insert a new type of note

author: Cinzia Malangone
date:    4th Jan 2018
version: 2.2.0.039

################################################################################
*/

--------------------------------------------------------
--  New note for duplication STUDY
--------------------------------------------------------
INSERT INTO NOTE_SUBJECT(id,subject) VALUES (9,'Duplication TAG');
