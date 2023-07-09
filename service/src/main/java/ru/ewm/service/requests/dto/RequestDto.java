package ru.ewm.service.requests.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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


    @JsonFormat(pattern = "yyyy:MM:dd HH:mm:ss")
    private LocalDateTime created;

    private Long event;
    private Long id;
    private Long requester;
    private RequestState status;

}
