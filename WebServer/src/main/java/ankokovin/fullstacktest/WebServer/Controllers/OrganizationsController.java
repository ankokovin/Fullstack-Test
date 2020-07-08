package ankokovin.fullstacktest.WebServer.Controllers;

import ankokovin.fullstacktest.WebServer.Exceptions.*;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Models.Input.CreateOrganizationInput;
import ankokovin.fullstacktest.WebServer.Models.Response.OrgListElement;
import ankokovin.fullstacktest.WebServer.Models.Response.Page;
import ankokovin.fullstacktest.WebServer.Models.Response.TreeNode;
import ankokovin.fullstacktest.WebServer.Models.Input.UpdateOrganizationInput;
import ankokovin.fullstacktest.WebServer.Services.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * Контроллер для работы с организациями
 */
@RestController
@Validated
@RequestMapping(
        value = "/api/organization",
        headers = "Accept=application/json",
        produces = "application/json")
public class OrganizationsController {

    /**
     * Количество организаций на странице по-умолчанию
     */
    private final String defaultPageCount = "10";


    @Autowired
    private OrganizationService organizationService;

    /**
     * Получение организации по идентификатору
     * @param id идентификатор
     * @return организация
     * @throws NoSuchRecordException - при отсутствии организации с данным идентификатором
     */
    @GetMapping("/{id}")
    public ResponseEntity<Organization> get(@PathVariable int id) throws NoSuchRecordException {
        return ResponseEntity.ok(organizationService.getById(id));
    }

    /**
     * Получение списка организаций с поддержкой поиска
     * @param page номер страницы (нумерация с единицы)
     * @param pageSize количество организаций на странице
     * @param name строка поиска организации
     * @return Список информации об организациях
     */
    @GetMapping
    public ResponseEntity<Page<List<OrgListElement>>> getPage(
            @RequestParam(value = "page", required = false, defaultValue = "1") @Positive int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = defaultPageCount ) @Positive int pageSize,
            @RequestParam(value = "searchName", required = false) String name) {
        return ResponseEntity.ok(organizationService.getAllWithCount(page, pageSize, name));
    }

    /**
     * Создание организации
     * @param model входная информация об организации
     * @return созданная организация
     * @throws SameNameException - при наличии организации с данным названием
     * @throws WrongHeadIdException - при ошибке в идентификаторе головной организации
     * @throws UnexpectedException - при неожиданной ошибке при создании организации
     */
    @PostMapping
    public ResponseEntity<Organization> create(
            @Valid @RequestBody CreateOrganizationInput model) throws SameNameException,
            WrongHeadIdException,
            UnexpectedException {
        return ResponseEntity.ok(organizationService.create(model));

    }

    /**
     * Обновление данных организации
     * @param model входная информация об организации
     * @return Обновлённая организация
     * @throws SameNameException - при наличии организации с данным названием
     * @throws WrongHeadIdException - при ошибке в идентификаторе головной организации
     * @throws UnexpectedException - при неожиданной ошибке при обновлении данных организации
     * @throws NoSuchRecordException - при отсутствии организации с данным идентификатором
     */
    @PostMapping("/update")
    public ResponseEntity<Organization> update(
            @Valid @RequestBody UpdateOrganizationInput model) throws SameNameException,
            WrongHeadIdException, UnexpectedException, NoSuchRecordException {
        return ResponseEntity.ok(organizationService.update(model));
    }

    /**
     * Удаление организации
     * @param id идентификатор организации
     * @return Удалённая организация
     * @throws DeleteHasChildException - при наличии дочерних элементов
     * @throws NoSuchRecordException - при отсутствии организации с данным идентификатором
     * @throws UnexpectedException - при неожиданной ошибке при удалении организации
     */
    @DeleteMapping
    public ResponseEntity<Organization> delete(@RequestBody int id) throws DeleteHasChildException,
            NoSuchRecordException, UnexpectedException {
        return ResponseEntity.ok(organizationService.delete(id));
    }

    /**
     * Получение древовидного списка организаций
     * @param id идентификатор организации (null для всех головных)
     * @param depth глубина поиска
     * @return Древовидный список
     * @throws NoSuchRecordException - при отсутствии организации с данным идентификатором
     */
    @GetMapping("/tree")
    public ResponseEntity<TreeNode<Organization>> getTree(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false, defaultValue = "2") @PositiveOrZero @Max(2) Integer depth) throws NoSuchRecordException {
        return ResponseEntity.ok(organizationService.getTree(id, depth));
    }


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
