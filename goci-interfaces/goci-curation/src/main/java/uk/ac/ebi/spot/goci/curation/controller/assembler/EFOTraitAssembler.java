package uk.ac.ebi.spot.goci.curation.controller.assembler;

import uk.ac.ebi.spot.goci.model.deposition.EFOTraitDTO;
import uk.ac.ebi.spot.goci.model.EfoTrait;

public class EFOTraitAssembler {

   public static EfoTrait disassemble(EFOTraitDTO efoTraitDTO) {
       EfoTrait efoTrait = new EfoTrait();
       efoTrait.setTrait(efoTraitDTO.getTrait());
       efoTrait.setUri(efoTraitDTO.getUri());
       efoTrait.setShortForm(efoTraitDTO.getShortForm());
       efoTrait.setMongoSeqId(efoTraitDTO.getMongoSeqId());
       return efoTrait;
    }
}
