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

    public void addError(Error error) {
        errors.add(error);
    }

    public boolean hasErrors() {
        return ! errors.isEmpty();
    }

    public String errorMessage() {
        return errors.stream()
                .map(e -> e.message)
                .collect(Collectors.joining(", "));
    }

}
