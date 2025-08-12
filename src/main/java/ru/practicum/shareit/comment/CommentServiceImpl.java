package ru.practicum.shareit.comment;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepo;
    private final BookingRepository bookingRepo;
    private final ItemRepository itemRepo;
    private final UserRepository userRepo;
    private final Clock clock;

    @Override
    @Transactional
    public CommentDto add(long itemId, long userId, @Valid CommentCreateDto dto) {
        //Найдём вещь и автора
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        User author = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        //Текущее время (один раз!)
        LocalDateTime now = LocalDateTime.now(clock);


        //Разрешено комментировать только после завершённой APPROVED-аренды
        boolean allowed = bookingRepo.existsByBooker_IdAndItem_IdAndStatusAndEndBefore(
                userId, itemId, BookingStatus.APPROVED, now);

        if (!allowed) {
            throw new ValidationException("Комментарий доступен только после завершённой аренды");
        }

        // Сохраняем комментарий
        Comment c = new Comment();
        c.setText(dto.getText().trim());
        c.setItem(item);
        c.setAuthor(author);
        c.setCreated(now);

        Comment saved = commentRepo.save(c);
        log.info("comment-saved id={}, item={}, author={}", saved.getId(), itemId, userId);

        // Отдаём DTO
        return CommentMapper.toDto(saved);
    }


    @Override
    @Transactional
    public List<CommentDto> listForItem(long itemId) {
        return commentRepo.findByItem_IdOrderByCreatedDesc(itemId)
                .stream().map(CommentMapper::toDto).toList();
    }
}

