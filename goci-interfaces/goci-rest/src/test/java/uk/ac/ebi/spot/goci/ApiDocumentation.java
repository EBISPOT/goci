package uk.ac.ebi.spot.goci;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.RequestDispatcher;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.halLinks;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Dani Welter
 * @date 16/11/16
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiDocumentation {

    @Rule
    public final JUnitRestDocumentation
            restDocumentation = new JUnitRestDocumentation("src/main/asciidoc/generated-snippets");

    private RestDocumentationResultHandler restDocumentationResultHandler;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.restDocumentationResultHandler = document("{method-name}",
                                                       preprocessRequest(prettyPrint()),
                                                       preprocessResponse(prettyPrint())
        );

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation))
                .alwaysDo(this.restDocumentationResultHandler)
                .build();
    }


    @Test
    public void index() throws Exception {
        this.mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("index"));
    }

    @Test
    public void pageExample () throws Exception {

        this.restDocumentationResultHandler.document(
                responseFields(
                        fieldWithPath("_links").description("<<resources-page-links,Links>> to other resources"),
                        fieldWithPath("_embedded").description("The list of resources"),
                        fieldWithPath("page.size").description("The number of resources in this page"),
                        fieldWithPath("page.totalElements").description("The total number of resources"),
                        fieldWithPath("page.totalPages").description("The total number of pages"),
                        fieldWithPath("page.number").description("The page number")
                ),
                links(halLinks(),
                        linkWithRel("self").description("This resource list"),
                        linkWithRel("first").description("The first page in the resource list"),
                        linkWithRel("next").description("The next page in the resource list"),
                        linkWithRel("prev").description("The previous page in the resource list"),
                        linkWithRel("last").description("The last page in the resource list")
                )

        );

        this.mockMvc.perform(get("/api/studies?page=1&size=1"))
                .andDo(print())
                .andExpect(status().isOk());
    }



    @Test
    public void errorExample() throws Exception {
        this.mockMvc
                .perform(get("/error")
                                 .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 400)
                                 .requestAttr(RequestDispatcher.ERROR_REQUEST_URI,
                                              "/notes")
                                 .requestAttr(RequestDispatcher.ERROR_MESSAGE,
                                              "The tag 'http://localhost:8080/api/studies/123' does not exist"))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(jsonPath("error", is("Bad Request")))
                .andExpect(jsonPath("timestamp", is(notNullValue())))
                .andExpect(jsonPath("status", is(400)))
                .andExpect(jsonPath("path", is(notNullValue())))
                .andDo(document("error-example",
                                responseFields(
                                        fieldWithPath("error").description("The HTTP error that occurred, e.g. `Bad Request`"),
                                        fieldWithPath("message").description("A description of the cause of the error"),
                                        fieldWithPath("path").description("The path to which the request was made"),
                                        fieldWithPath("status").description("The HTTP status code, e.g. `400`"),
                                        fieldWithPath("timestamp").description("The time, in milliseconds, at which the error occurred"))));
    }


    @Test
    public void apiExample () throws Exception {

        this.restDocumentationResultHandler.document(
                responseFields(
                        fieldWithPath("_links").description("<<resources-ontologies-links,Links>> to other resources")
                ),
                links(halLinks(),
                        linkWithRel("studies").description("Link to the studies in the GWAS Catalog"),
                        linkWithRel("associations").description("Link to all the associations in the GWAS Catalog"),
                        linkWithRel("ethnicities").description("Link to all the ancestry entries in the GWAS Catalog"),
                        linkWithRel("efoTraits").description("Link to all the EFO traits in the GWAS Catalog"),
                        linkWithRel("genes").description("Link to all the genes in the GWAS Catalog")
                        )
        );
        this.mockMvc.perform(get("/api").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }


//    @Test
//    public void studiesListExample () throws Exception {
//
//        this.mockMvc.perform(get("/api/studies").accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
//
//    }


}
