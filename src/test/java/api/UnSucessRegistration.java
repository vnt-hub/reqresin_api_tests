package api;

public class UnSucessRegistration {
    private String error;

    public UnSucessRegistration(String error) {
        this.error = error;
    }

    public UnSucessRegistration() {

    }

    public String getError() {
        return error;
    }
}
