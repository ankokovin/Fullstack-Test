package ankokovin.fullstacktest.webserver;

import ankokovin.fullstacktest.webserver.exceptions.BaseException;
import ankokovin.fullstacktest.webserver.generated.tables.pojos.Organization;
import ankokovin.fullstacktest.webserver.generated.tables.pojos.Worker;
import ankokovin.fullstacktest.webserver.models.Table;
import ankokovin.fullstacktest.webserver.models.errorresponse.DeleteHasChildResponse;
import ankokovin.fullstacktest.webserver.models.errorresponse.NoSuchRecordResponse;
import ankokovin.fullstacktest.webserver.models.errorresponse.WrongHeadIdResponse;
import ankokovin.fullstacktest.webserver.models.response.WorkerListElement;
import ankokovin.fullstacktest.webserver.models.response.WorkerPage;
import ankokovin.fullstacktest.webserver.models.response.WorkerTreeNode;
import ankokovin.fullstacktest.webserver.models.webinput.CreateWorkerInput;
import ankokovin.fullstacktest.webserver.models.webinput.UpdateWorkerInput;
import ankokovin.fullstacktest.webserver.repos.OrganizationRepository;
import ankokovin.fullstacktest.webserver.repos.WorkerRepository;
import ankokovin.fullstacktest.webserver.testhelpers.OrganizationHelpers;
import ankokovin.fullstacktest.webserver.testhelpers.WorkerHelpers;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WorkerControllerTests {
    private static final ankokovin.fullstacktest.webserver.generated.tables.Organization organization
            = ankokovin.fullstacktest.webserver.generated.tables.Organization.ORGANIZATION;
    private static final ankokovin.fullstacktest.webserver.generated.tables.Worker worker
            = ankokovin.fullstacktest.webserver.generated.tables.Worker.WORKER;
    private final String endPoint = "/api/worker";
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private DSLContext dsl;
    @Autowired
    private WorkerRepository workerRepository;
    @Autowired
    private OrganizationRepository organizationRepository;

    @BeforeEach
    void SetUp() {
        dsl.truncateTable(worker)
                .restartIdentity().cascade().execute();
        dsl.truncateTable(organization)
                .restartIdentity().cascade().execute();
    }


    @Nested
    class Create {
        @Test
        public void creates() {
            WorkerHelpers.create(restTemplate, endPoint);
        }

        @Test
        public void whenWrongHeadId_returns() {
            CreateWorkerInput input = new CreateWorkerInput("test", 1, null);
            WrongHeadIdResponse expected = new WrongHeadIdResponse(input.org_id, Table.ORGANIZATION);
            ResponseEntity<WrongHeadIdResponse> response = restTemplate.postForEntity(endPoint, input,
                    WrongHeadIdResponse.class);
            assertEquals(400, response.getStatusCodeValue());
            WrongHeadIdResponse actual = response.getBody();
            assertEquals(expected, actual);
        }

        @Test
        public void whenCreateNameNull_thenReturnsBadRequest() throws BaseException {
            Organization given = OrganizationHelpers.create(organizationRepository, dsl);
            CreateWorkerInput input = new CreateWorkerInput(null, given.getId(), null);
            ResponseEntity<Worker> response = restTemplate.postForEntity(endPoint, input,
                    Worker.class);
            assertEquals(400, response.getStatusCodeValue());
        }
    }

    @Nested
    class Update {
        final String updateEndPoint = endPoint + "/update";

        @Test
        public void updates() {
            Worker expected = WorkerHelpers.create(restTemplate, endPoint);
            expected.setWorkerName("Test1");
            UpdateWorkerInput input = new UpdateWorkerInput(expected.getId(),
                    expected.getWorkerName(), expected.getOrgId(), expected.getHeadId());
            ResponseEntity<Worker> response = restTemplate.postForEntity(updateEndPoint, input, Worker.class);
            assertEquals(200, response.getStatusCodeValue());
            Worker actual = response.getBody();
            assertEquals(expected, actual);
        }

        @Test
        public void whenWrongHeadId_returns() {
            Worker given = WorkerHelpers.create(restTemplate, endPoint);
            given.setOrgId(given.getOrgId() + 1);
            WrongHeadIdResponse expected = new WrongHeadIdResponse(given.getOrgId(), Table.ORGANIZATION);
            UpdateWorkerInput input = new UpdateWorkerInput(given.getId(),
                    given.getWorkerName(), given.getOrgId(), given.getHeadId());
            ResponseEntity<WrongHeadIdResponse> response = restTemplate.postForEntity(updateEndPoint, input,
                    WrongHeadIdResponse.class);
            assertEquals(400, response.getStatusCodeValue());
            WrongHeadIdResponse actual = response.getBody();
            assertEquals(expected, actual);
        }

        @Test
        public void whenUpdateNameNull_thenReturnsBadRequest() {
            Worker given = WorkerHelpers.create(restTemplate, endPoint);
            UpdateWorkerInput input = new UpdateWorkerInput(given.getId(), null, given.getOrgId(), null);
            ResponseEntity<Worker> response = restTemplate.postForEntity(endPoint, input,
                    Worker.class);
            assertEquals(400, response.getStatusCodeValue());
        }


    }

    @Nested
    class Delete {
        @Test
        public void deletes() {
            Worker given = WorkerHelpers.create(restTemplate, endPoint);
            restTemplate.delete(endPoint, given.getId());
        }

        @Test
        public void whenNoRecord_thenThrows() {
            int id = 42;
            ResponseEntity<NoSuchRecordResponse> resp = restTemplate.exchange(endPoint, HttpMethod.DELETE,
                    new HttpEntity<>(id), NoSuchRecordResponse.class, new HashMap<>());
            assertEquals(404, resp.getStatusCodeValue());
            assertNotNull(resp.getBody());
            assertEquals(id, resp.getBody().id);
        }

        @Test
        public void whenHasChild_thenThrows() throws BaseException {
            Worker given = WorkerHelpers.create(restTemplate, endPoint);
            WorkerHelpers.insert(1, given.getOrgId(), 1, given.getId(), "Hello", workerRepository);
            ResponseEntity<DeleteHasChildResponse> resp = restTemplate.exchange(endPoint, HttpMethod.DELETE,
                    new HttpEntity<>(given.getId()), DeleteHasChildResponse.class, new HashMap<>());
            assertEquals(403, resp.getStatusCodeValue());
            assertNotNull(resp.getBody());
            assertEquals(given.getId(), resp.getBody().id);
        }
    }

    @Nested
    class Get {

        @Nested
        class GetById {
            @Test
            public void whenNoSuchRecord_thenReturnNotFound() {
                int id = 42;
                String url = endPoint + "/" + id;
                ResponseEntity<NoSuchRecordResponse> response = restTemplate.getForEntity(url,
                        NoSuchRecordResponse.class);
                assertEquals(404, response.getStatusCodeValue());
                assertNotNull(response.getBody());
                assertEquals(id, response.getBody().id);
            }

            @Test
            public void whenOk_thenReturns() {
                Worker expected = WorkerHelpers.create(restTemplate, endPoint);
                String url = endPoint + "/" + expected.getId();
                ResponseEntity<Worker> response = restTemplate.getForEntity(url,
                        Worker.class);
                assertEquals(200, response.getStatusCodeValue());
                assertNotNull(response.getBody());
                assertEquals(expected, response.getBody());
            }
        }

        @Nested
        class GetAll {
            @Test
            public void whenWrongPage_thenReturnBadRequest() {
                String url = endPoint + "?page=-1";
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                assertEquals(400, response.getStatusCodeValue());
            }

            @Test
            public void whenWrongPageSize_thenReturnBadRequest() {
                String url = endPoint + "?pageSize=-1";
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                assertEquals(400, response.getStatusCodeValue());
            }

            @Test
            public void whenOk_thenReturns() {
                int cnt = 100;
                Worker[] given = Arrays.stream(WorkerHelpers.create(cnt, restTemplate, endPoint))
                        .sorted(Comparator.comparing(Worker::getWorkerName))
                        .toArray(Worker[]::new);
                int page = 2;
                int pageSize = 10;
                String url = endPoint + String.format("?page=%d&pageSize=%d", page, pageSize);
                Worker[] expected_organizations = Arrays.copyOfRange(given, (page - 1) * pageSize, page * pageSize);
                WorkerListElement[] expected = Arrays.stream(expected_organizations)
                        .map((x) -> new WorkerListElement(x.getId(), x.getWorkerName(),
                                null, null,
                                1, "TestOrg")).toArray(WorkerListElement[]::new);
                ResponseEntity<WorkerPage> response = restTemplate.getForEntity(url, WorkerPage.class);
                assertEquals(200, response.getStatusCodeValue());
                assertNotNull(response.getBody());
                assertEquals(page, response.getBody().page);
                assertEquals(pageSize, response.getBody().pageSize);
                assertEquals(cnt, response.getBody().total);
                assertArrayEquals(expected, response.getBody().list.toArray(new WorkerListElement[0]));
            }

            @Test
            public void whenSearch_thenReturns() throws BaseException {
                int exp_1 = 12;
                Organization[] organizations = OrganizationHelpers.create(2, organizationRepository, dsl);
                String orgName = organizations[0].getOrgName();
                WorkerHelpers.insert(exp_1, organizations[0].getId(), workerRepository);
                int r = 42;
                WorkerHelpers.insert(r, organizations[1].getId(), exp_1, workerRepository);
                Worker[] heads = new Worker[]
                        {
                                WorkerHelpers.insert(1, organizations[0].getId(), exp_1 + r, workerRepository)[0],
                                WorkerHelpers.insert(1, organizations[1].getId(), exp_1 + r + 1, workerRepository)[0]
                        };
                int exp_2 = 23;
                String nameTemplate = "Find me%d";
                Worker[] expectedWorker = WorkerHelpers.insert(exp_2, organizations[0].getId(),
                        r + exp_1 + heads.length, heads[0].getId(), nameTemplate, workerRepository);
                java.util.function.Function<Worker, WorkerListElement> mapToWorkerListElement =
                        (w) -> new WorkerListElement(w.getId(), w.getWorkerName(),
                                heads[w.getOrgId() - 1].getId(), heads[w.getOrgId() - 1].getWorkerName(),
                                organizations[w.getOrgId() - 1].getId(), organizations[w.getOrgId() - 1].getOrgName());

                WorkerListElement[] expected = Arrays.stream(expectedWorker)
                        .sorted(Comparator.comparing(Worker::getWorkerName))
                        .map(mapToWorkerListElement)
                        .toArray(WorkerListElement[]::new);
                WorkerHelpers.insert(exp_2, organizations[1].getId(),
                        r + exp_1 + exp_2 + heads.length, heads[1].getId(), nameTemplate, workerRepository);
                int pageSize = 15;
                WorkerListElement[] expectedFirstPage = Arrays.copyOfRange(expected, 0, pageSize);
                WorkerListElement[] expectedSecondPage = Arrays.copyOfRange(expected, pageSize, exp_2);
                String url = String.format("%s?page=%d&pageSize=%d&searchName=%s&searchOrgName=%s",
                        endPoint, 1, pageSize, nameTemplate.substring(0, 5), orgName);
                ResponseEntity<WorkerPage> response = restTemplate.getForEntity(url, WorkerPage.class);
                assertEquals(200, response.getStatusCodeValue());
                assertNotNull(response.getBody());
                assertArrayEquals(expectedFirstPage, response.getBody().list.toArray(new WorkerListElement[0]));
                url = String.format("%s?page=%d&pageSize=%d&searchName=%s&searchOrgName=%s",
                        endPoint, 2, pageSize, nameTemplate.substring(0, 5), orgName);
                response = restTemplate.getForEntity(url, WorkerPage.class);
                assertEquals(200, response.getStatusCodeValue());
                assertNotNull(response.getBody());
                assertArrayEquals(expectedSecondPage, response.getBody().list.toArray(new WorkerListElement[0]));
            }
        }

        @Nested
        class GetTree {
            private final String treeEndpoint = endPoint + "/tree";

            @Test
            void get_returns() throws BaseException {
                WorkerTreeNode given = new WorkerTreeNode(WorkerHelpers.setUp(organizationRepository, dsl));
                WorkerTreeNode expected = new WorkerTreeNode(
                        given.item,
                        given.children.stream()
                                .map(x -> new WorkerTreeNode(x.item, new ArrayList<>()))
                                .collect(Collectors.toList())
                );
                ResponseEntity<WorkerTreeNode> response = restTemplate.getForEntity(
                        treeEndpoint + "?depth=1",
                        WorkerTreeNode.class);
                assertEquals(200, response.getStatusCodeValue());
                assertEquals(expected, response.getBody());
            }

            @Test
            void get_bigDepth_errors() throws BaseException {
                WorkerHelpers.setUp(organizationRepository, dsl);
                ResponseEntity<String> response = restTemplate.getForEntity(
                        treeEndpoint + "?depth=3", String.class);
                assertEquals(400, response.getStatusCodeValue());
            }

            @Test
            void getNegativeDepth() {
                ResponseEntity<String> response = restTemplate.getForEntity(
                        treeEndpoint + "?depth=-1", String.class);
                assertEquals(400, response.getStatusCodeValue());
            }

            @Test
            void get_noSuchRecord() {
                Integer id = 42;
                ResponseEntity<NoSuchRecordResponse> response = restTemplate.getForEntity(
                        treeEndpoint + "?depth=1&id=" + id.toString(),
                        NoSuchRecordResponse.class);
                assertEquals(404, response.getStatusCodeValue());
                assertNotNull(response.getBody());
                assertEquals(id, response.getBody().id);
            }
        }
    }
}
