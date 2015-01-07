package uk.ac.ebi.spot.goci.curation.service;

import java.util.Collection;

/**
 * Created by emma on 07/01/15.
 *
 * @author emma
 *
 *         Service class used to deal with curator reported SNPs,
 *         which are entered as separate tags
 */
public class CuratorReportedSNP {

    private Collection<String> reportedSNPValue;

    public CuratorReportedSNP(Collection<String> reportedSNPValue) {
        this.reportedSNPValue = reportedSNPValue;
    }

    public CuratorReportedSNP() {

    }

    public Collection<String> getReportedSNPValue() {
        return reportedSNPValue;
    }

    public void setReportedSNPValue(Collection<String> reportedSNPValue) {
        this.reportedSNPValue = reportedSNPValue;
    }
}
