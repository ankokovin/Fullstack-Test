package ankokovin.fullstacktest.WebServer;

import ankokovin.fullstacktest.WebServer.Exceptions.BaseException;
import ankokovin.fullstacktest.WebServer.Exceptions.NoSuchRecordException;
import ankokovin.fullstacktest.WebServer.Exceptions.UnexpectedException;
import ankokovin.fullstacktest.WebServer.Exceptions.WrongHeadIdException;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
import ankokovin.fullstacktest.WebServer.Models.Table;
import ankokovin.fullstacktest.WebServer.Models.TreeNode;
import ankokovin.fullstacktest.WebServer.Models.WorkerListElement;
import ankokovin.fullstacktest.WebServer.Models.WorkerTreeListElement;
import ankokovin.fullstacktest.WebServer.Repos.OrganizationRepository;
import ankokovin.fullstacktest.WebServer.Repos.WorkerRepository;
import ankokovin.fullstacktest.WebServer.TestHelpers.WorkerHelpers;
import org.jooq.DSLContext;
import org.jooq.Record6;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.stream.Collectors;

import static ankokovin.fullstacktest.WebServer.TestHelpers.WorkerHelpers.setUp;
import static org.junit.jupiter.api.Assertions.*;

@JooqTest
public class WorkerRepositoryTests {
    private static final ankokovin.fullstacktest.WebServer.Generated.tables.Organization organization
            = ankokovin.fullstacktest.WebServer.Generated.tables.Organization.ORGANIZATION;
    private static final ankokovin.fullstacktest.WebServer.Generated.tables.Worker worker
            = ankokovin.fullstacktest.WebServer.Generated.tables.Worker.WORKER;
    @Autowired
    public WorkerRepository workerRepository;
    @Autowired
    public OrganizationRepository organizationRepository;
    @Autowired
    private DSLContext dslContext;

    @BeforeEach
    public void setup() {
        dslContext.truncateTable(worker).restartIdentity().cascade().execute();
        dslContext.truncateTable(organization).restartIdentity().cascade().execute();
    }



    @TestConfiguration
    static class WorkerServiceTestsConfiguration {

        @SuppressWarnings("unused")
        @Bean
        public WorkerRepository workerRepository() {
            return new WorkerRepository();
        }

        @Bean
        public OrganizationRepository organizationRepository() {
            return new OrganizationRepository();
        }
    }

    @Nested
    class Insert {
        @Test
        void whenNoHead_creates() throws BaseException {
            WorkerHelpers.insert(workerRepository, organizationRepository);
        }

        @Test
        void whenHead_creates() throws BaseException {
            Worker given = WorkerHelpers.insert(workerRepository, organizationRepository);
            Integer actual = workerRepository.insert(
                    "test",
                    given.getOrgId(),
                    given.getId());
            assertEquals(given.getId() + 1, actual);
        }


        @Test
        void whenWrongHeadWorker_throws() throws BaseException {
            Worker given = WorkerHelpers.insert(workerRepository, organizationRepository);
            WrongHeadIdException ex = assertThrows(WrongHeadIdException.class,
                    () -> workerRepository.insert(
                            "test",
                            given.getOrgId(),
                            given.getId() + 1
                    ));
            assertEquals(given.getId() + 1, ex.id);
        }


        @Test
        void whenWrongOrg_throws() throws BaseException {
            Worker given = WorkerHelpers.insert(workerRepository, organizationRepository);
            WrongHeadIdException ex = assertThrows(WrongHeadIdException.class,
                    () -> workerRepository.insert(
                            "test",
                            given.getOrgId() + 1,
                            null
                    ));
            assertEquals(given.getId() + 1, ex.id);
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
            Worker expected = WorkerHelpers.insert(workerRepository, organizationRepository);
            expected.setWorkerName("New name");
            int id = update(expected);
            assertEquals(expected.getId(), id);
            Worker actual = workerRepository.getById(id);
            assertEquals(expected, actual);
        }

        @Test
        void whenChangeOrg_updates() throws BaseException {
            Worker expected = WorkerHelpers.insert(workerRepository, organizationRepository);
            expected.setOrgId(organizationRepository.insert("Test2", null));
            int id = update(expected);
            assertEquals(expected.getId(), id);
            Worker actual = workerRepository.getById(id);
            assertEquals(expected, actual);
        }

        @Test
        void whenChangeHead_updates() throws BaseException {
            Worker[] given = WorkerHelpers.insert(2, workerRepository, organizationRepository);
            Worker expected = given[1];
            expected.setHeadId(given[0].getId());
            int id = update(expected);
            assertEquals(expected.getId(), id);
            Worker actual = workerRepository.getById(id);
            assertEquals(expected, actual);
        }

        @Test
        void whenOrgChangeWrong_throws() throws BaseException {
            Worker expected = WorkerHelpers.insert(workerRepository, organizationRepository);
            expected.setOrgId(expected.getOrgId() + 1);
            WrongHeadIdException ex = assertThrows(WrongHeadIdException.class,
                    () -> update(expected));
            assertEquals(expected.getOrgId(), ex.id);
            assertEquals(Table.ORGANIZATION, ex.to);
        }

        @Test
        void whenHeadChangeWrong_throws() throws BaseException {
            Worker expected = WorkerHelpers.insert(workerRepository, organizationRepository);
            expected.setHeadId(expected.getId() + 1);
            WrongHeadIdException ex = assertThrows(WrongHeadIdException.class,
                    () -> update(expected));
            assertEquals(expected.getHeadId(), ex.id);
            assertEquals(Table.WORKER, ex.to);
        }

