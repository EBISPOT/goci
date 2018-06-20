# Activate Python venv for the script - uncomment to run script on commandline
activate_this_file = "/path/to/bin/activate_this.py"
execfile(activate_this_file, dict(__file__ = activate_this_file))

import cx_Oracle
import argparse
import sys
import csv
from tqdm import tqdm

sys.path.insert(0, '/path/to/gwas_data_sources')
import gwas_data_sources


def get_all_studies_missing_event_data(event_type):
    '''
    Get all Study Ids that are Published (Housekeeping.Is_published=1)
    but do not have any data in the StudyEvent and Event tables.
    '''
    try:
        ip, port, sid, username, password = gwas_data_sources.get_db_properties('DEV3')
        dsn_tns = cx_Oracle.makedsn(ip, port, sid)
        connection = cx_Oracle.connect(username, password, dsn_tns)

        cursor = connection.cursor()
        
        cursor.prepare("""
            SELECT P.ID, P.PUBMED_ID, S.ID AS STUDY_ID 
            FROM PUBLICATION P, STUDY S, HOUSEKEEPING H 
            WHERE P.ID=S.PUBLICATION_ID 
                and S.HOUSEKEEPING_ID=H.ID 
                and H.IS_PUBLISHED=1
            MINUS
            SELECT P.ID, P.PUBMED_ID, S.ID AS STUDY_ID
            FROM PUBLICATION P, STUDY S, HOUSEKEEPING H, STUDY_EVENT SE, EVENT E
            WHERE S.PUBLICATION_ID=P.ID
                and S.HOUSEKEEPING_ID=H.ID
                and S.ID=SE.STUDY_ID and SE.EVENT_ID=E.ID 
                and E.EVENT_TYPE = :event_type
                and H.IS_PUBLISHED=1
        """)
        
        r = cursor.execute(None, {'event_type': event_type})

        studies = cursor.fetchall()
        
        cursor.close()
        connection.close()

    except cx_Oracle.DatabaseError, exception:
        print exception


    # Prepare data to generate Insert statements
    study_missing_added_date = []
    study_missing_published_date = []

    for result in tqdm(studies):
        # print result
        publication_id, pubmed_id, study_id = [str(col) for col in result]

        housekeeping_results = _check_housekeeping(study_id)

        # write to file to review data 
        for item in housekeeping_results:
            # Generate Insert statements for CREATED
            if event_type == 'STUDY_CREATION':
                if item[4] is not None:
                    # Example Insert Statement:
                    # INSERT INTO EVENT VALUES (NULL, TO_DATE('1999-01-01 10:01:01', 'yyyy-mm-dd hh24:mi:ss'), 'STUDY_CREATION', 14978333, null);
                    # Note: User_ID=14978333 is the ID for the automatic_mapping_process
                    insert_created = "INSERT INTO EVENT VALUES (NULL, TO_DATE("+ \
                        "'"+str(item[4])+"'" +", 'yyyy-mm-dd hh24:mi:ss'), "+ \
                        "'"+event_type+"'" +", 14978333, null)"

                    # Execute insert statements
                    if args.mode == 'production':
                        _execute_statements(study_id, insert_created)

                else:
                    study_missing_added_date.append(item[2])
                    # If STUDY_ADDED_DATE is not available, then use LAST_UPDATE_DATE
                    insert_created = "INSERT INTO EVENT VALUES (NULL, TO_DATE("+ \
                        "'"+str(item[6])+"'" +", 'yyyy-mm-dd hh24:mi:ss'), "+ \
                        "'"+event_type+"'" +", 14978333, null)"

                    # Execute insert statements
                    if args.mode == 'production':
                        _execute_statements(study_id, insert_created)

            
            # Generate Insert statements for PUBLISHED
            if event_type == 'STUDY_STATUS_CHANGE_PUBLISH_STUDY':
                if item[5] is not None:
                    # Note: User_ID=14978333 is the ID for the automatic_mapping_process
                    insert_published = "INSERT INTO EVENT VALUES (NULL, TO_DATE("+ \
                        "'"+str(item[5])+"'" +", 'yyyy-mm-dd hh24:mi:ss'), "+ \
                        "'"+event_type+"'" +", 14978333, null)"

                    # Execute insert statements
                    if args.mode == 'production':
                        _execute_statements(study_id, insert_published)

                else:
                    study_missing_published_date.append(item[2])
                    # For testing, no values expected based on query to get data on Line 27
                    print "** Missing: ", item[2]


    if len(study_missing_added_date) != 0:
        print "Studies missing Added Date: ", len(study_missing_added_date), \
        "\n", study_missing_added_date

    if len(study_missing_published_date) != 0:
        print "Studies missing Published Date: ", len(study_missing_published_date), \
        "\n", study_missing_published_date


