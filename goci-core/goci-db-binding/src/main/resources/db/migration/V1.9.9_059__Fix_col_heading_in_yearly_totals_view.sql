/*
################################################################################
Migration script to fix TOTAL column heading in YEARLY_CURATOR_TOTALS_SUMMARY_VIEW

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    Oct 05th 2015
version: 1.9.9.059 (pre 2.0)
################################################################################
*/
--------------------------------------------------------
--  CREATE YEARLY_CURATOR_TOTALS_SUMMARY_VIEW
--------------------------------------------------------

CREATE OR REPLACE VIEW YEARLY_TOTALS_SUMMARY_VIEW (ID, YEAR, CURATOR, CURATOR_TOTAL, CURATION_STATUS)
AS SELECT ROWNUM, V.*  FROM (SELECT EXTRACT (YEAR FROM (TRUNC(TO_DATE(S.PUBLICATION_DATE), 'YEAR'))) AS YEAR,
C.LAST_NAME AS CURATOR,
COUNT(C.LAST_NAME) AS CURATOR_TOTAL,
CS.STATUS AS CURATION_STATUS
FROM STUDY S, HOUSEKEEPING H, CURATOR C, CURATION_STATUS CS
WHERE S.HOUSEKEEPING_ID = H.ID
AND H.CURATION_STATUS_ID = CS.ID
AND H.CURATOR_ID = C.ID
GROUP BY TRUNC(TO_DATE(S.PUBLICATION_DATE), 'YEAR'), CS.STATUS, C.LAST_NAME
ORDER BY TRUNC(TO_DATE(S.PUBLICATION_DATE), 'YEAR') DESC, C.LAST_NAME) V;