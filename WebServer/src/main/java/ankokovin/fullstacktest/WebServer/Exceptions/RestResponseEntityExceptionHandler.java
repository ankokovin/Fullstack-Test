package ankokovin.fullstacktest.WebServer.Exceptions;

import ankokovin.fullstacktest.WebServer.Exceptions.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.springframework.http.HttpHeaders;
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
    protected ResponseEntity<Object> handleDeleteChild(DeleteHasChildException ex, WebRequest request) throws JsonProcessingException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", ex);
        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    };

    @ExceptionHandler(NoSuchRecordException.class)
    protected ResponseEntity<Object> handleNoSuchRecord(NoSuchRecordException ex, WebRequest request) throws JsonProcessingException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", ex);
        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    };

    @ExceptionHandler(WrongHeadIdException.class)
    protected ResponseEntity<Object> handleWrongHead(WrongHeadIdException ex, WebRequest request) throws JsonProcessingException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", ex);
        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    };

    @ExceptionHandler(SameNameException.class)
    protected ResponseEntity<Object> handleSameName(SameNameException ex, WebRequest request) throws JsonProcessingException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", ex);
        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    };
}

