package ankokovin.fullstacktest.WebServer;
import ankokovin.fullstacktest.WebServer.Exceptions.BaseException;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
import ankokovin.fullstacktest.WebServer.Models.CreateWorkerInput;
import org.junit.jupiter.api.Test;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Models.CreateOrganizationInput;
import ankokovin.fullstacktest.WebServer.Models.ErrorResponse.SameNameResponse;
import ankokovin.fullstacktest.WebServer.Models.ErrorResponse.WrongHeadIdResponse;
import ankokovin.fullstacktest.WebServer.Models.Table;
import ankokovin.fullstacktest.WebServer.Models.UpdateOrganizationInput;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.DSLContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WorkerControllerTests {
    private static final ankokovin.fullstacktest.WebServer.Generated.tables.Organization organization
            = ankokovin.fullstacktest.WebServer.Generated.tables.Organization.ORGANIZATION;
    private static final ankokovin.fullstacktest.WebServer.Generated.tables.Worker worker
            = ankokovin.fullstacktest.WebServer.Generated.tables.Worker.WORKER;
    public static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private TestRestTemplate restTemplate;

    private final String endPoint = "/api/worker";

    @Autowired
    private DSLContext dsl;

    @BeforeEach
    void SetUp() {
        dsl.truncateTable(ankokovin.fullstacktest.WebServer.Generated.tables.Worker.WORKER)
                .restartIdentity().cascade().execute();
        dsl.truncateTable(ankokovin.fullstacktest.WebServer.Generated.tables.Organization.ORGANIZATION)
                .restartIdentity().cascade().execute();
    }

    Worker[] create(int n) throws BaseException {
        assert n > 0;
        String org_name = "TestOrg";
        CreateOrganizationInput input_org = new CreateOrganizationInput(org_name, null);
        ResponseEntity<Organization> response_org = restTemplate.postForEntity("/api/organization", input_org,
                Organization.class);
        Organization org =  response_org.getBody();
        String nameTemplate = "Тест Тестовый Тестович %d";
        Worker[] expected = new Worker[n];
        Worker[] actual = new Worker[n];
        for (int i=0; i<n; ++i) {
            String name = String.format(nameTemplate, i);
            expected[i] = new Worker(i+1, name, org.getId(), null);
            CreateWorkerInput input = new CreateWorkerInput(name, org.getId(), null);
            ResponseEntity<Worker> response = restTemplate.postForEntity(endPoint, input,
                    Worker.class);
            actual[i] = response.getBody();
            assertEquals(200, response.getStatusCodeValue());
        }
        assertArrayEquals(expected, actual);
        return actual;
    }

    Worker create() throws BaseException {
        return create(1)[0];
    }

    @Nested
    class Create {
        @Test
        public void creates() throws BaseException {
            create();
        }

        @Test
        public void whenWrongHeadId_returns() throws BaseException {
            CreateWorkerInput input = new CreateWorkerInput("test", 1, null);
            WrongHeadIdResponse expected = new WrongHeadIdResponse(input.org_id, Table.ORGANIZATION);
            ResponseEntity<WrongHeadIdResponse> response = restTemplate.postForEntity(endPoint, input,
                    WrongHeadIdResponse.class);
            assertEquals(400, response.getStatusCodeValue());
            WrongHeadIdResponse actual = response.getBody();
            assertEquals(expected, actual);
        }
    }
}
