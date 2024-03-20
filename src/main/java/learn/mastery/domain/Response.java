package learn.mastery.domain;

import java.util.ArrayList;
import java.util.List;

public class Response {

    private final ArrayList<String> messages = new ArrayList<>();

    public boolean isSuccess() {
        return messages.isEmpty();
    }

    public List<String> getErrorMessages() {
        return new ArrayList<>(messages);
    }

    public void addErrorMessage(String message) {
        messages.add(message);
    }
}
