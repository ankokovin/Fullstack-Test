package ankokovin.fullstacktest.WebServer.Models.Input;

import javax.validation.constraints.NotNull;

/**
 * Информация об изменяемой организации
 */
public class UpdateOrganizationInput extends CreateOrganizationInput {
    /**
     * Идентификатор организации
     */
    @NotNull
    public Integer id;

    public UpdateOrganizationInput() {
        super();
    }

    /**
     * Информация об изменяемой организации
     * @param id Идентификатор организации
     * @param name Наименование организации
     * @param head_org_id Идентификатор головной организации
     */
    public UpdateOrganizationInput(Integer id, String name, Integer head_org_id) {
        super(name, head_org_id);
        this.id = id;
    }
}
