package ankokovin.fullstacktest.WebServer;

import ankokovin.fullstacktest.WebServer.Exceptions.*;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Models.Table;
import ankokovin.fullstacktest.WebServer.Models.TreeNode;
import ankokovin.fullstacktest.WebServer.Repos.OrganizationRepository;
import ankokovin.fullstacktest.WebServer.Repos.WorkerRepository;
import ankokovin.fullstacktest.WebServer.TestHelpers.OrganizationHelpers;
import ankokovin.fullstacktest.WebServer.TestHelpers.WorkerHelpers;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Record4;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.*;
import java.util.stream.Collectors;

import static ankokovin.fullstacktest.WebServer.TestHelpers.OrganizationHelpers.setUp;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unused")
@JooqTest
public class OrganizationRepositoryTests {

    private static final ankokovin.fullstacktest.WebServer.Generated.tables.Organization organization
            = ankokovin.fullstacktest.WebServer.Generated.tables.Organization.ORGANIZATION;
    private static final ankokovin.fullstacktest.WebServer.Generated.tables.Worker worker
            = ankokovin.fullstacktest.WebServer.Generated.tables.Worker.WORKER;
    @Autowired
    public OrganizationRepository organizationRepository;

    @Autowired
    public WorkerRepository workerRepository;
    @SuppressWarnings("unused")
    @Autowired
    private DSLContext dslContext;

    @BeforeEach
    public void setup() {
        dslContext.truncateTable(worker).restartIdentity().cascade().execute();
        dslContext.truncateTable(organization).restartIdentity().cascade().execute();
    }



    @SuppressWarnings("unused")
    @TestConfiguration
    static class OrganizationServiceTestsConfiguration {

        @SuppressWarnings("unused")
        @Bean
        public OrganizationRepository organizationRepository() {
            return new OrganizationRepository();
        }

        @Bean
        public WorkerRepository workerRepository() { return new WorkerRepository(); }
    }



    @Nested
    class Insert {
        @Test
        public void whenNoHead_thenOrganizationCreates() throws BaseException {
            OrganizationHelpers.create(organizationRepository, dslContext);
        }

        @Test
        public void whenHead_thenOrganizationCreates() throws BaseException {
            Organization given = OrganizationHelpers.create(organizationRepository, dslContext);
            String name = "ООО Тест2";
            Organization expected = new Organization(2, name, 1);
            int id = organizationRepository.insert(name, 1);
            assertEquals(2, id);

            Object[] actual = dslContext
                    .selectFrom(organization)
                    .fetchInto(Organization.class).toArray();
            assertArrayEquals(new Organization[]{given, expected}, actual);
        }

        @Test
        public void whenFirstWithHead_thenThrows() {
            WrongHeadIdException e = assertThrows(WrongHeadIdException.class,
                    () -> organizationRepository.insert("OOO Тест", 3));
            assertEquals(3, e.id);
        }

        @Test
        public void whenWrongHead_thenThrows() throws BaseException {
            Organization given = OrganizationHelpers.create(organizationRepository, dslContext);
            WrongHeadIdException e = assertThrows(WrongHeadIdException.class,
                    () -> organizationRepository.insert("OOO Тест2", given.getId() + 2));
            assertEquals(given.getId() + 2, e.id);
        }

        @Test
        public void whenSelfHead_thenThrows() {
            WrongHeadIdException e = assertThrows(WrongHeadIdException.class,
                    () -> organizationRepository.insert("OOO Тест", 1));
            assertEquals(1, e.id);
        }

        @Test
        public void whenSameName_thenThrows() throws BaseException {
            Organization given = OrganizationHelpers.create(organizationRepository, dslContext);

            SameNameException e = assertThrows(SameNameException.class,
                    () -> organizationRepository.insert(given.getOrgName(), null));
            assertEquals(given.getOrgName(), e.name);
        }
    }

    @Nested
    class Update {
        @Test
        public void whenChangeName_thenOrganizationUpdates() throws BaseException {
            Organization given = OrganizationHelpers.create(organizationRepository, dslContext);

            String name = "ООО Тест Изменено";
            Organization expected = new Organization(1, name, null);

            int id = organizationRepository.update(given.getId(), name, null);
            assertEquals(1, id);

            Object[] actual = dslContext
                    .selectFrom(organization)
                    .fetchInto(Organization.class).toArray();
            assertArrayEquals(new Organization[]{expected}, actual);
        }

