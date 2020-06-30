package ankokovin.fullstacktest.WebServer;

import ankokovin.fullstacktest.WebServer.Controllers.OrganizationsController;
import ankokovin.fullstacktest.WebServer.Exceptions.SameNameException;
import ankokovin.fullstacktest.WebServer.Generated.tables.pojos.Organization;
import ankokovin.fullstacktest.WebServer.Models.CreateOrganizationInput;
import ankokovin.fullstacktest.WebServer.Services.OrganizationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.JSON;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(OrganizationsController.class)
@Disabled
class OrganizationControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private OrganizationService service;


    @Test
    public void whenCreateCorrectNoHead_thenOrganizationCreates() throws Exception {
        String name = "Алексей";
        CreateOrganizationInput input = new CreateOrganizationInput();
        input.name = name;
        given(service.create(new CreateOrganizationInput(name, null)))
                .willReturn(new Organization(1,name, null));

        ObjectMapper objectMapper = new ObjectMapper();

        mvc.perform(post("/api/organization")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orgName", is(name)))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.headOrgId", is(nullValue())));

    }
}
