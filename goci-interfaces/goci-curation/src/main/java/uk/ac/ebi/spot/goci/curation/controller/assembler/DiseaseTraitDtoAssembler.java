package uk.ac.ebi.spot.goci.curation.controller.assembler;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ebi.spot.goci.curation.controller.rest.DiseaseTraitController;
import uk.ac.ebi.spot.goci.curation.dto.DiseaseTraitDto;
import uk.ac.ebi.spot.goci.curation.exception.FileUploadException;
import uk.ac.ebi.spot.goci.curation.util.FileHandler;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class DiseaseTraitDtoAssembler implements ResourceAssembler<DiseaseTrait, Resource<DiseaseTraitDto>> {

    private static final Logger log = LoggerFactory.getLogger(DiseaseTraitDtoAssembler.class);

    public Resource<DiseaseTraitDto> toResource(DiseaseTrait diseaseTrait) {
        DiseaseTraitDto diseaseTraitDTO = DiseaseTraitDto.builder()
                .id(diseaseTrait.getId())
                .trait(diseaseTrait.getTrait())
                .studies(diseaseTrait.getStudies().size())
                .build();
        final ControllerLinkBuilder controllerLinkBuilder = ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(DiseaseTraitController.class).getOneDiseaseTrait(diseaseTrait.getId()));
        Resource<DiseaseTraitDto> resource = new Resource<>(diseaseTraitDTO);
        resource.add(controllerLinkBuilder.withSelfRel());
        return resource;
    }

    public static List<DiseaseTraitDto> assemble(List<DiseaseTrait> diseaseTraits) {
        log.info("Inside Assemble method");
        List<DiseaseTraitDto> diseaseTraitDTOS = new ArrayList<>();
        diseaseTraits.forEach(diseaseTrait -> {
            log.info("diseaseTrait retrieved from file-:"+diseaseTrait.getTrait());
            DiseaseTraitDto diseaseTraitDTO = DiseaseTraitDto.builder()
                    .id(diseaseTrait.getId())
                    .trait(diseaseTrait.getTrait())
                    .build();
            diseaseTraitDTOS.add(diseaseTraitDTO);
        });
        return diseaseTraitDTOS;
    }

    public static DiseaseTrait disassemble(DiseaseTraitDto diseaseTraitDTO) {
        DiseaseTrait diseaseTrait = new DiseaseTrait();
        diseaseTrait.setTrait(diseaseTraitDTO.getTrait());
        return diseaseTrait;
    }

    public static List<DiseaseTrait> disassemble(MultipartFile multipartFile)  {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = FileHandler.getSchemaFromMultiPartFile(multipartFile);
        List<DiseaseTraitDto> diseaseTraitDtos;
        try {
            InputStream inputStream = multipartFile.getInputStream();
            MappingIterator<DiseaseTraitDto> iterator = mapper.readerFor(DiseaseTraitDto.class).with(schema).readValues(inputStream);
            diseaseTraitDtos = iterator.readAll();
        }catch (IOException ex){
            throw new FileUploadException("Could not read the file");
        }

        List<DiseaseTrait> diseaseTraits = new ArrayList<>();
        diseaseTraitDtos.forEach(diseaseTraitDTO -> {
            DiseaseTrait diseaseTrait = new DiseaseTrait();
            diseaseTrait.setTrait(diseaseTraitDTO.getTrait());
            diseaseTraits.add(diseaseTrait);
        });
        return diseaseTraits;
    }
}







