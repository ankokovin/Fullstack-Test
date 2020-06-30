package ankokovin.fullstacktest.WebServer;


import ankokovin.fullstacktest.WebServer.Exceptions.BaseException;
import ankokovin.fullstacktest.WebServer.Exceptions.SameNameException;
import ankokovin.fullstacktest.WebServer.Exceptions.WrongHeadIdException;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Models.CreateOrganizationInput;
import ankokovin.fullstacktest.WebServer.Repos.OrganizationRepository;
import ankokovin.fullstacktest.WebServer.Services.OrganizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class OrganizationServiceTests {

    @Autowired
    private OrganizationService organizationService;

    @MockBean
    private OrganizationRepository organizationRepository;

    @BeforeEach
    public void setUp() throws BaseException {
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
                .thenReturn(new Organization(2, name2, 1));

        String name3 = "ООО Тест-3";
        Mockito.when(organizationRepository.insert(name3, 1))
                .thenReturn(3)
                .thenThrow(new SameNameException(name3));
        Mockito.when(organizationRepository.getById(3))
                .thenReturn(new Organization(3, name3, 1));

        Mockito.when(organizationRepository.insert(name3, 3))
                .thenThrow(new WrongHeadIdException(3));

    }

    @Test
    public void whenCreateCorrectNoHead_thenOrganizationCreates() throws BaseException {
        String name = "ООО Тест";

        Organization actual = organizationService.create(new CreateOrganizationInput(name, null));
        assertEquals(new Organization(1, name, null), actual);
    }

    @Test
    public void whenCreateCorrectHead_thenOrganizationCreates() throws BaseException {
        String name = "ООО Тест-2";

        Organization actual = organizationService.create(new CreateOrganizationInput(name, 1));
        assertEquals(new Organization(2, name, 1), actual);
    }

    @Test
    public void whenCreateSameName_thenThrowsError() throws BaseException {
        String name = "ООО Тест-3";

        Organization actual_first = organizationService.create(new CreateOrganizationInput(name, 1));
        assertEquals(new Organization(3, name, 1), actual_first);

        SameNameException exception = assertThrows(SameNameException.class,
                () -> organizationService.create(new CreateOrganizationInput(name, 1)));
        assertEquals(name, exception.name);
    }

    public void whenCreateWrongHead_thenThrows() throws BaseException {
        String name = "ООО Тест-3";

        Organization actual_first = organizationService.create(new CreateOrganizationInput(name, 1));
    }

}
