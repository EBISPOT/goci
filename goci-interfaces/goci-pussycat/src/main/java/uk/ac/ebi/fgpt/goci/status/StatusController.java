package uk.ac.ebi.fgpt.goci.status;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * A simple controller that returns the status of this web application.  It returns details including version number,
 * build number, release date and uptime
 *
 * @author Tony Burdett
 * @date 06/09/12
 */
@Controller
@RequestMapping("/status")
public class StatusController implements InitializingBean {
    private @Value("${version}") String version;
    private @Value("${build.number}") String buildNumber;
    private @Value("${release.date}") String releaseDate;

    private StatusBean statusBean;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody StatusBean getStatus() {
        return statusBean;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.statusBean = new StatusBean(version, buildNumber, releaseDate, System.currentTimeMillis());
    }
}
