package ru.practicum.shareit.item.comment;

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

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentDto add(long itemId, long userId, @Valid CommentCreateDto dto) {
        //Найдём вещь и автора
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        LocalDateTime now = LocalDateTime.now();

        //Разрешено комментировать только после завершённой APPROVED-аренды
        boolean allowed = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
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

        Comment saved = commentRepository.save(c);
        log.info("comment-saved id={}, item={}, author={}", saved.getId(), itemId, userId);

        // Отдаём DTO
        return CommentMapper.toDto(saved);
    }


    @Override
    @Transactional
    public List<CommentDto> listForItem(long itemId) {
        return commentRepository.findByItemIdOrderByCreatedDesc(itemId)
                .stream().map(CommentMapper::toDto).toList();
    }
}

