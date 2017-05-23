package uk.ac.ebi.spot.goci.builder;

import uk.ac.ebi.spot.goci.model.Ancestry;
import uk.ac.ebi.spot.goci.model.Country;

import java.util.Collection;

/**
 * Created by dwelter on 26/04/17.
 */
public class CountryBuilder {

    private Country country = new Country();

    public CountryBuilder setId(Long id){
        country.setId(id);
        return this;
    }

    public CountryBuilder setCountryName(String countryName){
        country.setCountryName(countryName);
        return this;
    }

    public CountryBuilder setMajorArea(String majorArea){
        country.setMajorArea(majorArea);
        return this;
    }

    public CountryBuilder setRegion(String region){
        country.setRegion(region);
        return this;
    }

    public CountryBuilder setAncestriesOrigin(Collection<Ancestry> ancestries){
        country.setAncestriesOrigin(ancestries);
        return this;
    }

    public CountryBuilder setAncestriesRecruitment(Collection<Ancestry> ancestries){
        country.setAncestriesRecruitment(ancestries);
        return this;
    }

    public Country build(){
        return country;
    }
}
