package ankokovin.fullstacktest.WebServer.Controllers;

import ankokovin.fullstacktest.WebServer.Models.OrgListElement;
import ankokovin.fullstacktest.WebServer.Services.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;


import java.util.List;

@RestController
@RequestMapping("/api/organization")
public class OrganizationsController {

    @Autowired
    private OrganizationService service;

    @GetMapping(value = "{page}")
    public ResponseEntity<List<OrgListElement>> getAll(@PathVariable(value = "page") Long page) {
        // TODO: пагенация
        return ResponseEntity.ok(service.getAllWithCount());
    }

    public ResponseEntity<Organization> create(
            @RequestBody(required = true) String name,
            Integer org_id) {
        return ResponseEntity.ok(service.create(name, org_id));
    }
}
