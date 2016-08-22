/*
################################################################################
Update table that displays all deleted study events to include event description

author: Emma Hastings
date:    Aug 22th 2016
version: 2.1.2.039
################################################################################
*/
--------------------------------------------------------
-- Generate view
--------------------------------------------------------

CREATE OR REPLACE VIEW DELETED_STUDY_EVENTS_VIEW (
        ID,
        STUDY_ID,
        EVENT_DATE,
        EVENT_TYPE,
        EVENT_DESCRIPTION,
        EVENT_USER)
  AS SELECT ROWNUM, V.* FROM
  (SELECT DS.ID AS STUDY_ID, E.EVENT_DATE, E.EVENT_TYPE,  E.EVENT_DESCRIPTION, SU.EMAIL AS EVENT_USER
   FROM DELETED_STUDY DS, EVENT E , DELETED_STUDY_EVENT DSE, SECURE_USER SU
   WHERE DS.ID = DSE.DELETED_STUDY_ID
         AND E.ID = DSE.EVENT_ID
         AND SU.ID = E.USER_ID
  ORDER BY DS.ID DESC) V
