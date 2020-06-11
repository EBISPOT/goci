/*

################################################################################

Make cases + controls optional

author: Jon Stewart
date:    13 May 2020
version: 2.4.0.018

################################################################################
*/
ALTER TABLE UNPUBLISHED_ANCESTRY
MODIFY (CASES NULL);

ALTER TABLE UNPUBLISHED_ANCESTRY
MODIFY (CONTROLS NULL);
