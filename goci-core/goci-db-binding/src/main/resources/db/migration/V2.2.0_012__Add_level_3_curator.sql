/*

################################################################################

Migration script to add the curator 'Level 3 Curator'

author: Cinzia Malangone
date:    18th Nov 2016
version: 2.2.0.012
Note: The db EventType has already the info about this curation entry.
      Refactoring Event Type Enam task
################################################################################
*/

--------------------------------------------------------
--  ADD INFO INTO THE TABLES
--------------------------------------------------------
INSERT INTO CURATOR(LAST_NAME) VALUES ('Level 3 Curator');