package com.example.demo.service;

import com.example.demo.entity.Todo;
import com.example.demo.entity.User;
import com.example.demo.repository.TodoRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoService(TodoRepository todoRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    public List<Todo> findAllForUser(String username) {
        return todoRepository.findByOwnerUsername(username);
    }

    public List<Todo> findAll() {
        return todoRepository.findAll();
    }

    public Optional<Todo> findById(Long id) {
        return todoRepository.findById(id);
    }

    public Optional<Todo> findByIdForUser(Long id, String username) {
        return todoRepository.findById(id)
                .filter(todo -> todo.getOwner() != null && todo.getOwner().getUsername().equals(username));
    }

    public Todo create(Todo todo, String username) {
        User owner = userRepository.findByUsername(username).orElseThrow();
        todo.setId(null);
        todo.setUuid(UUID.randomUUID());
        todo.setOwner(owner);
        todo.setCreated_at(Instant.now());
        todo.setUpdated_at(Instant.now());
        return todoRepository.save(todo);
    }

    public Optional<Todo> update(Todo todo, String username) {
        return todoRepository.findById(todo.getId())
                .map(existing -> {
                    if (existing.getOwner() == null || !existing.getOwner().getUsername().equals(username)) {
                        throw new AccessDeniedException("Vous ne pouvez modifier que vos propres todos.");
                    }
                    existing.setTitle(todo.getTitle());
                    existing.setDescription(todo.getDescription());
                    existing.setCompleted(todo.isCompleted());
                    existing.setUpdated_at(Instant.now());
                    return todoRepository.save(existing);
                });
    }
}