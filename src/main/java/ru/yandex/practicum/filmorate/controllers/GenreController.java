package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/genres")
@RestController
public class GenreController {
    private final GenreService genreService;

    @ResponseBody
    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable String id) {
        return genreService.getGenre(Integer.parseInt(id));
    }

    @ResponseBody
    @GetMapping
    public List<Genre> getAllGenres() {
        return genreService.getAllGenres();
    }
}
