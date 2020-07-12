package ankokovin.fullstacktest.webserver.models.webinput;

import javax.validation.constraints.NotEmpty;

/**
 * Входная информация о создаваемой организации
 */
public class CreateOrganizationInput {

    /**
     * Наименование создаваемой организации
     */
    @NotEmpty
    public String name;

    /**
     * Идентификатор головной организации (может быть Null)
     */
    public Integer org_id;

    public CreateOrganizationInput() {

    }

    /**
     * Входная информация о создаваемой организации
     * @param name Наименование создаваемой организации
     * @param org_id Идентификатор головной организации
     */
    public CreateOrganizationInput(String name, Integer org_id) {
        this.name = name;
        this.org_id = org_id;
    }
}
