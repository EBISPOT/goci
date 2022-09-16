package uk.ac.ebi.spot.goci.curation.util;

import org.springframework.data.domain.Pageable;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class UriBuilder {

    public static URI buildUrl(String uri, Pageable pageable){
        return buildUrl(uri, pageable, "");
    }

    public static URI buildUrl(String uri, Pageable pageable, String status){
        UriComponentsBuilder targetUrl = UriComponentsBuilder.fromUriString(uri)
                .queryParam("page", pageable.getPageNumber() - 1)
                .queryParam("size", pageable.getPageSize());

        targetUrl = (status.isEmpty()) ? targetUrl : targetUrl.queryParam("status", status);
        return targetUrl.build().encode().toUri();
    }
}
