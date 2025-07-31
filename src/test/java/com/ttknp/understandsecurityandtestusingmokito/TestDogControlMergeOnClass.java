package com.ttknp.understandsecurityandtestusingmokito;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttknp.understandsecurityandtestusingmokito.configuration.SecurityConfig;
import com.ttknp.understandsecurityandtestusingmokito.controller.DogControl;
import com.ttknp.understandsecurityandtestusingmokito.entity.Dog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ** Test a Secured Spring Web MVC Endpoint with MockMvc
@Import(SecurityConfig.class) // For using security config
@WebMvcTest(controllers = DogControl.class)
/**
 Note should once for authenticate. roles or authorities
 @WithMockUser(username="admin",password = "12345",authorities={"ADMIN"})  *** Note username , password it's optional
*/
@WithMockUser(username = "admin", password = "12345", authorities = {"write", "read"})
public class TestDogControlMergeOnClass {
    /**
     MockMvc to perform integration testing of REST controllers.
     The @WebMvcTest annotation auto configure MockMvc instance as well.
     It disables full auto configuration and instead applies only configuration relevant to MVC tests.
     Using DogControl.class as the parameter, we are asking to initialize only one web controller
    */
    @Autowired
    private MockMvc mvc;


    @Test
    @WithMockUser(authorities = {"read"}) // Also write can access this
    void shouldReturn403Forbidden() throws Exception {
        MockHttpServletRequestBuilder getAllDogs = MockMvcRequestBuilders.get("/api/dogs");
        mvc.perform(getAllDogs)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn202AcceptedOnHttpGet() throws Exception {
        // same **
        mvc.perform(MockMvcRequestBuilders.get("/api/dogs")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(1001));
                /*  Why [0] just notice
                    [{"id":1001,"name":"Harry","type":"Alaskan Malamute","gender":"Male","price":12000.0},{"id":1002,"name":"Jia Ant","type":"Basset Hound","gender":"Male","price":19000.0},{"id":1003,"name":"Harry","type":"Beagle","gender":"Female","price":22000.0}]
                */
    }

    @Test
    void shouldReturn202AcceptedOnHttpGetHasParam() throws Exception {
        mvc.perform(get("/api/dogs/{id}", 1001)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()) // ** importance. behind the sense it prepares the real response
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                // ** MockMvcResultMatchers.jsonPath(..) it returns following your api
                .andExpect(jsonPath("$.id").value(1001)); // ** $.<map name key of body> then take a expected value

    }

    @Test
    void shouldReturn202AcceptedOnHttpGetHasParam2() throws Exception {
        mvc.perform(get("/api/dogs/by")
                         // mock param as by?id=1001
                        .param("id","1001")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()) // ** importance. behind the sense it prepares the real response
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(1001)); // ** $.<map name key of body> then take a expected value

    }

    @Test
    void shouldReturn202AcceptedOnHttpPost() throws Exception {
        mvc.perform(post("/api/dogs")
                        .content(asJsonString(new Dog(1004L, "Harry", "Beagle", "Female", 52000.0)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                // ** MockMvcResultMatchers.status().* it returns following your api
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void shouldReturn202AcceptedOnHttpPut() throws Exception {
        mvc.perform(put("/api/dogs/{id}",1002)
                        .content(asJsonString(new Dog(0L, "Harry", "Beagle", "Female", 52000.0)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                // ** MockMvcResultMatchers.status().* it returns following your api
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void shouldReturn202AcceptedOnHttpDeleteHasParam() throws Exception {
        mvc.perform(delete("/api/dogs/{id}", 1003)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    public String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
