package ankokovin.fullstacktest.WebServer.Services;

import ankokovin.fullstacktest.WebServer.Exceptions.NoSuchRecordException;
import ankokovin.fullstacktest.WebServer.Exceptions.UnexpectedException;
import ankokovin.fullstacktest.WebServer.Exceptions.WrongHeadIdException;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
import ankokovin.fullstacktest.WebServer.Models.CreateWorkerInput;
import ankokovin.fullstacktest.WebServer.Models.UpdateOrganizationInput;
import ankokovin.fullstacktest.WebServer.Models.UpdateWorkerInput;
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

    public Worker create(CreateWorkerInput model) throws WrongHeadIdException, UnexpectedException {
        int id = rep.insert(model.name, model.org_id, model.head_id);
        try {
            return getById(id);
        } catch (NoSuchRecordException e) {
            throw new UnexpectedException(e);
        }
    }

    public Worker update(UpdateWorkerInput model) throws WrongHeadIdException, UnexpectedException {
        try {
            int id = rep.update(model.id, model.name, model.org_id, model.head_id);
            return getById(id);
        } catch (NoSuchRecordException e) {
            throw new UnexpectedException(e);
        }
    }

    public Worker delete(Integer id) {
        throw new NotImplementedException();
    }

    public List<WorkerListElement> get(Integer page, String searchName, String searchOrgName) {
        throw new NotImplementedException();
    }
    public Worker getById(Integer id) throws NoSuchRecordException {
        return rep.getById(id);
    }

}
