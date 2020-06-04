package uk.ac.ebi.spot.goci.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import uk.ac.ebi.spot.goci.model.deposition.BodyOfWorkDto;
import uk.ac.ebi.spot.goci.model.deposition.DepositionAuthor;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSampleDto;
import uk.ac.ebi.spot.goci.model.deposition.DepositionStudyDto;

@Mapper
public interface BeanMapper {
    BeanMapper MAPPER = Mappers.getMapper(BeanMapper.class);

    UnpublishedStudy convert(DepositionStudyDto studyDto);

    @Mapping(source = "size", target = "sampleSize")
    UnpublishedAncestry convert(DepositionSampleDto sampleDto);

    @Mapping(source = "bodyOfWorkId", target = "publicationId")
    BodyOfWork convert(BodyOfWorkDto bodyOfWorkDto);

    default String depositionAuthorToAuthor(DepositionAuthor author){
        if(author != null) {
            if (author.getGroup() != null) {
                return author.getGroup();
            } else {
                return author.getFirstName() + ' ' + author.getLastName();
            }
        }
        return null;
    }
}
