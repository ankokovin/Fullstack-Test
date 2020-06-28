package ankokovin.fullstacktest.WebServer.Repos;

import  ankokovin.fullstacktest.WebServer.Generated.tables.Organization;
import  ankokovin.fullstacktest.WebServer.Generated.tables.Worker;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.jooq.impl.DSL.count;

@Repository
public class OrganizationRepository {

    @Autowired
    private DSLContext dsl;

    private final Organization organization = Organization.ORGANIZATION;
    private final Worker worker = Worker.WORKER;

    @Transactional(readOnly = true)
    public List<ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization> getAll() {
        return dsl.selectFrom(organization).fetchInto(
                ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization.class
        );
    }

    public Result<Record2<String, Integer>> getAllWithCount() {
        return dsl.select(organization.ORG_NAME, count(worker.ID))
                .from(organization.join(worker).on(worker.ORG_ID.eq(organization.ID)))
                .groupBy(organization.ORG_NAME)
                .fetch();

    }

    @Transactional
    public Integer insert(String name, Integer org_id) {
        return dsl.insertInto(organization)
                .values(name, org_id)
                .returning(organization.ID)
                .fetchOne()
                .getValue(organization.ID);
    }

    @Transactional
    public ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization getById(Integer id) {
        return dsl.select()
                .from(organization)
                .where(organization.ID.eq(id))
                .fetchOneInto(ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization.class);
    }
}
