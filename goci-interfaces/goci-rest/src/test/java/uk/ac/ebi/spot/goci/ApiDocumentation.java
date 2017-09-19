package uk.ac.ebi.spot.goci;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
@Ignore
public class ApiDocumentation {

    @Rule
    public final JUnitRestDocumentation
            restDocumentation = new JUnitRestDocumentation("src/main/asciidoc/generated-snippets");

    private RestDocumentationResultHandler restDocumentationResultHandler;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    protected final ResponseFieldsSnippet pagingFields = responseFields(
            fieldWithPath("first").optional().description("Whether this is the first page of results"),
            fieldWithPath("last").optional().description("Whether this is the last page of results"),
            fieldWithPath("totalPages").optional().description("Total number of pages available for this result"),
            fieldWithPath("totalElements").optional().description("Total number of elements for this result"),
            fieldWithPath("size").optional().description("Maximum page size"),
            fieldWithPath("number").optional().description("Current page number"),
            fieldWithPath("numberOfElements").optional().description("Number of elements on this page"),
            fieldWithPath("sort").optional().description("Sort order of the page"));

    @Before
    public void setUp() {
        this.restDocumentationResultHandler = document("{method-name}",
                                                       preprocessRequest(prettyPrint()),
                                                       preprocessResponse(prettyPrint())
        );

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation).uris()
                               .withScheme("https")
                               .withHost("www.ebi.ac.uk")
                               .withPort(443))

                .alwaysDo(this.restDocumentationResultHandler)
                .build();
    }


