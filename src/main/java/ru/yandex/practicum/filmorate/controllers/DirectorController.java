package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService service;

    @PostMapping
    public Director create(@RequestBody Director director) {
        return service.create(director);
    }

    @PutMapping
    public Director update(@RequestBody @Valid Director director) {
        return service.update(director);
    }

    @GetMapping("/{id}")
    public Director get(@PathVariable int id) {
        return service.get(id);
    }

    @GetMapping
    public List<Director> getAll() {
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        service.delete(id);
    }
}