        @Test
        public void whenChangeHead_thenOrganizationUpdates() throws BaseException {
            Organization[] given = OrganizationHelpers.create(2, organizationRepository, dslContext);

            Organization expected = new Organization(given[1].getId(), given[1].getOrgName(), given[0].getId());
            int id = organizationRepository.update(given[1].getId(), given[1].getOrgName(), given[0].getId());
            assertEquals(2, id);

            Object[] actual = dslContext
                    .selectFrom(organization)
                    .fetchInto(Organization.class).toArray();
            assertArrayEquals(new Organization[]{given[0], expected}, actual);
        }

        @Test
        public void whenChangeHeadSelf_thenThrows() throws BaseException {
            Organization given = OrganizationHelpers.create(organizationRepository, dslContext);

            WrongHeadIdException e = assertThrows(WrongHeadIdException.class,
                    () -> organizationRepository.update(given.getId(), given.getOrgName(), given.getId()));
            assertEquals(given.getId(), e.id);
        }

        @Test
        public void whenWrongHead_thenThrows() throws BaseException {
            Organization given = OrganizationHelpers.create(organizationRepository, dslContext);

            Integer new_head_id = given.getId() + 1;
            WrongHeadIdException e = assertThrows(WrongHeadIdException.class,
                    () -> organizationRepository.update(given.getId(), given.getOrgName(), new_head_id));
            assertEquals(new_head_id, e.id);
        }

        @Test
        public void whenSameName_thenThrows() throws BaseException {
            Organization[] given = OrganizationHelpers.create(2, organizationRepository, dslContext);

            SameNameException e = assertThrows(SameNameException.class,
                    () -> organizationRepository.update(
                            given[1].getId(),
                            given[0].getOrgName(),
                            given[1].getHeadOrgId()));
            assertEquals(given[0].getOrgName(), e.name);
        }

        @Test
        public void whenIdNotPresent_thenThrows() {
            NoSuchRecordException e = assertThrows(NoSuchRecordException.class,
                    () -> organizationRepository.update(1, "test", null));
            assertEquals(e.id, 1);
        }
    }

    @Nested
    class Delete {
        @Test
        public void whenCorrectId_thenDeletes() throws BaseException {
            Organization given = OrganizationHelpers.create(organizationRepository, dslContext);

            int id = organizationRepository.delete(given.getId());
            assertEquals(given.getId(), id);
            assertEquals(0, dslContext.selectCount().from(organization).fetchOneInto(Integer.class));
        }

        @Test
        public void whenIncorrectId_thenThrows() {
            NoSuchRecordException e = assertThrows(NoSuchRecordException.class,
                    () -> organizationRepository.delete(1));
            assertEquals(e.id, 1);
        }

        @Test
        public void whenHasWorkers_thenThrows() throws BaseException {
            Organization expected = OrganizationHelpers.create(organizationRepository, dslContext);

            dslContext.insertInto(worker).values(42, "Test", expected.getId(), null).execute();
            DeleteHasChildException e = assertThrows(DeleteHasChildException.class,
                    () -> organizationRepository.delete(expected.getId()));
            assertEquals(Table.WORKER, e.table);
            assertEquals(expected.getId(), e.id);
        }

        @Test
        public void whenHasSubOrganizations_thenThrows() throws BaseException {
            Organization[] given = OrganizationHelpers.create(2, organizationRepository, dslContext);

            Organization expected = new Organization(given[1].getId(), given[1].getOrgName(), given[0].getId());
            int id = organizationRepository.update(given[1].getId(), given[1].getOrgName(), given[0].getId());
            assertEquals(2, id);

            Object[] actual = dslContext
                    .selectFrom(organization)
                    .fetchInto(Organization.class).toArray();
            assertArrayEquals(new Organization[]{given[0], expected}, actual);
            dslContext.insertInto(worker).values(42, "Test", expected.getId(), null).execute();
            DeleteHasChildException e = assertThrows(DeleteHasChildException.class,
                    () -> organizationRepository.delete(given[0].getId()));
            assertEquals(Table.ORGANIZATION, e.table);
            assertEquals(given[0].getId(), e.id);
        }

        @Test
        public void whenHasWorkersAndSubOrganizations_thenThrows() throws BaseException {
            Organization[] given = OrganizationHelpers.create(2, organizationRepository, dslContext);

            Organization expected = new Organization(given[1].getId(), given[1].getOrgName(), given[0].getId());
            int id = organizationRepository.update(given[1].getId(), given[1].getOrgName(), given[0].getId());
            assertEquals(2, id);

            Object[] actual = dslContext
                    .selectFrom(organization)
                    .fetchInto(Organization.class).toArray();
            assertArrayEquals(new Organization[]{given[0], expected}, actual);
            dslContext.insertInto(worker).values(42, "Test", expected.getId(), null).execute();

            DeleteHasChildException e = assertThrows(DeleteHasChildException.class,
                    () -> organizationRepository.delete(given[0].getId()));
            assertEquals(Table.ORGANIZATION, e.table);
            assertEquals(given[0].getId(), e.id);
        }
    }

