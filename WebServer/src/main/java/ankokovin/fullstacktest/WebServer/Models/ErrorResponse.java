package ankokovin.fullstacktest.WebServer.Models;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponse {
    public String message;
    public ErrorResponse() {

    }
    public ErrorResponse(String message) {
        this.message = message;
    }
}
