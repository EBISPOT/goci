/*

################################################################################
Migration script to add the curation status 'Requires re-curation'

author: Trish Whetzel
date:    19th Feb 2019
version: 2.3.0.004
################################################################################
*/

--------------------------------------------------------
--  ADD INFO INTO THE TABLES. Please read the note
--------------------------------------------------------

INSERT INTO CURATION_STATUS(STATUS) VALUES ('Requires re-curation');
INSERT INTO "EVENT_TYPE"(ID,ACTION,EVENT_TYPE,TRANSLATED_EVENT) VALUES (EVENT_TYPE_SEQ.nextval,'Requires re-curation','STUDY_STATUS_CHANGE_REQUIRES_RE-CURATION','Study status changed to requires re-curation');
