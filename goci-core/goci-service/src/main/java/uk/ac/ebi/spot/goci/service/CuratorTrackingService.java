package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.CuratorTracking;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.StudyTrackingView;
import uk.ac.ebi.spot.goci.repository.CuratorTrackingRepository;


import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by cinzia on 01/12/2016.
 */
@Service
public class CuratorTrackingService {

    private CuratorTrackingRepository curatorTrackingRepository;


    @Autowired
    public CuratorTrackingService (CuratorTrackingRepository curatorTrackingRepository) {
        this.curatorTrackingRepository = curatorTrackingRepository;

    }


    protected void createCuratorTracking(StudyTrackingView entry, Study study, Date eventDate, String curator, String levelCuration) {

        CuratorTracking curatorEntry = new CuratorTracking();
        Calendar calendar;

        curatorEntry.setPubmedId(entry.getPubmedId());
        curatorEntry.setStudy(study);
        curatorEntry.setCurator(curator);
        curatorEntry.setLevelCurationDate(eventDate);
        calendar = new GregorianCalendar();
        calendar.setTime(eventDate);
        calendar.add(Calendar.DATE, 1);
        curatorEntry.setWeek(calendar.get(Calendar.WEEK_OF_YEAR));
        curatorEntry.setYear(calendar.get(Calendar.YEAR));
        curatorEntry.setLevelCuration(levelCuration);
        curatorTrackingRepository.save(curatorEntry);
    }

    public List<Object> statsByWeek(int year, int week) {
        return curatorTrackingRepository.statsByWeek(year, week);
    }

    public List<Object> statsByCuration(String curatorName) {
        return curatorTrackingRepository.statsByCuration(curatorName);
    }

    public List<String> findAllCurators() {
        return curatorTrackingRepository.findAllCurators();
    }

    public void deleteAll() { curatorTrackingRepository.deleteAll();}
}
