package ankokovin.fullstacktest.WebServer.Controllers;

import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
import ankokovin.fullstacktest.WebServer.Models.CreateWorkerInput;
import ankokovin.fullstacktest.WebServer.Models.UpdateWorkerInput;
import ankokovin.fullstacktest.WebServer.Models.WorkerListElement;
import ankokovin.fullstacktest.WebServer.Exceptions.NotImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/worker",
        headers = "Accept=application/json",
        produces = "application/json")
public class WorkerController {

    @PostMapping
    public ResponseEntity<Worker> create(
            @RequestBody CreateWorkerInput model) {
        throw new NotImplementedException();
    }

    @PutMapping
    public ResponseEntity<Worker> update(
            @RequestBody UpdateWorkerInput model){
        throw new NotImplementedException();
    }

    @DeleteMapping
    public ResponseEntity<Worker> delete(
            @RequestBody Integer id
    ){
        throw new NotImplementedException();
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
