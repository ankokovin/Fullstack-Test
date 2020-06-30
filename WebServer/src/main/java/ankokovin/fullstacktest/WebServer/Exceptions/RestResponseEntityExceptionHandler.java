package ankokovin.fullstacktest.WebServer.Exceptions;

import ankokovin.fullstacktest.WebServer.Models.ErrorResponse.DeleteHasChildResponse;
import ankokovin.fullstacktest.WebServer.Models.ErrorResponse.NoSuchRecordResponse;
import ankokovin.fullstacktest.WebServer.Models.ErrorResponse.SameNameResponse;
import ankokovin.fullstacktest.WebServer.Models.ErrorResponse.WrongHeadIdResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.registerSubtypes(new NamedType(DeleteHasChildException.class, "DeleteHasChildException"));
        MAPPER.registerSubtypes(new NamedType(NoSuchRecordException.class, "NoSuchRecordException"));
        MAPPER.registerSubtypes(new NamedType(SameNameException.class, "SameNameException"));
        MAPPER.registerSubtypes(new NamedType(WrongHeadIdException.class, "WrongHeadIdException"));
    }

    @ExceptionHandler(DeleteHasChildException.class)
    protected ResponseEntity<Object> handleDeleteChild(DeleteHasChildException ex, WebRequest request) {
        DeleteHasChildResponse response = new DeleteHasChildResponse(ex.id, ex.table);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchRecordException.class)
    protected ResponseEntity<Object> handleNoSuchRecord(NoSuchRecordException ex, WebRequest request) {
        NoSuchRecordResponse response = new NoSuchRecordResponse(ex.id);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WrongHeadIdException.class)
    protected ResponseEntity<Object> handleWrongHead(WrongHeadIdException ex, WebRequest request) {
        WrongHeadIdResponse response = new WrongHeadIdResponse(ex.id);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SameNameException.class)
    protected ResponseEntity<Object> handleSameName(SameNameException ex, WebRequest request) {
        SameNameResponse response = new SameNameResponse(ex.name);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}

