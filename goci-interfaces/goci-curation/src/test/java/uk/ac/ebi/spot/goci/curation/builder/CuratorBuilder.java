package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.model.Curator;

/**
 * Created by emma on 15/02/2016.
 *
 * @author emma
 *         <p>
 *         Curator builder used in testing
 */
public class CuratorBuilder {

    private Curator curator;

    public CuratorBuilder setId(Long id) {
        curator.setId(id);
        return this;
    }

    public CuratorBuilder setFirstName(String firstName) {
        curator.setFirstName(firstName);
        return this;
    }

    public CuratorBuilder setLastName(String lastName) {
        curator.setLastName(lastName);
        return this;
    }

    public CuratorBuilder setEmail(String email) {
        curator.setEmail(email);
        return this;
    }

    public CuratorBuilder setUserName(String userName) {
        curator.setUserName(userName);
        return this;
    }

    public Curator build() {
        return curator;
    }
}