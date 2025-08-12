package ru.practicum.shareit.comment;

public class CommentMapper {
    public static CommentDto toDto(Comment c) {

        CommentDto dto = new CommentDto();
        dto.setId(c.getId());
        dto.setText(c.getText());
        dto.setAuthorName(c.getAuthor().getName());
        dto.setCreated(c.getCreated());
        return dto;
    }
}