    @Nested
    class Get {
        @Nested
        class GetById {
            @Test
            public void whenIdInTable_thenReturn() throws BaseException {
                Organization expected = OrganizationHelpers.create(organizationRepository, dslContext);

                Organization actual = organizationRepository.getById(expected.getId());
                assertEquals(expected.getId(), actual.getId());
            }

            @Test
            public void whenIdNotInTable_thenThrows() throws BaseException {
                Organization expected = OrganizationHelpers.create(organizationRepository, dslContext);

                NoSuchRecordException e = assertThrows(NoSuchRecordException.class,
                        () -> organizationRepository.getById(expected.getId() + 1));
            }
        }

        @Nested
        class GetAllPagedSearch {
            @Test
            void whenAskedAll_thenReturns() throws BaseException {
                int pageSize = 42;
                Organization[] expected = OrganizationHelpers.create(pageSize, organizationRepository, dslContext);
                List<Record4<Integer, String, Integer, Integer>> actual
                        = organizationRepository.getAllWithCount(1, pageSize, null);
                assertEquals(pageSize, actual.size());
                for (int i = 0; i < pageSize; i++) {
                    assertEquals(expected[i].getId(), actual.get(i).component1());
                    assertEquals(expected[i].getOrgName(), actual.get(i).component2());
                    assertEquals(0, actual.get(i).component3());
                }
            }
            @Test
            void whenAskedPage_thenReturnsPage() throws BaseException {
                int pageSize = 42;
                OrganizationHelpers.create(pageSize, organizationRepository, dslContext);
                Organization[] expected =
                        OrganizationHelpers.create(pageSize, pageSize, organizationRepository, dslContext);
                List<Record4<Integer, String, Integer, Integer>> actual
                        = organizationRepository.getAllWithCount(2, pageSize, null);
                assertEquals(pageSize, actual.size());
                for (int i = 0; i < pageSize; i++) {
                    assertEquals(expected[i].getId(), actual.get(i).component1());
                    assertEquals(expected[i].getOrgName(), actual.get(i).component2());
                    assertEquals(0, actual.get(i).component3());
                }
            }
            @Test
            void whenAskedUnevenPage_thenReturnsPage() throws BaseException {
                int expectedPageSize = 42;
                int pageSize = 100;
                int pageNum = 2;
                Organization[] expected = OrganizationHelpers.create(pageSize, organizationRepository, dslContext);
                List<Record4<Integer, String, Integer, Integer>> actual
                        = organizationRepository.getAllWithCount(pageNum , expectedPageSize, null);
                assertEquals(expectedPageSize, actual.size());
                for (int i = 0; i < expectedPageSize; i++) {
                    assertEquals(expected[(pageNum-1)*expectedPageSize + i].getId(), actual.get(i).component1());
                    assertEquals(expected[(pageNum-1)*expectedPageSize + i].getOrgName(), actual.get(i).component2());
                    assertEquals(0, actual.get(i).component3());
                }
            }

