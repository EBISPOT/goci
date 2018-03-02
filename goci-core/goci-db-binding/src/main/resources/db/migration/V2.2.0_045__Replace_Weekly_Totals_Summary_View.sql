/*################################################################################

Migration script to replace WEEKLY_TOTALS_SUMMARY_VIEW with new Publication table

author:  C Malangone
date:    March 2018
version: 2.2.0.045
################################################################################
*/

CREATE OR REPLACE VIEW WEEKLY_TOTALS_SUMMARY_VIEW AS
  SELECT
    ROWNUM AS ID,
    V."WEEK",
    V."WEEKLY_ENTRIES",
    V."WEEKLY_STUDIES"
  FROM (SELECT
          TRUNC(H.STUDY_ADDED_DATE, 'D') AS WEEK,
          COUNT(*)                       AS WEEKLY_ENTRIES,
          COUNT(DISTINCT P.PUBMED_ID)    AS WEEKLY_STUDIES
        FROM HOUSEKEEPING H, STUDY S, PUBLICATION P
        WHERE S.HOUSEKEEPING_ID = H.ID
              AND P.ID = S.PUBLICATION_ID
              AND H.STUDY_ADDED_DATE IS NOT NULL
        GROUP BY TRUNC(H.STUDY_ADDED_DATE, 'D')
        ORDER BY TRUNC(H.STUDY_ADDED_DATE, 'D') DESC) V;