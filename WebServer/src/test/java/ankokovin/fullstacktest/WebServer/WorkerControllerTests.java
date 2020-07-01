package ankokovin.fullstacktest.WebServer;

import ankokovin.fullstacktest.WebServer.Exceptions.BaseException;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
import ankokovin.fullstacktest.WebServer.Models.*;
import ankokovin.fullstacktest.WebServer.Models.ErrorResponse.WrongHeadIdResponse;
import ankokovin.fullstacktest.WebServer.Repos.OrganizationRepository;
import ankokovin.fullstacktest.WebServer.Repos.WorkerRepository;
import ankokovin.fullstacktest.WebServer.TestHelpers.OrganizationHelpers;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ankokovin.fullstacktest.WebServer.TestHelpers.WorkerHelpers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WorkerControllerTests {
    private static final ankokovin.fullstacktest.WebServer.Generated.tables.Organization organization
            = ankokovin.fullstacktest.WebServer.Generated.tables.Organization.ORGANIZATION;
    private static final ankokovin.fullstacktest.WebServer.Generated.tables.Worker worker
            = ankokovin.fullstacktest.WebServer.Generated.tables.Worker.WORKER;
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
    }

    @Nested
    class Update {
        final String updateEndPoint = endPoint + "/update";

        @Test
        public void updates() {
            Worker expected =  WorkerHelpers.create(restTemplate, endPoint);
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
            Worker given =  WorkerHelpers.create(restTemplate, endPoint);
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
    }

    @Nested
    class Delete {
        @Test
        public void deletes() {
            Worker given =  WorkerHelpers.create(restTemplate, endPoint);
            restTemplate.delete(endPoint, given.getId());
        }
    }

    @Nested
    class Get {
        @Test
        public void whenWrongPage_thenReturnBadRequest() {
            String url = endPoint+"?page=-1";
            ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
            assertEquals(400, response.getStatusCodeValue());
        }
        @Test
        public void whenWrongPageSize_thenReturnBadRequest() {
            String url = endPoint+"?pageSize=-1";
            ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class, -1);
            assertEquals(400, response.getStatusCodeValue());
        }
        @Test
        public void whenOk_thenReturns() {
            Worker[] given = WorkerHelpers.create(100, restTemplate, endPoint);
            int page = 2;
            int pageSize = 10;
            String url = endPoint+String.format("?page=%d&pageSize=%d",page,pageSize);
            Worker[] expected_orgs = Arrays.copyOfRange(given, (page-1)*pageSize, page*pageSize);
            WorkerListElement[] expected = Arrays.stream(expected_orgs)
                    .map((x) -> new WorkerListElement(x.getId(), x.getWorkerName(),
                            null, null,
                            1, "TestOrg")).toArray(WorkerListElement[]::new);
            ResponseEntity<WorkerListElement[]> response = restTemplate.getForEntity(url, WorkerListElement[].class);
            assertEquals(200, response.getStatusCodeValue());
            assertArrayEquals(expected, response.getBody());
        }
        @Test
        public void whenSearch_thenReturns() throws BaseException {
            int exp_1 = 12;
            Organization[] orgs = OrganizationHelpers.create(2, organizationRepository,dsl);
            String orgName = orgs[0].getOrgName();
            Worker[] expectedWorker = WorkerHelpers.insert(exp_1, orgs[0].getId(),workerRepository);
            WorkerListElement[] expected = Arrays.stream(expectedWorker)
                    .map((x) -> new WorkerListElement(x.getId(), x.getWorkerName(),
                            null, null,
                            orgs[0].getId(), orgs[0].getOrgName())).toArray(WorkerListElement[]::new);
            int r = 42;
            WorkerHelpers.insert(r,orgs[1].getId(),exp_1,workerRepository);
            Worker head = WorkerHelpers.insert(1,orgs[1].getId(),exp_1+r, workerRepository)[0];
            int exp_2 = 23;
            String nameTemplate = "Find me%d";
            expectedWorker = WorkerHelpers.insert(exp_2,orgs[1].getId(),
                    r + exp_1 + 1, head.getId(),nameTemplate,workerRepository);
            expected = ArrayUtils.addAll(expected, Arrays.stream(expectedWorker)
                    .map((x) -> new WorkerListElement(x.getId(), x.getWorkerName(),
                            head.getId(), head.getWorkerName(),
                            orgs[1].getId(), orgs[1].getOrgName())).toArray(WorkerListElement[]::new));
            int pageSize = exp_1 + exp_2 - 10;
            int expSize_1 = pageSize;
            int expSize_2 = 10;
            WorkerListElement[] expectedFirstPage = Arrays.copyOfRange(expected, 0, expSize_1);
            WorkerListElement[] expectedSecondPage = Arrays.copyOfRange(expected, expSize_1, expSize_1+expSize_2);
            String url = String.format("%s?page=%d&pageSize=%d&searchName=%s&searchOrgName=%s",
                    endPoint,1,pageSize,nameTemplate.substring(0, 5),orgName);
            ResponseEntity<WorkerListElement[]> response = restTemplate.getForEntity(url, WorkerListElement[].class);
            assertEquals(200, response.getStatusCodeValue());
            assertArrayEquals(expectedFirstPage, response.getBody());
            url = String.format("%s?page=%d&pageSize=%d&searchName=%s&searchOrgName=%s",
                    endPoint,2,pageSize,nameTemplate.substring(0, 5),orgName);
            response = restTemplate.getForEntity(url, WorkerListElement[].class);
            assertEquals(200, response.getStatusCodeValue());
            assertArrayEquals(expectedSecondPage, response.getBody());
        }
    }
}
