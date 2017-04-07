package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.NoteSubject;

import java.util.Collection;

/**
 * Created by xinhe on 06/04/2017.
 */
@RepositoryRestResource(exported = false)
public interface NoteSubjectRepository extends JpaRepository<NoteSubject, Long> {

    NoteSubject findBySubjectIgnoreCase(String subject);
}
