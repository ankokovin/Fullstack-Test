package ankokovin.fullstacktest.WebServer;
import ankokovin.fullstacktest.WebServer.Exceptions.*;

import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
import ankokovin.fullstacktest.WebServer.Models.Table;
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

    @Nested
    class Update {

        int update(Worker expected) throws NoSuchRecordException, WrongHeadIdException, UnexpectedException {
            return workerRepository.update(expected.getId(),
                    expected.getWorkerName(),
                    expected.getOrgId(),
                    expected.getHeadId());
        }

        @Test
        void whenChangeName_updates() throws BaseException {
            Worker expected = create();
            expected.setWorkerName("New name");
            int id = update(expected);
            assertEquals(expected.getId(), id);
            Worker actual = workerRepository.getById(id);
            assertEquals(expected, actual);
        }

        @Test
        void whenChangeOrg_updates() throws  BaseException {
            Worker expected = create();
            expected.setOrgId(organizationRepository.insert("Test2", null));
            int id = update(expected);
            assertEquals(expected.getId(), id);
            Worker actual = workerRepository.getById(id);
            assertEquals(expected, actual);
        }

        @Test
        void whenChangeHead_updates() throws BaseException {
            Worker[] given = create(2);
            Worker expected = given[1];
            expected.setHeadId(given[0].getId());
            int id = update(expected);
            assertEquals(expected.getId(), id);
            Worker actual = workerRepository.getById(id);
            assertEquals(expected, actual);
        }

        @Test
        void whenOrgChangeWrong_throws() throws BaseException {
            Worker expected = create();
            expected.setOrgId(expected.getOrgId()+1);
            WrongHeadIdException ex = assertThrows(WrongHeadIdException.class,
                    () -> update(expected));
            assertEquals(expected.getOrgId(), ex.id);
            assertEquals(Table.ORGANIZATION, ex.to);
        }
        @Test
        void whenHeadChangeWrong_throws() throws BaseException {
            Worker expected = create();
            expected.setHeadId(expected.getId()+1);
            WrongHeadIdException ex = assertThrows(WrongHeadIdException.class,
                    () -> update(expected));
            assertEquals(expected.getHeadId(), ex.id);
            assertEquals(Table.WORKER, ex.to);
        }
        @Test
        void whenHeadChangeLoop_throws() throws BaseException {
            Worker[] given = create(2);
            given[0].setHeadId(given[1].getId());
            update(given[0]);
            given[1].setHeadId(given[0].getId());
            WrongHeadIdException ex = assertThrows(WrongHeadIdException.class,
                    () -> update(given[1]));
            assertEquals(given[1].getOrgId(), ex.id);
            assertEquals(Table.WORKER, ex.to);
        }
        @Test
        void whenLongHeadChangeLoop_throws() throws BaseException {
            Worker[] given = create(3);
            given[0].setHeadId(given[1].getId());
            update(given[0]);
            given[1].setHeadId(given[2].getId());
            update(given[1]);
            given[2].setHeadId(given[0].getId());
            WrongHeadIdException ex = assertThrows(WrongHeadIdException.class,
                    () -> update(given[2]));
            assertEquals(given[2].getOrgId(), ex.id);
            assertEquals(Table.WORKER, ex.to);
        }
        @Test
        void whenNoRecordWithId() throws BaseException {
            Worker given = create();
            given.setId(given.getId()+1);
            NoSuchRecordException ex = assertThrows(NoSuchRecordException.class,
                    () -> update(given));
            assertEquals(given.getId(), ex.id);
        }
    }

    @Nested
    class Delete {
        @Test
        void whenCorrectId_deletes() throws BaseException {
            Worker given = create();
            int id = workerRepository.delete(given.getId());
            assertEquals(given.getId(), id);
        }

        @Test
        void whenWrongId_throws() throws BaseException {
            int id = 42;
            NoSuchRecordException e = assertThrows(NoSuchRecordException.class,
                    () -> workerRepository.delete(id));
            assertEquals(e.id, id);
        }
    }
}
