package ankokovin.fullstacktest.WebServer;

import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
import ankokovin.fullstacktest.WebServer.Models.CreateOrganizationInput;
import ankokovin.fullstacktest.WebServer.Models.CreateWorkerInput;
import ankokovin.fullstacktest.WebServer.Models.ErrorResponse.WrongHeadIdResponse;
import ankokovin.fullstacktest.WebServer.Models.Table;
import ankokovin.fullstacktest.WebServer.Models.UpdateWorkerInput;
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

    @BeforeEach
    void SetUp() {
        dsl.truncateTable(worker)
                .restartIdentity().cascade().execute();
        dsl.truncateTable(organization)
                .restartIdentity().cascade().execute();
    }

    Worker[] create(@SuppressWarnings("SameParameterValue") int n) {
        assert n > 0;
        String org_name = "TestOrg";
        CreateOrganizationInput input_org = new CreateOrganizationInput(org_name, null);
        ResponseEntity<Organization> response_org = restTemplate.postForEntity("/api/organization", input_org,
                Organization.class);
        Organization org = response_org.getBody();
        assert org != null;
        String nameTemplate = "Тест Тестовый Тестович %d";
        Worker[] expected = new Worker[n];
        Worker[] actual = new Worker[n];
        for (int i = 0; i < n; ++i) {
            String name = String.format(nameTemplate, i);
            expected[i] = new Worker(i + 1, name, org.getId(), null);
            CreateWorkerInput input = new CreateWorkerInput(name, org.getId(), null);
            ResponseEntity<Worker> response = restTemplate.postForEntity(endPoint, input,
                    Worker.class);
            actual[i] = response.getBody();
            assertEquals(200, response.getStatusCodeValue());
        }
        assertArrayEquals(expected, actual);
        return actual;
    }

    Worker create() {
        return create(1)[0];
    }

    @Nested
    class Create {
        @Test
        public void creates() {
            create();
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
            Worker expected = create();
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
            Worker given = create();
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
            Worker given = create();
            restTemplate.delete(endPoint, given.getId());
        }
    }
}
