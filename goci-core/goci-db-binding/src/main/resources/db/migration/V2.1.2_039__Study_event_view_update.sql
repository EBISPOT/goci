/*
################################################################################
Update table that displays all study events to include event description

author: Emma Hastings
date:    Aug 22th 2016
version: 2.1.2.039
################################################################################
*/
--------------------------------------------------------
-- Generate view
--------------------------------------------------------

CREATE OR REPLACE VIEW STUDY_EVENTS_VIEW (
        ID,
        STUDY_ID,
        EVENT_DATE,
        EVENT_TYPE,
        EVENT_DESCRIPTION,
        EVENT_USER)
  AS SELECT ROWNUM, V.* FROM
  (SELECT S.ID AS STUDY_ID, E.EVENT_DATE, E.EVENT_TYPE, E.EVENT_DESCRIPTION, SU.EMAIL AS EVENT_USER
   FROM STUDY S, EVENT E , STUDY_EVENT SE, SECURE_USER SU
   WHERE S.ID = SE.STUDY_ID
    AND E.ID = SE.EVENT_ID
    AND SU.ID = E.USER_ID
  ORDER BY S.ID DESC) V
