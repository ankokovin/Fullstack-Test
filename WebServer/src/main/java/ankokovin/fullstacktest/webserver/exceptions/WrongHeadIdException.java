package ankokovin.fullstacktest.webserver.exceptions;

import ankokovin.fullstacktest.webserver.models.Table;

/**
 * Неверный идентификатор сущности
 */
public class WrongHeadIdException extends BaseException {

    /**
     * Идентификатор
     */
    public final Integer id;
    /**
     * Таблица сущности
     */
    public final Table to;

    public WrongHeadIdException(Integer id, Table to, String message, Throwable cause) {
        super(message, cause);
        this.id = id;
        this.to = to;
    }

    public WrongHeadIdException(Integer id, Table to, String message) {
        this(id, to, message, null);
    }

    public WrongHeadIdException(Integer id, Table to) {
        this(id, to, null);
    }


}
