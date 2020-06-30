package ankokovin.fullstacktest.WebServer.Repos;

import ankokovin.fullstacktest.WebServer.Exceptions.*;
import  ankokovin.fullstacktest.WebServer.Generated.tables.Organization;
import  ankokovin.fullstacktest.WebServer.Generated.tables.Worker;
import ankokovin.fullstacktest.WebServer.Generated.tables.records.OrganizationRecord;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.defaultValue;

@Repository
public class OrganizationRepository {

    @Autowired
    public DSLContext dsl;

    private final Organization organization = Organization.ORGANIZATION;
    private final Worker worker = Worker.WORKER;

    @Transactional(readOnly = true)
    public List<ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization> getAll() {
        return dsl.selectFrom(organization).fetchInto(
                ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization.class
        );
    }

    @Transactional(readOnly = true)
    public Result<Record2<String, Integer>> getAllWithCount() {
        return dsl.select(organization.ORG_NAME, count(worker.ID))
                .from(organization.join(worker).on(worker.ORG_ID.eq(organization.ID)))
                .groupBy(organization.ORG_NAME)
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
        } catch(org.springframework.dao.DataIntegrityViolationException ex){
            String message = ex.getMessage();
            if (message != null) {
                if (ex instanceof org.springframework.dao.DuplicateKeyException) {
                    if (message.contains("Key (org_name)")) throw new SameNameException(name);
                }
                if (message.contains("organization_check") ||
                        message.contains("Key (head_org_id)")) throw new WrongHeadIdException(org_id);
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
                    throw new WrongHeadIdException(org_id);
                } else if (message.contains("(org_name)")) {
                    throw new SameNameException(name);
                }
            }
            throw new UnexpectedException(ex);
        } catch (org.springframework.jdbc.UncategorizedSQLException ex) {
            String message = ex.getMessage();
            if (message != null && message.contains("check_org_head")) {
                throw new WrongHeadIdException(org_id);
            }
            throw new UnexpectedException(ex);
        } catch (NoSuchRecordException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    @Transactional
    public ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization getById(Integer id)
            throws NoSuchRecordException{
        return dsl.select()
                .from(organization)
                .where(organization.ID.eq(id))
                .fetchOneInto(ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization.class);
    }


    @Transactional
    public Integer delete(Integer id) throws NoSuchRecordException {
        throw new NotImplementedException();
    }

}
