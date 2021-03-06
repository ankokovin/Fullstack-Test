package ankokovin.fullstacktest.webserver.controllers;

import ankokovin.fullstacktest.webserver.exceptions.DeleteHasChildException;
import ankokovin.fullstacktest.webserver.exceptions.NoSuchRecordException;
import ankokovin.fullstacktest.webserver.exceptions.UnexpectedException;
import ankokovin.fullstacktest.webserver.exceptions.WrongHeadIdException;
import ankokovin.fullstacktest.webserver.generated.tables.pojos.Worker;
import ankokovin.fullstacktest.webserver.models.errorresponse.DeleteHasChildResponse;
import ankokovin.fullstacktest.webserver.models.errorresponse.NoSuchRecordResponse;
import ankokovin.fullstacktest.webserver.models.errorresponse.WrongHeadIdResponse;
import ankokovin.fullstacktest.webserver.models.response.Page;
import ankokovin.fullstacktest.webserver.models.response.TreeNode;
import ankokovin.fullstacktest.webserver.models.response.WorkerListElement;
import ankokovin.fullstacktest.webserver.models.response.WorkerTreeListElement;
import ankokovin.fullstacktest.webserver.models.webinput.CreateWorkerInput;
import ankokovin.fullstacktest.webserver.models.webinput.UpdateWorkerInput;
import ankokovin.fullstacktest.webserver.services.WorkerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * Контроллер для работы с сотрудниками
 */
@RestController
@Validated
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
     *
     * @param model Информация о создаваемом работнике
     * @return Созданный работник
     * @throws WrongHeadIdException - при ошибке в идентификаторе начальника
     * @throws UnexpectedException  - при неожиданной ошибке при создании работника
     */
    @Operation(summary = "Create worker")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Worker was created successfully"),
            @ApiResponse(responseCode = "400", description = "Worker cannot have a head with such id",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = WrongHeadIdResponse.class))}),
    }
    )
    @PostMapping
    public ResponseEntity<Worker> create(
            @Valid @RequestBody CreateWorkerInput model) throws WrongHeadIdException, UnexpectedException {
        return ResponseEntity.ok(workerService.create(model));
    }

    /**
     * Обновление информации о работнике
     *
     * @param model Информация об обновляемом работнике
     * @return Обновлённый работник
     * @throws WrongHeadIdException  - при ошибке в идентификаторе начальника
     * @throws UnexpectedException   - при неожиданной ошибке при создании работника
     * @throws NoSuchRecordException - при отсутствии работника с данным идентификатором
     */
    @Operation(summary = "Update worker")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Worker was updated successfully"),
            @ApiResponse(responseCode = "404", description = "Worker with such id was not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = NoSuchRecordResponse.class))}
            ),
            @ApiResponse(responseCode = "400", description = "Worker cannot have a head with such id",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = WrongHeadIdResponse.class))}),
    }
    )
    @PostMapping("/update")
    public ResponseEntity<Worker> update(
            @Valid @RequestBody UpdateWorkerInput model) throws WrongHeadIdException, UnexpectedException, NoSuchRecordException {
        return ResponseEntity.ok(workerService.update(model));
    }

    /**
     * Удаление работника
     *
     * @param id Идентификатор работника
     * @return Удалённый работник
     * @throws NoSuchRecordException   - при отсутствии работника с данным идентификатором
     * @throws DeleteHasChildException - при наличии подчинённых
     * @throws UnexpectedException     - при неожиданной ошибке при удалении работника
     */
    @Operation(summary = "Delete worker")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Worker was deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Worker with such id was not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = NoSuchRecordResponse.class))}
            ),
            @ApiResponse(responseCode = "403", description = "Worker with dependant workers cannot be deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeleteHasChildResponse.class))}),
    }
    )
    @DeleteMapping
    public ResponseEntity<Worker> delete(
            @RequestBody int id
    ) throws NoSuchRecordException, DeleteHasChildException, UnexpectedException {
        return ResponseEntity.ok(workerService.delete(id));
    }

    /**
     * Получение работника по идентификатору
     *
     * @param id идентификатор
     * @return работник
     * @throws NoSuchRecordException - при отсутствии работника с данным идентификатором
     */
    @Operation(summary = "Get worker by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Worker was found and returned successfully"),
            @ApiResponse(responseCode = "404", description = "Worker with such id was not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = NoSuchRecordResponse.class))}
            )}
    )
    @GetMapping("/{id}")
    public ResponseEntity<Worker> get(@PathVariable int id) throws NoSuchRecordException {
        return ResponseEntity.ok(workerService.getById(id));
    }

    /**
     * Получение списка работников с поддержкой поиска
     *
     * @param page          Номер страницы (нумерация с единицы)
     * @param pageSize      Количество работников на странице
     * @param searchName    Строка поиска работника
     * @param searchOrgName Строка поиска организации
     * @return Список работников
     */
    @Operation(summary = "Get paged worker list with search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Worker page was found and returned successfully"),
    }
    )
    @GetMapping
    public ResponseEntity<Page<List<WorkerListElement>>> getPage(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = defaultPageCount) Integer pageSize,
            @RequestParam(value = "searchName", required = false) String searchName,
            @RequestParam(value = "searchOrgName", required = false) String searchOrgName) {
        if (page <= 0 || pageSize <= 0) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(workerService.get(page, pageSize, searchName, searchOrgName));
    }

    /**
     * Получение древовидного списка работников
     *
     * @param id    Идентификатор работника головной вершины (может быть Null)
     * @param depth Глубина поиска
     * @return Древовидный список работника
     * @throws NoSuchRecordException - при отсутствии работника с данным идентификатором
     */
    @Operation(summary = "Get worker tree")
    @GetMapping("/tree")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Worker node was found and returned successfully"),
            @ApiResponse(responseCode = "404", description = "Worker with such id was not found")
    }
    )
    public ResponseEntity<TreeNode<WorkerTreeListElement>> getTree(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false, defaultValue = "2") @PositiveOrZero @Max(2) Integer depth
    ) throws NoSuchRecordException {
        return ResponseEntity.ok(workerService.getTree(depth, id));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
