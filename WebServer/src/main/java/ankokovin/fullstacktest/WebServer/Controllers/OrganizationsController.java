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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для работы с организациями
 */
@RestController
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
    private OrganizationService service;


    /**
     * Получение списка организаций с поддержкой поиска
     * @param page номер страницы (нумерация с единицы)
     * @param pageSize количество организаций на странице
     * @param name строка поиска организации
     * @return Список информации об организациях
     */
    @GetMapping
    public ResponseEntity<Page<List<OrgListElement>>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = defaultPageCount ) int pageSize,
            @RequestParam(value = "searchName", required = false) String name) {
        if (page <= 0 || pageSize <= 0) return ResponseEntity.badRequest().body(null);
        return ResponseEntity.ok(service.getAllWithCount(page, pageSize, name));
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
            @RequestBody CreateOrganizationInput model) throws SameNameException,
            WrongHeadIdException,
            UnexpectedException {
        return ResponseEntity.ok(service.create(model));

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
            @RequestBody UpdateOrganizationInput model) throws SameNameException,
            WrongHeadIdException, UnexpectedException, NoSuchRecordException {
        return ResponseEntity.ok(service.update(model));
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
    public ResponseEntity<Organization> delete(@RequestBody Integer id) throws DeleteHasChildException,
            NoSuchRecordException, UnexpectedException {
        return ResponseEntity.ok(service.delete(id));
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
            @RequestParam(required = false, defaultValue = "2") Integer depth) throws NoSuchRecordException {
        if (depth <= 0 || depth > 2) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(service.getTree(id, depth));
    }
}
