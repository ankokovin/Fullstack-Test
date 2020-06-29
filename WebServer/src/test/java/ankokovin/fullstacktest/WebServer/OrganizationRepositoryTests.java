package ankokovin.fullstacktest.WebServer;

import ankokovin.fullstacktest.WebServer.Exceptions.SameNameException;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Repos.OrganizationRepository;
import org.jooq.DSLContext;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;


@JooqTest
@RunWith(SpringRunner.class)
public class OrganizationRepositoryTests {

    private static final ankokovin.fullstacktest.WebServer.Generated.tables.Organization organization
            = ankokovin.fullstacktest.WebServer.Generated.tables.Organization.ORGANIZATION;
    private static final ankokovin.fullstacktest.WebServer.Generated.tables.Worker worker
            = ankokovin.fullstacktest.WebServer.Generated.tables.Worker.WORKER;

    @TestConfiguration
    static class OrganizationServiceTestsConfiguration {

        @Bean
        public OrganizationRepository organizationRepository() {
            return new OrganizationRepository();
        }
    }

    @Autowired
    private DSLContext dslContext;

    @Autowired
    public OrganizationRepository organizationRepository;

    @Before
    public void setup(){
        dslContext.truncateTable(worker).restartIdentity().cascade().execute();
        dslContext.truncateTable(organization).restartIdentity().cascade().execute();
    }

    @Test
    public void whenCreateCorrectNoHead_thenOrganizationCreates() throws SameNameException {
        //given
        String name = "ООО Тест";

        int id = organizationRepository.insert(name, null);
        Assert.assertEquals(1,id);

        Object[] actual = dslContext
                .selectFrom(organization)
                .fetchInto(Organization.class).toArray();
        Assert.assertArrayEquals(new  Organization[]{new Organization(1, name,null)}, actual);
    }
}