//    @Test
//    public void index() throws Exception {
//        this.mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(document("docs/helindex"));
//    }

    @Test
    public void pageExample () throws Exception {

        this.mockMvc.perform(get("/gwas/rest/api/studies?page=1&size=1").contextPath("/gwas/rest"))
                .andDo(this.restDocumentationResultHandler.document(
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
                              linkWithRel("last").description("The last page in the resource list"),
                              linkWithRel("profile").description("Study resource profile"),
                              linkWithRel("search").description("Available search methods")
                        )

                ))
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
                                              "The tag 'https://www.ebi.ac.uk/gwas/rest/api/studies/123' does not exist"))
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

        this.mockMvc.perform(get("/gwas/rest/api").contextPath("/gwas/rest").accept(MediaType.APPLICATION_JSON))
                .andDo(this.restDocumentationResultHandler.document(
                        responseFields(
                                fieldWithPath("_links").description("<<resources-ontologies-links,Links>> to other resources")
                        ),
                        links(halLinks(),
                              linkWithRel("studies").description("Link to the studies in the GWAS Catalog"),
                              linkWithRel("associations").description("Link to all the associations in the GWAS Catalog"),
                              linkWithRel("efoTraits").description("Link to all the EFO traits in the GWAS Catalog"),
                              linkWithRel("singleNucleotidePolymorphisms").description("Link to all the SNPs in the GWAS Catalog"),
                              linkWithRel("profile").description("Link to the API profile")
                              )))
                .andExpect(status().isOk());
    }


    @Test
    public void studiesListExample () throws Exception {

        this.mockMvc.perform(get("/gwas/rest/api/studies").contextPath("/gwas/rest").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void studiesSearchExample () throws Exception {

        this.mockMvc.perform(get("/gwas/rest/api/studies/search").contextPath("/gwas/rest").accept(MediaType.APPLICATION_JSON))
                .andDo(this.restDocumentationResultHandler.document(
                        links(halLinks(),
                              linkWithRel("self").description("This study"),
                              linkWithRel("findByAssociationsId").description("Search for a study via an association using parameter associationId"),
                              linkWithRel("findByGenotypingTechnologiesGenotypingTechnology").description("Search for a study via the genotyping technology used using parameter genotypingTechnology"),
//                              linkWithRel("findByAccessionId").description("Search for a study using the parameter accessionId"),
                              linkWithRel("findByGxg").description("Search for a study via whether it is a gene-gene interaction study, using the parameter gxg"),
                              linkWithRel("findStudyDistinctByAssociationsSnpInteractionTrue").description("Search for studies that have associations that are SNP-SNP interactions "),
                              linkWithRel("findByPubmedId").description("Search for a study using the parameter pubmedId"),
                              linkWithRel("findByGxe").description("Search for a study by whether it is a gene-environment interaction study, using the parameter gxe"),
                              linkWithRel("findByFullPvalueSet").description("Search for a study by whether full summary statistics are available, using the parameter fullPvalueSet"),
                              linkWithRel("findStudyDistinctByAssociationsMultiSnpHaplotypeTrue").description("Search for studies that have associations that are multi-SNP haplotypes"),
                              linkWithRel("findByUserRequested").description("Search for a study by whether its addition to the Catalog was requested by a user, using the parameter userRequested "),
                              linkWithRel("findByAuthor").description("Search for a study by its first author using the parameter authorContainingIgnoreCase"),
//                              linkWithRel("findByDiseaseTraitId").description("Search for a study via the annotated disease term using the parameter diseaseTraitId"),
                              linkWithRel("findByEfoTrait").description("Search for a study via the annotated EFO term using the parameter efTrait"),
                              linkWithRel("findByEfoUri").description("Search for a study via the annotated EFO term using the parameter uri"),
                              linkWithRel("findByDiseaseTrait").description("Search for a study via the annotated disease term using the parameter diseaseTrait")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    public void studiesExample() throws Exception {

        this.mockMvc.perform(get("/gwas/rest/api/studies/{study_accession_id}", "GCST000854").contextPath("/gwas/rest").accept(MediaType.APPLICATION_JSON))
                .andDo( this.restDocumentationResultHandler.document(
                        pathParameters(
                                parameterWithName("study_accession_id").description("The accession id of the study in the GWAS Catalog")
//                                parameterWithName("projection_name").optional().description("Optional projection for more convenient display of results")
                                ),
                        responseFields(
                                fieldWithPath("_links").description("<<studies-links,Links>> to other resources"),
                                fieldWithPath("pubmedId").description("The pubmed ID for the study"),
                                fieldWithPath("accessionId").description("The study's GWAS Catalog accession ID"),
                                fieldWithPath("fullPvalueSet").description("Whether full summary statistics are available for this study"),
                                fieldWithPath("author").description("The study's first author"),
                                fieldWithPath("publicationDate").description("The study's publication date"),
                                fieldWithPath("publication").description("The study's journal"),
                                fieldWithPath("title").description("The study title"),
                                fieldWithPath("initialSampleSize").description("Initial sample description"),
                                fieldWithPath("replicationSampleSize").description("Replication sample description"),
                                fieldWithPath("gxe").description("Whether the study investigates a gene-environment interaction"),
                                fieldWithPath("gxg").description("Whether the study investigates a gene-gene interaction"),
//                                fieldWithPath("genomewideArray").description("Whether a genome-wide array was used"),
//                                fieldWithPath("targetedArray").description("Whether a targted array was used"),
                                fieldWithPath("snpCount").description("Number of SNPs passing QC"),
                                fieldWithPath("qualifier").description("Qualifier of number of SNPs passing QC (eg >)"),
                                fieldWithPath("imputed").description("Whether SNPs were imputed"),
                                fieldWithPath("pooled").description("Whether samples were pooled"),
                                fieldWithPath("studyDesignComment").description("Any other relevant study design information"),
                                fieldWithPath("userRequested").description("Whether the addition of this study to the Catalog was requested by a user"),
                                fieldWithPath("platforms").description("Genotyping platform(s) used in this study"),
                                fieldWithPath("genotypingTechnologies").description("Genotyping technology used in this study"),
                                fieldWithPath("diseaseTrait").description("Free text description of the trait investigated in this study"),
//                                fieldWithPath("efoTraits").description("EFO traits annotated to this study"),
                                fieldWithPath("ancestries").description("Ancestry entries for this study")
                                ),
                        links(halLinks(),
                              linkWithRel("self").description("This study"),
                              linkWithRel("study").description("This study"),
//                              linkWithRel("ancestries").description("<<overview-pagination,Paginated>> list of <<ancestries-resources,ancestries>> in this study"),
//                              linkWithRel("diseaseTrait").description("<<overview-pagination,Paginated>> list of <<diseaseTrait-resources,disease traits>> in this study"),
                              linkWithRel("efoTraits").description("<<overview-pagination,Paginated>> list of <<efoTraits-resources,EFO traits>> in this study"),
//                              linkWithRel("platforms").description("<<overview-pagination,Paginated>> list of <<platforms-resources,platforms>> in this study"),
                              linkWithRel("associations").description("<<overview-pagination,Paginated>> list of <<associations-resources,associations>> in this study"),
                              linkWithRel("snps").description("<<overview-pagination,Paginated>> list of <<snps-resources,SNPs>> in this study"),
                              linkWithRel("associationsByStudySummary").description("Convenience representation of associations with all trait and SNP information not present in the study")
//                              linkWithRel("genotypingTechnologies").description("<<overview-pagination,Paginated>> list of <<genotypingTechnologies-resources,genotyping technologies>> in this study")
                        )

                ))
                .andExpect(status().isOk());
    }

    @Test
    public void associationsListExample () throws Exception {

        this.mockMvc.perform(get("/gwas/rest/api/associations").contextPath("/gwas/rest").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void associationsSearchExample () throws Exception {

        this.mockMvc.perform(get("/gwas/rest/api/associations/search").contextPath("/gwas/rest").accept(MediaType.APPLICATION_JSON))
                .andDo(this.restDocumentationResultHandler.document(
                        links(halLinks(),
                              linkWithRel("self").description("This association"),
                              linkWithRel("findByStudyAccessionId").description("Search for an association via a study using parameter accessionId"),
                              linkWithRel("findByPubmedId").description("Search for an association via a study using parameter pubmedId"),
                              linkWithRel("findByEfoTrait").description("Search for an association via its annotated EFO traits using parameter efoTrait"),
                              linkWithRel("findByRsId").description("Search for an association via a SNP using parameter rsId")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    public void associationsExample() throws Exception {
        this.mockMvc.perform(get("/gwas/rest/api/associations/{association_id}", "16510553").contextPath("/gwas/rest").accept(MediaType.APPLICATION_JSON))
                .andDo( this.restDocumentationResultHandler.document(
                        pathParameters(
                                parameterWithName("association_id").description("The id of the association in the GWAS Catalog")),

                        responseFields(
                                fieldWithPath("_links").description("<<associations-links,Links>> to other resources"),
                                fieldWithPath("riskFrequency").description("Reported risk/effect allele frequency associated with strongest SNP in controls"),
                                fieldWithPath("pvalue").description("Reported p-value"),
                                fieldWithPath("pvalueMantissa").description("Mantissa of reported p-value"),
                                fieldWithPath("pvalueExponent").description("Exponent of reported p-value"),
                                fieldWithPath("pvalueDescription").description("Information describing context of p-value"),
                                fieldWithPath("multiSnpHaplotype").description("Whether the association is for a multi-SNP haplotype"),
                                fieldWithPath("snpInteraction").description("Whether the association is for a SNP-SNP interaction"),
//                                fieldWithPath("snpApproved").description("The study title"),
                                fieldWithPath("snpType").description("Whether a SNP has previously been reported"),
                                fieldWithPath("standardError").description("Standard error of the effect size"),
                                fieldWithPath("range").description("95% confidence interval"),
                                fieldWithPath("orPerCopyNum").description("Odds ratio"),
//                                fieldWithPath("orPerCopyRecip").description("Whether a targted array was used"),
//                                fieldWithPath("orPerCopyRecipRange").description("Number of SNPs passing QC"),
                                fieldWithPath("betaNum").description("Beta coefficient"),
                                fieldWithPath("betaUnit").description("Beta coefficient unit"),
                                fieldWithPath("betaDirection").description("Beta coefficient direction"),
                                fieldWithPath("description").description("Additional beta coefficient comment"),

                                fieldWithPath("lastMappingDate").description("Last time this association was mapped to Ensembl"),
                                fieldWithPath("lastUpdateDate").description("Last time this association was updated"),
                                fieldWithPath("loci").description("A convenience concept linking associations to one or more risk alleles"),
//                                fieldWithPath("_embedded").description("Embedded information including the study this association belongs to"),
                                fieldWithPath("genes").description("Genes that the SNPs for this association are annotated to")
                        ),
                        links(halLinks(),
                              linkWithRel("self").description("This association"),
                              linkWithRel("association").description("This association"),
//                              linkWithRel("loci").description("<<overview-pagination,Paginated>> list of <<loci-resources,locis>> in this association"),
                              linkWithRel("snps").description("<<overview-pagination,Paginated>> list of <<snps-resources,SNPs>> in this association"),
//                              linkWithRel("genes").description("<<overview-pagination,Paginated>> list of <<genes-resources,genes>> in this association"),
                              linkWithRel("efoTraits").description("<<overview-pagination,Paginated>> list of <<efoTraits-resources,EFO traits>> in this association"),
                              linkWithRel("study").description("Link to the <<studies-resources,study>> for this association")

                        )

                ))
                .andExpect(status().isOk());
    }





    @Test
    public void singleNucleotidePolymorphismsListExample () throws Exception {

        this.mockMvc.perform(get("/gwas/rest/api/singleNucleotidePolymorphisms").contextPath("/gwas/rest").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void singleNucleotidePolymorphismsSearchExample () throws Exception {

        this.mockMvc.perform(get("/gwas/rest/api/singleNucleotidePolymorphisms/search").contextPath("/gwas/rest").accept(MediaType.APPLICATION_JSON))
                .andDo(this.restDocumentationResultHandler.document(
                        links(halLinks(),
                              linkWithRel("self").description("This association"),
                              linkWithRel("findByAssociationId").description("Search for SNPs via an association using parameter associationId"),
                              linkWithRel("findByRsId").description("Search for SNPs using parameter rsId"),
                              linkWithRel("findByBpLocation").description("Search for SNPs via their base pair location"),
                              linkWithRel("findByDiseaseTrait").description("Search for SNPs via a study using parameter diseaseTrait"),
                              linkWithRel("findByEfoTrait").description("Search for SNPs via a study using parameter efoTrait"),
                              linkWithRel("findByChromBpLocationRange").description("Search for SNPs on a certain chromosome and within a given base pair location range"),
                              linkWithRel("findByPubmedId").description("Search for SNPs via a study using parameter pubmedId"),
                              linkWithRel("findByGene").description("Search for SNPs via a gene using parameter geneName")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    public void singleNucleotidePolymorphismsExample() throws Exception {
        this.mockMvc.perform(get("/gwas/rest/api/singleNucleotidePolymorphisms/{rs_id}", "rs7329174").contextPath("/gwas/rest").accept(MediaType.APPLICATION_JSON))
                .andDo( this.restDocumentationResultHandler.document(
                        pathParameters(
                                parameterWithName("rs_id").description("The rs Id of the SNP")),
                        responseFields(
                            fieldWithPath("rsId").description("The SNP's rs Id"),
                            fieldWithPath("merged").description("Whether this SNP has been merged with another SNP in a newer genome build"),
                            fieldWithPath("functionalClass").description("The SNP's functional class"),
                            fieldWithPath("lastUpdateDate").description("The last date this SNP's mapping information was updated"),
                            fieldWithPath("locations").description("The SNP's genomic locations"),
                            fieldWithPath("genomicContexts").description("The genomic contexts for this SNP, incl upstream, downstream and mapped genes"),
                            fieldWithPath("genes").description("A list of the genes that this SNP is located in or near"),
                            fieldWithPath("mergedInto").description("SNP the present SNP was merged with as part of a more recent genome build"),
                            fieldWithPath("_links").description("<<snp-links,Links>> to other resources")
                            ),
                        links(halLinks(),
                              linkWithRel("self").description("This SNP"),
                              linkWithRel("singleNucleotidePolymorphism").description("This SNP"),
                              linkWithRel("studies").description("Link to the <<studies-resources,studies>> this SNP is identified in"),
                              linkWithRel("associations").description("Associations this SNP is found in"),
                              linkWithRel("associationsBySnpSummary").description("A convenience projection of associations for this SNP with all the information not found in the SNP endpoint")
                              )
                ))
                .andExpect(status().isOk());
    }

    @Test
    public void efoTraitsListExample () throws Exception {

        this.mockMvc.perform(get("/gwas/rest/api/efoTraits").contextPath("/gwas/rest").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void efoTraitsSearchExample () throws Exception {

        this.mockMvc.perform(get("/gwas/rest/api/efoTraits/search").contextPath("/gwas/rest").accept(MediaType.APPLICATION_JSON))
                .andDo(this.restDocumentationResultHandler.document(
                        links(halLinks(),
                              linkWithRel("self").description("This endpoint"),
                              linkWithRel("findByAssociationsId").description("Search for EFO traits via an association using parameter associationId"),
                              linkWithRel("findByEfoUri").description("Search for EFO traits using parameter URI"),
                              linkWithRel("findByEfoTrait").description("Search for EFO traits using parameter trait"),
                              linkWithRel("findByPubmedId").description("Search for EFO traits via a study using parameter pubmedId"))
                ))
                .andExpect(status().isOk());
    }

    @Test
    public void efoTraitsExample() throws Exception {
        this.mockMvc.perform(get("/gwas/rest/api/efoTraits/{short_form}", "EFO_0001060").contextPath("/gwas/rest").accept(MediaType.APPLICATION_JSON))
                .andDo( this.restDocumentationResultHandler.document(
                        pathParameters(
                                parameterWithName("short_form").description("The URI short form of this EFO trait")),
                        responseFields(
                                fieldWithPath("trait").description("The trait name or label"),
                                fieldWithPath("uri").description("The trait URI or unique identifier"),
                                fieldWithPath("shortForm").description("The URI shortform"),
                                 fieldWithPath("_links").description("<<trait-links,Links>> to other resources")
                        ),
                        links(halLinks(),
                              linkWithRel("self").description("This EFO trait"),
                              linkWithRel("efoTrait").description("This EFO trait"),
                              linkWithRel("studies").description("Link to the <<studies-resources,studies>> this SNP is identified in"),
                              linkWithRel("associations").description("Associations this SNP is found in"),
                              linkWithRel("associationsByTraitSummary").description("A convenience projection of associations for this trait with all the information not found in the trait endpoint")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    public void snpLocationExample() throws Exception {
        this.mockMvc.perform(get("/gwas/rest/api/snpLocation/{range}", "10:95000000-96000000").contextPath("/gwas/rest").accept(MediaType.APPLICATION_JSON))
                .andDo( this.restDocumentationResultHandler.document(
                        pathParameters(
                                parameterWithName("range").description("The range of interest, in format chr:bpLocationStart-bpLocationEnd")),
                        responseFields(
                                fieldWithPath("_embedded").description("The main content. See <<resources-single-nucleotide-polymorphism,SNP resource specification>> for details"),
                                fieldWithPath("_links").description("<<snp-links,Links>> to other resources"),
                                fieldWithPath("page.size").description("The number of resources in this page"),
                                fieldWithPath("page.totalElements").description("The total number of resources"),
                                fieldWithPath("page.totalPages").description("The total number of pages"),
                                fieldWithPath("page.number").description("The page number")
                        ),
                        links(halLinks(),
                              linkWithRel("self").description("This SNP")

                        )
                ))
                .andExpect(status().isOk());
    }


    @Test
    public void parentMappingExample() throws Exception {
        this.mockMvc.perform(get("/gwas/rest/api/parentMapping/{ontologyTermId}", "EFO_0000305").contextPath("/gwas/rest").accept(MediaType.APPLICATION_JSON))
                .andDo( this.restDocumentationResultHandler.document(
                        pathParameters(
                                parameterWithName("ontologyTermId").description("The ontology term ID (in shortform or full URI)")),
                        responseFields(
                                fieldWithPath("uri").description("The full URI of the requested term"),
                                fieldWithPath("trait").description("The label of the requested term"),
                                fieldWithPath("parentUri").description("The URI of the mapped parent term"),
                                fieldWithPath("parent").description("The label of the mapped parent term"),
                                fieldWithPath("colour").description("The hex code of the colour associated with the mapped parent"),
                                fieldWithPath("colourLabel").description("The convenience label for the colour"),
                                fieldWithPath("message").description("Error message if the trait could not be mapped")
                        )
                ))
                .andExpect(status().isOk());
    }


}
