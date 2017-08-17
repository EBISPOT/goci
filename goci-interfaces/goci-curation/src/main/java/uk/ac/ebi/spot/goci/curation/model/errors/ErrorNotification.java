package uk.ac.ebi.spot.goci.curation.model.errors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by xinhe on 12/04/2017.
 * This notification class can be used in all sort of validation process.
 * See the notification pattern here:
 * https://martinfowler.com/articles/replaceThrowWithNotification.html
 */
public class ErrorNotification {


    private List<Error> errors = new ArrayList<>();

    public ErrorNotification() {
    }

    public ErrorNotification(List<Error> errors) {
        this.errors = errors;
    }


    public void addError(Error error) {
        errors.add(error);
    }

    public void addError(List<Error> errorsToAdd) {
        errorsToAdd.stream().forEach(error -> errors.add(error));
    }



    public boolean hasErrors() {
        return ! errors.isEmpty();
    }

    public String errorMessage() {
        return errors.stream()
                .map(e -> e.message)
                .collect(Collectors.joining(", "));
    }

    public List<Error> getErrors() {
        return errors;
    }

}
