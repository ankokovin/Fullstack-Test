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

    public Organization[] create(Integer cnt) throws Exception {
        String name = "ООО Тест%d";
        Organization[] expected = new Organization[cnt];


        for (int i=0; i<cnt; ++i) {
            String fName = String.format(name,i);
            int id = organizationRepository.insert(fName, null);
            assertEquals(i+1, id);
            expected[i] = new Organization(id, fName, null);
        }
        Object[] actual = dslContext
                .selectFrom(organization)
                .fetchInto(Organization.class).toArray();
        assertArrayEquals(expected, actual);
        return expected;
    }

    public Organization create() throws Exception {
        return create(1)[0];
    }

    @Nested
    class Insert {
        @Test
        public void whenNoHead_thenOrganizationCreates() throws Exception {
            create();
        }

        @Test
        public void whenHead_thenOrganizationCreates() throws Exception {
            Organization given = create();
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
        public void whenWrongHead_thenThrows() throws Exception {
            Organization given = create();
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
        public void whenSameName_thenThrows() throws Exception {
            Organization given = create();
            SameNameException e = assertThrows(SameNameException.class,
                    () -> organizationRepository.insert(given.getOrgName(),null));
            assertEquals(given.getOrgName(), e.name);
        }
    }

    @Nested
    class Update {
        @Test
        public void whenChangeName_thenOrganizationUpdates() throws Exception {
            Organization given = create();
            String name = "ООО Тест Изменено";
            Organization expected = new Organization(1, name, null);

            int id = organizationRepository.update(given.getId(), name, null);
            assertEquals(1, id);

            Object[] actual = dslContext
                    .selectFrom(organization)
                    .fetchInto(Organization.class).toArray();
            assertArrayEquals(new Organization[]{expected}, actual);
        }

        @Test
        public void whenChangeHead_thenOrganizationUpdates() throws Exception {
            Organization[] given = create(2);
            Organization expected = new Organization(given[1].getId(), given[1].getOrgName(), given[0].getId());
            int id = organizationRepository.update(given[1].getId(),given[1].getOrgName(), given[0].getId());
            assertEquals(2, id);

            Object[] actual = dslContext
                    .selectFrom(organization)
                    .fetchInto(Organization.class).toArray();
            assertArrayEquals(new Organization[]{given[0], expected}, actual);
        }

        @Test
        public void whenChangeHeadSelf_thenThrows() throws Exception {
            Organization given = create();
            WrongHeadIdException e = assertThrows(WrongHeadIdException.class,
                    () -> organizationRepository.update(given.getId(), given.getOrgName(), given.getId()));
            assertEquals( given.getId(), e.id);
        }

        @Test
        public void whenWrongHead_thenThrows() throws Exception {
            Organization given = create();
            WrongHeadIdException e = assertThrows(WrongHeadIdException.class,
                    () -> organizationRepository.update(given.getId(),given.getOrgName(), given.getId()+1));
            assertEquals(given.getId(), e.id);
        }

        @Test
        public void whenSameName_thenThrows() throws Exception {
            Organization[] given = create(2);
            SameNameException e = assertThrows(SameNameException.class,
                    () -> organizationRepository.update(
                            given[1].getId(),
                            given[0].getOrgName(),
                            given[1].getHeadOrgId()));
            assertEquals(given[0].getOrgName(), e.name);
        }
    }

}
