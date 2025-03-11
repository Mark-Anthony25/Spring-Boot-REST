package com.example.crudapi.controller;

import com.example.crudapi.model.User;
import com.example.crudapi.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class) // Test only the UserController layer
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository; // Mock the repository to isolate the controller logic

    @Autowired
    private ObjectMapper objectMapper; // Used to convert objects to JSON for requests

    @Test
    void whenValidUserInput_thenReturnsCreatedStatus() throws Exception {
        // Arrange: Create a valid user object
        User validUser = new User();
        validUser.setName("John Doe");
        validUser.setEmail("johndoe@example.com");

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(validUser);

        // Act and Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk()); // Expect HTTP 200 OK on success
    }

    @Test
    void whenInvalidEmail_thenReturnsBadRequest() throws Exception {
        // Arrange: User with invalid email
        User invalidUser = new User();
        invalidUser.setName("John Doe");
        invalidUser.setEmail("notanemail");

        // Act and Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest()) // Expect HTTP 400 Bad Request
                .andExpect(jsonPath("$.email").value("Invalid email format")); // Check the exact error message
    }

    @Test
    void whenBlankName_thenReturnsBadRequest() throws Exception {
        // Arrange: User with blank name
        User invalidUser = new User();
        invalidUser.setName("");
        invalidUser.setEmail("johndoe@example.com");

        // Act and Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest()) // Expect HTTP 400 Bad Request
                .andExpect(jsonPath("$.name").value("Name is Required")); // Check name validation error
    }

    @Test
    void whenEmailIsBlank_thenReturnsBadRequest() throws Exception {
        // Arrange: User with blank email
        User invalidUser = new User();
        invalidUser.setName("John Doe");
        invalidUser.setEmail("");

        // Act and Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest()) // Expect HTTP 400 Bad Request
                .andExpect(jsonPath("$.email").value("Email is Required")); // Check email validation error
    }

    @Test
    void whenBothNameAndEmailAreBlank_thenReturnsBadRequest() throws Exception {
        // Arrange: User with blank name and email
        User invalidUser = new User();
        invalidUser.setName("");
        invalidUser.setEmail("");

        // Act and Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest()) // Expect HTTP 400 Bad Request
                .andExpect(jsonPath("$.name").value("Name is Required")) // Check name validation error
                .andExpect(jsonPath("$.email").value("Email is Required")); // Check email validation error
    }
}