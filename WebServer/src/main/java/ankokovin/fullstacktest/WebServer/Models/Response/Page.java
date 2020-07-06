package ankokovin.fullstacktest.WebServer.Models.Response;

public class Page<T> {
    public Integer page;
    public Integer pageSize;
    public Integer total;
    public T list;
    public Page(){}
    public Page(Integer page, Integer pageSize, Integer total, T list) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
    }
}
