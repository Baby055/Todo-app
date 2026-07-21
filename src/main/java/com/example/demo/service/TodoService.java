package com.example.demo.service;

import com.example.demo.entity.Todo;
import com.example.demo.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> findAll() {
        return todoRepository.findAll();
    }

    public Optional<Todo> findById(Long id) {
        return todoRepository.findById(id);
    }

    public Optional<Todo> update(Todo todo) {
        if (todo.getId() == null || !todoRepository.existsById(todo.getId())) {
            return Optional.empty();
        }
        return Optional.of(todoRepository.save(todo));
    }
}