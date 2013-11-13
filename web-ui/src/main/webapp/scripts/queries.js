

/*
 * Copyright (c) 2013 EMBL - European Bioinformatics Institute
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

var exampleQueries = [

    {
        shortname : "Query 1",
        description: "People who were born in Berlin before 1900",
        query: "PREFIX : <http://dbpedia.org/resource/>\n" +
            "PREFIX dbo: <http://dbpedia.org/ontology/>\n\n" +
            "SELECT ?name ?birth ?death ?person WHERE {\n" +
            "?person dbo:birthPlace :Berlin .\n" +
            "?person dbo:birthDate ?birth .\n" +
            "?person foaf:name ?name .\n" +
            "?person dbo:deathDate ?death .\n" +
            "FILTER (?birth < \"1900-01-01\"^^xsd:date) . \n" +
            "}   \n" +
            "ORDER BY ?name"
    }

]

