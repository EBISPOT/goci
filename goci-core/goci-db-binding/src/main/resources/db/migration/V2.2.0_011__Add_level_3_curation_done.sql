/*

################################################################################

Migration script to add the curation status 'Level 3 curation done'

author: Cinzia Malangone
date:    18th Nov 2016
version: 2.2.0.011

Note: The db EventType has already the info about this curation entry.
      Refactoring Event Type Enam task
################################################################################
*/

--------------------------------------------------------
--  ADD INFO INTO THE TABLES. Please read the note
--------------------------------------------------------

INSERT INTO CURATION_STATUS(STATUS) VALUES ('Level 3 curation done');