            @Test
            void whenAskedUnevenPage2_thenReturnsPage() throws BaseException {
                int expectedPageSize = 42;
                int pageSize = 100;
                int pageNum = 1;
                Organization[] expected
                        = OrganizationHelpers.create(expectedPageSize, organizationRepository, dslContext);
                List<Record4<Integer, String, Integer, Integer>> actual
                        = organizationRepository.getAllWithCount(pageNum , pageSize , null);
                assertEquals(expectedPageSize, actual.size());
                for (int i = 0; i < expectedPageSize; i++) {
                    assertEquals(expected[(pageNum-1)*expectedPageSize + i].getId(), actual.get(i).component1());
                    assertEquals(expected[(pageNum-1)*expectedPageSize + i].getOrgName(), actual.get(i).component2());
                    assertEquals(0, actual.get(i).component3());
                }
            }
            @Test
            void whenAskedSearch_thenReturnsPage() throws BaseException {
                int offset = 42;
                int pageSize = 100;
                int pageNum = 1;
                OrganizationHelpers.create(offset, organizationRepository, dslContext);
                String searchTerm = "me!";
                String filter = "Test " + searchTerm + "%d";
                List<Organization> expected = new LinkedList<>();
                for (int i = 0; i < 5; i++) {
                    int c = 32;
                    if (i%2 == 0) {
                        expected.addAll(Arrays.asList(OrganizationHelpers
                                .create(c, offset, filter, organizationRepository, dslContext)));
                    } else {
                        OrganizationHelpers.create(c, offset, organizationRepository, dslContext);
                    }
                    offset +=c ;
                }
                List<Record4<Integer, String, Integer, Integer>> actual
                        = organizationRepository.getAllWithCount(pageNum , pageSize , searchTerm);
                int expectedPageSize = Math.min(pageSize, expected.size());
                for (int i = 0; i < expectedPageSize; i++) {
                    assertEquals(expected.get((pageNum-1)*expectedPageSize + i).getId(), actual.get(i).component1());
                    assertEquals(expected.get((pageNum-1)*expectedPageSize + i).getOrgName(), actual.get(i).component2());
                    assertEquals(0, actual.get(i).component3());
                }
            }
            @Test
            void whenAsked_thenReturnsWithWorkerCounts() throws BaseException {
                int pageSize = 25;
                Organization[] expected = OrganizationHelpers.create(pageSize, organizationRepository, dslContext);
                int offsetWorker = 0;
                for (int i = 0; i < pageSize; i++) {
                    WorkerHelpers.insert(i, expected[i].getId(),offsetWorker, workerRepository);
                    offsetWorker += i;
                }
                List<Record4<Integer, String, Integer, Integer>> actual
                        = organizationRepository.getAllWithCount(1,pageSize,null);

                assertEquals(pageSize, actual.size());
                for (int i = 0; i < pageSize; i++) {
                    assertEquals(expected[i].getId(), actual.get(i).component1());
                    assertEquals(expected[i].getOrgName(), actual.get(i).component2());
                    assertEquals(i, actual.get(i).component3());
                }
            }
            @Test
            void whenAskedSearch_hasExact_thenExactReturnedFirst() throws BaseException {
                String target = "Hello";
                String noise = "Noise "+target+"%d";
                int startCnt = 10;
                Organization[] given = OrganizationHelpers.create(startCnt,0,
                        noise, organizationRepository, dslContext);
                Organization exact_match_target = OrganizationHelpers.create(1,startCnt,target,organizationRepository,
                        dslContext)[0];
                int endCnt = 10;
                int pageSize = startCnt + endCnt + 1;
                given = ArrayUtils.addAll(given, OrganizationHelpers.create(endCnt, startCnt+1, noise,
                        organizationRepository, dslContext));
                Organization[] expected =  ArrayUtils.addAll(new Organization[]{exact_match_target}, given);
                List<Record4<Integer, String, Integer, Integer>> actual = organizationRepository.getAllWithCount(1,
                        pageSize, target);
                assertEquals(pageSize, actual.size());
                for (int i = 0; i < pageSize; i++) {
                    assertEquals(expected[i].getId(), actual.get(i).component1());
                    assertEquals(expected[i].getOrgName(), actual.get(i).component2());
                    assertEquals(0, actual.get(i).component3());
                    assertEquals(i == 0 ? 1 : 7, actual.get(i).component4());
                }
            }
        }

        @Nested
        class GetTree {


            @Test
            void returnsTree() throws NoSuchRecordException {
                TreeNode<Organization> expected = setUp(dslContext);
                TreeNode<Organization> actual = organizationRepository.getTree(5, null);
                assertEquals(expected, actual);
            }
            @Test
            void depth_limit() throws NoSuchRecordException {
                TreeNode<Organization> given = setUp(dslContext);
                TreeNode<Organization> expected = new TreeNode<>(given.item,
                            given.children.stream()
                                    .map(x->new TreeNode<>(x.item))
                                    .collect(Collectors.toList())
                        );
                TreeNode<Organization> actual = organizationRepository.getTree(1, null);
                assertEquals(expected, actual);
            }
            @Test
            void custom_head() throws NoSuchRecordException {
                TreeNode<Organization> given = setUp(dslContext);
                TreeNode<Organization> expected = given.children.get(1);
                TreeNode<Organization> actual = organizationRepository.getTree(5, expected.item.getId());
                assertEquals(expected, actual);
            }

            @Test
            void throws_noSuchRecord() {
                TreeNode<Organization> given = setUp(dslContext);
                Integer expected = 42;
                NoSuchRecordException actual = assertThrows(NoSuchRecordException.class,
                        () -> organizationRepository.getTree(1, 42));
                assertEquals(expected, actual.id);
            }
        }
    }
}
