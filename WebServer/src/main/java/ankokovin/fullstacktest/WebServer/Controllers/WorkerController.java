package ankokovin.fullstacktest.WebServer.Controllers;

import ankokovin.fullstacktest.WebServer.Exceptions.*;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
import ankokovin.fullstacktest.WebServer.Models.Input.CreateWorkerInput;
import ankokovin.fullstacktest.WebServer.Models.Input.UpdateWorkerInput;
import ankokovin.fullstacktest.WebServer.Models.Response.Page;
import ankokovin.fullstacktest.WebServer.Models.Response.TreeNode;
import ankokovin.fullstacktest.WebServer.Models.Response.WorkerListElement;
import ankokovin.fullstacktest.WebServer.Models.Response.WorkerTreeListElement;
import ankokovin.fullstacktest.WebServer.Services.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для работы с сотрудниками
 */
@RestController
@RequestMapping(value = "/api/worker",
        headers = "Accept=application/json",
        produces = "application/json")
public class WorkerController {

    /**
     * Количество сотрудников на странице по-умолчанию
     */
    private final String defaultPageCount = "25";

    @Autowired
    WorkerService workerService;

    /**
     * Создание работника
     * @param model Информация о создаваемом работнике
     * @return Созданный работник
     * @throws WrongHeadIdException - при ошибке в идентификаторе начальника
     * @throws UnexpectedException - при неожиданной ошибке при создании работника
     */
    @PostMapping
    public ResponseEntity<Worker> create(
            @RequestBody CreateWorkerInput model) throws WrongHeadIdException, UnexpectedException {
        return ResponseEntity.ok(workerService.create(model));
    }

    /**
     * Обновление информации о работнике
     * @param model Информация об обновляемом работнике
     * @return Обновлённый работник
     * @throws WrongHeadIdException - при ошибке в идентификаторе начальника
     * @throws UnexpectedException - при неожиданной ошибке при создании работника
     * @throws NoSuchRecordException - при отсутствии работника с данным идентификатором
     */
    @PostMapping("/update")
    public ResponseEntity<Worker> update(
            @RequestBody UpdateWorkerInput model) throws WrongHeadIdException, UnexpectedException, NoSuchRecordException {
        return ResponseEntity.ok(workerService.update(model));
    }

    /**
     * Удаление работника
     * @param id Идентификатор работника
     * @return Удалённый работник
     * @throws NoSuchRecordException - при отсутствии работника с данным идентификатором
     * @throws DeleteHasChildException - при наличии подчинённых
     * @throws UnexpectedException - при неожиданной ошибке при удалении работника
     */
    @DeleteMapping
    public ResponseEntity<Worker> delete(
            @RequestBody Integer id
    ) throws NoSuchRecordException, DeleteHasChildException, UnexpectedException {
        return ResponseEntity.ok(workerService.delete(id));
    }

    /**
     * Получение работника по идентификатору
     * @param id идентификатор
     * @return работник
     * @throws NoSuchRecordException - при отсутствии работника с данным идентификатором
     */
    @GetMapping("/{id}")
    public ResponseEntity<Worker> get(@PathVariable int id) throws NoSuchRecordException {
        return ResponseEntity.ok(workerService.getById(id));
    }

    /**
     * Получение списка работников с поддержкой поиска
     * @param page Номер страницы (нумерация с единицы)
     * @param pageSize Количество работников на странице
     * @param searchName Строка поиска работника
     * @param searchOrgName Строка поиска организации
     * @return Список работников
     */
    @GetMapping
    public ResponseEntity<Page<List<WorkerListElement>>> getPage(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = defaultPageCount ) Integer pageSize,
            @RequestParam(value = "searchName", required = false) String searchName,
            @RequestParam(value = "searchOrgName", required = false) String searchOrgName) {
        if (page <= 0 || pageSize <= 0) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(workerService.get(page, pageSize, searchName, searchOrgName));
    }

    /**
     * Получение древовидного списка работников
     * @param id Идентификатор работника головной вершины (может быть Null)
     * @param depth Глубина поиска
     * @return Древовидный список работника
     * @throws NoSuchRecordException - при отсутствии работника с данным идентификатором
     */
    @GetMapping("/tree")
    public ResponseEntity<TreeNode<WorkerTreeListElement>> getTree(
            @RequestParam(required = false) Integer id,
            @RequestParam Integer depth
    ) throws NoSuchRecordException {
        if (depth <= 0 || depth > 2) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(workerService.getTree(depth, id));
    }
}
