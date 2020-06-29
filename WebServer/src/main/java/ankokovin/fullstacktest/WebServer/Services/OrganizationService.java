package ankokovin.fullstacktest.WebServer.Services;


import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Models.OrgListElement;
import ankokovin.fullstacktest.WebServer.Models.TreeNode;
import ankokovin.fullstacktest.WebServer.NotImplementedException;
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

    public Organization create(String name, Integer org_id) {
        Integer result_id = rep.insert(name, org_id);
        return getById(result_id);
    }

    public Organization getById(Integer id) {
        return rep.getById(id);
    }

    public Organization update(Integer id, String name, Integer org_id) {
        throw new NotImplementedException();
    }

    public Organization delete(Integer id) {
        throw new NotImplementedException();
    }

    public TreeNode<Organization> getTree(Integer root, Integer depth) {
        throw new NotImplementedException();
    }
}
