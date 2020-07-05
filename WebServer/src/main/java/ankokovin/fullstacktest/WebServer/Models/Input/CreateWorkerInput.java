package ankokovin.fullstacktest.WebServer.Models.Input;

/**
 * Входная информация о создаваемом работника
 */
public class CreateWorkerInput {

    /**
     * Имя работника
     */
    public final String name;
    /**
     * Идентификатор организации
     */
    public final Integer org_id;
    /**
     * Идентификатор начальника (может быть Null)
     */
    public final Integer head_id;


    /**
     * Входная информация о создаваемом работника
     * @param name Имя работника
     * @param org_id Идентификатор организации
     * @param head_id Идентификатор начальника (может быть Null)
     */
    public CreateWorkerInput(String name, Integer org_id, Integer head_id) {
        this.name = name;
        this.org_id = org_id;
        this.head_id = head_id;
    }
}
