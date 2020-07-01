package ankokovin.fullstacktest.WebServer;

import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Models.*;
import ankokovin.fullstacktest.WebServer.Models.ErrorResponse.NoSuchRecordResponse;
import ankokovin.fullstacktest.WebServer.Models.ErrorResponse.SameNameResponse;
import ankokovin.fullstacktest.WebServer.Models.ErrorResponse.WrongHeadIdResponse;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

import static ankokovin.fullstacktest.WebServer.TestHelpers.OrganizationHelpers.setUp;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrganizationControllerTests {

    private final String endPoint = "/api/organization";
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private DSLContext dsl;

    @BeforeEach
    void SetUp() {
        dsl.truncateTable(ankokovin.fullstacktest.WebServer.Generated.tables.Worker.WORKER)
                .restartIdentity().cascade().execute();
        dsl.truncateTable(ankokovin.fullstacktest.WebServer.Generated.tables.Organization.ORGANIZATION)
                .restartIdentity().cascade().execute();
    }

    Organization[] create(int n) {
        assert n > 0;
        Organization[] result = new Organization[n];
        String nameTemplate = "Тест-%d";
        for (int i = 0; i < n; ++i) {
            String name = String.format(nameTemplate, i);
            CreateOrganizationInput input = new CreateOrganizationInput(name, null);
            Organization expected = new Organization(i + 1, name, null);
            ResponseEntity<Organization> response = restTemplate.postForEntity(endPoint, input,
                    Organization.class);
            assertEquals(expected, response.getBody());
            result[i] = expected;
        }
        return result;
    }

    Organization create() {
        return create(1)[0];
    }

    @Nested
    class Create {
        @Test
        public void whenCreateCorrectNoHead_thenOrganizationCreates() {
            create();
        }

        @Test
        public void whenCreateCorrectHead_thenOrganizationCreates() {
            Organization given = create();
            String name = "Алексей2";
            CreateOrganizationInput input = new CreateOrganizationInput(name, given.getId());
            Organization expected = new Organization(given.getId() + 1, name, given.getId());
            ResponseEntity<Organization> response = restTemplate.postForEntity(endPoint, input,
                    Organization.class);
            assertEquals(expected, response.getBody());
        }

        @Test
        public void whenCreateWrongHead_thenReturnsException() {
            String name = "Алексей";
            int org_id = 1;
            CreateOrganizationInput input = new CreateOrganizationInput(name, org_id);
            WrongHeadIdResponse expected = new WrongHeadIdResponse(org_id, Table.ORGANIZATION);
            ResponseEntity<WrongHeadIdResponse> response = restTemplate.postForEntity(endPoint, input,
                    WrongHeadIdResponse.class);
            assertEquals(400, response.getStatusCodeValue());
            System.out.println(response);
            WrongHeadIdResponse actual = response.getBody();
            assertNotNull(actual);
            assertEquals(expected, actual);
        }

        @Test
        public void whenCreateSameName_thenReturnsException() {
            Organization given = create();
            String name = given.getOrgName();
            CreateOrganizationInput input = new CreateOrganizationInput(name, null);
            SameNameResponse expected = new SameNameResponse(name);
            ResponseEntity<SameNameResponse> response = restTemplate.postForEntity(endPoint, input,
                    SameNameResponse.class);
            assertEquals(400, response.getStatusCodeValue());
            System.out.println(response);
            SameNameResponse actual = response.getBody();
            assertNotNull(actual);
            assertEquals(expected, actual);
        }
    }

    @Nested
    class Update {
        private final String endPointUpdate = endPoint + "/update";

        @Test
        public void whenUpdateCorrect_thenUpdates() {
            Organization given = create();
            String newName = "Aleksei";
            given.setOrgName(newName);
            UpdateOrganizationInput input = new UpdateOrganizationInput(given.getId(), newName, null);
            ResponseEntity<Organization> response = restTemplate.postForEntity(endPointUpdate, input,
                    Organization.class);
            assertEquals(200, response.getStatusCodeValue());
            assertEquals(given, response.getBody());
        }

        @Test
        public void whenSameName() {
            Organization[] given = create(2);
            UpdateOrganizationInput input = new UpdateOrganizationInput(
                    given[0].getId(),
                    given[1].getOrgName(),
                    null);
            SameNameResponse expected = new SameNameResponse(given[1].getOrgName());
            ResponseEntity<SameNameResponse> response = restTemplate.postForEntity(endPointUpdate, input,
                    SameNameResponse.class);
            assertEquals(400, response.getStatusCodeValue());
            System.out.println(response);
            SameNameResponse actual = response.getBody();
            assertNotNull(actual);
            assertEquals(expected, actual);
        }

        @Test
        public void whenUpdateWrongHead() {
            Organization[] given = create(2);
            UpdateOrganizationInput input = new UpdateOrganizationInput(
                    given[1].getId(),
                    given[1].getOrgName(),
                    given[1].getId());
            WrongHeadIdResponse expected = new WrongHeadIdResponse(given[1].getId(), Table.ORGANIZATION);
            ResponseEntity<WrongHeadIdResponse> response = restTemplate.postForEntity(endPointUpdate, input,
                    WrongHeadIdResponse.class);
            assertEquals(400, response.getStatusCodeValue());
            System.out.println(response);
            WrongHeadIdResponse actual = response.getBody();
            assertNotNull(actual);
            assertEquals(expected, actual);
        }
    }

    @Nested
    class Delete {
        @Test
        public void whenDeleteSucceeds() {
            Organization given = create();
            restTemplate.delete(endPoint, given.getId(), Organization.class);
        }
    }

    @Nested
    class Get {
        @Nested
        class GetAll {

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
                Organization[] given = create(100);
                int page = 2;
                int pageSize = 10;
                Organization[] expected_orgs = Arrays.copyOfRange(given, (page-1) * pageSize, page*pageSize);
                OrgListElement[] expected = Arrays.stream(expected_orgs)
                        .map((org) -> new OrgListElement(org.getId(), org.getOrgName(), 0))
                        .toArray(OrgListElement[]::new);
                String url = endPoint+String.format("?page=%d&pageSize=%d",page, pageSize);
                ResponseEntity<OrgListElement[]> response
                        = restTemplate.getForEntity(url, OrgListElement[].class);
                assertEquals(200, response.getStatusCodeValue());
                assertArrayEquals(expected, response.getBody());
            }
            @Test
            public void whenSearchOk_thenReturns() {
                Organization[] given = create(100);
                int page = 1;
                int pageSize = 1;
                Organization[] expected_orgs =  new Organization[]{given[42]};
                OrgListElement[] expected = Arrays.stream(expected_orgs)
                        .map((org) -> new OrgListElement(org.getId(), org.getOrgName(), 0))
                        .toArray(OrgListElement[]::new);
                String url = endPoint+String.format("?page=%d&pageSize=%d&searchName=%s",
                        page, pageSize, "42");
                ResponseEntity<OrgListElement[]> response
                        = restTemplate.getForEntity(url, OrgListElement[].class);
                assertEquals(200, response.getStatusCodeValue());
                assertArrayEquals(expected, response.getBody());
            }
        }

        @Nested
        class GetTree {
            private final String treeEndpoint = endPoint + "/tree";
            @Test
            void get_returns() {
                OrganizationTreeNode expected = new OrganizationTreeNode(setUp(dsl));
                ResponseEntity<OrganizationTreeNode> response = restTemplate.getForEntity(
                        treeEndpoint+"?depth=5",
                        OrganizationTreeNode.class);
                assertEquals(200, response.getStatusCodeValue());
                assertEquals(expected, response.getBody());
            }
            @Test
            void getNegativeDepth(){
                ResponseEntity<OrganizationTreeNode> response = restTemplate.getForEntity(
                        treeEndpoint+"?depth=-1",
                        OrganizationTreeNode.class);
                assertEquals(400, response.getStatusCodeValue());
            }
            @Test
            void get_noSuchRecord() {
                Integer id = 42;
                ResponseEntity<NoSuchRecordResponse> response = restTemplate.getForEntity(
                        treeEndpoint+"?depth=1&id="+id.toString(),
                        NoSuchRecordResponse.class);
                assertEquals(404, response.getStatusCodeValue());
                assertEquals(id, response.getBody().id);
            }
        }
    }
}
