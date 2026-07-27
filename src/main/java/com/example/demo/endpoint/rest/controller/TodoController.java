package com.example.demo.endpoint.rest.controller;

import com.example.demo.entity.Todo;
import com.example.demo.service.TodoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todo")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // USER : uniquement ses propres todos
    @GetMapping
    public List<Todo> getMine(Authentication authentication) {
        return todoService.findAllForUser(authentication.getName());
    }

    // USER : un de ses todos par id
    @GetMapping("/{id}")
    public ResponseEntity<Todo> getMineById(@PathVariable Long id, Authentication authentication) {
        return todoService.findByIdForUser(id, authentication.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // USER : créer un todo (appartient automatiquement à l'utilisateur connecté)
    @PostMapping
    public ResponseEntity<Todo> create(@RequestBody Todo todo, Authentication authentication) {
        return ResponseEntity.ok(todoService.create(todo, authentication.getName()));
    }

    // USER : modifier un de ses todos
    @PutMapping
    public ResponseEntity<Todo> update(@RequestBody Todo todo, Authentication authentication) {
        return todoService.update(todo, authentication.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ADMIN uniquement : tous les todos, tous utilisateurs confondus
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Todo> getAll() {
        return todoService.findAll();
    }

    // ADMIN uniquement : un todo par id, peu importe le propriétaire
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Todo> getByIdAdmin(@PathVariable Long id) {
        return todoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}