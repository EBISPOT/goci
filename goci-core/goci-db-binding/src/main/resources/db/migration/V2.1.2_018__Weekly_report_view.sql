/*

################################################################################
Migration script to create WEEKLY_CURATOR_TOTALS_SUMMARY_VIEW

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author: Dani Welter
date:    Apr 25th 2016
version: 2.1.2.018
################################################################################
*/

--------------------------------------------------------
--  CREATE WEEKLY_CURATOR_TOTALS_SUMMARY_VIEW
--------------------------------------------------------

CREATE OR REPLACE VIEW WEEKLY_TOTALS_SUMMARY_VIEW (WEEK, ENTRIES, STUDIES)
AS SELECT TRUNC(H.STUDY_ADDED_DATE, 'D') AS WEEK, COUNT(*) AS ENTRIES, COUNT(DISTINCT S.PUBMED_ID) AS STUDIES
FROM HOUSEKEEPING H, STUDY S
WHERE S.HOUSEKEEPING_ID = H.ID
AND H.STUDY_ADDED_DATE IS NOT NULL
GROUP BY TRUNC(H.STUDY_ADDED_DATE, 'D')
ORDER BY TRUNC(H.STUDY_ADDED_DATE, 'D') DESC