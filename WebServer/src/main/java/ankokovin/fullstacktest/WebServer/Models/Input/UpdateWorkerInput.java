package ankokovin.fullstacktest.WebServer.Models.Input;

/**
 * Информация об изменяемом работнике
 */
public class UpdateWorkerInput extends CreateWorkerInput {
    /**
     * Идентификатор работника
     */
    public final Integer id;

    /**
     * Информация об изменяемом работнике
     * @param id Идентификатор работника
     * @param name Имя работника
     * @param org_id Идентификатор организации
     * @param head_id Идентификатор начальника (может быть Null)
     */
    public UpdateWorkerInput(Integer id, String name, Integer org_id, Integer head_id) {
        super(name, org_id, head_id);
        this.id = id;
    }
}
