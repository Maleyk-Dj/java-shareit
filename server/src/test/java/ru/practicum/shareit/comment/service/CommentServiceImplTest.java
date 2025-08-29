package ru.practicum.shareit.comment.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.ShareItApp; // Импорт вашего основного класса приложения server
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Transactional
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = ShareItApp.class) // <-- ИСПРАВЛЕНО
class CommentServiceImplTest {

    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {

        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        userRepository.save(booker);

        item = new Item();
        item.setName("Drill");
        item.setDescription("A powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);
    }

    @Test
    void testAddComment_Success() {

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        bookingRepository.save(booking);

        CommentCreateDto commentDto = new CommentCreateDto("Great item!");

        CommentDto createdComment = commentService.add(item.getId(), booker.getId(), commentDto);

        assertNotNull(createdComment);
        assertNotNull(createdComment.getId());
        assertEquals("Great item!", createdComment.getText());
        assertEquals(booker.getName(), createdComment.getAuthorName());
    }

    @Test
    void testAddComment_NoCompletedBooking_ThrowsValidationException() {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        bookingRepository.save(booking);

        CommentCreateDto commentDto = new CommentCreateDto("Great item!");

        assertThrows(ValidationException.class, () -> commentService.add(item.getId(), booker.getId(), commentDto));
    }

    @Test
    void testAddComment_ItemNotFound_ThrowsNotFoundException() {
        CommentCreateDto commentDto = new CommentCreateDto("Test comment");
        assertThrows(NotFoundException.class, () -> commentService.add(999L, booker.getId(), commentDto));
    }

    @Test
    void testAddComment_UserNotFound_ThrowsNotFoundException() {
        CommentCreateDto commentDto = new CommentCreateDto("Test comment");
        assertThrows(NotFoundException.class, () -> commentService.add(item.getId(), 999L, commentDto));
    }
    @Test
    void listForItem_returnsEmpty_whenNoComments() {
        List<CommentDto> dtos = commentService.listForItem(item.getId());
        assertThat(dtos).isEmpty();
    }

    @Test
    void listForItem_returnsOnlyThisItemComments_sortedByCreatedDesc_andMapsDto() {
        // Комментарии к целевой вещи
        LocalDateTime now = LocalDateTime.now();
        Comment c1 = saveComment(item, booker, "first",  now.minusMinutes(5));
        Comment c2 = saveComment(item, booker, "second", now.minusMinutes(3));
        Comment c3 = saveComment(item, booker, "third",  now.minusMinutes(1));

        // Комментарий к другой вещи — не должен попасть в результат
        Item otherItem = new Item();
        otherItem.setName("Saw");
        otherItem.setDescription("Other item");
        otherItem.setAvailable(true);
        otherItem.setOwner(owner);
        itemRepository.save(otherItem);
        saveComment(otherItem, booker, "other", now.minusMinutes(2));

        // act
        List<CommentDto> dtos = commentService.listForItem(item.getId());

        // assert: только 3 комментария текущей вещи
        assertThat(dtos).hasSize(3);

        // порядок по created: DESC → third, second, first
        assertThat(dtos).extracting(CommentDto::getText)
                .containsExactly("third", "second", "first");

        // маппинг полей DTO
        assertThat(dtos.get(0).getAuthorName()).isEqualTo(booker.getName());
        assertThat(dtos.get(0).getCreated()).isEqualTo(c3.getCreated());
    }

    private Comment saveComment(Item item, User author, String text, LocalDateTime created) {
        Comment c = new Comment();
        c.setText(text);
        c.setItem(item);
        c.setAuthor(author);
        c.setCreated(created);
        return commentRepository.save(c);
    }
}