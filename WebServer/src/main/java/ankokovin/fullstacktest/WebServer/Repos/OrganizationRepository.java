package ankokovin.fullstacktest.WebServer.Repos;
import ankokovin.fullstacktest.WebServer.Exceptions.*;
import ankokovin.fullstacktest.WebServer.Generated.tables.Organization;
import ankokovin.fullstacktest.WebServer.Generated.tables.Worker;
import ankokovin.fullstacktest.WebServer.Generated.tables.records.OrganizationRecord;
import ankokovin.fullstacktest.WebServer.Models.Table;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.defaultValue;

@Repository
public class OrganizationRepository {

    private final Organization organization = Organization.ORGANIZATION;
    private final Worker worker = Worker.WORKER;
    @Autowired
    public DSLContext dsl;

    @Transactional(readOnly = true)
    public List<Record3<Integer, String, Integer>> getAllWithCount(int pageNum, int pageSize, String searchName) {

        SelectJoinStep<Record3<Integer, String, Integer>> s =  dsl.select(organization.ID, organization.ORG_NAME, count(worker.ID))
                .from(organization.leftJoin(worker).on(worker.ORG_ID.eq(organization.ID)));
        SelectConditionStep<Record3<Integer, String, Integer>> cond =
                searchName != null
                ?
                s.where(organization.ORG_NAME.contains(searchName))
                : (SelectConditionStep<Record3<Integer, String, Integer>>) s;
                return cond
                        .groupBy(organization.ID)
                        .orderBy(organization.ID)
                        .limit(pageSize)
                        .offset((pageNum - 1) * pageSize)
                        .fetch();

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
    public ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization getById(Integer id)
            throws NoSuchRecordException {
        ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization result = dsl.select()
                .from(organization)
                .where(organization.ID.eq(id))
                .fetchOneInto(ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization.class);
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
