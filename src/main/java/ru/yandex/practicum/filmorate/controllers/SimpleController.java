package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class SimpleController<T extends Entity> {
        protected final HashMap<Integer, T> entities = new HashMap<>();
        private int nextId = 1;

        public T addEntity(T entity) {
            entity.setId(nextId++);
            entities.put(entity.getId(), entity);
            return entity;
        }

        public T updateEntity(T entity) {
            if (entities.containsKey(entity.getId())) {
                entities.put(entity.getId(), entity);
            } else {
                log.debug("Не найден элемент для обновления, его ID - '{}'", entity.getId());
                throw new exceptions.ValidationException("Нет элемента с ID - " + entity.getId());
            }
            return entity;
        }

        public List<T> getEntity() {
            return new ArrayList<>(entities.values());
        }
}
