package ankokovin.fullstacktest.WebServer.Repos;
import ankokovin.fullstacktest.WebServer.Exceptions.*;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Generated.tables.Worker;
import ankokovin.fullstacktest.WebServer.Generated.tables.records.OrganizationRecord;
import ankokovin.fullstacktest.WebServer.Models.Table;
import ankokovin.fullstacktest.WebServer.Models.TreeNode;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.*;

@Repository
public class OrganizationRepository {

    private final ankokovin.fullstacktest.WebServer.Generated.tables.Organization organization
            = ankokovin.fullstacktest.WebServer.Generated.tables.Organization.ORGANIZATION;
    private final Worker worker = Worker.WORKER;
    @Autowired
    public DSLContext dsl;

    public OrganizationRepository() {}
    public OrganizationRepository(DSLContext dsl){this.dsl = dsl;}

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
    @Transactional(readOnly = true)
    private TreeNode<Organization> getTree(
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
