package ru.ewm.service.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ewm.service.util.RequestState;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {

    private LocalDateTime created;
    private Long event;
    private Long id;
    private Long requester;
    private RequestState status;

}
