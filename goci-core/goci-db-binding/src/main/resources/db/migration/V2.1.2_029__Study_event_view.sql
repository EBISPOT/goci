/*
################################################################################
Create a view table that displays all study events

author: Emma Hastings
date:    May 31th 2016
version: 2.1.2.029
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
        EVENT_USER)
  AS SELECT ROWNUM, V.* FROM
  (SELECT S.ID AS STUDY_ID, E.EVENT_DATE, E.EVENT_TYPE, SU.EMAIL AS EVENT_USER
   FROM STUDY S, EVENT E , STUDY_EVENT SE, SECURE_USER SU
   WHERE S.ID = SE.STUDY_ID
    AND E.ID = SE.EVENT_ID
    AND SU.ID = E.USER_ID
  ORDER BY S.ID DESC) V
