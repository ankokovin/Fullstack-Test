package ankokovin.fullstacktest.webserver.models.response;

/**
 * Информация о работнике
 */
public class WorkerTreeListElement {
    /**
     * Идентификатор работника
     */
    public int id;
    /**
     * Имя работника
     */
    public String name;
    /**
     * Идентификатор организации
     */
    public Integer org_id;
    /**
     * Название организации
     */
    public String org_name;

    @SuppressWarnings("unused")
    public WorkerTreeListElement() {
    }

    public WorkerTreeListElement(int id, String name,
                                 int org_id, String org_name) {
        this.id = id;
        this.name = name;
        this.org_id = org_id;
        this.org_name = org_name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkerTreeListElement element = (WorkerTreeListElement) o;
        return id == element.id &&
                name.equals(element.name) &&
                org_id.equals(element.org_id) &&
                org_name.equals(element.org_name);
    }
}
