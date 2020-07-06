package ankokovin.fullstacktest.WebServer.Repos;
import ankokovin.fullstacktest.WebServer.Exceptions.*;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Generated.tables.Worker;
import ankokovin.fullstacktest.WebServer.Generated.tables.records.OrganizationRecord;
import ankokovin.fullstacktest.WebServer.Models.Table;
import ankokovin.fullstacktest.WebServer.Models.Response.TreeNode;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.*;

/**
 * Репозиторий работы с организациями
 */
@Repository
public class OrganizationRepository {

    private final ankokovin.fullstacktest.WebServer.Generated.tables.Organization organization
            = ankokovin.fullstacktest.WebServer.Generated.tables.Organization.ORGANIZATION;
    private final Worker worker = Worker.WORKER;
    @Autowired
    public DSLContext dsl;


    public OrganizationRepository() {}
    public OrganizationRepository(DSLContext dsl){this.dsl = dsl;}

    public Integer getCount(String searchName) {
        if (searchName == null) return dsl.select(count()).from(organization).fetchOneInto(Integer.class);
        return dsl.select(count())
                .from(organization)
                .where(lower(organization.ORG_NAME).contains(lower(val(searchName))))
                .fetchOneInto(Integer.class);
    }

    /**
     * Получение списка организаций с количеством работников с поддержкой поиска
     * @param pageNum - Номер страницы (нумерация с единицы)
     * @param pageSize - Количество записей на одной странице
     * @param searchName - Строка поиска организации
     * @return Список записей.
     * Состав записи: ID, Name, Количество организаций, позиция вхождения строки поиска организации
     */
    @Transactional(readOnly = true)
    public List<Record4<Integer, String, Integer, Integer>> getAllWithCount(int pageNum, int pageSize, String searchName) {
        Field<Integer> subStringIdx = position(lower(organization.ORG_NAME), lower(val(searchName)));
        SelectJoinStep<Record4<Integer, String, Integer, Integer>> s =  dsl.select(
                organization.ID, organization.ORG_NAME, count(worker.ID), subStringIdx)
                .from(organization.leftJoin(worker).on(worker.ORG_ID.eq(organization.ID)));
        Condition cond = searchName == null ? trueCondition() : subStringIdx.greaterThan(val(0));
                return s.where(cond)
                        .groupBy(organization.ID)
                        .orderBy(subStringIdx, organization.ID)
                        .limit(pageSize)
                        .offset((pageNum - 1) * pageSize)
                        .fetch();

    }

    /**
     * Получение вершины дерева
     * @param root - Организация, находящаяся в вершине
     * @param maxDepth - Текущая глубина поиска
     * @return Вершина дерева
     */
    @Transactional(readOnly = true)
    private TreeNode<Organization> getTree(
            @NotNull
            Organization root,
            Integer maxDepth) {
        if (maxDepth == 0) return new TreeNode<>(root);
        return new TreeNode<>(root, dsl.selectFrom(organization)
                .where(organization.HEAD_ORG_ID.eq(root.getId()))
                .fetchInto(Organization.class)
                .parallelStream()
                .map(organization1 -> getTree(organization1, maxDepth - 1))
                .collect(Collectors.toList()));
    }

    /**
     * Получение вершины дерева
     * @param maxDepth - максимально допустимая глубина
     * @param rootId - идентификатор вершины, с которой происходит обход. Null - обход со всех коренных вершин
     * @return Вершина дерева
     * @throws NoSuchRecordException - в случае отсутствия вершины с идентификатором rootId
     */
    @Transactional(readOnly = true)
    public TreeNode<Organization> getTree(
            int maxDepth,
            Integer rootId) throws NoSuchRecordException {
        assert maxDepth > 0;
        if (rootId == null) return new TreeNode<>(null, dsl.selectFrom(organization)
                    .where(organization.HEAD_ORG_ID.isNull())
                    .fetchInto(Organization.class)
                    .parallelStream()
                    .map(organization1 -> getTree(organization1, maxDepth - 1))
                    .collect(Collectors.toList()));
        return getTree(getById(rootId), maxDepth);
    }