def _check_housekeeping(id):
    '''
    For each study_id missing "Event" data, 
    check it's Housekeeping information. 
    '''
    try:
        ip, port, sid, username, password = gwas_data_sources.get_db_properties('DEV3')
        dsn_tns = cx_Oracle.makedsn(ip, port, sid)
        connection = cx_Oracle.connect(username, password, dsn_tns)

        cursor = connection.cursor()
        
        cursor.prepare("""
            SELECT P.ID, P.PUBMED_ID, S.ID, 
            TO_CHAR(H.LAST_UPDATE_DATE, 'yyyy-mm-dd hh24:mi:ss'),
            TO_CHAR(H.STUDY_ADDED_DATE, 'yyyy-mm-dd hh24:mi:ss'),
            TO_CHAR(H.CATALOG_PUBLISH_DATE, 'yyyy-mm-dd hh24:mi:ss'),
            P.PUBLICATION_DATE
            FROM PUBLICATION P, STUDY S, HOUSEKEEPING H
            WHERE S.PUBLICATION_ID=P.ID
            and S.HOUSEKEEPING_ID=H.ID
            and S.ID = :id
        """)
        
        r = cursor.execute(None, {'id': id})

        housekeeping_details = cursor.fetchall()
        
        cursor.close()
        connection.close()

    except cx_Oracle.DatabaseError, exception:
        print exception


    return housekeeping_details


def _execute_statements(study_id, sql_statement):
    '''
    Insert a list of Insert statements into the EVENT and STUDY_EVENT tables.
    '''

    # Prepare output file to Append STUDY_EVENT Insert statements to existing file
    output_file = open('missing_event_data.txt', 'a')
    csvout = csv.writer(output_file)

    try:
        ip, port, sid, username, password = gwas_data_sources.get_db_properties('DEV3')
        dsn_tns = cx_Oracle.makedsn(ip, port, sid)
        connection = cx_Oracle.connect(username, password, dsn_tns)

        cursor = connection.cursor()
        
        # https://stackoverflow.com/questions/35327135/retrieving-identity-of-most-recent-insert-in-oracle-db-12c
        new_id = cursor.var(cx_Oracle.NUMBER)
        sql_event = sql_statement + " returning id into :new_id"

        # Write data to file to review 
        csvout.writerow(["StudyID: "+study_id])
        csvout.writerow([sql_event])

        # Get Event_ID of last row inserted
        cursor.execute(sql_event, {'new_id': new_id})
        event_id = int(new_id.getvalue())


        # Execute STUDY_EVENT Insert statement
        sql_study_event = "INSERT INTO STUDY_EVENT VALUES (" + str(study_id) + ", "+ str(event_id) +")"

        # Write data to file to review
        csvout.writerow([sql_study_event+"\n"])

        cursor.execute(sql_study_event)
        
        # commit or rollback changes
        if args.mode == 'production':
            cursor.execute('COMMIT')
        else:
            cursor.execute('ROLLBACK')

        cursor.close()
        connection.close()

    except cx_Oracle.DatabaseError, exception:
        print exception


