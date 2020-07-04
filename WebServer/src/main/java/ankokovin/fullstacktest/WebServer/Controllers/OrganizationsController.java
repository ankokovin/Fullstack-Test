package ankokovin.fullstacktest.WebServer.Controllers;

import ankokovin.fullstacktest.WebServer.Exceptions.BaseException;
import ankokovin.fullstacktest.WebServer.Exceptions.NoSuchRecordException;
import ankokovin.fullstacktest.WebServer.Exceptions.NotImplementedException;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Models.CreateOrganizationInput;
import ankokovin.fullstacktest.WebServer.Models.OrgListElement;
import ankokovin.fullstacktest.WebServer.Models.TreeNode;
import ankokovin.fullstacktest.WebServer.Models.UpdateOrganizationInput;
import ankokovin.fullstacktest.WebServer.Services.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/api/organization",
        headers = "Accept=application/json",
        produces = "application/json")
public class OrganizationsController {

    private final String defaultPageCount = "25";
    private final int maxPageCount = 100;

    @Autowired
    private OrganizationService service;



    @GetMapping
    public ResponseEntity<List<OrgListElement>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = defaultPageCount ) int pageSize,
            @RequestParam(value = "searchName", required = false) String name) {
        if (page <= 0 || pageSize <= 0) return ResponseEntity.badRequest().body(null);
        return ResponseEntity.ok(service.getAllWithCount(page, pageSize, name));
    }

    @PostMapping
    public ResponseEntity<Organization> create(
            @RequestBody CreateOrganizationInput model) throws BaseException {
        return ResponseEntity.ok(service.create(model));

    }

    @PostMapping("/update")
    public ResponseEntity<Organization> update(
            @RequestBody UpdateOrganizationInput model) throws BaseException {
        return ResponseEntity.ok(service.update(model));
    }

    @DeleteMapping
    public ResponseEntity<Organization> delete(@RequestBody Integer id) throws BaseException {
        return ResponseEntity.ok(service.delete(id));
    }

    @GetMapping("/tree")
    public ResponseEntity<TreeNode<Organization>> getTree(
            @RequestParam(required = false) Integer id,
            @RequestParam Integer depth) throws NoSuchRecordException {
        if (depth <= 0 || depth > 2) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(service.getTree(id, depth));
    }
}
