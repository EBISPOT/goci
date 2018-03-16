/*

################################################################################

Migration script to create the table

   Publication
   Author (Abstraction)
   Publication_Authors

Add column publication_id to the table Study

author: Cinzia Malangone
date:    Feb 2018
version: 2.2.0.040
################################################################################
*/

--------------------------------------------------------
--  CREATE TABLE, FOREIGN KEY AUTHOR
--------------------------------------------------------
  CREATE TABLE "AUTHOR" (
     "ID" NUMBER(19,0),
     "FULLNAME" VARCHAR2(4000 BYTE) NOT NULL,
     -- convert the fullname Junicode
     "FULLNAME_STANDARD" VARCHAR2(4000 BYTE) NOT NULL,
     "FIRST_NAME" VARCHAR2(200 CHAR),
     "LAST_NAME" VARCHAR2(500 CHAR),
     "INITIALS" VARCHAR2(50 CHAR),
     "AFFILIATION" VARCHAR2(700 CHAR),
     "ORCID" VARCHAR2(255 CHAR),
     "CREATED" TIMESTAMP,
     "UPDATED" TIMESTAMP,
     CONSTRAINT AUTHORS_UNIQUE UNIQUE (FULLNAME,FIRST_NAME, LAST_NAME, INITIALS, AFFILIATION, ORCID)
     );

  ALTER TABLE "AUTHOR" ADD PRIMARY KEY ("ID") ENABLE;

    CREATE SEQUENCE AUTHOR_TYPE_SEQ
     START WITH 1
     INCREMENT BY 1
     CACHE 1000;

--------------------------------------------------------
--  CREATE TABLE, FOREIGN KEY PUBLICATION
--------------------------------------------------------
  CREATE TABLE "PUBLICATION" (
     "ID" NUMBER(19,0),
     "PUBMED_ID" VARCHAR2(255 CHAR) NOT NULL,
     "PUBLICATION" VARCHAR2(255 CHAR),
     "TITLE" VARCHAR2(4000 BYTE),
     "PUBLICATION_DATE" DATE,
     "FIRST_AUTHOR_ID" NUMBER(19,0),
     "CREATED" TIMESTAMP,
     "UPDATED" TIMESTAMP
     );

  ALTER TABLE "PUBLICATION" ADD PRIMARY KEY ("ID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for First_Author
--------------------------------------------------------
  ALTER TABLE "PUBLICATION" ADD CONSTRAINT "PUBLICATION_FIRST_AUTHOR_ID_FK" FOREIGN KEY ("FIRST_AUTHOR_ID")
	  REFERENCES "AUTHOR" ("ID") ENABLE;

  CREATE SEQUENCE PUBLICATION_TYPE_SEQ
     START WITH 1
     INCREMENT BY 1
     CACHE 1000;


--------------------------------------------------------
-- Create temporary trigger on PUBLICATION
--------------------------------------------------------
CREATE OR REPLACE TRIGGER PUBLICATION_TRG
BEFORE INSERT ON PUBLICATION
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT PUBLICATION_TYPE_SEQ.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER PUBLICATION_TRG ENABLE;

--------------------------------------------------------
--  CREATE TABLE, FOREIGN KEY PUBLICATION_AUTHORS
--------------------------------------------------------
 CREATE TABLE "PUBLICATION_AUTHORS" (
      "PUBLICATION_ID" NUMBER(19,0) NOT NULL,
      "AUTHOR_ID" NUMBER(19,0) NOT NULL,
      -- ORDER is a reserved word.
      "SORT" NUMBER(5,0) DEFAULT 0,
      CONSTRAINT PUBLICATION_AUTHORS_UNIQUE UNIQUE (PUBLICATION_ID, AUTHOR_ID, SORT)
      );



--------------------------------------------------------
--  Ref Constraints for Table PUBLICATION_AUTHORS
--------------------------------------------------------
  ALTER TABLE "PUBLICATION_AUTHORS" ADD CONSTRAINT "PUBLICATION_AUTH_ID_FK" FOREIGN KEY ("PUBLICATION_ID")
	  REFERENCES "PUBLICATION" ("ID") ENABLE;
  ALTER TABLE "PUBLICATION_AUTHORS" ADD CONSTRAINT "AUTHOR_PUBLICATION_ID_FK" FOREIGN KEY ("AUTHOR_ID")
	  REFERENCES "AUTHOR" ("ID") ENABLE;


--------------------------------------------------------
--  Add publication_id to study (foreigner key)
--------------------------------------------------------

ALTER TABLE "STUDY" ADD "PUBLICATION_ID" NUMBER(19,0);

--------------------------------------------------------
--  Ref Constraints for Table Publication
--------------------------------------------------------
  ALTER TABLE "STUDY" ADD CONSTRAINT "STUDY_PUBLICATION_ID_FK" FOREIGN KEY ("PUBLICATION_ID")
	  REFERENCES "PUBLICATION" ("ID") ENABLE;

