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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.ac.ebi.spot.goci.curation.constants.Endpoint;
import uk.ac.ebi.spot.goci.curation.constants.EntityType;
import uk.ac.ebi.spot.goci.curation.controller.assembler.DiseaseTraitDtoAssembler;
import uk.ac.ebi.spot.goci.model.deposition.DiseaseTraitDto;
import uk.ac.ebi.spot.goci.curation.exception.ResourceNotFoundException;
import uk.ac.ebi.spot.goci.curation.service.DiseaseTraitService;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import javax.validation.Valid;
import java.net.URI;

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

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DiseaseTraitDto> addDiseaseTraits(@Valid @RequestBody DiseaseTraitDto diseaseTrait1) {
        DiseaseTrait diseaseTrait = DiseaseTraitDtoAssembler.disassemble(diseaseTrait1);
        diseaseTrait = diseaseTraitService.createDiseaseTrait(diseaseTrait);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(diseaseTrait1.getId()).toUri();
        diseaseTrait1.setId(diseaseTrait.getId());
        return new ResponseEntity<>(diseaseTrait1, ResponseEntity.created(location).build().getHeaders(), HttpStatus.CREATED);
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

    @DeleteMapping(value ="/{traitId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String deleteDiseaseTrait(@PathVariable Long traitId){
        diseaseTraitService.deleteDiseaseTrait(traitId);
        return "Done";
    }

    @GetMapping(value = "/search", produces = MediaTypes.HAL_JSON_VALUE)
    public Object searchDiseaseTraitsByParam(PagedResourcesAssembler<DiseaseTrait> assembler,
                                             @RequestParam String query,
                                             Pageable pageable) {
        Page<DiseaseTrait> pagedDiseaseTraits = diseaseTraitService.searchByParameter(query, pageable);
        final ControllerLinkBuilder linkBuilder = ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder
                        .methodOn(DiseaseTraitController.class)
                        .getDiseaseTraits(assembler, pageable)
        );
        return assembler.toResource(pagedDiseaseTraits, diseaseTraitDtoAssembler, linkBuilder.withSelfRel());
    }
}
