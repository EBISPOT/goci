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
//                              linkWithRel("ancestries").description("Link to all the ancestry entries in the GWAS Catalog"),
                              linkWithRel("efoTraits").description("Link to all the EFO traits in the GWAS Catalog"),
//                              linkWithRel("genes").description("Link to all the genes in the GWAS Catalog"),
//                              linkWithRel("regions").description("Link to all the chromsome regions in the GWAS Catalog"),
//                              linkWithRel("countries").description("Link to all the countries in the GWAS Catalog"),
//                              linkWithRel("entrezGenes").description("Link to all the Entrez gene IDs in the GWAS Catalog"),
//                              linkWithRel("ensemblGenes").description("Link to all the Ensembl gene IDs in the GWAS Catalog"),
//                              linkWithRel("mappingMetadatas").description("Link to the genomic mapping metadata in the GWAS Catalog"),
//                              linkWithRel("platforms").description("Link to all the sequencing platforms in the GWAS Catalog"),
//                              linkWithRel("genomicContexts").description("Link to all the genomic contexts in the GWAS Catalog"),
//                              linkWithRel("riskAlleles").description("Link to all the risk alleles in the GWAS Catalog"),
//                              linkWithRel("diseaseTraits").description("Link to all the disease traits in the GWAS Catalog"),
//                              linkWithRel("locations").description("Link to all the bp locations in the GWAS Catalog"),
//                              linkWithRel("loci").description("Link to all the association-risk allele locus link objects in the GWAS Catalog"),
                              linkWithRel("singleNucleotidePolymorphisms").description("Link to all the SNPs in the GWAS Catalog"),
