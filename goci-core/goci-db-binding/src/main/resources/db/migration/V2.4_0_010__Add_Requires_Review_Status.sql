/*

################################################################################

Migration script to add the curation status 'Requires Review'

author: Jon Stewart
date:    05 March 2020
version: 2.4.0.010

################################################################################
*/

--------------------------------------------------------
--  ADD INFO INTO THE TABLES
--------------------------------------------------------

INSERT INTO CURATION_STATUS(STATUS) VALUES ('Requires Review');
INSERT INTO "EVENT_TYPE"(ID,ACTION,EVENT_TYPE,TRANSLATED_EVENT) VALUES (EVENT_TYPE_SEQ.nextval,'Requires Review',
'STUDY_STATUS_CHANGE_REQUIRES_REVIEW','Study status changed to Requires Review');
INSERT INTO "EVENT_TYPE"(ID,ACTION,EVENT_TYPE,TRANSLATED_EVENT) VALUES (EVENT_TYPE_SEQ.nextval,'Deposition Import',
'STUDY_STATUS_CHANGE_DEPOSITION_IMPORT','Study created by Deposition Import');
