package ankokovin.fullstacktest.WebServer.TestHelpers;

import ankokovin.fullstacktest.WebServer.Exceptions.BaseException;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Worker;
import ankokovin.fullstacktest.WebServer.Repos.OrganizationRepository;
import ankokovin.fullstacktest.WebServer.Repos.WorkerRepository;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class WorkerHelpers {
    private static final ankokovin.fullstacktest.WebServer.Generated.tables.Organization organization
            = ankokovin.fullstacktest.WebServer.Generated.tables.Organization.ORGANIZATION;
    private static final ankokovin.fullstacktest.WebServer.Generated.tables.Worker worker
            = ankokovin.fullstacktest.WebServer.Generated.tables.Worker.WORKER;

    public static Worker[] create(int n, int org_id, int offset, WorkerRepository workerRepository) throws BaseException {
        String nameTemplate = "Тест Тестовый Тестович %d";
        Worker[] expected = new Worker[n];
        Worker[] actual = new Worker[n];
        for (int i = 0; i < n; ++i) {
            String name = String.format(nameTemplate, i);
            expected[i] = new Worker(i + 1 + offset, name, org_id, null);
            actual[i] = workerRepository.getById(workerRepository.insert(name, org_id, null));
        }
        assertArrayEquals(expected, actual);
        return actual;
    }
    public static Worker[] create(int n, int org_id, WorkerRepository workerRepository) throws BaseException {
        return create(n, org_id,0,  workerRepository);
    }

        public static Worker[] create(int n, WorkerRepository workerRepository, OrganizationRepository organizationRepository)
            throws BaseException {
        assert n > 0;
        int org_id = organizationRepository.insert("Test", null);
        return create(n, org_id, workerRepository);
    }

    public static Worker create(WorkerRepository workerRepository, OrganizationRepository organizationRepository) throws BaseException {
        return create(1, workerRepository, organizationRepository)[0];
    }
}
