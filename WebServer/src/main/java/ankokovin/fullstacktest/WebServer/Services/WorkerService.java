package ankokovin.fullstacktest.WebServer.Services;

import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
import ankokovin.fullstacktest.WebServer.Models.WorkerListElement;
import ankokovin.fullstacktest.WebServer.Exceptions.NotImplementedException;
import ankokovin.fullstacktest.WebServer.Repos.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkerService {

    @Autowired
    private WorkerRepository rep;

    public Worker create(String name, Integer org_id, Integer head_id) {
        throw new NotImplementedException();
    }

    public Worker update(Integer id, String name, Integer org_id, Integer head_id) {
        throw new NotImplementedException();
    }

    public Worker delete(Integer id) {
        throw new NotImplementedException();
    }

    public List<WorkerListElement> get(Integer page, String searchName, String searchOrgName) {
        throw new NotImplementedException();
    }
    public Worker getById(Integer id) {
        throw new NotImplementedException();
    }

}
