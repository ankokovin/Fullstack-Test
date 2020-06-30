package ankokovin.fullstacktest.WebServer;

import ankokovin.fullstacktest.WebServer.Controllers.OrganizationsController;
import ankokovin.fullstacktest.WebServer.Exceptions.SameNameException;
import ankokovin.fullstacktest.WebServer.Exceptions.WrongHeadIdException;
import ankokovin.fullstacktest.WebServer.Generated.tables.Worker;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Models.CreateOrganizationInput;
import ankokovin.fullstacktest.WebServer.Services.OrganizationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.DSLContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrganizationControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    private final String endPoint = "/api/organization";

    @Autowired
    private DSLContext dsl;

    @BeforeEach
    void SetUp() {
        dsl.truncateTable(ankokovin.fullstacktest.WebServer.Generated.tables.Worker.WORKER)
                .restartIdentity().cascade().execute();
        dsl.truncateTable(ankokovin.fullstacktest.WebServer.Generated.tables.Organization.ORGANIZATION)
                .restartIdentity().cascade().execute();
    }

    @Nested
    class Create {
        @Test
        public void whenCreateCorrectNoHead_thenOrganizationCreates() throws Exception {
            String name = "Алексей";
            CreateOrganizationInput input = new CreateOrganizationInput(name, null);
            Organization expected = new Organization(1, name, null);
            ResponseEntity<Organization> response = restTemplate.postForEntity(endPoint, input,
                    Organization.class);
            assertEquals(expected, response.getBody());
        }
        @Test
        public void whenCreateCorrectHead_thenOrganizationCreates() throws Exception {
            whenCreateCorrectNoHead_thenOrganizationCreates();
            String name = "Алексей2";
            CreateOrganizationInput input = new CreateOrganizationInput(name, 1);
            Organization expected = new Organization(2, name, 1);
            ResponseEntity<Organization> response = restTemplate.postForEntity(endPoint, input,
                    Organization.class);
            assertEquals(expected, response.getBody());
        }
        @Test
        public void whenCreateWrongHead_thenReturnsException() throws Exception {
            String name = "Алексей";
            CreateOrganizationInput input = new CreateOrganizationInput(name, 1);
            ResponseEntity<Object> response = restTemplate.postForEntity(endPoint, input,
                    Object.class);
            assertEquals(400, response.getStatusCodeValue());
            System.out.println(response);
            Map<String, Object> response_body = (Map<String, Object>)response.getBody();
            assertEquals(input.org_id, ((Map<String, Object>)response_body.get("error")).get("id"));
        }

    }
}
