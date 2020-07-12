package ankokovin.fullstacktest.webserver.exceptions;

import ankokovin.fullstacktest.webserver.models.Table;

/**
 * Запись имеет зависимые и не может быть удалена
 */
public class DeleteHasChildException extends BaseException {
    /**
     * Идентификатор зависимой записи
     */
    public final int id;
    /**
     * Таблица зависимой записи
     */
    public final Table table;

    public DeleteHasChildException(int id, Table table, String message, Throwable cause) {
        super(message, cause);
        this.id = id;
        this.table = table;
    }

    public DeleteHasChildException(int id, Table table, Throwable cause) {
        this(id, table, null, cause);
    }

    public DeleteHasChildException(int id, Table table) {
        this(id, table, null);
    }
}
