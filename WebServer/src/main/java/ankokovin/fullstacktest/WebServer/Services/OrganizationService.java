package ankokovin.fullstacktest.WebServer.Services;


import ankokovin.fullstacktest.WebServer.Exceptions.*;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Models.Input.CreateOrganizationInput;
import ankokovin.fullstacktest.WebServer.Models.Response.OrgListElement;
import ankokovin.fullstacktest.WebServer.Models.Response.TreeNode;
import ankokovin.fullstacktest.WebServer.Models.Input.UpdateOrganizationInput;
import ankokovin.fullstacktest.WebServer.Repos.OrganizationRepository;
import org.jooq.Record4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис работы с организациями
 */
@Service
public class OrganizationService {
    @Autowired
    private OrganizationRepository rep;

    /**
     * Получение информации об организациях с поддержкой поиска
     * @param pageNum - номер страницы (нумерация с единицы)
     * @param pageSize - количество записей на странице
     * @param searchName - строка поиска
     * @return Список информации об организациях
     */
    public List<OrgListElement> getAllWithCount(int pageNum, int pageSize, String searchName) {
        List<OrgListElement> result = new ArrayList<>();
        for (Record4<Integer, String, Integer, Integer> rec : rep.getAllWithCount(pageNum, pageSize, searchName)) {
            result.add(new OrgListElement(rec.component1(), rec.component2(), rec.component3()));
        }
        return result;
    }

    /**
     * Создание организации
     * @param model - входная информация об организации
     * @return созданная организация
     * @throws SameNameException - при наличии организации с данным именем
     * @throws WrongHeadIdException - при указании несуществующего идентификатора головной организации
     * @throws UnexpectedException - при неожиданной ошибке в ходе создания организации
     */
    public Organization create(CreateOrganizationInput model) throws
            SameNameException,
            WrongHeadIdException,
            UnexpectedException {
        Integer result_id = rep.insert(model.name, model.org_id);
        try {
            return getById(result_id);
        } catch (NoSuchRecordException ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * Получение организации по идентификатору
     * @param id идентификатор организации
     * @return Организация
     * @throws NoSuchRecordException - при отсутствии организации с данным идентификатором
     */
    public Organization getById(Integer id) throws NoSuchRecordException {
        return rep.getById(id);
    }

    /**
     * Обновление данных организации
     * @param model - входная информация об организации
     * @return Изменённая организация
     * @throws WrongHeadIdException - при указании несуществующего идентификатора головной организации или при цикличной зависимости
     * @throws SameNameException - при наличии организации с данным именем
     * @throws UnexpectedException - при неожиданной ошибке в ходе создания организации
     * @throws NoSuchRecordException - при отсутствии организации с данным идентификатором
     */
    public Organization update(UpdateOrganizationInput model) throws
            WrongHeadIdException,
            SameNameException,
            UnexpectedException,
            NoSuchRecordException {
        try {
            int id = rep.update(model.id, model.name, model.org_id);
            return getById(id);
        } catch (SameNameException | WrongHeadIdException | NoSuchRecordException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * Удаление данных об организации
     * @param id - идентификатор организации
     * @return Удалённая организация
     * @throws NoSuchRecordException - при отстутствии организации с данным идентификатором
     * @throws DeleteHasChildException - при наличии дочерних организаций
     * @throws UnexpectedException - при неожиданной ошибке в ходе удаления организации
     */
    public Organization delete(Integer id) throws NoSuchRecordException, DeleteHasChildException, UnexpectedException {
        Organization res = getById(id);
        int res_id = rep.delete(id);
        if (res_id != id) throw new UnexpectedException("Delete returned wrong id");
        return res;
    }

    /**
     * Получение информации об организациях в виде дерева
     * @param root - идентификатор организации (может быть Null)
     * @param depth - глубина поиска
     * @return Дерево организаций
     * @throws NoSuchRecordException - при отсутствии организации с данным идентификатором
     */
    public TreeNode<Organization> getTree(Integer root, Integer depth) throws NoSuchRecordException {
        return rep.getTree(depth, root);
    }
}
