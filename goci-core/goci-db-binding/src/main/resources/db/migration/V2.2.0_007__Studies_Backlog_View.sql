/*

################################################################################
Migration script to update WEEKLY_CURATOR_TOTALS_SUMMARY_VIEW

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author: Dani Welter
date:    Apr 28th 2016
version: 2.1.2.020
################################################################################
*/

--------------------------------------------------------
--  CREATE STUDIES_BACKLOG_VIEW
--------------------------------------------------------

CREATE OR REPLACE VIEW STUDIES_BACKLOG_VIEW (ID, EVENT_DAY, STUDY_CREATION, STUDY_PUBLISHED)
AS SELECT ROWNUM, V.* FROM (SELECT TO_CHAR(EVENT_DATE_ROW,'yyyymmdd') AS EVENT_DAY,
     MAX(DECODE( EVENT_TYPE, 'STUDY_CREATION', TOTAL, 0 )) STUDY_CREATION,
     MAX(DECODE( EVENT_TYPE, 'STUDY_STATUS_CHANGE_PUBLISH_STUDY', TOTAL, 0 )) STUDY_PUBLISHED
     FROM  (SELECT TRUNC(EVENT_DATE) as EVENT_DATE_ROW, EVENT_TYPE, COUNT(*) as TOTAL from EVENT
     WHERE EVENT_TYPE in ('STUDY_STATUS_CHANGE_PUBLISH_STUDY', 'STUDY_CREATION')
     GROUP BY TRUNC(EVENT_DATE), EVENT_TYPE)
     GROUP BY EVENT_DATE_ROW
     ORDER BY EVENT_DATE_ROW ASC
) V;