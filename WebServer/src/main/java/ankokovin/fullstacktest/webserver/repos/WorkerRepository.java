package ankokovin.fullstacktest.webserver.repos;

import ankokovin.fullstacktest.webserver.exceptions.DeleteHasChildException;
import ankokovin.fullstacktest.webserver.exceptions.NoSuchRecordException;
import ankokovin.fullstacktest.webserver.exceptions.UnexpectedException;
import ankokovin.fullstacktest.webserver.exceptions.WrongHeadIdException;
import ankokovin.fullstacktest.webserver.generated.tables.Organization;
import ankokovin.fullstacktest.webserver.generated.tables.pojos.Worker;
import ankokovin.fullstacktest.webserver.generated.tables.records.WorkerRecord;
import ankokovin.fullstacktest.webserver.models.Table;
import ankokovin.fullstacktest.webserver.models.response.TreeNode;
import ankokovin.fullstacktest.webserver.models.response.WorkerTreeListElement;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.*;

/**
 * Репозиторий работы с работниками
 */
@Repository
public class WorkerRepository {

    private final ankokovin.fullstacktest.webserver.generated.tables.Worker worker
            = ankokovin.fullstacktest.webserver.generated.tables.Worker.WORKER;
    private final Organization organization
            = Organization.ORGANIZATION;
    @Autowired
    private DSLContext dsl;

    public WorkerRepository() {
    }

    public WorkerRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    /**
     * Получить количество записей
     *
     * @param workerName шаблон имени работника
     * @param orgName    шаблон названия организации
     * @return количество записей
     */
    public Integer getCount(String workerName, String orgName) {
        Condition workerCond = lower(worker.WORKER_NAME).contains(lower(val(workerName)));
        Condition orgCond = lower(organization.ORG_NAME).contains(lower(val(orgName)));
        if (orgName == null) {
            SelectJoinStep<Record1<Integer>> step = dsl.select(count()).from(worker);
            if (workerName == null) return step.fetchOneInto(Integer.class);
            return step.where(workerCond).fetchOneInto(Integer.class);
        } else {
            SelectJoinStep<Record1<Integer>> step = dsl.select(count())
                    .from(worker)
                    .join(organization)
                    .on(worker.ORG_ID.eq(organization.ID));
            Condition cond = orgCond;
            if (workerName != null) cond = cond.and(workerCond);
            return step.where(cond).fetchOneInto(Integer.class);
        }
    }

