# java-filmorate
Template repository for Filmorate project.
![image](https://github.com/Bo4oN/java-filmorate/assets/126141420/758a24df-79f3-4680-8b84-b4f30492b63c)

## add-director.

### Описание задачи
В информацию о фильмах должно быть добавлено имя режиссёра. После этого должна появиться следующая функциональность.

* Вывод всех фильмов режиссёра, отсортированных по количеству лайков.
* Вывод всех фильмов режиссёра, отсортированных по годам.
### API

**GET /films/director/{directorId}?sortBy=[year,likes]**
Возвращает список фильмов режиссера отсортированных по количеству лайков или году выпуска.

**POST /films**

``` json
{
    "name": "New film",
    "releaseDate": "1999-04-30",
    "description": "New film about friends",
    "duration": 120,
    "mpa": { "id": 3},
    "genres": [{ "id": 1}],
    "director": [{ "id": 1}]
}
```
**GET /directors** - Список всех режиссёров

**GET /directors/{id}**- Получение режиссёра по id

**POST /directors** - Создание режиссёра

**PUT /directors** - Изменение режиссёра

``` json
{
"id": 1,
"name": "New director"
}
```
**DELETE /directors/{id}** - Удаление режиссёра