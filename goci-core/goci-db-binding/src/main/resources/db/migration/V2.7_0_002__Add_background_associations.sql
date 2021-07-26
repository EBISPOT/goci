/*

################################################################################

Migration script to create table ASSOCIATION_BKG_EFO_TRAIT and its constraints

author: Ala Abid
date:    07 jun 2021
version: 2.7.0.002
################################################################################
*/

--------------------------------------------------------
--  CREATE TABLE, INDEX, FOREIGN KEY
--------------------------------------------------------

CREATE TABLE ASSOCIATION_BKG_EFO_TRAIT (ASSOCIATION_ID NUMBER(19,0), EFO_TRAIT_ID NUMBER(19,0));
ALTER TABLE ASSOCIATION_BKG_EFO_TRAIT MODIFY (EFO_TRAIT_ID NOT NULL);
ALTER TABLE ASSOCIATION_BKG_EFO_TRAIT MODIFY (ASSOCIATION_ID NOT NULL);
ALTER TABLE ASSOCIATION_BKG_EFO_TRAIT ADD CONSTRAINT ASSC_BKG_EFO_ASSC_ID_FK FOREIGN KEY (ASSOCIATION_ID) REFERENCES ASSOCIATION (ID);
ALTER TABLE ASSOCIATION_BKG_EFO_TRAIT ADD CONSTRAINT ASSC_BKG_EFO_TRAIT_ID_FK FOREIGN KEY (EFO_TRAIT_ID) REFERENCES EFO_TRAIT (ID);
