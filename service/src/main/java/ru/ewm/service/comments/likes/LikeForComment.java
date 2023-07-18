package ru.ewm.service.comments.likes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LikeForComment {
    private Long commentId;
    private Long count;
    private Boolean isLike;

}
