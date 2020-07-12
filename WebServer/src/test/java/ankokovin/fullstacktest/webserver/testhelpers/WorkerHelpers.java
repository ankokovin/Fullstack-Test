package ankokovin.fullstacktest.webserver.testhelpers;

import ankokovin.fullstacktest.webserver.exceptions.BaseException;
import ankokovin.fullstacktest.webserver.generated.tables.pojos.Organization;
import ankokovin.fullstacktest.webserver.generated.tables.pojos.Worker;
import ankokovin.fullstacktest.webserver.models.webinput.CreateOrganizationInput;
import ankokovin.fullstacktest.webserver.models.webinput.CreateWorkerInput;
import ankokovin.fullstacktest.webserver.models.response.TreeNode;
import ankokovin.fullstacktest.webserver.models.response.WorkerTreeListElement;
import ankokovin.fullstacktest.webserver.repos.OrganizationRepository;
import ankokovin.fullstacktest.webserver.repos.WorkerRepository;
import org.jooq.DSLContext;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorkerHelpers {
    private static final ankokovin.fullstacktest.webserver.generated.tables.Organization organization
            = ankokovin.fullstacktest.webserver.generated.tables.Organization.ORGANIZATION;
    private static final ankokovin.fullstacktest.webserver.generated.tables.Worker worker
            = ankokovin.fullstacktest.webserver.generated.tables.Worker.WORKER;

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
            String name = String.format(workerNameTemplate, i + 1 + offset);
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


    public static TreeNode<WorkerTreeListElement> setUp(OrganizationRepository organizationRepository,
                                                        DSLContext dslContext) throws BaseException {
        Organization[] orgs = OrganizationHelpers.create(2,organizationRepository,dslContext);
        TreeNode<Worker> workersTree = new TreeNode<>(null,
                Arrays.asList(
                        new TreeNode<>(new Worker(1,"Test",1,null)),
                        new TreeNode<>(new Worker(2,"Test2",2,null)),
                        new TreeNode<>(new Worker(3,"Test3",2,null),
                                Arrays.asList(
                                        new TreeNode<>(new Worker(4,"Test4",2,3)),
                                        new TreeNode<>(new Worker(5,"Test5",2,3),
                                                Collections.singletonList(new TreeNode<>(new Worker(6, "Test6", 2, 5))))
                                )
                        )
                ));
        push(workersTree, dslContext);
        Worker[] check_added = unroll(workersTree).toArray(new Worker[0]);
        Worker[] res = dslContext.selectFrom(worker).fetchInto(Worker.class).toArray(new Worker[0]);
        assertArrayEquals(check_added, res);
        return new TreeNode<>(null, workersTree.children.parallelStream()
                .map(x->transform(x,x.item.getOrgId(),orgs[x.item.getOrgId()-1].getOrgName())).collect(Collectors.toList()));
    }
    public static TreeNode<WorkerTreeListElement> transform(TreeNode<Worker> workerNode, int org_id, String org_name){
        WorkerTreeListElement element = new WorkerTreeListElement(
                workerNode.item.getId(), workerNode.item.getWorkerName(), org_id, org_name);
        return new TreeNode<>(element, workerNode.children.parallelStream()
                .map(x->transform(x, org_id, org_name)).collect(Collectors.toList()));

    }

    public static void  push(TreeNode<Worker> workerTreeNode, DSLContext dslContext) {
        if (workerTreeNode.item != null) dslContext.insertInto(worker).values(
                workerTreeNode.item.getId(),
                workerTreeNode.item.getWorkerName(),
                workerTreeNode.item.getOrgId(),
                workerTreeNode.item.getHeadId()).execute();
        if (workerTreeNode.children != null) workerTreeNode.children.forEach(it->push(it, dslContext));
    }

    public static List<Worker> unroll(TreeNode<Worker> orgs) {
        ArrayList<Worker> result = new ArrayList<>();
        if (orgs.item != null) result.add(orgs.item);
        if (orgs.children != null && orgs.children.size() > 0) result.addAll(
                orgs.children.stream()
                        .map(WorkerHelpers::unroll)
                        .reduce((x, y) -> {
                            x.addAll(y);
                            return x;
                        }).get());
        return result;
    }
}
