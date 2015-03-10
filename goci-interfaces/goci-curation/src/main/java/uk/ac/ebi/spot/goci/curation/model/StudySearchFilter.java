package uk.ac.ebi.spot.goci.curation.model;

/**
 * Created by emma on 22/01/15.
 *
 * @author emma
 *         <p/>
 *         Service class used to deal with filter options passed back from HTML form.
 */
public class StudySearchFilter {

    private Long statusSearchFilterId;

    private Long curatorSearchFilterId;

    private String monthFilter;

    private String yearFilter;

    private String pubmedId;

    public StudySearchFilter() {
    }

    public StudySearchFilter(Long statusSearchFilterId,
                             Long curatorSearchFilterId,
                             String monthFilter,
                             String yearFilter, String pubmedId) {
        this.statusSearchFilterId = statusSearchFilterId;
        this.curatorSearchFilterId = curatorSearchFilterId;
        this.monthFilter = monthFilter;
        this.yearFilter = yearFilter;
        this.pubmedId = pubmedId;
    }

    public Long getStatusSearchFilterId() {
        return statusSearchFilterId;
    }

    public void setStatusSearchFilterId(Long statusSearchFilterId) {
        this.statusSearchFilterId = statusSearchFilterId;
    }

    public Long getCuratorSearchFilterId() {
        return curatorSearchFilterId;
    }

    public void setCuratorSearchFilterId(Long curatorSearchFilterId) {
        this.curatorSearchFilterId = curatorSearchFilterId;
    }

    public String getMonthFilter() {
        return monthFilter;
    }

    public void setMonthFilter(String monthFilter) {
        this.monthFilter = monthFilter;
    }

    public String getYearFilter() {
        return yearFilter;
    }

    public void setYearFilter(String yearFilter) {
        this.yearFilter = yearFilter;
    }

    public String getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }
}
