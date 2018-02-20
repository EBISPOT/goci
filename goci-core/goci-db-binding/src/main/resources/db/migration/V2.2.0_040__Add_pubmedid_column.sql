/*
################################################################################
Migration script to add a new column for the count of distinct PubMedIds
into the Monthly_Totals_Summary_View

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Trish Whetzel
date:   19 February 2018
version: 2.2.0.040
################################################################################
*/

--------------------------------------------------------
-- CREATE NEW VIEW DEFINITION
--------------------------------------------------------
CREATE OR REPLACE VIEW MONTHLY_TOTALS_SUMMARY_VIEW (ID, YEAR, MONTH, PUBMED_TOTAL, CURATOR, CURATOR_TOTAL, CURATION_STATUS, MONTHLY_TOTAL)
AS SELECT ROWNUM, V.* FROM (SELECT YEAR_E AS YEAR, MONTH_E AS MONTH, PUBMED_TOTAL, CURATOR, CURATOR_TOTAL, CURATION_STATUS, MONTHLY_TOTAL FROM (
    (SELECT EXTRACT (YEAR FROM (TRUNC(TO_DATE(S.PUBLICATION_DATE), 'YEAR'))) AS YEAR_E,
            EXTRACT (MONTH FROM (TRUNC(TO_DATE(S.PUBLICATION_DATE), 'MONTH'))) AS MONTH_E,
            COUNT(DISTINCT(S.PUBMED_ID)) AS PUBMED_TOTAL,
            C.LAST_NAME AS CURATOR,
            COUNT(C.LAST_NAME) AS CURATOR_TOTAL,
            CS.STATUS AS CURATION_STATUS
     FROM STUDY S, HOUSEKEEPING H, CURATOR C, CURATION_STATUS CS
     WHERE S.HOUSEKEEPING_ID = H.ID
           AND H.CURATION_STATUS_ID = CS.ID
           AND H.CURATOR_ID = C.ID
     GROUP BY TRUNC(TO_DATE(S.PUBLICATION_DATE), 'YEAR'), TRUNC(TO_DATE(S.PUBLICATION_DATE), 'MONTH'),CS.STATUS, C.LAST_NAME
     ORDER BY TRUNC(TO_DATE(S.PUBLICATION_DATE), 'YEAR') DESC,TRUNC(TO_DATE(S.PUBLICATION_DATE), 'MONTH') DESC, C.LAST_NAME
    ) E FULL JOIN (SELECT EXTRACT (YEAR FROM (TRUNC(TO_DATE(PUBLICATION_DATE), 'YEAR'))) AS YEAR_N,
                          EXTRACT (MONTH FROM (TRUNC(TO_DATE(PUBLICATION_DATE), 'MONTH'))) AS MONTH_N,
                          COUNT(ID) AS MONTHLY_TOTAL
                   FROM STUDY
                   GROUP BY TRUNC(TO_DATE(PUBLICATION_DATE), 'MONTH'), TRUNC(TO_DATE(PUBLICATION_DATE), 'YEAR')
                   ORDER BY TRUNC(TO_DATE(PUBLICATION_DATE), 'MONTH') DESC) N on YEAR_N = YEAR_E AND MONTH_N = MONTH_E)) V;