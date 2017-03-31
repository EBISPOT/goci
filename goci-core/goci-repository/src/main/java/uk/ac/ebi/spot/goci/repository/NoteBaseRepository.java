package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.ac.ebi.spot.goci.model.Note;

/**
 * Created by cinzia on 28/03/2017.
 */
@NoRepositoryBean
public interface  NoteBaseRepository <T extends Note>  extends JpaRepository<Note, Long> {
}
