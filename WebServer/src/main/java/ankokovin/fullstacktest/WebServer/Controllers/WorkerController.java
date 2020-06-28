package ankokovin.fullstacktest.WebServer.Controllers;

import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
import ankokovin.fullstacktest.WebServer.Models.WorkerListElement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

@RestController
@RequestMapping("/api/worker")
public class WorkerController {

    @PostMapping
    public ResponseEntity<Worker> create(
            @RequestBody(required = true)String name,
            @RequestBody(required = true)Integer org_id,
            @RequestBody(required = false)Integer head_id
    ) {
        throw new NotImplementedException();
    }

    @PutMapping
    public ResponseEntity<Worker> update(
            @RequestBody(required = true)Integer id,
            @RequestBody(required = true)String name,
            @RequestBody(required = true)Integer org_id,
            @RequestBody(required = false)Integer head_id
    ){
        throw new NotImplementedException();
    }

    @DeleteMapping
    public ResponseEntity<Worker> delete(
            @RequestBody(required = true)Integer id
    ){
        throw new NotImplementedException();
    }

    @GetMapping(value = "{page}")
    public ResponseEntity<List<WorkerListElement>> get(@PathVariable(value = "page") Long page){
        //TODO: пагинация и поиск по орге и/или по имени
        throw new NotImplementedException();
    }

}
