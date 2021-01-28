package uk.ac.ebi.spot.goci.curation.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.data.web.SortDefault;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.ac.ebi.spot.goci.curation.constants.Endpoint;
import uk.ac.ebi.spot.goci.curation.constants.EntityType;
import uk.ac.ebi.spot.goci.curation.controller.assembler.DiseaseTraitDtoAssembler;
import uk.ac.ebi.spot.goci.curation.dto.AnalysisCacheDto;
import uk.ac.ebi.spot.goci.curation.dto.AnalysisDTO;
import uk.ac.ebi.spot.goci.curation.dto.DiseaseTraitDto;
import uk.ac.ebi.spot.goci.curation.dto.FileUploadRequest;
import uk.ac.ebi.spot.goci.curation.exception.FileValidationException;
import uk.ac.ebi.spot.goci.curation.exception.ResourceNotFoundException;
import uk.ac.ebi.spot.goci.curation.service.DiseaseTraitService;
import uk.ac.ebi.spot.goci.curation.util.FileHandler;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Endpoint.API_V1 + Endpoint.DISEASE_TRAITS)
public class DiseaseTraitController {

    private static final Logger log = LoggerFactory.getLogger(DiseaseTraitController.class);

    private DiseaseTraitService diseaseTraitService;
    private DiseaseTraitDtoAssembler diseaseTraitDtoAssembler;

    public DiseaseTraitController(DiseaseTraitService diseaseTraitService,
                                  DiseaseTraitDtoAssembler diseaseTraitDtoAssembler) {
        this.diseaseTraitService = diseaseTraitService;
        this.diseaseTraitDtoAssembler = diseaseTraitDtoAssembler;
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public Object getDiseaseTraits(PagedResourcesAssembler<DiseaseTrait> assembler,
                                   @SortDefault(sort = "trait", direction = Sort.Direction.DESC)
                                   @PageableDefault(size = 10, page = 0) Pageable pageable) {

        Page<DiseaseTrait> pagedDiseaseTraits = diseaseTraitService.getDiseaseTraits(pageable);
        final ControllerLinkBuilder linkBuilder = ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder
                        .methodOn(DiseaseTraitController.class)
                        .getDiseaseTraits(assembler, pageable)
        );
        return assembler.toResource(pagedDiseaseTraits, diseaseTraitDtoAssembler, linkBuilder.withSelfRel());
    }

    @GetMapping(value = "/{traitId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DiseaseTraitDto getOneDiseaseTrait(@PathVariable Long traitId) {
        return diseaseTraitService.getDiseaseTrait(traitId)
                .map(diseaseTrait -> DiseaseTraitDto.builder()
                        .id(diseaseTrait.getId())
                        .trait(diseaseTrait.getTrait())
                        .build()
                ).orElseThrow(() -> new ResourceNotFoundException(EntityType.DISEASE_TRAIT, traitId));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DiseaseTraitDto> addDiseaseTraits(@Valid @RequestBody DiseaseTraitDto diseaseTrait1) {
        DiseaseTrait diseaseTrait = DiseaseTraitDtoAssembler.disassemble(diseaseTrait1);
        diseaseTrait = diseaseTraitService.createDiseaseTrait(diseaseTrait);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(diseaseTrait1.getId()).toUri();
        diseaseTrait1.setId(diseaseTrait.getId());
        return new ResponseEntity<>(diseaseTrait1, ResponseEntity.created(location).build().getHeaders(), HttpStatus.CREATED);
    }

    @PutMapping("/{traitId}")
    public DiseaseTraitDto updateDiseaseTraits(@RequestBody DiseaseTraitDto diseaseTraitDto,
                                               @PathVariable Long traitId) {
        return diseaseTraitService.updateDiseaseTrait(diseaseTraitDto, traitId)
                .map(diseaseTrait -> DiseaseTraitDto.builder()
                        .id(diseaseTrait.getId())
                        .trait(diseaseTrait.getTrait())
                        .build()
                ).orElseThrow(() -> new ResourceNotFoundException(EntityType.DISEASE_TRAIT, traitId));
    }

    @DeleteMapping("/{traitId}")
    public String deleteDiseaseTrait(@PathVariable Long traitId){
        diseaseTraitService.deleteDiseaseTrait(traitId);
        return "Done";
    }

    @PostMapping("/uploads")
    public Object updloadDiseaseTraits(@Valid FileUploadRequest fileUploadRequest, BindingResult result) {
        if (result.hasErrors()) {
            throw new FileValidationException(result);
        }
        MultipartFile multipartFile = fileUploadRequest.getMultipartFile();
        List<DiseaseTrait> diseaseTraits = DiseaseTraitDtoAssembler.disassemble(multipartFile);
        diseaseTraits = diseaseTraitService.createDiseaseTraits(diseaseTraits);
        log.info("{} {} were created", diseaseTraits.size(), EntityType.DISEASE_TRAIT);
        return new ResponseEntity<>(DiseaseTraitDtoAssembler.assemble(diseaseTraits), HttpStatus.CREATED);
    }

    @PostMapping("/analysis")
    public AnalysisCacheDto similaritySearchAnalysis(@Valid FileUploadRequest fileUploadRequest, BindingResult result) {
        if (result.hasErrors()) {
            throw new FileValidationException(result);
        }
        List<AnalysisDTO> analysisDTO = FileHandler.serializeDiseaseTraitAnalysisFile(fileUploadRequest);
        log.info("{} disease traits were ingested for analysis", analysisDTO.size());
        String analysisId = UUID.randomUUID().toString();
        AnalysisCacheDto analysisCacheDto = diseaseTraitService.similaritySearch(analysisDTO, analysisId,50.0);
        log.info("Analysis done, retrievable in future with id {}", analysisId);
        return analysisCacheDto;
    }

    @GetMapping("/analysis/{analysisId}")
    @ResponseBody
    public Object similaritySearchAnalysisCsvDownload(HttpServletResponse response,
                                                      @PathVariable String analysisId) throws IOException {
        log.info("Retrieving Cached Analysis with ID  : {}", analysisId);
        List<AnalysisDTO> analysisDTO = new ArrayList<>();
        double threshold = 50.0;
        AnalysisCacheDto cache = diseaseTraitService.similaritySearch(analysisDTO, analysisId, threshold);
        analysisDTO = cache.getAnalysisResult();

        analysisDTO.sort(Comparator.comparingDouble(AnalysisDTO::getDegree).reversed());

        String result = FileHandler.serializePojoToTsv(analysisDTO);
        log.info(result);
        response.setContentType("text/csv;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=analysis.csv");
        response.getOutputStream().flush();
        return result;
    }
}
