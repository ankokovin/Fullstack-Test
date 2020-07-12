package ankokovin.fullstacktest.webserver.exceptions;

import ankokovin.fullstacktest.webserver.models.errorresponse.DeleteHasChildResponse;
import ankokovin.fullstacktest.webserver.models.errorresponse.NoSuchRecordResponse;
import ankokovin.fullstacktest.webserver.models.errorresponse.SameNameResponse;
import ankokovin.fullstacktest.webserver.models.errorresponse.WrongHeadIdResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Обработчик ошибок - преобразование в Response
 */
@SuppressWarnings("unused")
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
    protected ResponseEntity<DeleteHasChildResponse> handleDeleteChild(DeleteHasChildException ex, WebRequest request) {
        DeleteHasChildResponse response = new DeleteHasChildResponse(ex.id, ex.table);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NoSuchRecordException.class)
    protected ResponseEntity<NoSuchRecordResponse> handleNoSuchRecord(NoSuchRecordException ex, WebRequest request) {
        NoSuchRecordResponse response = new NoSuchRecordResponse(ex.id);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WrongHeadIdException.class)
    protected ResponseEntity<WrongHeadIdResponse> handleWrongHead(WrongHeadIdException ex, WebRequest request) {
        WrongHeadIdResponse response = new WrongHeadIdResponse(ex.id, ex.to);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SameNameException.class)
    protected ResponseEntity<SameNameResponse> handleSameName(SameNameException ex, WebRequest request) {
        SameNameResponse response = new SameNameResponse(ex.name);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}

