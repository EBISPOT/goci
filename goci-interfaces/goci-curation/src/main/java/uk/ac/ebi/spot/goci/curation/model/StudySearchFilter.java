package uk.ac.ebi.spot.goci.curation.model;

/**
 * Created by emma on 22/01/15.
 *
 * @author emma
 *         <p>
 *         Service class used to deal with filter options passed back from HTML form.
 */
public class StudySearchFilter {

    private Long statusSearchFilterId;

    private Long curatorSearchFilterId;

    private String monthFilter;

    private String yearFilter;

    private String pubmedId;

    private String author;

    private String studyType;

    private Long efoTraitSearchFilterId;

    private String notesQuery;

    public StudySearchFilter() {
    }

    public StudySearchFilter(Long statusSearchFilterId,
                             Long curatorSearchFilterId,
                             String monthFilter,
                             String yearFilter,
                             String pubmedId,
                             String author,
                             String studyType,
                             Long efoTraitSearchFilterId, String notesQuery) {
        this.statusSearchFilterId = statusSearchFilterId;
        this.curatorSearchFilterId = curatorSearchFilterId;
        this.monthFilter = monthFilter;
        this.yearFilter = yearFilter;
        this.pubmedId = pubmedId;
        this.author = author;
        this.studyType = studyType;
        this.efoTraitSearchFilterId = efoTraitSearchFilterId;
        this.notesQuery = notesQuery;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStudyType() {
        return studyType;
    }

    public StudySearchFilter setStudyType(String studyType) {
        this.studyType = studyType;
        return this;
    }

    public Long getEfoTraitSearchFilterId() {
        return efoTraitSearchFilterId;
    }

    public StudySearchFilter setEfoTraitSearchFilterId(Long efoTraitSearchFilterId) {
        this.efoTraitSearchFilterId = efoTraitSearchFilterId;
        return this;
    }

    public String getNotesQuery() {
        return notesQuery;
    }

    public StudySearchFilter setNotesQuery(String notesQuery) {
        this.notesQuery = notesQuery;
        return this;
    }
}
