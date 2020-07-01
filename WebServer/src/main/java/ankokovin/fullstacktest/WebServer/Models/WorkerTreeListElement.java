package ankokovin.fullstacktest.WebServer.Models;

import java.util.Objects;

public class WorkerTreeListElement {
    public int id;
    public String name;
    public Integer org_id;
    public String org_name;
    public WorkerTreeListElement(){}
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

    @Override
    public int hashCode() {
        return Objects.hash(id, name, org_id, org_name);
    }

    @Override
    public String toString() {
        return "WorkerTreeListElement{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", org_id=" + org_id +
                ", org_name='" + org_name + '\'' +
                '}';
    }
}
