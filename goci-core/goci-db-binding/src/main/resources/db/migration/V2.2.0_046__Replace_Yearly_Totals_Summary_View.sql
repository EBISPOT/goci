/*################################################################################

Migration script to replace YEARLY_TOTALS_SUMMARY_VIEW with new Publication table

author:  C Malangone
date:    March 2018
version: 2.2.0.046
################################################################################
*/


CREATE OR REPLACE VIEW YEARLY_TOTALS_SUMMARY_VIEW AS
  SELECT
    ROWNUM AS ID,
    V."YEAR",
    V."CURATOR",
    V."CURATOR_TOTAL",
    V."CURATION_STATUS"
  FROM (SELECT
          EXTRACT(YEAR FROM (TRUNC(TO_DATE(P.PUBLICATION_DATE), 'YEAR'))) AS YEAR,
          C.LAST_NAME                                                     AS CURATOR,
          COUNT(C.LAST_NAME)                                              AS CURATOR_TOTAL,
          CS.STATUS                                                       AS CURATION_STATUS
        FROM STUDY S, HOUSEKEEPING H, CURATOR C, CURATION_STATUS CS, PUBLICATION P
        WHERE S.HOUSEKEEPING_ID = H.ID
              AND H.CURATION_STATUS_ID = CS.ID
              AND H.CURATOR_ID = C.ID
              AND P.ID = S.PUBLICATION_ID
        GROUP BY TRUNC(TO_DATE(P.PUBLICATION_DATE), 'YEAR'), CS.STATUS, C.LAST_NAME
        ORDER BY TRUNC(TO_DATE(P.PUBLICATION_DATE), 'YEAR') DESC, C.LAST_NAME) V;


