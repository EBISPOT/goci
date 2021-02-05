package uk.ac.ebi.spot.goci.curation.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ebi.spot.goci.curation.constants.Endpoint;
import uk.ac.ebi.spot.goci.curation.constants.EntityType;
import uk.ac.ebi.spot.goci.curation.controller.assembler.DiseaseTraitDtoAssembler;
import uk.ac.ebi.spot.goci.curation.dto.AnalysisCacheDto;
import uk.ac.ebi.spot.goci.curation.dto.AnalysisDTO;
import uk.ac.ebi.spot.goci.curation.dto.FileUploadRequest;
import uk.ac.ebi.spot.goci.curation.exception.FileValidationException;
import uk.ac.ebi.spot.goci.curation.service.DiseaseTraitService;
import uk.ac.ebi.spot.goci.curation.util.FileHandler;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Endpoint.API_V1 + Endpoint.DISEASE_TRAITS)
public class DiseaseTraitUploadController {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private DiseaseTraitService diseaseTraitService;

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
        double threshold = 90.0;
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

    @GetMapping("/templates")
    @ResponseBody
    public Object fileUploadTemplateDownload(HttpServletResponse response,
                                             @RequestParam(value = "file") String fileUploadType) throws IOException {
        response.setContentType("text/csv;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename="+fileUploadType+".tsv");
        response.getOutputStream().flush();
        return FileHandler.getTemplate(fileUploadType);
    }
}