package ru.ewm.service.comments.likes;

public class LikeMapper {
    public static LikeDto toLikeDto(Like like) {
        return new LikeDto(
                like.getId(),
                like.getUser().getId(),
                like.getComment().getId(),
                like.getIsLike()
        );
    }
}
