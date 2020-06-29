package ankokovin.fullstacktest.WebServer.Repos;

import ankokovin.fullstacktest.WebServer.Exceptions.NoSuchRecordException;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
import ankokovin.fullstacktest.WebServer.Exceptions.NotImplementedException;
import org.jooq.DSLContext;
import org.jooq.Record5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WorkerRepository {

    @Autowired
    private DSLContext dsl;

    public Worker create(String name, Integer org_id, Integer head_id) {
        throw new NotImplementedException();
    }

    public Integer update(Integer id, String name, Integer org_id, Integer head_id) {
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

    public Worker getById(Integer id) {
        throw new NotImplementedException();
    }
}
