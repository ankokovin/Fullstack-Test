package ankokovin.fullstacktest.webserver.models.response;


import java.util.Objects;

/**
 * Информация о работнике
 */
public class WorkerListElement {
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
    /**
     * Идентификатор начальника
     */
    public Integer head_id;
    /**
     * Имя начальника
     */
    public String head_name;

    @SuppressWarnings("unused")
    public WorkerListElement() {
    }

    public WorkerListElement(int id, String name, Integer head_id, String head_name, Integer org_id, String org_name) {
        this.id = id;
        this.name = name;
        this.head_id = head_id;
        this.head_name = head_name;
        this.org_id = org_id;
        this.org_name = org_name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkerListElement that = (WorkerListElement) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(org_id, that.org_id) &&
                Objects.equals(org_name, that.org_name) &&
                Objects.equals(head_id, that.head_id) &&
                Objects.equals(head_name, that.head_name);
    }
}
