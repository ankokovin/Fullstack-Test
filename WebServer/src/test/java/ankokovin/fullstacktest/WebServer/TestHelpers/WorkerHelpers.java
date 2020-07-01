package ankokovin.fullstacktest.WebServer.TestHelpers;

import ankokovin.fullstacktest.WebServer.Exceptions.*;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
import ankokovin.fullstacktest.WebServer.Models.CreateOrganizationInput;
import ankokovin.fullstacktest.WebServer.Models.CreateWorkerInput;
import ankokovin.fullstacktest.WebServer.Repos.OrganizationRepository;
import ankokovin.fullstacktest.WebServer.Repos.WorkerRepository;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorkerHelpers {
    private static final ankokovin.fullstacktest.WebServer.Generated.tables.Organization organization
            = ankokovin.fullstacktest.WebServer.Generated.tables.Organization.ORGANIZATION;
    private static final ankokovin.fullstacktest.WebServer.Generated.tables.Worker worker
            = ankokovin.fullstacktest.WebServer.Generated.tables.Worker.WORKER;

    public static Worker[] insert(int n, int offset,
                                  String organizationName, OrganizationRepository organizationRepository,
                                  String workerNameTemplate, WorkerRepository workerRepository)
            throws BaseException {
        assert n > 0;
        int org_id = organizationRepository.insert(organizationName, null);
        return insert(n, org_id, offset, workerNameTemplate, workerRepository);
    }

    public static Worker[] insert(int n, int org_id, int offset,
                                  String workerNameTemplate, WorkerRepository workerRepository)
            throws BaseException {
        return insert(n, org_id, offset, null, workerNameTemplate, workerRepository);
    }

    public static Worker[] insert(int n, int org_id, int offset, Integer head_id,
                                  String workerNameTemplate, WorkerRepository workerRepository)
            throws BaseException {
        Worker[] expected = new Worker[n];
        Worker[] actual = new Worker[n];
        for (int i = 0; i < n; ++i) {
            String name = String.format(workerNameTemplate, i);
            expected[i] = new Worker(i + 1 + offset, name, org_id, head_id);
            actual[i] = workerRepository.getById(workerRepository.insert(name, org_id, head_id));
        }
        assertArrayEquals(expected, actual);
        return actual;
    }


    public static Worker[] insert(int n, int org_id, int offset, WorkerRepository workerRepository) throws BaseException {
        String nameTemplate = "Тест Тестовый Тестович %d";
        return insert(n, org_id, offset, nameTemplate, workerRepository);
    }
    public static Worker[] insert(int n, int org_id, WorkerRepository workerRepository) throws BaseException {
        return insert(n, org_id,0,  workerRepository);
    }

        public static Worker[] insert(int n, WorkerRepository workerRepository, OrganizationRepository organizationRepository)
            throws BaseException {
        assert n > 0;
        int org_id = organizationRepository.insert("Test", null);
        return insert(n, org_id, workerRepository);
    }

    public static Worker insert(WorkerRepository workerRepository, OrganizationRepository organizationRepository) throws BaseException {
        return insert(1, workerRepository, organizationRepository)[0];
    }


    public static Worker[] create(int n, TestRestTemplate restTemplate, String endPoint) {
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

    public static Worker create(TestRestTemplate restTemplate, String endPoint) {
        return create(1, restTemplate, endPoint)[0];
    }
}
