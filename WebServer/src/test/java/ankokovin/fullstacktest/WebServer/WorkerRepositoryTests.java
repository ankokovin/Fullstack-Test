package ankokovin.fullstacktest.WebServer;
import ankokovin.fullstacktest.WebServer.Exceptions.BaseException;
import ankokovin.fullstacktest.WebServer.Exceptions.SameNameException;
import ankokovin.fullstacktest.WebServer.Exceptions.WrongHeadIdException;

import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
import ankokovin.fullstacktest.WebServer.Repos.OrganizationRepository;
import ankokovin.fullstacktest.WebServer.Repos.WorkerRepository;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import static org.junit.jupiter.api.Assertions.*;

@JooqTest
public class WorkerRepositoryTests {
    private static final ankokovin.fullstacktest.WebServer.Generated.tables.Organization organization
            = ankokovin.fullstacktest.WebServer.Generated.tables.Organization.ORGANIZATION;
    private static final ankokovin.fullstacktest.WebServer.Generated.tables.Worker worker
            = ankokovin.fullstacktest.WebServer.Generated.tables.Worker.WORKER;
    @TestConfiguration
    static class WorkerServiceTestsConfiguration {

        @SuppressWarnings("unused")
        @Bean
        public WorkerRepository workerRepository() {
            return new WorkerRepository();
        }

        @Bean
        public OrganizationRepository organizationRepository() { return new OrganizationRepository(); }
    }

    @Autowired
    private DSLContext dslContext;

    @Autowired
    public WorkerRepository workerRepository;

    @Autowired
    public OrganizationRepository organizationRepository;

    @BeforeEach
    public void setup(){
        dslContext.truncateTable(worker).restartIdentity().cascade().execute();
        dslContext.truncateTable(organization).restartIdentity().cascade().execute();
    }

    Worker[] create(int n) throws BaseException {
        assert n > 0;
        int org_id = organizationRepository.insert("Test", null);
        String nameTemplate = "Тест Тестовый Тестович %d";
        Worker[] expected = new Worker[n];
        Worker[] actual = new Worker[n];
        for (int i=0; i<n; ++i) {
            String name = String.format(nameTemplate, i);
            expected[i] = new Worker(i+1, name, org_id, null);
            actual[i] = workerRepository.getById(workerRepository.insert(name,org_id,null));
        }
        assertArrayEquals(expected, actual);
        return actual;
    }

    Worker create() throws BaseException {
        return create(1)[0];
    }

    @Nested
    class Insert {
        @Test
        void whenNoHead_creates() throws BaseException {
            create();
        }

        @Test
        void whenHead_creates() throws BaseException {
            Worker given = create();
            Integer actual = workerRepository.insert(
                    "test",
                    given.getOrgId(),
                    given.getId());
            assertEquals(given.getId()+1, actual);
        }



        @Test
        void whenWrongHeadWorker_throws() throws BaseException {
            Worker given = create();
            WrongHeadIdException ex = assertThrows(WrongHeadIdException.class,
                    () -> workerRepository.insert(
                            "test",
                            given.getOrgId(),
                            given.getId()+1
                    ));
            assertEquals(given.getId()+1, ex.id);
        }



        @Test
        void whenWrongOrg_throws() throws BaseException {
            Worker given = create();
            WrongHeadIdException ex = assertThrows(WrongHeadIdException.class,
                    () -> workerRepository.insert(
                            "test",
                            given.getOrgId()+1,
                            null
                    ));
            assertEquals(given.getId()+1, ex.id);
        }

    }
}