//                              linkWithRel("ancestralGroups").description("Link to all the ancestral groups in the GWAS Catalog"),
//                              linkWithRel("genotypingTechnologies").description("Link to all the genotyping technologies in the GWAS Catalog"),
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
                              linkWithRel("findByEfoTraitsId").description("Search for a study via the annotated EFO terms using the parameter efoTraitId"),
                              linkWithRel("findByGxg").description("Search for a study via whether it is a gene-gene interaction study, using the parameter gxg"),
                              linkWithRel("findStudyDistinctByAssociationsSnpInteractionTrue").description("Search for studies that have associations that are SNP-SNP interactions "),
                              linkWithRel("findByPubmedId").description("Search for a study using the parameter pubmedId"),
                              linkWithRel("findByGxe").description("Search for a study by whether it is a gene-environment interaction study, using the parameter gxe"),
                              linkWithRel("findByFullPvalueSet").description("Search for a study by whether full summary statistics are available, using the parameter fullPvalueSet"),
                              linkWithRel("findStudyDistinctByAssociationsMultiSnpHaplotypeTrue").description("Search for studies that have associations that are multi-SNP haplotypes"),
                              linkWithRel("findByUserRequested").description("Search for a study by whether its addition to the Catalog was requested by a user, using the parameter userRequested "),
                              linkWithRel("findByAuthor").description("Search for a study by its first author using the parameter authorContainingIgnoreCase"),
                              linkWithRel("findByDiseaseTraitId").description("Search for a study via the annotated disease term using the parameter diseaseTraitId"),
                              linkWithRel("findByEfoTrait").description("Search for a study via the annotated EFO term using the parameter efoTraitsTrait"),
                              linkWithRel("findByEfoUri").description("Search for a study via the annotated EFO term using the parameter efoTraitsURI"),
                              linkWithRel("findByDiseaseTrait").description("Search for a study via the annotated disease term using the parameter diseaseTraitTrait")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    public void studiesExample() throws Exception {

        this.mockMvc.perform(get("/gwas/rest/api/studies/{study_id}", "5993").contextPath("/gwas/rest").accept(MediaType.APPLICATION_JSON))
                .andDo( this.restDocumentationResultHandler.document(
                        pathParameters(
                                parameterWithName("study_id").description("The id of the study in the GWAS Catalog")
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
                              linkWithRel("findByStudyId").description("Search for an association via a study using parameter studyId"),
                              linkWithRel("findByPubmedId").description("Search for an association via a study using parameter pubmedId"),
                              linkWithRel("findByLociStrongestRiskAllelesSnpId").description("Search for an association via its strongest risk alleles using parameter snpId"),
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




//    @Test
//    public void genomicContextsExample() throws Exception {
//        this.mockMvc.perform(get("/gwas/rest/api/genomicContexts/{genomicContext_id}", "20777953").contextPath("/gwas/rest").accept(MediaType.APPLICATION_JSON))
//                .andDo( this.restDocumentationResultHandler.document(
//                        pathParameters(
//                                parameterWithName("genomicContext_id").description("The id of the genomic context in the GWAS Catalog")),
//                        responseFields(
//                                fieldWithPath("_links").description("Links to other resources"),
//                                fieldWithPath("isIntergenic").description("Whether the SNP is intergenic"),
//                                fieldWithPath("isUpstream").description("Whether the linked gene is upstream to the SNP"),
//                                fieldWithPath("isDownstream").description("Whether the linked gene is downstream to the SNP"),
//                                fieldWithPath("distance").description("The distance between the current SNP and gene"),
//                                fieldWithPath("source").description("NCBI or Ensembl gene"),
//                                fieldWithPath("mappingMethod").description("How the mapping was obtained"),
//                                fieldWithPath("isClosestGene").description("Whether gene is the closest one to the current SNP")
//                        )
//                ))
//                .andExpect(status().isOk());
//    }

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
                              linkWithRel("findByRiskAllelesLociAssociationId").description("Search for SNPs via an association using parameter associationId"),
                              linkWithRel("findByRiskAllelesLociId").description("Search for SNPs via a locus using parameter lociId"),
                              linkWithRel("findByRiskAllelesLociAssociationStudyId").description("Search for SNPs via a study using parameter studyId"),
                              linkWithRel("findByLocationsId").description("Search for SNPs via a lociation using parameter locationId"),
                              linkWithRel("findByRsId").description("Search for SNPs using parameter rsId"),
                              linkWithRel("findByBpLocation").description("Search for SNPs via their base pair location"),
                              linkWithRel("findByRiskAllelesLociAssociationStudyDiseaseTraitId").description("Search for SNPs via a study using parameter diseaseTraitId"),
                              linkWithRel("findByChromBpLocation").description("Search for SNPs on a certain chromosome and within a given base pair location range"),
                              linkWithRel("findByPubmedId").description("Search for SNPs via a study using parameter pubmedId"),
                              linkWithRel("findByGene").description("Search for SNPs via a gene using parameter geneName")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    public void singleNucleotidePolymorphismsExample() throws Exception {
        this.mockMvc.perform(get("/gwas/rest/api/singleNucleotidePolymorphisms/{snp_id}", "15078813").contextPath("/gwas/rest").accept(MediaType.APPLICATION_JSON))
                .andDo( this.restDocumentationResultHandler.document(
                        pathParameters(
                                parameterWithName("snp_id").description("The id of the SNP in the GWAS Catalog")),
                        responseFields(
                            fieldWithPath("rsId").description("The SNP's rs Id"),
                            fieldWithPath("merged").description("Whether this SNP has been merged with another SNP in a newer genome build"),
                            fieldWithPath("functionalClass").description("The SNP's functional class"),
                            fieldWithPath("lastUpdateDate").description("The last date this SNP's mapping information was updated"),
                            fieldWithPath("locations").description("The SNP's genomic locations"),
                            fieldWithPath("genomicContexts").description("The genomic contexts for this SNP, incl upstream, downstream and mapped genes"),
//                            fieldWithPath("riskAlleles").description("A list of the risk alleles identified for this SNP"),
                            fieldWithPath("genes").description("A list of the genes that this SNP is located in or near"),
                            fieldWithPath("mergedInto").description("SNP the present SNP was merged with as part of a more recent genome build"),
//                            fieldWithPath("_embedded").description("Embedded information"),
                            fieldWithPath("_links").description("<<snp-links,Links>> to other resources")
                            ),
                        links(halLinks(),
                              linkWithRel("self").description("This SNP"),
                              linkWithRel("singleNucleotidePolymorphism").description("This SNP"),
                              linkWithRel("studies").description("Link to the <<studies-resources,studies>> this SNP is identified in"),
//                              linkWithRel("locations").description("This SNP's chromosomal location(s)"),
//                              linkWithRel("currentSnp").description("Current rsId in case of a merged SNP"),
//                              linkWithRel("genes").description("Genes this SNP is located in or near"),
//                              linkWithRel("riskAlleles").description("Risk alleles for this SNP"),
                              linkWithRel("associations").description("Associations this SNP is found in"),
                              linkWithRel("associationsBySnpSummary").description("A convenience projection of associations for this SNP with all the information not found in the SNP endpoint")
                              )
                ))
                .andExpect(status().isOk());
    }

//    @Test
//    public void ancestriesListExample () throws Exception {
//
//        this.mockMvc.perform(get("/gwas/rest/api/ancestries").contextPath("/gwas/rest").accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
//
//    }

//    @Test
//    public void ancestriesExample() throws Exception {
//        this.mockMvc.perform(get("/gwas/rest/api/ancestries/{ancestry_id}?projection={projection_name}", "14852643", "ancestry").contextPath("/gwas/rest").accept(MediaType.APPLICATION_JSON))
//                .andDo( this.restDocumentationResultHandler.document(
//                        pathParameters(
//                                parameterWithName("ancestry_id").description("The id of the ancestry entry in the GWAS Catalog"),
//                                parameterWithName("projection_name").optional().description("Optional projection for more convenient display of results")),
//                        responseFields(
//                                fieldWithPath("_links").description("<<ancestries-links,Links>> to other resources"),
//                                fieldWithPath("type").description("The stage this ancestry entry applies to"),
//                                fieldWithPath("numberOfIndividuals").description("The number of individuals in this sample"),
////                                fieldWithPath("ancestralGroup").description("The wider ancestral group(s) this sample belongs to"),
////                                fieldWithPath("countryOfOrigin").description("The countries of origin of the sample"),
////                                fieldWithPath("countryOfRecruitment").description("The countries of recruitment of the sample"),
//                                fieldWithPath("description").description("Additional sample information such as recruitment information"),
////                                fieldWithPath("previouslyReported").description("Whether this cohort was previously reported"),
////                                fieldWithPath("notes").description("Any other relevant ancestry-related information")
//                                fieldWithPath("ancestralGroups").description("Wider ancestral categories the individuals in this sample belonged to"),
//                                fieldWithPath("countryOfOrigin").description("Country of origin of the individuals in this sample"),
//                                fieldWithPath("countryOfRecruitment").description("Country of recruitment of the individuals in this sample")
//                        ),
//                        links(halLinks(),
//                              linkWithRel("self").description("This ancestry entry"),
//                              linkWithRel("ancestry").description("This ancestry entry"),
//                              linkWithRel("study").description("Link to the <<studies-resources,studies>> for this ancestry entry")
////                              linkWithRel("ancestralGroups").description("Link to the <<ancestralGroups-resources,ancestral groups>> for this ancestry entry"),
////                              linkWithRel("countryOfOrigin").description("Link to the <<countryOfOrigin-resources,countries of origin>> for this ancestry entry"),
////                              linkWithRel("countryOfRecruitment").description("Link to the <<countryOfRecruitment-resources,countries of recruitment>> for this ancestry entry")
//                        )
//
//                ))
//                .andExpect(status().isOk());
//    }


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