def get_studies_missing_first_publication_event():
    '''
    For studies that have both a 'STUDY_STATUS_CHANGE_PUBLISH_STUDY' Event 
    and a Housekeeping.Catalog_Publish_Date, check if the 
    Housekeeping.Catalog_Publish_Date is earlier than the 'STUDY_STATUS_CHANGE_PUBLISH_STUDY' Event
    and if so create a new Event for this "first" Publication event.
    '''
    try:
        ip, port, sid, username, password = gwas_data_sources.get_db_properties('DEV3')
        dsn_tns = cx_Oracle.makedsn(ip, port, sid)
        connection = cx_Oracle.connect(username, password, dsn_tns)

        cursor = connection.cursor()

        # Get all StudyIds from Housekeeping where Status is Published
        sql_housekeeping_published_status = """
            SELECT P.ID, P.PUBMED_ID, S.ID, H.CATALOG_PUBLISH_DATE 
            FROM PUBLICATION P, STUDY S, HOUSEKEEPING H, CURATION_STATUS CS 
            WHERE S.PUBLICATION_ID=P.ID and S.HOUSEKEEPING_ID=H.ID 
            and H.CURATION_STATUS_ID=CS.ID and H.CURATION_STATUS_ID=6
            and H.CATALOG_PUBLISH_DATE IS NOT NULL
        """

        cursor.execute(sql_housekeeping_published_status)

        housekeeping_published_studies = cursor.fetchall()

        # For Testing
        missing_initial_publication_event = []

        # Review Catalog_Publish_Date for each StudyId
        for hp_study in housekeeping_published_studies:
            hp_study_id = str(hp_study[2])
            hp_publish_date = hp_study[3]

            # Query Study-StudyEvent-Event with this study_id
            cursor.prepare("""
                SELECT P.ID, P.PUBMED_ID, S.ID, E.EVENT_DATE 
                FROM PUBLICATION P, STUDY S, STUDY_EVENT SE, EVENT E 
                WHERE S.PUBLICATION_ID=P.ID and S.ID=SE.STUDY_ID 
                and SE.EVENT_ID=E.ID 
                and E.EVENT_TYPE='STUDY_STATUS_CHANGE_PUBLISH_STUDY' 
                and S.ID = :id 
                and ROWNUM <= 1 
                ORDER BY E.EVENT_DATE DESC
                """)

            r = cursor.execute(None, {'id': hp_study_id})

            initial_publication_event = cursor.fetchone()


            event_type = 'STUDY_STATUS_CHANGE_PUBLISH_STUDY'

            if initial_publication_event is not None and hp_publish_date < initial_publication_event[3]:

                insert_initial_published = "INSERT INTO EVENT VALUES (NULL, TO_DATE("+ \
                        "'"+str(hp_publish_date)+"'" +", 'yyyy-mm-dd hh24:mi:ss'), "+ \
                        "'"+event_type+"'" + ", 14978333, null)"

                _execute_statements(hp_study_id, insert_initial_published)
            elif initial_publication_event is None:
                missing_initial_publication_event.append(hp_study_id)
            else:
                pass

        # print "Studies missing InitialPubEvent Date: ", len(missing_initial_publication_event)

        cursor.close()
        connection.close()

    except cx_Oracle.DatabaseError, exception:
        print exception


if __name__ == '__main__':
    '''
    Find Publications/Studies that are marked as Published 
    but do not have any Created or Published Event data.
    '''

    # Commandline arguments
    parser = argparse.ArgumentParser()
    parser.add_argument('--mode', default='debug', choices=['debug', 'production'], 
                        help='Run as (default: debug).')
    args = parser.parse_args()


    # Get all studies marked as Published in the 
    # Housekeeping table, but do not have a 'CREATED' StudyEvent
    CREATED = 'STUDY_CREATION'
    get_all_studies_missing_event_data(CREATED)

    
    # Get all studies marked as Published in the 
    # Housekeeping table, but do not have a 'STUDY_STATUS_CHANGE_PUBLISH_STUDY' StudyEvent
    PUBLISHED = 'STUDY_STATUS_CHANGE_PUBLISH_STUDY'
    get_all_studies_missing_event_data(PUBLISHED)


    # Check if the Housekeeping.Catalog_Publish_Date is earlier 
    # than the 'STUDY_STATUS_CHANGE_PUBLISH_STUDY' Event
    get_studies_missing_first_publication_event()





