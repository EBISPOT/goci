/*
################################################################################
Create a view table that displays weekly study progress

author: Trish Whetzel
date:    Apr 10th 2018
version: 2.3.0.002
################################################################################
*/
--------------------------------------------------------
-- Generate view
--------------------------------------------------------

CREATE OR REPLACE VIEW WEEKLY_PROGRESS_VIEW (
    ID,
    WEEK_START_DAY,
    PUBMED_ID,
    STUDY_ID,
    EVENT_TYPE)
  AS SELECT ROWNUM, V.* FROM
    (SELECT TRUNC(TO_DATE(E.EVENT_DATE), 'D') AS WEEK_START_DAY, P.PUBMED_ID, S.ID AS STUDY_ID, E.EVENT_TYPE
     FROM STUDY S, EVENT E , STUDY_EVENT SE, PUBLICATION P
     WHERE S.ID = SE.STUDY_ID
           AND E.ID = SE.EVENT_ID
           AND S.PUBLICATION_ID=P.ID
     ORDER BY WEEK_START_DAY DESC) V