/*################################################################################

Migration script to replace  STUDY_TRACKING_VIEW with new Publication table

author:  C Malangone
date:    March 2018
version: 2.2.0.044
################################################################################
*/

CREATE OR REPLACE VIEW STUDY_TRACKING_VIEW AS
  SELECT DISTINCT
    S.ID AS                         STUDY_ID,
    DECODE(SE.STUDY_ID, NULL, 0, 1) HAS_EVENT,
    CS.STATUS,
    P.PUBMED_ID,
    S.HOUSEKEEPING_ID,
    P.PUBLICATION_DATE,
    HK."LAST_UPDATE_DATE",
    HK."CATALOG_PUBLISH_DATE",
    HK."STUDY_ADDED_DATE",
    HK."CURATION_STATUS_ID",
    HK."CURATOR_ID",
    HK."CATALOG_UNPUBLISH_DATE",
    HK."UNPUBLISH_REASON_ID",
    HK."IS_PUBLISHED"
  FROM STUDY S LEFT JOIN PUBLICATION P ON (S.PUBLICATION_ID =P.ID) LEFT JOIN HOUSEKEEPING HK ON (HK.ID = S.HOUSEKEEPING_ID)
    JOIN CURATION_STATUS CS ON (HK.CURATION_STATUS_ID = CS.ID)
    LEFT JOIN STUDY_EVENT SE ON (SE.STUDY_ID = S.ID)
  ORDER BY S.ID ASC;