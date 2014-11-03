package uk.ac.ebi.fgpt.goci.status;

/**
 * A simple javabean that encapsulates some status about the statistics of the service
 *
 * @author Tony Burdett
 * @date 06/09/12
 */
public class StatusBean {
    private final String version;
    private final String buildNumber;
    private final String releaseDate;
    private final long startupTime;

    public StatusBean(String version, String buildNumber, String releaseDate, long startupTime) {
        this.version = version;
        this.buildNumber = buildNumber;
        this.releaseDate = releaseDate;
        this.startupTime = startupTime;
    }

    public String getVersion() {
        return version;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public long getStartupTime() {
        return startupTime;
    }
}
