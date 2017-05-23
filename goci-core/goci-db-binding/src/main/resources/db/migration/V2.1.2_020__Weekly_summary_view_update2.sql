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
--  CREATE WEEKLY_CURATOR_TOTALS_SUMMARY_VIEW
--------------------------------------------------------

CREATE OR REPLACE VIEW WEEKLY_TOTALS_SUMMARY_VIEW (ID, WEEK, WEEKLY_ENTRIES, WEEKLY_STUDIES)
AS SELECT ROWNUM, V.* FROM (SELECT TRUNC(H.STUDY_ADDED_DATE, 'D') AS WEEK, COUNT(*) AS WEEKLY_ENTRIES, COUNT(DISTINCT S.PUBMED_ID) AS WEEKLY_STUDIES
FROM HOUSEKEEPING H, STUDY S
WHERE S.HOUSEKEEPING_ID = H.ID
AND H.STUDY_ADDED_DATE IS NOT NULL
GROUP BY TRUNC(H.STUDY_ADDED_DATE, 'D')
ORDER BY TRUNC(H.STUDY_ADDED_DATE, 'D') DESC) V;