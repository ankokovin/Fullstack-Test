package ankokovin.fullstacktest.WebServer.TestHelpers;

import ankokovin.fullstacktest.WebServer.Exceptions.BaseException;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Repos.OrganizationRepository;
import org.jooq.DSLContext;

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
}
