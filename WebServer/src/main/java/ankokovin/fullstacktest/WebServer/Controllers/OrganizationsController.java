package ankokovin.fullstacktest.WebServer.Controllers;

import ankokovin.fullstacktest.WebServer.Exceptions.BaseException;
import ankokovin.fullstacktest.WebServer.Models.CreateOrganizationInput;
import ankokovin.fullstacktest.WebServer.Models.OrgListElement;
import ankokovin.fullstacktest.WebServer.Models.TreeNode;
import ankokovin.fullstacktest.WebServer.Exceptions.NotImplementedException;
import ankokovin.fullstacktest.WebServer.Models.UpdateOrganizationInput;
import ankokovin.fullstacktest.WebServer.Services.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;



import java.util.List;

@RestController
@RequestMapping(
        value = "/api/organization",
        headers = "Accept=application/json",
        produces = "application/json")
public class OrganizationsController {

    @Autowired
    private OrganizationService service;

    @GetMapping("/{page}")
    public ResponseEntity<List<OrgListElement>> getAll(
            @PathVariable(value = "page") Long page,
            @RequestParam(value = "searchName", required = false) String name) {
        // TODO: пагинация и поиск
        throw new NotImplementedException();
    }

    @PostMapping
    public ResponseEntity<Organization> create(
            @RequestBody CreateOrganizationInput model)  throws BaseException{
            return ResponseEntity.ok(service.create(model));

    }

    @PostMapping("/update")
    public ResponseEntity<Organization> update(
            @RequestBody UpdateOrganizationInput model) throws BaseException {
        throw new NotImplementedException();
    }

    @DeleteMapping
    public ResponseEntity<Organization> delete(@RequestBody Integer id) throws BaseException{
        throw new NotImplementedException();
    }

    @GetMapping
    public ResponseEntity<TreeNode<Organization>> getTree(
            @RequestParam Integer root,
            @RequestParam Integer depth) {
        throw new NotImplementedException();
    }
}
