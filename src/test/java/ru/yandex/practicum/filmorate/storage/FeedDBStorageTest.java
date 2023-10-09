package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDaoStorage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.ReviewDaoStorage.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDaoStorage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FeedDBStorageTest {
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final ReviewDbStorage reviewDbStorage;

    private Film film1;
    private Film film2;

    private User user1;
    private User user2;
    private User user3;
    private User user4;

    private Review review1;

    @BeforeEach
    public void fillDataBase() {
        user1 = new User(1, "mail@yandex.ru", "login1", "",
                LocalDate.of(2001, 12, 12));
        user2 = new User(2, "mail@yandex.ru", "login2", "",
                LocalDate.of(1969, 5, 12));
        user3 = new User(3, "mail@yandex.ru", "login3", "",
                LocalDate.of(2068, 11, 12));
        user4 = new User(4, "mail@yandex.ru", "login4", "",
                LocalDate.of(2001, 12, 12));
        userDbStorage.add(user1);
        userDbStorage.add(user2);
        userDbStorage.add(user3);
        userDbStorage.add(user4);

        film1 = new Film(1, "SEVEN-1", "Бред Питт зря переехал с семьей",
                LocalDate.of(1995, 9, 22), 127L, new Mpa(4, "R"));
        film2 = new Film(2, "SEVEN-1", "Бред Питт зря переехал с семьей",
                LocalDate.of(1995, 9, 22), 127L, new Mpa(4, "R"));
        filmDbStorage.add(film1);
        filmDbStorage.add(film2);

        review1 = new Review(1, "content", true, user1.getId(), film1.getId(), 1);
    }

    @Test
    public void shouldAddFriendAndReturnFriendsOneActivitySuccessfully() {
        userDbStorage.addFriend(user1.getId(), user2.getId());
        userDbStorage.addFriend(user1.getId(), user3.getId());
        userDbStorage.addFriend(user2.getId(), user4.getId());
        Assertions.assertThat(userDbStorage.getUserFeed(user1.getId()).size()).isEqualTo(2);
        Assertions.assertThat(userDbStorage.getUserFeed(user1.getId()).get(0).getEventType()).isEqualTo(EventType.FRIEND);
        Assertions.assertThat(userDbStorage.getUserFeed(user1.getId()).get(0).getOperation()).isEqualTo(Operation.ADD);
    }

    @Test
    void shouldRemoveFriendAndReturnTwoActivitiesSuccessfully() {
        userDbStorage.addFriend(user1.getId(), user2.getId());
        userDbStorage.addFriend(user1.getId(), user3.getId());
        userDbStorage.addFriend(user2.getId(), user4.getId());
        userDbStorage.deleteFriend(user2.getId(), user4.getId());
        List<Event> list = userDbStorage.getUserFeed(user1.getId());
        List<Event> list1 = userDbStorage.getUserFeed(user2.getId());
        Assertions.assertThat(list.size()).isEqualTo(2);
        Assertions.assertThat(list.get(1).getEventType()).isEqualTo(EventType.FRIEND);
        Assertions.assertThat(list.get(1).getOperation()).isEqualTo(Operation.ADD);
        Assertions.assertThat(list1.get(1).getEntityId()).isEqualTo(user4.getId());
        Assertions.assertThat(list1.get(1).getOperation()).isEqualTo(Operation.REMOVE);
    }

    @Test
    void shouldAddLikeAndReturnThreeActivitiesSuccessfully() {
        userDbStorage.addFriend(user1.getId(), user2.getId());
        userDbStorage.addFriend(user1.getId(), user3.getId());
        filmDbStorage.addLike(film1.getId(), user1.getId());
        filmDbStorage.addLike(film1.getId(), user3.getId());
        filmDbStorage.addLike(film2.getId(), user3.getId());
        List<Event> list = userDbStorage.getUserFeed(user1.getId());
        Assertions.assertThat(list.size()).isEqualTo(3);
        Assertions.assertThat(list.get(0).getEventType()).isEqualTo(EventType.FRIEND);
        Assertions.assertThat(list.get(0).getOperation()).isEqualTo(Operation.ADD);
    }

    @Test
    void shouldRemoveLikeAndReturnFourActivitiesSuccessfully() {
        userDbStorage.addFriend(user1.getId(), user2.getId());
        userDbStorage.addFriend(user1.getId(), user3.getId());
        filmDbStorage.addLike(film1.getId(), user1.getId());
        filmDbStorage.deleteLike(film1.getId(), user3.getId());
        List<Event> list = userDbStorage.getUserFeed(user1.getId());
        Assertions.assertThat(list.size()).isEqualTo(3);
        Assertions.assertThat(list.get(0).getEventType()).isEqualTo(EventType.FRIEND);
        Assertions.assertThat(list.get(0).getOperation()).isEqualTo(Operation.ADD);
    }

    @Test
    void shouldAddReviewSuccessfully() {
        reviewDbStorage.addReview(review1);
        Event event = userDbStorage.getUserFeed(user1.getId()).get(0);
        Assertions.assertThat(event.getUserId()).isEqualTo(1);
        Assertions.assertThat(event.getOperation()).isEqualTo(Operation.ADD);
    }

    @Test
    void shouldDeleteReviewSuccessfully() {
        reviewDbStorage.addReview(review1);
        Assertions.assertThat(userDbStorage.getUserFeed(user1.getId()).size()).isEqualTo(1);
        reviewDbStorage.deleteReview(review1.getReviewId());
        Assertions.assertThat(userDbStorage.getUserFeed(user1.getId()).size()).isEqualTo(2);
    }

    @Test
    void shouldUpdateReviewSuccessfully() {
        reviewDbStorage.addReview(review1);
        Assertions.assertThat(userDbStorage.getUserFeed(user1.getId()).size()).isEqualTo(1);
        review1.setContent("new content");
        reviewDbStorage.updateReview(review1);
        Assertions.assertThat(userDbStorage.getUserFeed(user1.getId()).get(1).getOperation()).isEqualTo(Operation.UPDATE);
    }
}