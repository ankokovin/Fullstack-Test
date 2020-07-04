package ankokovin.fullstacktest.WebServer.Services;

import ankokovin.fullstacktest.WebServer.Exceptions.*;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
import ankokovin.fullstacktest.WebServer.Models.*;
import ankokovin.fullstacktest.WebServer.Repos.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public Worker delete(Integer id) throws NoSuchRecordException, DeleteHasChildException, UnexpectedException {
        Worker res = getById(id);
        rep.delete(id);
        return res;
    }

    public List<WorkerListElement> get(Integer page, Integer pageSize, String searchName, String searchOrgName) {
        return rep.getAll(page, pageSize, searchOrgName, searchName).stream()
                .map(x-> new WorkerListElement(
                        x.component1(),
                        x.component2(),
                        x.component3(),
                        x.component4(),
                        x.component5(),
                        x.component6()))
                .collect(Collectors.toList());
    }

    public Worker getById(Integer id) throws NoSuchRecordException {
        return rep.getById(id);
    }

    public TreeNode<WorkerTreeListElement> getTree(int depth, Integer root) throws NoSuchRecordException {
        return rep.getTree(depth, root);
    }

}
