package ankokovin.fullstacktest.WebServer.Controllers;

import ankokovin.fullstacktest.WebServer.Exceptions.NoSuchRecordException;
import ankokovin.fullstacktest.WebServer.Exceptions.UnexpectedException;
import ankokovin.fullstacktest.WebServer.Exceptions.WrongHeadIdException;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
import ankokovin.fullstacktest.WebServer.Models.CreateWorkerInput;
import ankokovin.fullstacktest.WebServer.Models.UpdateWorkerInput;
import ankokovin.fullstacktest.WebServer.Models.WorkerListElement;
import ankokovin.fullstacktest.WebServer.Exceptions.NotImplementedException;
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
    ) throws NoSuchRecordException {
        return ResponseEntity.ok(workerService.delete(id));
    }

    @GetMapping(value = "{page}")
    public ResponseEntity<List<WorkerListElement>> get(
            @PathVariable(value = "page") Long page,
            @RequestParam(value = "searchName") String searchName,
            @RequestParam(value = "searchOrgName") String searchOrgName){
        //TODO: пагинация и поиск по орге и/или по имени
        throw new NotImplementedException();
    }

    //TODO: getTree?
}
