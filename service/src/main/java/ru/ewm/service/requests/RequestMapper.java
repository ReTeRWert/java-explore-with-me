package ru.ewm.service.requests;

import ru.ewm.service.requests.dto.RequestDto;

public class RequestMapper {

    public static RequestDto toRequestDto(Request request) {
        return new RequestDto(
                request.getCreated(),
                request.getEvent().getId(),
                request.getId(),
                request.getRequester().getId(),
                request.getStatus()
        );
    }
}
