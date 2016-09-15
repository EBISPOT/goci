/*
################################################################################
Create table that displays all ethnicity events

author: Emma Hastings
date:    Aug 23th 2016
version: 2.1.2.045
################################################################################
*/
--------------------------------------------------------
-- Generate view
--------------------------------------------------------

CREATE OR REPLACE VIEW ETHNICITY_EVENTS_VIEW (
        ID,
        STUDY_ID,
        ETHNICITY_ID,
        EVENT_DATE,
        EVENT_TYPE,
        EVENT_DESCRIPTION,
        EVENT_USER)
  AS SELECT ROWNUM, V.* FROM
  (SELECT S.ID AS STUDY_ID, ETH.ID AS ETHNICITY_ID , E.EVENT_DATE, E.EVENT_TYPE, E.EVENT_DESCRIPTION, SU.EMAIL AS EVENT_USER
   FROM STUDY S, ETHNICITY ETH, EVENT E , ETHNICITY_EVENT EE, SECURE_USER SU
   WHERE ETH.ID = EE.ETHNICITY_ID
         AND S.ID = ETH.STUDY_ID
         AND E.ID = EE.EVENT_ID
         AND SU.ID = E.USER_ID
   ORDER BY S.ID DESC) V