    /**
     * Внесение организации в базу данных
     * @param name - название организации
     * @param org_id - идентификатор головной организации (может быть Null)
     * @return идентификатор созданной организации
     * @throws SameNameException - при попытке создания организации с повторяющимся названием
     * @throws WrongHeadIdException - при попытке указать несущесвующий идентификатор головной организации
     * @throws UnexpectedException - при неожиданной ошибке во время внесения записи в базу данных
     */
    @Transactional
    public Integer insert(String name, Integer org_id) throws
            SameNameException,
            WrongHeadIdException,
            UnexpectedException {
        try {
            return dsl.insertInto(organization)
                    .values(defaultValue(), name, org_id)
                    .returning(organization.ID)
                    .fetchOne()
                    .getValue(organization.ID);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            String message = ex.getMessage();
            if (message != null) {
                if (ex instanceof org.springframework.dao.DuplicateKeyException) {
                    if (message.contains("Key (org_name)")) throw new SameNameException(name);
                }
                if (message.contains("organization_check") ||
                        message.contains("Key (head_org_id)"))
                    throw new WrongHeadIdException(org_id, Table.ORGANIZATION);
            }
            throw new UnexpectedException(ex);
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * Обновление данных организации в базе данных
     * @param id - идентификатор организации
     * @param name - название организации
     * @param org_id - идентификатор головной организации (может быть Null)
     * @return идентификатор организации
     * @throws SameNameException - при попытке изменения название организации на уже существующее
     * @throws WrongHeadIdException - при попытке указать несуществующий идентификатор головной организации или при цикличной зависимости
     * @throws NoSuchRecordException - при отсутствии записи с данным идентификатором
     * @throws UnexpectedException - при неожиданной ошибке во время внесения записи в базу данных
     */
    @Transactional
    public Integer update(Integer id, String name, Integer org_id) throws
            SameNameException,
            WrongHeadIdException,
            NoSuchRecordException,
            UnexpectedException {
        try {
            OrganizationRecord result = dsl.update(organization)
                    .set(organization.ORG_NAME, name)
                    .set(organization.HEAD_ORG_ID, org_id)
                    .where(organization.ID.eq(id))
                    .returning(organization.ID)
                    .fetchOne();
            if (result == null) {
                throw new NoSuchRecordException(id);
            }
            return result.getValue(organization.ID);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            String message = ex.getMessage();
            if (message != null) {
                if (message.contains("(head_org_id)")) {
                    throw new WrongHeadIdException(org_id, Table.ORGANIZATION);
                } else if (message.contains("(org_name)")) {
                    throw new SameNameException(name);
                }
            }
            throw new UnexpectedException(ex);
        } catch (org.springframework.jdbc.UncategorizedSQLException ex) {
            String message = ex.getMessage();
            if (message != null && message.contains("check_org_head")) {
                throw new WrongHeadIdException(org_id, Table.ORGANIZATION);
            }
            throw new UnexpectedException(ex);
        } catch (NoSuchRecordException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * Получение организации по её идентификатору
     * @param id идентификатор организации
     * @return Организация
     * @throws NoSuchRecordException - при отсутствии организации с данным идентификатором
     */
    @Transactional
    public Organization getById(Integer id)
            throws NoSuchRecordException {
        Organization result = dsl.select()
                .from(organization)
                .where(organization.ID.eq(id))
                .fetchOneInto(Organization.class);
        if (result == null) throw new NoSuchRecordException(id);
        else return result;
    }


    /**
     * Удаление организации из базы данных
     * @param id - идентификатор организации
     * @return идентификатор организации
     * @throws NoSuchRecordException - при отсутствии организации с данным идентификатором
     * @throws UnexpectedException - при неожиданной ошибке в ходе удаления организации из базы данных
     * @throws DeleteHasChildException - при наличии зависимых организаций
     */
    @Transactional
    public Integer delete(Integer id) throws NoSuchRecordException, UnexpectedException, DeleteHasChildException {
        try {
            OrganizationRecord result = dsl.deleteFrom(organization)
                    .where(organization.ID.eq(id))
                    .returning()
                    .fetchOne();
            if (result == null) {
                throw new NoSuchRecordException(id);
            } else {
                return result.getId();
            }
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            String message = ex.getMessage();
            if (message == null) throw ex;
            else if (message.contains("is still referenced")) {
                int child_id = Integer.parseInt(message.split("\\)=\\(")[1].split("\\)")[0]);
                if (message.contains("from table \"organization\"")) {
                    throw new DeleteHasChildException(child_id, Table.ORGANIZATION, ex);
                }
                if (message.contains("from table \"worker\"")) {
                    throw new DeleteHasChildException(child_id, Table.WORKER, ex);
                }
                throw new UnexpectedException("Unexpected reference",
                        new DeleteHasChildException(child_id, Table.UNKNOWN, ex));
            }
            throw new UnexpectedException(ex);
        } catch (NoSuchRecordException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

}
