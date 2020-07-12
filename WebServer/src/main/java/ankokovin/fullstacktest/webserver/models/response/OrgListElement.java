package ankokovin.fullstacktest.webserver.models.response;

/**
 * Информация об организации
 */
@SuppressWarnings("unused")
public class OrgListElement {
    /**
     * Идентификатор организации
     */
    public final Integer id;
    /**
     * Название организации
     */
    public final String name;
    /**
     * Количество сотрудников организации
     */
    public final Integer count;

    /**
     * Информация об организации
     * @param id Идентификатор организации
     * @param name Название организации
     * @param count Количество сотрудников организации
     */
    public OrgListElement(int id, String name, Integer count) {
        this.id = id;
        this.count = count;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrgListElement that = (OrgListElement) o;
        return id.equals(that.id) &&
                name.equals(that.name) &&
                count.equals(that.count);
    }
}
