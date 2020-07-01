package ankokovin.fullstacktest.WebServer.Repos;

import ankokovin.fullstacktest.WebServer.Exceptions.NoSuchRecordException;
import ankokovin.fullstacktest.WebServer.Exceptions.NotImplementedException;
import ankokovin.fullstacktest.WebServer.Exceptions.UnexpectedException;
import ankokovin.fullstacktest.WebServer.Exceptions.WrongHeadIdException;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
import ankokovin.fullstacktest.WebServer.Generated.tables.records.WorkerRecord;
import ankokovin.fullstacktest.WebServer.Models.Table;
import ankokovin.fullstacktest.WebServer.Models.TreeNode;
import ankokovin.fullstacktest.WebServer.Models.WorkerTreeListElement;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.jooq.impl.DSL.*;

@Repository
public class WorkerRepository {

    private final ankokovin.fullstacktest.WebServer.Generated.tables.Worker worker
            = ankokovin.fullstacktest.WebServer.Generated.tables.Worker.WORKER;
    private final ankokovin.fullstacktest.WebServer.Generated.tables.Organization organization
            = ankokovin.fullstacktest.WebServer.Generated.tables.Organization.ORGANIZATION;
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

    @Transactional
    public List<Record6<Integer, String, Integer, String, Integer, String>> getAll(
            Integer pageNum,
            Integer pageSize,
            String org_name,
            String worker_name) {
        ankokovin.fullstacktest.WebServer.Generated.tables.Worker headWorker = worker.as("headWorker");
        SelectOnConditionStep<Record6<Integer, String, Integer, String, Integer, String>> preCond = dsl.select(worker.ID,
                worker.WORKER_NAME,
                headWorker.ID,
                headWorker.WORKER_NAME,
                organization.ID,
                organization.ORG_NAME
                )
                .from(worker)
                .leftJoin(organization)
                .on(worker.ORG_ID.eq(organization.ID))
                .leftJoin(headWorker)
                .on(worker.HEAD_ID.eq(headWorker.ID));
        Condition condition = DSL.falseCondition();
        SelectConditionStep<Record6<Integer, String, Integer, String, Integer, String>> postCond;
        if (org_name != null) condition = condition.or(lower(organization.ORG_NAME).contains(lower(org_name)));
        if (worker_name != null) condition = condition.or(lower(worker.WORKER_NAME).contains(lower(worker_name)));
        if (worker_name != null || org_name != null) {
            postCond = preCond.where(condition);
        } else {
            postCond = preCond.where(trueCondition());
        }
        return postCond
                .orderBy(worker.ID)
                .limit(pageSize)
                .offset((pageNum-1)*pageSize)
                .fetch();
    }
    @Transactional(readOnly = true)
    public TreeNode<WorkerTreeListElement> getTree(int maxDepth, Integer root_id) {
        throw new NotImplementedException();
    }

    @Transactional
    public Worker getById(Integer id) throws NoSuchRecordException {
        Worker result = dsl.selectFrom(worker).where(worker.ID.eq(id)).fetchOneInto(Worker.class);
        if (result == null) throw new NoSuchRecordException(id);
        return result;
    }
}