        @Test
        void whenHeadChangeLoop_throws() throws BaseException {
            Worker[] given = WorkerHelpers.insert(2, workerRepository, organizationRepository);
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
            Worker[] given = WorkerHelpers.insert(3, workerRepository, organizationRepository);
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
            Worker given = WorkerHelpers.insert(workerRepository, organizationRepository);
            given.setId(given.getId() + 1);
            NoSuchRecordException ex = assertThrows(NoSuchRecordException.class,
                    () -> update(given));
            assertEquals(given.getId(), ex.id);
        }
    }

    @Nested
    class Delete {
        @Test
        void whenCorrectId_deletes() throws BaseException {
            Worker given = WorkerHelpers.insert(workerRepository, organizationRepository);
            int id = workerRepository.delete(given.getId());
            assertEquals(given.getId(), id);
        }

        @Test
        void whenWrongId_throws() {
            int id = 42;
            NoSuchRecordException e = assertThrows(NoSuchRecordException.class,
                    () -> workerRepository.delete(id));
            assertEquals(id, e.id);
        }
    }

    @Nested
    class Get {
        @Nested
        class GetById {
            @Test
            void whenWrongId_throws() {
                int id = 42;
                NoSuchRecordException e = assertThrows(NoSuchRecordException.class,
                        () -> workerRepository.getById(id));
                assertEquals(id, e.id);
            }
        }
        @Nested
        class GetAll {
            @Test
            void whenAskedAll_returns() throws BaseException {
                int cnt = 10;
                Worker[] expected = WorkerHelpers.insert(cnt, workerRepository, organizationRepository);
                List<Record6<Integer, String, Integer, String, Integer, String>> actual
                        = workerRepository.getAll(1, cnt, null, null);
                assertEquals(cnt, actual.size());
                for (int i = 0; i < cnt; i++) {
                    assertEquals(expected[i].getId(), actual.get(i).component1());
                    assertEquals(expected[i].getWorkerName(), actual.get(i).component2());
                    assertNull(actual.get(i).component3());
                    assertNull(actual.get(i).component4());
                    assertEquals(1, actual.get(i).component5());
                    assertEquals("Test", actual.get(i).component6());
                }
            }
            @Test
            void whenSearchOrg_thenReturns() throws BaseException {
                int cnt = 32;
                WorkerHelpers.insert(cnt, workerRepository, organizationRepository);
                int expected_cnt = 14;
                String orgName = "Find me";
                Worker[] expected = WorkerHelpers.insert(expected_cnt, cnt,
                        orgName,organizationRepository,"WorkerName%d", workerRepository);
                List<Record6<Integer, String, Integer, String, Integer, String>> actual
                        = workerRepository.getAll(1, expected_cnt+5,orgName.substring(1,6),null);
                assertEquals(expected_cnt, actual.size());
                for (int i = 0; i < expected_cnt; i++) {
                    assertEquals(expected[i].getId(), actual.get(i).component1());
                    assertEquals(expected[i].getWorkerName(), actual.get(i).component2());
                    assertNull(actual.get(i).component3());
                    assertNull(actual.get(i).component4());
                    assertEquals(2, actual.get(i).component5());
                    assertEquals(orgName, actual.get(i).component6());
                }
            }
            @Test
            void whenSearchWorker_thenReturns() throws BaseException {
                int cnt = 32;
                WorkerHelpers.insert(cnt, workerRepository, organizationRepository);
                int expected_cnt = 14;
                String workerNameTemplate = "Find me%d";
                Worker[] expected = WorkerHelpers.insert(expected_cnt, 1, cnt,workerNameTemplate, workerRepository);
                List<Record6<Integer, String, Integer, String, Integer, String>> actual
                        = workerRepository.getAll(1, expected_cnt+5,
                        null,workerNameTemplate.substring(1,6));
                assertEquals(expected_cnt, actual.size());
                for (int i = 0; i < expected_cnt; i++) {
                    assertEquals(expected[i].getId(), actual.get(i).component1());
                    assertEquals(expected[i].getWorkerName(), actual.get(i).component2());
                    assertNull(actual.get(i).component3());
                    assertNull(actual.get(i).component4());
                    assertEquals(1, actual.get(i).component5());
                    assertEquals("Test", actual.get(i).component6());
                }
            }
        }
        @Nested
        class GetTree{
            @Test
            void returns() throws BaseException {
                TreeNode<WorkerTreeListElement> expected = setUp(organizationRepository, dslContext);
                TreeNode<WorkerTreeListElement> actual = workerRepository.getTree(5,null);
                assertEquals(expected, actual);
            }

            @Test
            void depth_limit() throws BaseException {
                TreeNode<WorkerTreeListElement> given = setUp(organizationRepository, dslContext);
                TreeNode<WorkerTreeListElement> expected = new TreeNode<>(given.item,
                        given.children.stream().map(x->new TreeNode<>(x.item)).collect(Collectors.toList()));
                TreeNode<WorkerTreeListElement> actual = workerRepository.getTree(1,null);
                assertEquals(expected, actual);
            }
            @Test
            void custom_head() throws BaseException {
                TreeNode<WorkerTreeListElement> given = setUp(organizationRepository, dslContext);
                TreeNode<WorkerTreeListElement> expected = given.children.get(2);
                TreeNode<WorkerTreeListElement> actual = workerRepository.getTree(5, expected.item.id);
                assertEquals(expected, actual);
            }
        }
    }
}
