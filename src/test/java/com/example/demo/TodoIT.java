package com.example.demo;

import com.example.demo.entity.Todo;
import com.example.demo.repository.TodoRepository;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoIT implements TestcontainersInitializer {

    @Autowired
    TodoRepository todoRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private Todo savedTodo;

    @BeforeEach
    void setUp() {
        todoRepository.deleteAll();
        Todo todo = new Todo(null, UUID.randomUUID(), "Test todo",
                "Description de test", false, Instant.now(), Instant.now());
        savedTodo = todoRepository.save(todo);
    }

    @Test
    void getAll_returnsAllTodos() {
        ResponseEntity<Todo[]> response = restTemplate.getForEntity("/todo", Todo[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getById_nonExistingId_returns404() {
        ResponseEntity<Todo> response = restTemplate.getForEntity("/todo/999999", Todo.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
        void update_existingTodo_returnsUpdatedTodo() {
        savedTodo.setTitle("Titre modifié");
        savedTodo.setCompleted(true);

        HttpEntity<Todo> requestEntity = new HttpEntity<>(savedTodo);
        ResponseEntity<Todo> response = restTemplate.exchange(
                "/todo", HttpMethod.PUT, requestEntity, Todo.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTitle()).isEqualTo("Titre modifié");
    }
}