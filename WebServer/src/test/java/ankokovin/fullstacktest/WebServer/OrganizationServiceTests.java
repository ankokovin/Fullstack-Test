package ankokovin.fullstacktest.WebServer;


import ankokovin.fullstacktest.WebServer.Exceptions.SameNameException;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Repos.OrganizationRepository;
import ankokovin.fullstacktest.WebServer.Services.OrganizationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
public class OrganizationServiceTests {

    @TestConfiguration
    static class OrganizationServiceTestsConfiguration {

        @Bean
        public OrganizationService organizationService() {
            return new OrganizationService();
        }
    }

    @Autowired
    private OrganizationService organizationService;

    @MockBean
    private OrganizationRepository organizationRepository;

    @Before
    public void setUp() throws SameNameException {
        String name1 = "ООО Тест";
        Mockito.when(organizationRepository.insert(name1, null))
                .thenReturn(1)
                .thenThrow(new SameNameException(name1));
        Mockito.when(organizationRepository.getById(1))
                .thenReturn(new Organization(1, name1, null));

        String name2 = "ООО Тест-2";
        Mockito.when(organizationRepository.insert(name2, 1))
                .thenReturn(2)
                .thenThrow(new SameNameException(name2));
        Mockito.when(organizationRepository.getById(2))
                .thenReturn(new Organization(2,name2, 1));

        String name3 = "ООО Тест-3";
        Mockito.when(organizationRepository.insert(name3, 1))
                .thenReturn(3)
                .thenThrow(new SameNameException(name3));
        Mockito.when(organizationRepository.getById(3))
                .thenReturn(new Organization(3,name3, 1));

    }



    @Test
    public void whenCreateCorrectNoHead_thenOrganizationCreates() throws SameNameException {
        String name = "ООО Тест";

        Organization actual = organizationService.create(name, null);
        Assert.assertEquals(new Organization(1,name, null), actual);
    }
    @Test
    public void whenCreateCorrectHead_thenOrganizationCreates() throws SameNameException {
        String name = "ООО Тест-2";

        Organization actual = organizationService.create(name, 1);
        Assert.assertEquals(new Organization(2,name, 1), actual);
    }
    @Test
    public void whenCreateSameName_thenThrowsError() throws SameNameException {
        String name = "ООО Тест-3";

        Organization actual_first = organizationService.create(name, 1);
        Assert.assertEquals(new Organization(3,name, 1), actual_first);

        SameNameException exception = Assert.assertThrows(SameNameException.class,
                () -> organizationService.create(name, 1));
        Assert.assertEquals(name, exception.name);
    }
}
