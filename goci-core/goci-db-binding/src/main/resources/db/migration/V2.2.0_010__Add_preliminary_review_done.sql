/*

################################################################################

Migration script to add the curation status 'Preliminary review done'

author: Cinzia Malangone
date:    18th Nov 2016
version: 2.2.0.010

################################################################################
*/

--------------------------------------------------------
--  ADD INFO INTO THE TABLES
--------------------------------------------------------

INSERT INTO CURATION_STATUS(STATUS) VALUES ('Preliminary review done');
INSERT INTO "EVENT_TYPE"(ID,ACTION,EVENT_TYPE,TRANSLATED_EVENT) VALUES (EVENT_TYPE_SEQ.nextval,'Preliminary review done','STUDY_STATUS_CHANGE_PRELIMINARY_REVIEW_DONE','Study status changed to preliminary review done');
