package ankokovin.fullstacktest.webserver.models.Input;

/**
 * Информация об изменяемой организации
 */
public class UpdateOrganizationInput extends CreateOrganizationInput {
    /**
     * Идентификатор организации
     */
    public int id;

    public UpdateOrganizationInput() {
        super();
    }

    /**
     * Информация об изменяемой организации
     * @param id Идентификатор организации
     * @param name Наименование организации
     * @param head_org_id Идентификатор головной организации
     */
    public UpdateOrganizationInput(int id, String name, Integer head_org_id) {
        super(name, head_org_id);
        this.id = id;
    }
}
