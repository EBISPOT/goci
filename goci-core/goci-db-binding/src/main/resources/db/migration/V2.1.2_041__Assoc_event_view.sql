/*
################################################################################
Create table that displays all association events

author: Emma Hastings
date:    Aug 22th 2016
version: 2.1.2.041
################################################################################
*/
--------------------------------------------------------
-- Generate view
--------------------------------------------------------

CREATE OR REPLACE VIEW ASSOCIATION_EVENTS_VIEW (
        ID,
        STUDY_ID,
        ASSOCIATION_ID,
        EVENT_DATE,
        EVENT_TYPE,
        EVENT_DESCRIPTION,
        EVENT_USER)
  AS SELECT ROWNUM, V.* FROM
  (SELECT S.ID AS STUDY_ID, A.ID AS ASSOCIATION_ID , E.EVENT_DATE, E.EVENT_TYPE, E.EVENT_DESCRIPTION, SU.EMAIL AS EVENT_USER
   FROM STUDY S, ASSOCIATION A, EVENT E , ASSOCIATION_EVENT AE, SECURE_USER SU
   WHERE A.ID = AE.ASSOCIATION_ID
         AND S.ID = A.STUDY_ID
         AND E.ID = AE.EVENT_ID
         AND SU.ID = E.USER_ID
   ORDER BY S.ID DESC) V
