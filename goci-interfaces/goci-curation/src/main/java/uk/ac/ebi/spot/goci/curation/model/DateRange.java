package uk.ac.ebi.spot.goci.curation.model;

import java.util.Date;

/**
 * Created by emma on 02/02/15.
 * @author emma
 *
 * Model object to store a date rnage that can be used to calculate monthly curator totals
 *
 */
public class DateRange {

    private Date dateFrom;
    private Date dateTo;

    public DateRange() {
    }

    public DateRange(Date dateFrom, Date dateTo) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }


    public boolean contains(Date date){
      return (date.after(getDateFrom()) && date.before(getDateTo()));
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }
}
