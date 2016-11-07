package uk.ac.ebi.spot.goci.curation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.spot.goci.curation.model.StudySearchFilter;
import uk.ac.ebi.spot.goci.curation.service.reports.ReportService;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.repository.*;

import java.util.Collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Cinzia 31/10/2016
 *
 * @author cinzia
 *         <p>
 *         Report controller, used to return d3 graph data.
 */

@Controller
@RequestMapping("/reports/studies_backlog")
public class StudiesBacklogReportController {

    private StudiesBacklogViewRepository studiesBacklogViewRepository;

    @Autowired
    public StudiesBacklogReportController(StudiesBacklogViewRepository studiesBacklogViewRepository) {
        this.studiesBacklogViewRepository = studiesBacklogViewRepository;

        }

        @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
        public String getViewResult(Model model) {

            return "report_study_backlog";
        }

        @RequestMapping(value = "/getJsonData", method = RequestMethod.GET, produces = "application/json")
        @ResponseBody
        public List<StudiesBacklogView> helloRest() {
           return this.studiesBacklogViewRepository.findAll();

        }

    }
