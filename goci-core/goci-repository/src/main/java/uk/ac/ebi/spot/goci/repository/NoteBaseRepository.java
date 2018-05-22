package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Note;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.Collection;

/**
 * Created by cinzia on 28/03/2017.
 */
@NoRepositoryBean
public interface  NoteBaseRepository <T extends Note>  extends JpaRepository<T, Long> {
        public Collection<T> findByStudyId(Long studyId);
        public Collection<T> findByGenericId(Long genericId);
}
