package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Entity;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
public class SimpleController {
        private final HashMap<Integer, Entity> entities = new HashMap<>();
        private int nextId = 1;

        @ResponseBody
        @PostMapping
        public Entity addEntity(@Valid @RequestBody Entity entity) {
            log.info("Получен запрос на добовление пользователя.");
            entity.setId(nextId++);
            entities.put(entity.getId(), entity);
            log.info("Пользователь успешно добавлен.");
            return entity;
        }

        @ResponseBody
        @PutMapping
        public Entity updateEntity(@Valid @RequestBody Entity user) {
            log.info("Получен запрос на обновление пользователя.");
            if (entities.containsKey(user.getId())) {
                entities.put(user.getId(), user);
                log.info("Пользователь успешно изменен.");
            } else {
                log.debug("Обновляется не добавленный пользователь, его ID - '{}'", user.getId());
                throw new exceptions.ValidationException("Не добавлен пользователь с ID - " + user.getId());

            }
            return user;
        }

        @ResponseBody
        @GetMapping
        public List<Entity> getEntity() {
            return new ArrayList<>(entities.values());
        }

}
