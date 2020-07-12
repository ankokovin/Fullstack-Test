package ankokovin.fullstacktest.webserver.exceptions;

/**
 * Отсутствует запись с данным идентификатором
 */
public class NoSuchRecordException extends BaseException {
    /**
     * Идентификатор
     */
    public final Integer id;

    public NoSuchRecordException(Integer id, String message, Throwable cause) {
        super(message, cause);
        this.id = id;
    }

    public NoSuchRecordException(Integer id, String message) {
        this(id, message, null);
    }

    public NoSuchRecordException(Integer id) {
        this(id, null);
    }
}
