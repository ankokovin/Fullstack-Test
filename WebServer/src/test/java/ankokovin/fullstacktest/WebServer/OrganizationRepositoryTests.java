package ankokovin.fullstacktest.WebServer;

import ankokovin.fullstacktest.WebServer.Exceptions.SameNameException;
import ankokovin.fullstacktest.WebServer.Exceptions.WrongHeadIdException;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Repos.OrganizationRepository;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@JooqTest
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

    @BeforeEach
    public void setup(){
        dslContext.truncateTable(worker).restartIdentity().cascade().execute();
        dslContext.truncateTable(organization).restartIdentity().cascade().execute();
    }

    @Nested
    class Insert {
        @Test
        public void whenNoHead_thenOrganizationCreates() throws SameNameException {
            String name = "ООО Тест";
            Organization expected = new Organization(1, name, null);

            int id = organizationRepository.insert(name, null);
            assertEquals(1, id);


            Object[] actual = dslContext
                    .selectFrom(organization)
                    .fetchInto(Organization.class).toArray();
            assertArrayEquals(new Organization[]{expected}, actual);
        }

        @Test
        public void whenHead_thenOrganizationCreates() throws SameNameException {
            whenNoHead_thenOrganizationCreates();
            Organization given = new Organization(1, "ООО Тест", null);
            String name = "ООО Тест2";
            Organization expected = new Organization(2, name, 1);
            int id = organizationRepository.insert(name, 1);
            assertEquals(2, id);

            Object[] actual = dslContext
                    .selectFrom(organization)
                    .fetchInto(Organization.class).toArray();
            assertArrayEquals(new Organization[]{given, expected}, actual);
        }

        @Test
        public void whenFirstWithHead_thenThrows() {
            WrongHeadIdException e = assertThrows(WrongHeadIdException.class,
                    () -> organizationRepository.insert("OOO Тест",3));
            assertEquals( 1, e.id);
        }

        @Test
        public void whenWrongHead_thenThrows() throws SameNameException {
            whenNoHead_thenOrganizationCreates();
            Organization given = new Organization(1, "ООО Тест", null);
            WrongHeadIdException e = assertThrows(WrongHeadIdException.class,
                    () -> organizationRepository.insert("OOO Тест2",3));
            assertEquals(2, e.id);
        }

        @Test
        public void whenSelfHead_thenThrows() {
            WrongHeadIdException e = assertThrows(WrongHeadIdException.class,
                    () -> organizationRepository.insert("OOO Тест",1));
            assertEquals( 1, e.id);
        }

        @Test
        public void whenSameName_thenThrows() throws SameNameException {
            whenNoHead_thenOrganizationCreates();
            Organization given = new Organization(1, "ООО Тест", null);
            SameNameException e = assertThrows(SameNameException.class,
                    () -> organizationRepository.insert(given.getOrgName(),null));
            assertEquals(given.getOrgName(), e.name);
        }
    }

}
