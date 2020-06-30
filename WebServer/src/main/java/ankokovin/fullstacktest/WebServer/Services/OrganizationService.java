package ankokovin.fullstacktest.WebServer.Services;


import ankokovin.fullstacktest.WebServer.Exceptions.*;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Models.CreateOrganizationInput;
import ankokovin.fullstacktest.WebServer.Models.OrgListElement;
import ankokovin.fullstacktest.WebServer.Models.TreeNode;
import ankokovin.fullstacktest.WebServer.Models.UpdateOrganizationInput;
import ankokovin.fullstacktest.WebServer.Repos.OrganizationRepository;
import org.jooq.Record2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrganizationService
{
    @Autowired
    private OrganizationRepository rep;

    public List<OrgListElement> getAllWithCount() {
        List<OrgListElement> result = new ArrayList<>();
        for (Record2<String, Integer> rec: rep.getAllWithCount()) {
            result.add(new OrgListElement(rec.component1(),rec.component2()));
        }
        return result;
    }

    public Organization create(CreateOrganizationInput model) throws
            SameNameException,
            WrongHeadIdException,
            UnexpectedException {
        Integer result_id = rep.insert(model.name, model.org_id);
        try {
            return getById(result_id);
        } catch (NoSuchRecordException ex) {
            throw new UnexpectedException(ex);
        }
    }

    public Organization getById(Integer id) throws NoSuchRecordException {
        return rep.getById(id);
    }

    public Organization update(UpdateOrganizationInput model) throws
            WrongHeadIdException,
            SameNameException,
            UnexpectedException {
        try {
            int id = rep.update(model.id, model.name, model.org_id);
            return getById(id);
        } catch (SameNameException | WrongHeadIdException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    public Organization delete(Integer id) throws NoSuchRecordException, DeleteHasChildException, UnexpectedException {
        Organization res = getById(id);
        int res_id = rep.delete(id);
        if (res_id != id) throw new UnexpectedException("Delete returned wrong id");
        return res;
    }

    public TreeNode<Organization> getTree(Integer root, Integer depth) {
        throw new NotImplementedException();
    }
}
