package uk.ac.ebi.spot.goci.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.EnsemblRestcallHistory;
import uk.ac.ebi.spot.goci.model.RestResponseResult;
import uk.ac.ebi.spot.goci.repository.EnsemblRestcallHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.JsonNode;

import java.util.Collection;

/**
 * Created by Cinzia on 31/01/2017.
 *
 * @author cinzia
 *         <p>
 *         EnsemblRestCallHistoryService provides some methods to create and to retrieve from EnsemblRestCallHistory
 *         table.
 *         The goal is avoid to query Ensembl if the data are already stored.
 */
@Service
public class EnsemblRestcallHistoryService {

    private EnsemblRestcallHistoryRepository ensemblRestcallHistoryRepository;


    @Autowired
    public EnsemblRestcallHistoryService(EnsemblRestcallHistoryRepository ensemblRestcallHistoryRepository) {
        this.ensemblRestcallHistoryRepository = ensemblRestcallHistoryRepository;
    }

    //Without release version, the data are not stored.
    public EnsemblRestcallHistory create(RestResponseResult resultResponseResult, String type, String param,
                                         String eRelease) {
        EnsemblRestcallHistory ensemblRestcallHistory = new EnsemblRestcallHistory();
        String restApiError = resultResponseResult.getError();


        if ((resultResponseResult.getStatus() == 200) || (resultResponseResult.getStatus() == 400)) {
            if (eRelease != null) {
                if (!(eRelease.isEmpty())) {
                    try {
                        ensemblRestcallHistory.setEnsemblUrl(resultResponseResult.getUrl());
                        ensemblRestcallHistory.setEnsemblParam(param);
                        ensemblRestcallHistory.setRequestType(type);
                        ensemblRestcallHistory.setEnsemblVersion(eRelease);

                        // Check for any errors
                        if (restApiError != null && !restApiError.isEmpty()) {
                            ensemblRestcallHistory.setEnsemblError(restApiError);
                        } else {
                            ensemblRestcallHistory.setEnsemblResponse(resultResponseResult.getRestResult().toString());
                        }

                        this.ensemblRestcallHistoryRepository.save(ensemblRestcallHistory);
                    } catch (Exception e) {
                        // BEWARE: the following code MUST NOT block Ensembl Rest API Call
                    }
                }
            }
        }
        return ensemblRestcallHistory;
    }

    // BEWARE:If the Ensembl release is valid, the system can try to retrieve the data from the table
    public RestResponseResult getEnsemblRestCallByTypeAndParamAndVersion(String type, String param, String eRelease) {
        RestResponseResult restResponseResult = null;

        // Without release it is pointless stores the info.
        if (eRelease != null) {
            if (!(eRelease.isEmpty())) {
                try {
                    Collection<EnsemblRestcallHistory> urls =
                            ensemblRestcallHistoryRepository.findByRequestTypeAndEnsemblParamAndEnsemblVersion(type,
                                    param, eRelease);
                    if (urls.size() > 0) {
                        EnsemblRestcallHistory result = urls.iterator().next();
                        restResponseResult = new RestResponseResult();
                        restResponseResult.setUrl(result.getEnsemblUrl());
                        String restApiError = result.getEnsemblError();

                        if (restApiError != null && !restApiError.isEmpty()) {
                            restResponseResult.setError(restApiError);
                        } else {
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode jsonNode = mapper.convertValue(result.getEnsemblResponse().toString(), JsonNode.class);
                            restResponseResult.setRestResult(jsonNode);
                        }
                    }
                } catch (Exception e) {
                    // BEWARE: the following code MUST NOT block Ensembl Rest API Call
                }
            }
        }
        return restResponseResult;
    }
}
