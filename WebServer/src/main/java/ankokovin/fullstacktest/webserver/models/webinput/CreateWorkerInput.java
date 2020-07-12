package ankokovin.fullstacktest.webserver.models.webinput;

import javax.validation.constraints.NotEmpty;

/**
 * Входная информация о создаваемом работника
 */
public class CreateWorkerInput {

    /**
     * Имя работника
     */
    @NotEmpty
    public final String name;
    /**
     * Идентификатор организации
     */
    public final int org_id;
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
    public CreateWorkerInput(String name, int org_id, Integer head_id) {
        this.name = name;
        this.org_id = org_id;
        this.head_id = head_id;
    }
}
