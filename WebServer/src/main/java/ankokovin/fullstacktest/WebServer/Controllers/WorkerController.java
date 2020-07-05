package ankokovin.fullstacktest.WebServer.Controllers;

import ankokovin.fullstacktest.WebServer.Exceptions.*;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
import ankokovin.fullstacktest.WebServer.Models.Input.CreateWorkerInput;
import ankokovin.fullstacktest.WebServer.Models.Input.UpdateWorkerInput;
import ankokovin.fullstacktest.WebServer.Models.Response.TreeNode;
import ankokovin.fullstacktest.WebServer.Models.Response.WorkerListElement;
import ankokovin.fullstacktest.WebServer.Models.Response.WorkerTreeListElement;
import ankokovin.fullstacktest.WebServer.Services.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/worker",
        headers = "Accept=application/json",
        produces = "application/json")
public class WorkerController {

    private final String defaultPageCount = "25";

    @Autowired
    WorkerService workerService;

    @PostMapping
    public ResponseEntity<Worker> create(
            @RequestBody CreateWorkerInput model) throws WrongHeadIdException, UnexpectedException {
        return ResponseEntity.ok(workerService.create(model));
    }

    @PostMapping("/update")
    public ResponseEntity<Worker> update(
            @RequestBody UpdateWorkerInput model) throws WrongHeadIdException, UnexpectedException {
        return ResponseEntity.ok(workerService.update(model));
    }

    @DeleteMapping
    public ResponseEntity<Worker> delete(
            @RequestBody Integer id
    ) throws NoSuchRecordException, DeleteHasChildException, UnexpectedException {
        return ResponseEntity.ok(workerService.delete(id));
    }


    @GetMapping
    public ResponseEntity<List<WorkerListElement>> get(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = defaultPageCount ) Integer pageSize,
            @RequestParam(value = "searchName", required = false) String searchName,
            @RequestParam(value = "searchOrgName", required = false) String searchOrgName) {
        if (page <= 0 || pageSize <= 0) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(workerService.get(page, pageSize, searchName, searchOrgName));
    }

    @GetMapping("/tree")
    public ResponseEntity<TreeNode<WorkerTreeListElement>> get(
            @RequestParam(required = false) Integer id,
            @RequestParam Integer depth
    ) throws NoSuchRecordException {
        if (depth <= 0 || depth > 2) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(workerService.getTree(depth, id));
    }
}
