package ankokovin.fullstacktest.WebServer.Controllers;

import ankokovin.fullstacktest.WebServer.Models.OrgListElement;
import ankokovin.fullstacktest.WebServer.Models.TreeNode;
import ankokovin.fullstacktest.WebServer.NotImplementedException;
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
            @RequestBody(required = true) String name,
            @RequestBody(required = false) Integer org_id) {
        return ResponseEntity.ok(service.create(name, org_id));
    }

    @PutMapping
    public ResponseEntity<Organization> update(
            @RequestBody(required = true) Integer id,
            @RequestBody(required = true) String name,
            @RequestBody(required = false) Integer org_id) {
        throw new NotImplementedException();
    }

    @DeleteMapping
    public ResponseEntity<Organization> delete(@RequestBody(required = true) Integer id) {
        throw new NotImplementedException();
    }

    @GetMapping
    public ResponseEntity<TreeNode<Organization>> getTree(
            @RequestBody(required = true) Integer root,
            @RequestBody(required = true) Integer depth) {
        throw new NotImplementedException();
    }
}
