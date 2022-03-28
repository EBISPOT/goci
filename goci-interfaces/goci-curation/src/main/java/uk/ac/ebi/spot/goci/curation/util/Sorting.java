package uk.ac.ebi.spot.goci.curation.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.Map;

public class Sorting {

    public static final int MAX_PAGE_ITEM_DISPLAY = 25;
    
    public static Sort sortByLastNameAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "lastName").ignoreCase());
    }

    public static Sort sortByStatusAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "status").ignoreCase());
    }

    public static Sort sortByTraitAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "trait").ignoreCase());
    }

    public static Sort sortByPublicationDateAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "publicationId.publicationDate"));
    }

    public static Sort sortByPublicationDateDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC, "publicationId.publicationDate"));
    }

    public static Sort sortByAuthorAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "publicationId.firstAuthor.fullname").ignoreCase());
    }

    public static Sort sortByAuthorDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC, "publicationId.firstAuthor.fullname").ignoreCase());
    }

    public static Sort sortByTitleAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "publicationId.title").ignoreCase());
    }

    public static Sort sortByTitleDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC, "publicationId.title").ignoreCase());
    }

    public static Sort sortByPublicationAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "publicationId.publication").ignoreCase());
    }

    public static Sort sortByPublicationDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC, "publicationId.publication").ignoreCase());
    }

    public static Sort sortByPubmedIdAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "publicationId.pubmedId"));
    }

    public static Sort sortByUserRequestedAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "userRequested"));
    }

    public static Sort sortByUserRequestedDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC, "userRequested"));
    }

    public static Sort sortByOpenTargetsAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "openTargets"));
    }

    public static Sort sortByOpenTargetsDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC, "openTargets"));
    }

    public static Sort sortByPubmedIdDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC, "publicationId.pubmedId"));
    }

    public static Sort sortByDiseaseTraitAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "diseaseTrait.trait").ignoreCase());
    }

    public static Sort sortByDiseaseTraitDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC, "diseaseTrait.trait").ignoreCase());
    }

    public static Sort sortByEfoTraitAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "efoTraits.trait").ignoreCase());
    }

    public static Sort sortByEfoTraitDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC, "efoTraits.trait").ignoreCase());
    }

    public static Sort sortByCuratorAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "housekeeping.curator.lastName").ignoreCase());
    }

    public static Sort sortByCuratorDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC, "housekeeping.curator.lastName").ignoreCase());
    }

    public static Sort sortByCurationStatusAsc() {
        return new Sort(new Sort.Order(Sort.Direction.ASC,
                "housekeeping.curationStatus.status"));
    }

    public static Sort sortByCurationStatusDesc() {
        return new Sort(new Sort.Order(Sort.Direction.DESC,
                "housekeeping.curationStatus.status"));
    }
    
    public static Sort findSort(String sortType) {
        
        Sort sort = sortByPublicationDateDesc();

        Map<String, Sort> sortTypeMap = new HashMap<>();
        sortTypeMap.put("authorsortasc", sortByAuthorAsc());
        sortTypeMap.put("authorsortdesc", sortByAuthorDesc());
        sortTypeMap.put("titlesortasc", sortByTitleAsc());
        sortTypeMap.put("titlesortdesc", sortByTitleDesc());
        sortTypeMap.put("publicationdatesortasc", sortByPublicationDateAsc());
        sortTypeMap.put("publicationdatesortdesc", sortByPublicationDateDesc());
        sortTypeMap.put("pubmedsortasc", sortByPubmedIdAsc());
        sortTypeMap.put("pubmedsortdesc", sortByPubmedIdDesc());
        sortTypeMap.put("userrequestedsortasc", sortByUserRequestedAsc());
        sortTypeMap.put("userrequestedsortdesc", sortByUserRequestedDesc());
        sortTypeMap.put("opentargetssortasc", sortByOpenTargetsAsc());
        sortTypeMap.put("opentargetssortdesc", sortByOpenTargetsDesc());
        sortTypeMap.put("publicationsortasc", sortByPublicationAsc());
        sortTypeMap.put("publicationsortdesc", sortByPublicationDesc());
        sortTypeMap.put("efotraitsortasc", sortByEfoTraitAsc());
        sortTypeMap.put("efotraitsortdesc", sortByEfoTraitDesc());
        sortTypeMap.put("diseasetraitsortasc", sortByDiseaseTraitAsc());
        sortTypeMap.put("diseasetraitsortdesc", sortByDiseaseTraitDesc());
        sortTypeMap.put("curatorsortasc", sortByCuratorAsc());
        sortTypeMap.put("curatorsortdesc", sortByCuratorDesc());
        sortTypeMap.put("curationstatussortasc", sortByCurationStatusAsc());
        sortTypeMap.put("curationstatussortdesc", sortByCurationStatusDesc());
        
        if (sortType != null && !sortType.isEmpty()) {
            sort = sortTypeMap.get(sortType);
        }

        return sort;
    }
}
