package ankokovin.fullstacktest.WebServer.Repos;

import ankokovin.fullstacktest.WebServer.Exceptions.NoSuchRecordException;
import ankokovin.fullstacktest.WebServer.Exceptions.NotImplementedException;
import ankokovin.fullstacktest.WebServer.Exceptions.UnexpectedException;
import ankokovin.fullstacktest.WebServer.Exceptions.WrongHeadIdException;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
import ankokovin.fullstacktest.WebServer.Generated.tables.records.WorkerRecord;
import ankokovin.fullstacktest.WebServer.Models.Table;
import org.jooq.DSLContext;
import org.jooq.Record5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.jooq.impl.DSL.defaultValue;

@Repository
public class WorkerRepository {

    private final ankokovin.fullstacktest.WebServer.Generated.tables.Worker worker = ankokovin.fullstacktest.WebServer.Generated.tables.Worker.WORKER;
    @Autowired
    private DSLContext dsl;

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
                throw new WrongHeadIdException(head_id, Table.WORKER);
            throw new UnexpectedException(ex);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            String message = ex.getMessage();
            if (message == null) throw ex;
            if (message.contains("Key (org_id)"))
                throw new WrongHeadIdException(org_id, Table.ORGANIZATION);
            throw new UnexpectedException(ex);
        }
    }

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
                throw new WrongHeadIdException(head_id, Table.WORKER);
            }
            throw ex;
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            String message = ex.getMessage();
            if (message == null) throw new UnexpectedException(ex);
            if (message.contains("Key (org_id)")) {
                throw new WrongHeadIdException(org_id, Table.ORGANIZATION);
            } else if (message.contains(" Key (head_id)")) {
                throw new WrongHeadIdException(head_id, Table.WORKER);
            }
            throw new UnexpectedException(ex);
        } catch (NoSuchRecordException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    @Transactional
    public Integer delete(Integer id) throws NoSuchRecordException {
        WorkerRecord result = dsl.deleteFrom(worker)
                .where(worker.ID.eq(id))
                .returning(worker.ID)
                .fetchOne();
        if (result == null) throw new NoSuchRecordException(id);
        return result.getValue(worker.ID);
    }

    public List<Record5<String, Integer, String, Integer, String>> getAll(
            Integer page,
            String org_name,
            String head_name) {
        throw new NotImplementedException();
    }

    @Transactional
    public Worker getById(Integer id) throws NoSuchRecordException {
        Worker result = dsl.selectFrom(worker).where(worker.ID.eq(id)).fetchOneInto(Worker.class);
        if (result == null) throw new NoSuchRecordException(id);
        return result;
    }
}
