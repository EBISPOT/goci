/*

################################################################################
Migration script to add indexes to improve mapping pipeline queries
author: Jon Stewart
date:    24 Jan 2020
version: 2.4.0.005
################################################################################
*/

CREATE INDEX HK_PUB_DATE_IDX ON HOUSEKEEPING (CATALOG_PUBLISH_DATE);
CREATE INDEX HK_UNPUB_DATE_IDX ON HOUSEKEEPING (CATALOG_UNPUBLISH_DATE);
CREATE INDEX ARG_LOC_ID_IDX ON AUTHOR_REPORTED_GENE (LOCUS_ID);
