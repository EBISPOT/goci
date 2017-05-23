package uk.ac.ebi.spot.goci.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;

/**
 * Javadocs go here!
 *
 * @author Cinzia
 * @date 16/11/16
 */

@Service
public class CuratorService {
    private CuratorRepository curatorRepository;

    @Autowired
    public CuratorService(CuratorRepository curatorRepository) {
        this.curatorRepository = curatorRepository;
    }


    public Curator findByLastName(String lastName) {
        Curator curator = curatorRepository.findByLastName(lastName);
        return curator;
    }

    public Curator findByLastNameIgnoreCase(String lastName) {
        Curator curator = curatorRepository.findByLastNameIgnoreCase(lastName);
        return curator;
    }

    public Curator getCuratorIdByEmail(String email) {
        Curator curator = curatorRepository.findByEmail(email);
        return curator;
    }


    public Curator findOne(Long id) {
        Curator curator = curatorRepository.findOne(id);
        return curator;
    }
}
