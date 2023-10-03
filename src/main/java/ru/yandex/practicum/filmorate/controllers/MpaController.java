package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/mpa")
@RestController
public class MpaController {

    private final MpaService mpaService;

    @ResponseBody
    @GetMapping("/{id}")
    public Mpa getMpa(@PathVariable String id) {
        return mpaService.getMpa(Integer.parseInt(id));
    }

    @ResponseBody
    @GetMapping
    public List<Mpa> getAllMpa() {
        return mpaService.getAllMpa();
    }
}
