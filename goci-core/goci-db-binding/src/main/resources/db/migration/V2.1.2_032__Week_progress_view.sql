/*
################################################################################
Create a view table that displays weekly study progress

author: Emma Hastings
date:    Jun 09th 2016
version: 2.1.2.032
################################################################################
*/
--------------------------------------------------------
-- Generate view
--------------------------------------------------------

CREATE OR REPLACE VIEW WEEKLY_PROGRESS_VIEW (
        ID,
        WEEK_START_DAY,
        STUDY_ID,
        EVENT_TYPE)
  AS SELECT ROWNUM, V.* FROM
  (SELECT TRUNC(TO_DATE(E.EVENT_DATE), 'D') AS WEEK_START_DAY, S.ID AS STUDY_ID, E.EVENT_TYPE
   FROM STUDY S, EVENT E , STUDY_EVENT SE
   WHERE S.ID = SE.STUDY_ID
         AND E.ID = SE.EVENT_ID
   ORDER BY WEEK_START_DAY DESC) V