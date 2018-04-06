/*
################################################################################
Migration script to modify Monthly_Totals_Summary_View to change columns
used in Group By clause to fix query error.

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatible with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Trish Whetzel
date:   27 March 2018
version: 2.3.0.001
################################################################################
*/

--------------------------------------------------------
-- CREATE NEW VIEW DEFINITION
--------------------------------------------------------
CREATE OR REPLACE VIEW MONTHLY_TOTALS_SUMMARY_VIEW (ID, YEAR, MONTH, PUBMED_ID, ACCESSION_ID, AUTHOR, CURATOR, CURATION_STATUS, CURATOR_TOTAL)
  AS SELECT ROWNUM, V.* FROM (SELECT YEAR_E AS YEAR, MONTH_E AS MONTH, PUBMED_ID, ACCESSION_ID, AUTHOR, CURATOR, CURATION_STATUS, CURATOR_TOTAL
                              FROM (
                                SELECT EXTRACT (YEAR FROM (TRUNC(TO_DATE(P.PUBLICATION_DATE), 'YEAR'))) AS YEAR_E,
                                       EXTRACT (MONTH FROM (TRUNC(TO_DATE(P.PUBLICATION_DATE), 'MONTH'))) AS MONTH_E,
                                       P.PUBMED_ID AS PUBMED_ID,
                                       S.ACCESSION_ID AS ACCESSION_ID,
                                       A.FULLNAME AS AUTHOR,
                                       C.LAST_NAME AS CURATOR,
                                       COUNT(C.LAST_NAME) AS CURATOR_TOTAL,
                                       CS.STATUS AS CURATION_STATUS
                                FROM STUDY S, PUBLICATION P, AUTHOR A, HOUSEKEEPING H, CURATOR C, CURATION_STATUS CS
                                WHERE S.HOUSEKEEPING_ID = H.ID
                                      AND S.PUBLICATION_ID = P.ID
                                      AND H.CURATION_STATUS_ID = CS.ID
                                      AND H.CURATOR_ID = C.ID
                                      AND P.FIRST_AUTHOR_ID=A.ID
                                GROUP BY TRUNC(TO_DATE(P.PUBLICATION_DATE), 'YEAR'), TRUNC(TO_DATE(P.PUBLICATION_DATE), 'MONTH'),
                                  P.PUBMED_ID, S.ACCESSION_ID, A.FULLNAME, CS.STATUS, C.LAST_NAME
                                ORDER BY TRUNC(TO_DATE(P.PUBLICATION_DATE), 'YEAR') DESC, TRUNC(TO_DATE(P.PUBLICATION_DATE), 'MONTH') DESC, C.LAST_NAME)) V;