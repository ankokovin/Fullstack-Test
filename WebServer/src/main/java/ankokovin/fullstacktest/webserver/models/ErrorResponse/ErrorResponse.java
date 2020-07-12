package ankokovin.fullstacktest.webserver.models.ErrorResponse;

public class ErrorResponse {
    public String message;

    public ErrorResponse() {

    }

    public ErrorResponse(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorResponse that = (ErrorResponse) o;
        return message.equals(that.message);
    }


    @Override
    public String toString() {
        return "ErrorResponse{" +
                "message='" + message + '\'' +
                '}';
    }
}
