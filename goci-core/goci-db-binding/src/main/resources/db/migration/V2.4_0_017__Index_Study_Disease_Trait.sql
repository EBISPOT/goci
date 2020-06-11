/*

################################################################################

Add indexes for Study-Disease Trait join table

author: Jon Stewart
date:    5 May 2020
version: 2.4.0.017

################################################################################
*/
CREATE INDEX ST_DT_IDX ON STUDY_DISEASE_TRAIT (DISEASE_TRAIT_ID);
