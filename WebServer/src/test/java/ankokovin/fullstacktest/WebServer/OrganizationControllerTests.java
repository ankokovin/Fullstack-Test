package ankokovin.fullstacktest.WebServer;

import ankokovin.fullstacktest.WebServer.Exceptions.BaseException;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Models.*;
import ankokovin.fullstacktest.WebServer.Models.ErrorResponse.DeleteHasChildResponse;
import ankokovin.fullstacktest.WebServer.Models.ErrorResponse.NoSuchRecordResponse;
import ankokovin.fullstacktest.WebServer.Models.ErrorResponse.SameNameResponse;
import ankokovin.fullstacktest.WebServer.Models.ErrorResponse.WrongHeadIdResponse;
import ankokovin.fullstacktest.WebServer.Models.Input.CreateOrganizationInput;
import ankokovin.fullstacktest.WebServer.Models.Input.UpdateOrganizationInput;
import ankokovin.fullstacktest.WebServer.Models.Response.OrgListElement;
import ankokovin.fullstacktest.WebServer.Models.Response.OrganizationPage;
import ankokovin.fullstacktest.WebServer.Models.Response.OrganizationTreeNode;
import ankokovin.fullstacktest.WebServer.Repos.OrganizationRepository;
import ankokovin.fullstacktest.WebServer.TestHelpers.OrganizationHelpers;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private OrganizationRepository organizationRepository;

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
        public void whenCreateNameNull_thenReturnsBadRequest(){
            CreateOrganizationInput input = new CreateOrganizationInput(null,null);
            ResponseEntity<Organization> response = restTemplate.postForEntity(endPoint, input,
                    Organization.class);
            assertEquals(400, response.getStatusCodeValue());
        }

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
        public void whenUpdateIdNull_thenReturnsBadRequest(){
            CreateOrganizationInput input = new UpdateOrganizationInput(null,"test", null);
            ResponseEntity<Organization> response = restTemplate.postForEntity(endPointUpdate, input,
                    Organization.class);
            assertEquals(400, response.getStatusCodeValue());
        }


        @Test
        public void whenUpdateNameNull_thenReturnsBadRequest(){
            Organization given = create();
            CreateOrganizationInput input = new UpdateOrganizationInput(given.getId(),null, null);
            ResponseEntity<Organization> response = restTemplate.postForEntity(endPointUpdate, input,
                    Organization.class);
            assertEquals(400, response.getStatusCodeValue());
        }

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
        public void whenDeleteIdEmpty_thenReturnsBadRequest(){
            ResponseEntity<NoSuchRecordResponse> resp = restTemplate.exchange(endPoint, HttpMethod.DELETE,
                    new HttpEntity<>(""), NoSuchRecordResponse.class, new HashMap<>());
            assertEquals(400, resp.getStatusCodeValue());
        }
        @Test
        public void whenDeleteIdNull_thenReturnsBadRequest(){
            ResponseEntity<NoSuchRecordResponse> resp = restTemplate.exchange(endPoint, HttpMethod.DELETE,
                   null, NoSuchRecordResponse.class, new HashMap<>());
            assertEquals(400, resp.getStatusCodeValue());
        }
        @Test
        public void whenDeleteSucceeds() {
            Organization given = create();
            restTemplate.delete(endPoint, given.getId(), Organization.class);
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
            Organization given = create();
            OrganizationHelpers.create(1,1,given.getId(),organizationRepository, dsl);
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
                String url = endPoint+"/"+id;
                ResponseEntity<NoSuchRecordResponse> response = restTemplate.getForEntity(url,
                        NoSuchRecordResponse.class);
                assertEquals(404, response.getStatusCodeValue());
                assertNotNull(response.getBody());
                assertEquals(id, response.getBody().id);
            }

            @Test
            public void whenOk_thenReturns() {
                Organization expected = create();
                String url = endPoint+"/"+expected.getId();
                ResponseEntity<Organization> response = restTemplate.getForEntity(url,
                        Organization.class);
                assertEquals(200, response.getStatusCodeValue());
                assertNotNull(response.getBody());
                assertEquals(expected, response.getBody());
            }
        }

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
                int cnt = 100;
                Organization[] given = create(cnt);
                int page = 2;
                int pageSize = 10;
                Organization[] expected_orgs = Arrays.copyOfRange(given, (page-1) * pageSize, page*pageSize);
                OrgListElement[] expected = Arrays.stream(expected_orgs)
                        .map((org) -> new OrgListElement(org.getId(), org.getOrgName(), 0))
                        .toArray(OrgListElement[]::new);
                String url = endPoint+String.format("?page=%d&pageSize=%d",page, pageSize);
                ResponseEntity<OrganizationPage> response
                        = restTemplate.getForEntity(url, OrganizationPage.class);
                assertEquals(200, response.getStatusCodeValue());
                assertNotNull(response.getBody());
                assertEquals(page, response.getBody().page);
                assertEquals(pageSize, response.getBody().pageSize);
                assertEquals(cnt, response.getBody().total);
                assertArrayEquals(expected, response.getBody().list.toArray(new OrgListElement[0]));
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
                ResponseEntity<OrganizationPage> response
                        = restTemplate.getForEntity(url, OrganizationPage.class);
                assertEquals(200, response.getStatusCodeValue());
                assertNotNull(response.getBody());
                assertArrayEquals(expected, response.getBody().list.toArray(new OrgListElement[0]));
            }
        }

        @Nested
        class GetTree {
            private final String treeEndpoint = endPoint + "/tree";
            @Test
            void get_returns() {
                OrganizationTreeNode given = new OrganizationTreeNode(setUp(dsl));
                OrganizationTreeNode expected = new OrganizationTreeNode(
                        given.item,
                        given.children.stream()
                                .map(x->new OrganizationTreeNode(x.item, new ArrayList<>()))
                                .collect(Collectors.toList())
                );
                ResponseEntity<OrganizationTreeNode> response = restTemplate.getForEntity(
                        treeEndpoint+"?depth=1",
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
                assertNotNull(response.getBody());
                assertEquals(id, response.getBody().id);
            }
            @Test
            void get_bigDepth_errors() {
                OrganizationTreeNode given = new OrganizationTreeNode(OrganizationHelpers.setUp(dsl));
                ResponseEntity<Object> response = restTemplate.getForEntity(
                        treeEndpoint+"?depth=3", Object.class);
                assertEquals(400, response.getStatusCodeValue());
            }
        }
    }
}
