package ankokovin.fullstacktest.WebServer.Services;

import ankokovin.fullstacktest.WebServer.Exceptions.*;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
import ankokovin.fullstacktest.WebServer.Models.*;
import ankokovin.fullstacktest.WebServer.Repos.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис работы с работниками
 */
@Service
public class WorkerService {

    @Autowired
    private WorkerRepository rep;


    /**
     * Создание работника
     * @param model входная информация о работнике
     * @return созданный работник
     * @throws WrongHeadIdException - при попытке указать несущесвующий идентификатор начальника или начальника из другой организации
     * @throws UnexpectedException - при неожиданной ошибке при создании работника
     */
    public Worker create(CreateWorkerInput model) throws WrongHeadIdException, UnexpectedException {
        int id = rep.insert(model.name, model.org_id, model.head_id);
        try {
            return getById(id);
        } catch (NoSuchRecordException e) {
            throw new UnexpectedException(e);
        }
    }

    /**
     * Обновление данных работника
     * @param model - входная информация о работнике
     * @return обновлённый работник
     * @throws WrongHeadIdException - при указании несуществующего идентификатора начальника, из другой организации или при цикличной зависимости
     * @throws UnexpectedException - при неожиданной ошибке при обновлении работника
     */
    public Worker update(UpdateWorkerInput model) throws WrongHeadIdException, UnexpectedException {
        try {
            int id = rep.update(model.id, model.name, model.org_id, model.head_id);
            return getById(id);
        } catch (NoSuchRecordException e) {
            throw new UnexpectedException(e);
        }
    }

    /**
     * Удаление работника
     * @param id - идентификатор работника
     * @return удалённый работник
     * @throws NoSuchRecordException - при отсутствии работника с данным идентификатором
     * @throws DeleteHasChildException - при наличии подчинённых
     * @throws UnexpectedException - при неожиданной ошибке
     */
    public Worker delete(Integer id) throws NoSuchRecordException, DeleteHasChildException, UnexpectedException {
        Worker res = getById(id);
        rep.delete(id);
        return res;
    }

    /**
     * Получение списка работников с поддержкой поиска
     * @param page - номер страницы (нумерация с единицы)
     * @param pageSize - размер страницы
     * @param searchName - строка поиска работника
     * @param searchOrgName - строка поиска организации
     * @return список информации о работниках
     */
    public List<WorkerListElement> get(Integer page, Integer pageSize, String searchName, String searchOrgName) {
        return rep.getAll(page, pageSize, searchOrgName, searchName).stream()
                .map(x-> new WorkerListElement(
                        x.component1(),
                        x.component2(),
                        x.component3(),
                        x.component4(),
                        x.component5(),
                        x.component6()))
                .collect(Collectors.toList());
    }

    /**
     * Получение работника по идентификатору
     * @param id - идентификатор работника
     * @return работник
     * @throws NoSuchRecordException - при отсутствии работника с данным идентификатором
     */
    public Worker getById(Integer id) throws NoSuchRecordException {
        return rep.getById(id);
    }

    /**
     * Получение древовидного списка работников
     * @param depth глубина поиска
     * @param root идентификатор головного работника (может быть Null)
     * @return Дерево работников
     * @throws NoSuchRecordException - при отсутствии работника с данным идентификатором
     */
    public TreeNode<WorkerTreeListElement> getTree(int depth, Integer root) throws NoSuchRecordException {
        return rep.getTree(depth, root);
    }

}
