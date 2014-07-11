package pl.com.turski.app.model;

/**
 * User: Adam
 */
public class WriteTagResponse {
    private int status;
    private String message;

    public WriteTagResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
