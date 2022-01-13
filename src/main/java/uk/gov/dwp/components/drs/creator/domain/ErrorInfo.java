package uk.gov.dwp.components.drs.creator.domain;

public class ErrorInfo {

    private String developerMessage = null;
    private String userMessage = null;
    private boolean isTransient = false;

    public ErrorInfo() {
        // Default, empty constructor
    }

    public ErrorInfo(String message, String localizedMessage, boolean isTransient) {
        this.developerMessage = message;
        this.userMessage = localizedMessage;
        this.isTransient = isTransient;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public boolean isTransient() {
        return isTransient;
    }

    public void setTransient(boolean isTransient) {
        this.isTransient = isTransient;
    }
}
