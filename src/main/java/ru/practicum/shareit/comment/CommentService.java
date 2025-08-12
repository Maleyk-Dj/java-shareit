package ru.practicum.shareit.comment;

import java.util.List;

public interface CommentService {

    CommentDto add(long itemId, long userId, CommentCreateDto dto);

    List<CommentDto> listForItem(long itemId);


}
