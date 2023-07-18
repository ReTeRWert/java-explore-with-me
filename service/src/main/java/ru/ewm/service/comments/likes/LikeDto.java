package ru.ewm.service.comments.likes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LikeDto {
    private Long id;
    private Long userId;
    private Long commentId;
    private Boolean isLike;
}