    /**
     * Внесение работника в базу данных
     *
     * @param name    Имя работника
     * @param org_id  Идентификатор организации
     * @param head_id Идентификатор начальника (может быть Null)
     * @return идентификатор созданного работника
     * @throws WrongHeadIdException - при попытке указать несущесвующий идентификатор начальника или начальника из другой организации
     * @throws UnexpectedException  - при неожиданной ошибке при внесении записи в базу данных
     */
    @Transactional
    public Integer insert(String name, Integer org_id, Integer head_id) throws WrongHeadIdException, UnexpectedException {
        try {
            return dsl.insertInto(worker)
                    .values(defaultValue(), name, org_id, head_id)
                    .returning(worker.ID)
                    .fetchOne()
                    .getValue(worker.ID);
        } catch (org.springframework.jdbc.UncategorizedSQLException ex) {
            String message = ex.getMessage();
            if (message == null) throw ex;
            if (message.contains("id must not be equal head_id"))
                throw new WrongHeadIdException(head_id, ankokovin.fullstacktest.webserver.models.Table.WORKER);
            throw new UnexpectedException(ex);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            String message = ex.getMessage();
            if (message == null) throw ex;
            if (message.contains("Key (org_id)"))
                throw new WrongHeadIdException(org_id, ankokovin.fullstacktest.webserver.models.Table.ORGANIZATION);
            throw new UnexpectedException(ex);
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * Обновление данных о работнике
     *
     * @param id      - идентификатор работника
     * @param name    - имя работника
     * @param org_id  - идентификатор организации
     * @param head_id - идентификатор начальника (может быть Null)
     * @return идентификатор работника
     * @throws WrongHeadIdException  - при попытке указать несущесвующий идентификатор начальника или начальника из другой организации
     * @throws NoSuchRecordException - при отсутствии записи с данным идентификатором
     * @throws UnexpectedException   - при неожиданной ошибке во время внесения записи в базу данных
     */
    @Transactional
    public Integer update(Integer id, String name, Integer org_id, Integer head_id) throws
            WrongHeadIdException,
            NoSuchRecordException,
            UnexpectedException {
        try {
            WorkerRecord result = dsl.update(worker)
                    .set(worker.ORG_ID, org_id)
                    .set(worker.HEAD_ID, head_id)
                    .set(worker.WORKER_NAME, name)
                    .where(worker.ID.eq(id))
                    .returning(worker.ID)
                    .fetchOne();
            if (result == null) throw new NoSuchRecordException(id);
            return result.getValue(worker.ID);
        } catch (org.springframework.jdbc.UncategorizedSQLException ex) {
            String message = ex.getMessage();
            if (message == null) throw new UnexpectedException(ex);
            if (message.contains("check_worker_head")) {
                throw new WrongHeadIdException(head_id, ankokovin.fullstacktest.webserver.models.Table.WORKER);
            }
            throw new UnexpectedException(ex);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            String message = ex.getMessage();
            if (message == null) throw new UnexpectedException(ex);
            if (message.contains("Key (org_id)")) {
                throw new WrongHeadIdException(org_id, ankokovin.fullstacktest.webserver.models.Table.ORGANIZATION);
            } else if (message.contains(" Key (head_id)")) {
                throw new WrongHeadIdException(head_id, ankokovin.fullstacktest.webserver.models.Table.WORKER);
            }
            throw new UnexpectedException(ex);
        } catch (NoSuchRecordException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * Удаление работника из системы
     *
     * @param id - идентификатор работника
     * @return идентификатор работника
     * @throws NoSuchRecordException   - при отсутствии записи с данным идентификатором
     * @throws DeleteHasChildException - при наличии работников, имеющих данного в качестве начальника
     * @throws UnexpectedException-    при неожиданной ошибке во время удаления записи из базы данных
     */
    @Transactional
    public Integer delete(Integer id) throws NoSuchRecordException, DeleteHasChildException, UnexpectedException {
        try {
            WorkerRecord result = dsl.deleteFrom(worker)
                    .where(worker.ID.eq(id))
                    .returning(worker.ID)
                    .fetchOne();
            if (result == null) throw new NoSuchRecordException(id);
            return result.getValue(worker.ID);
        } catch (NoSuchRecordException ex) {
            throw ex;
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            String message = ex.getMessage();
            if (message != null && message.contains("is still referenced")) {
                int child_id = Integer.parseInt(message.split("\\)=\\(")[1].split("\\)")[0]);
                if (message.contains("from table \"worker\"")) {
                    throw new DeleteHasChildException(child_id, ankokovin.fullstacktest.webserver.models.Table.WORKER);
                }
                throw new UnexpectedException(new DeleteHasChildException(child_id, Table.UNKNOWN));

            }
            throw new UnexpectedException(ex);
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * Получение списка работников с поддержкой поиска
     *
     * @param pageNum     - номер страницы (нумерация с единицы)
     * @param pageSize    - количество записей на одной странице
     * @param org_name    - строка поиска организации (может быть Null)
     * @param worker_name - строка поиска работника (может быть Null)
     * @return Запись о работнике:
     * 1) идентификатор
     * 2) имя
     * 3) идентификатор начальника
     * 4) имя начальника
     * 5) идентификатор организации
     * 6) название организации
     * 7) позиция вхождения строки поиска организации
     * 8) позиция вхождения строки поиска работника
     */
    @Transactional
    public List<Record8<Integer, String, Integer, String, Integer, String, Integer, Integer>> getAll(
            Integer pageNum,
            Integer pageSize,
            String org_name,
            String worker_name) {
        ankokovin.fullstacktest.webserver.generated.tables.Worker headWorker = worker.as("headWorker");
        Field<Integer> workerNameSubstringId = position(lower(worker.WORKER_NAME), lower(inline(worker_name)));
        Field<Integer> organizationNameSubstringId = position(lower(organization.ORG_NAME), lower(inline(org_name)));
        SelectOnConditionStep<Record8<Integer, String, Integer, String, Integer, String, Integer, Integer>> preCond = dsl.select(worker.ID,
                worker.WORKER_NAME,
                headWorker.ID,
                headWorker.WORKER_NAME,
                organization.ID,
                organization.ORG_NAME,
                organizationNameSubstringId,
                workerNameSubstringId
        )
                .from(worker)
                .leftJoin(organization)
                .on(worker.ORG_ID.eq(organization.ID))
                .leftJoin(headWorker)
                .on(worker.HEAD_ID.eq(headWorker.ID));
        Condition condition = DSL.trueCondition();
        if (org_name != null) condition = condition.and(organizationNameSubstringId.greaterThan(0));
        if (worker_name != null) condition = condition.and(workerNameSubstringId.greaterThan(0));
        return preCond.where(condition)
                .orderBy(
                        workerNameSubstringId,
                        organizationNameSubstringId,
                        worker.WORKER_NAME,
                        organization.ORG_NAME,
                        worker.ID)
                .limit(pageSize)
                .offset((pageNum - 1) * pageSize)
                .fetch();
    }

    /**
     * Получение вершины дерева
     *
     * @param maxDepth текущая глубина
     * @param element  информация о работнике в текущей вершине
     * @return Вершина дерева
     */
    @Transactional(readOnly = true)
    private TreeNode<WorkerTreeListElement> getTree(int maxDepth, WorkerTreeListElement element) {
        if (maxDepth == 0) return new TreeNode<>(element);
        return new TreeNode<>(element, dsl.selectFrom(worker)
                .where(worker.HEAD_ID.eq(element.id))
                .fetch()
                .map(x -> getTree(maxDepth - 1,
                        new WorkerTreeListElement(x.getId(),
                                x.getWorkerName(),
                                element.org_id,
                                element.org_name))));
    }

    /**
     * Получение вершины дерева
     *
     * @param maxDepth максимальная глубина поиска
     * @param root_id  идентификатор работника (может быть Null)
     * @return Вершина дерева
     * @throws NoSuchRecordException - при отсутствии записи работника с данным идентификатором
     */
    @Transactional(readOnly = true)
    public TreeNode<WorkerTreeListElement> getTree(int maxDepth, Integer root_id) throws NoSuchRecordException {
        assert maxDepth > 0;
        if (root_id != null) {
            Record3<String, Integer, String> org = dsl.select(worker.WORKER_NAME, organization.ID, organization.ORG_NAME)
                    .from(worker).join(organization).on(worker.ORG_ID.eq(organization.ID))
                    .where(worker.ID.eq(root_id)).fetchOne();
            if (org == null) throw new NoSuchRecordException(root_id);
            return getTree(maxDepth,
                    new WorkerTreeListElement(root_id, org.component1(), org.component2(), org.component3()));
        }
        return new TreeNode<>(null, dsl.select(worker.ID, worker.WORKER_NAME, organization.ID, organization.ORG_NAME)
                .from(worker).join(organization).on(worker.ORG_ID.eq(organization.ID))
                .where(worker.HEAD_ID.isNull())
                .orderBy(organization.ID, worker.ID)
                .fetch().parallelStream()
                .map(x -> getTree(maxDepth - 1,
                        new WorkerTreeListElement(x.component1(), x.component2(), x.component3(), x.component4())))
                .collect(Collectors.toList()));
    }

    /**
     * Получение работника по идентификатору
     *
     * @param id идентификатор работника
     * @return Работник
     * @throws NoSuchRecordException - при отсутствии записи работника с данным идентификатором
     */
    @Transactional
    public Worker getById(Integer id) throws NoSuchRecordException {
        Worker result = dsl.selectFrom(worker).where(worker.ID.eq(id)).fetchOneInto(Worker.class);
        if (result == null) throw new NoSuchRecordException(id);
        return result;
    }
}
