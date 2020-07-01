package ankokovin.fullstacktest.WebServer.TestHelpers;

import ankokovin.fullstacktest.WebServer.Exceptions.BaseException;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Models.TreeNode;
import ankokovin.fullstacktest.WebServer.Repos.OrganizationRepository;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrganizationHelpers {
    private static final ankokovin.fullstacktest.WebServer.Generated.tables.Organization organization
            = ankokovin.fullstacktest.WebServer.Generated.tables.Organization.ORGANIZATION;
    public static Organization[] create(int cnt, int offset,
                                 String namePattern,
                                 OrganizationRepository organizationRepository,
                                 DSLContext dslContext) throws BaseException {
        assert cnt >= 0;
        assert offset >= 0;
        assert namePattern != null;
        Organization[] expected = new Organization[cnt];
        for (int i = 0; i < cnt; ++i) {
            String fName = String.format(namePattern, i+offset);
            int id = organizationRepository.insert(fName, null);
            assertEquals(offset + i + 1, id);
            expected[i] = new Organization(id, fName, null);
        }
        Object[] actual = dslContext
                .selectFrom(organization)
                .offset(offset)
                .fetchInto(Organization.class).toArray();
        assertArrayEquals(expected, actual);
        return expected;
    }

    public static Organization[] create(int cnt, int offset,
                                 OrganizationRepository organizationRepository,
                                 DSLContext dslContext) throws BaseException {
        String name = "ООО Тест%d";
        return create(cnt, offset, name, organizationRepository, dslContext);
    }

    public static Organization[] create(int cnt,
                                 OrganizationRepository organizationRepository,
                                 DSLContext dslContext) throws BaseException {
        return create(cnt, 0, organizationRepository, dslContext);
    }

    public static Organization create(OrganizationRepository organizationRepository,
            DSLContext dslContext) throws BaseException {
        return create(1, organizationRepository, dslContext)[0];
    }

    public static TreeNode<Organization> setUp(DSLContext dslContext) {
        TreeNode<Organization> given = new TreeNode<>(null,
                Arrays.asList(
                        new TreeNode<>(new Organization(1,"Test",null)),
                        new TreeNode<>(new Organization(2,"Test2",null)),
                        new TreeNode<>(new Organization(3,"Test3",null),
                                Arrays.asList(
                                        new TreeNode<>(new Organization(4,"Test4",3)),
                                        new TreeNode<>(new Organization(5,"Test5",3),
                                                Collections.singletonList(new TreeNode<>(new Organization(6, "Test6", 5))))
                                )
                        )
                ));
        push(given, dslContext);
        Organization[] check_added = unroll(given, dslContext).toArray(new Organization[0]);
        Organization[] res = dslContext.selectFrom(organization).fetchInto(Organization.class).toArray(new Organization[0]);
        assertArrayEquals(check_added, res);
        return given;
    }

    public static void  push(TreeNode<Organization> orgs, DSLContext dslContext) {
        if (orgs.item != null) dslContext.insertInto(organization).values(
                orgs.item.getId(),
                orgs.item.getOrgName(),
                orgs.item.getHeadOrgId()).execute();
        if (orgs.children != null) orgs.children.forEach(it->push(it, dslContext));
    }

    public static List<Organization> unroll(TreeNode<Organization> orgs,  DSLContext dslContext) {
        ArrayList<Organization> result = new ArrayList<>();
        if (orgs.item != null) result.add(orgs.item);
        if (orgs.children != null && orgs.children.size() > 0) result.addAll(
                orgs.children.stream()
                        .map(x -> unroll(x, dslContext))
                        .reduce((x, y) -> {
                            x.addAll(y);
                            return x;
                        }).get());
        return result;
    }
}
