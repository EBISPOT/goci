/*
################################################################################
Create table that displays all deleted ethnicity events

author: Emma Hastings
date:    Aug 23th 2016
version: 2.1.2.046
################################################################################
*/
--------------------------------------------------------
-- Generate view
--------------------------------------------------------

CREATE OR REPLACE VIEW DELETED_ETH_EVENTS_VIEW (
        ID,
        STUDY_ID,
        ETHNICITY_ID,
        EVENT_DATE,
        EVENT_TYPE,
        EVENT_DESCRIPTION,
        EVENT_USER)
    AS SELECT ROWNUM, V.* FROM
        (SELECT DETH.STUDY_ID AS STUDY_ID, DETH.ID AS ETHNICITY_ID , E.EVENT_DATE, E.EVENT_TYPE, E.EVENT_DESCRIPTION, SU.EMAIL AS EVENT_USER
         FROM DELETED_ETHNICITY DETH, EVENT E , DELETED_ETHNICITY_EVENT DEE, SECURE_USER SU
         WHERE DETH.ID = DEE.DELETED_ETHNICITY_ID
               AND E.ID = DEE.EVENT_ID
               AND SU.ID = E.USER_ID
         ORDER BY DETH.STUDY_ID DESC) V
