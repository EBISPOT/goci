package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.SingleNucleotidePolymorphismRepository;

import java.util.Collection;
import java.util.List;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 14/01/15
 */
@Service
public class SingleNucleotidePolymorphismService {
    private SingleNucleotidePolymorphismRepository snpRepository;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    public SingleNucleotidePolymorphismService(SingleNucleotidePolymorphismRepository snpRepository) {
        this.snpRepository = snpRepository;
    }

    protected Logger getLog() {
        return log;
    }

    /**
     * A facade service around a {@link uk.ac.ebi.spot.goci.repository.SingleNucleotidePolymorphismRepository} that
     * retrieves all SNPs, and then within the same datasource transaction additionally loads other objects referenced
     * by this SNP (so Genes and Regions).
     * <p>
     * Use this when you know you will need deep information about a SNP and do not have an open session that can be
     * used to lazy load extra data.
     *
     * @return a list of SingleNucleotidePolymorphisms
     */
    @Transactional(readOnly = true)
    public List<SingleNucleotidePolymorphism> deepFindAll() {
        List<SingleNucleotidePolymorphism> allSnps = snpRepository.findAll();
        // iterate over all Snps and grab region info
        getLog().info("Obtained " + allSnps.size() + " SNPs, starting deep load...");
        allSnps.forEach(this::loadAssociatedData);
        return allSnps;
    }

    @Transactional(readOnly = true)
    public List<SingleNucleotidePolymorphism> deepFindAll(Sort sort) {
        List<SingleNucleotidePolymorphism> allSnps = snpRepository.findAll(sort);
        // iterate over all Snps and grab region info
        getLog().info("Obtained " + allSnps.size() + " SNPs, starting deep load...");
        allSnps.forEach(this::loadAssociatedData);
        return allSnps;
    }

    @Transactional(readOnly = true)
    public Page<SingleNucleotidePolymorphism> deepFindAll(Pageable pageable) {
        Page<SingleNucleotidePolymorphism> allSnps = snpRepository.findAll(pageable);
        // iterate over all Snps and grab region info
        getLog().info("Obtained " + allSnps.getSize() + " SNPs, starting deep load...");
        allSnps.forEach(this::loadAssociatedData);
        return allSnps;
    }

    @Transactional(readOnly = true)
    public SingleNucleotidePolymorphism deepFetchOne(SingleNucleotidePolymorphism snp) {
        loadAssociatedData(snp);
        return snp;
    }

    @Transactional(readOnly = true)
    public Collection<SingleNucleotidePolymorphism> deepFetchAll(Collection<SingleNucleotidePolymorphism> snps) {
        snps.forEach(this::loadAssociatedData);
        return snps;
    }

    public void loadAssociatedData(SingleNucleotidePolymorphism snp) {
        int regionCount = snp.getRegions().size();
        int geneCount = snp.getGenes().size();
        getLog().info(
                "SNP '" + snp.getRsId() + "' is mapped to " + regionCount + " regions and " + geneCount + " genes");
    }
}
