package ankokovin.fullstacktest.WebServer.Repos;

import ankokovin.fullstacktest.WebServer.Exceptions.*;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
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

    @Autowired
    private DSLContext dsl;

    private final ankokovin.fullstacktest.WebServer.Generated.tables.Worker worker = ankokovin.fullstacktest.WebServer.Generated.tables.Worker.WORKER;

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

    public Integer update(Integer id, String name, Integer org_id, Integer head_id) throws
            SameNameException,
            WrongHeadIdException,
            NoSuchRecordException{
        throw new NotImplementedException();
    }

    public Integer delete(Integer id) throws NoSuchRecordException {
        throw new NotImplementedException();
    }

    public List<Record5<String,Integer,String,Integer,String>> getAll(
            Integer page,
            String org_name,
            String head_name) {
      throw new NotImplementedException();
    }

    public Worker getById(Integer id) throws NoSuchRecordException {
        return dsl.selectFrom(worker).where(worker.ID.eq(id)).fetchOneInto(Worker.class);
    }
}
