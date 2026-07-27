package com.example.demo;

import com.example.demo.entity.Role;
import com.example.demo.entity.Todo;
import com.example.demo.entity.User;
import com.example.demo.repository.TodoRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoIT implements TestcontainersInitializer {

    @Autowired
    TodoRepository todoRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    private TestRestTemplate restTemplate;

    private Todo savedTodo;
    private String userToken;

    @BeforeEach
    void setUp() {
        todoRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("alice");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(Role.USER);
        userRepository.save(user);

        userToken = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        Todo todo = new Todo(null, UUID.randomUUID(), "Test todo",
                "Description de test", false, Instant.now(), Instant.now(), user);
        savedTodo = todoRepository.save(todo);
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);
        return headers;
    }

    @Test
    void getAll_returnsOnlyOwnTodos() {
        ResponseEntity<Todo[]> response = restTemplate.exchange(
                "/todo", HttpMethod.GET, new HttpEntity<>(authHeaders()), Todo[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getById_nonExistingId_returns404() {
        ResponseEntity<Todo> response = restTemplate.exchange(
                "/todo/999999", HttpMethod.GET, new HttpEntity<>(authHeaders()), Todo.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void update_ownTodo_returnsUpdatedTodo() {
        savedTodo.setTitle("Titre modifié");
        savedTodo.setCompleted(true);

        HttpEntity<Todo> requestEntity = new HttpEntity<>(savedTodo, authHeaders());
        ResponseEntity<Todo> response = restTemplate.exchange(
                "/todo", HttpMethod.PUT, requestEntity, Todo.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTitle()).isEqualTo("Titre modifié");
    }

    @Test
    void getAll_withoutAuth_returnsUnauthorized() {
        ResponseEntity<String> response = restTemplate.getForEntity("/todo", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}