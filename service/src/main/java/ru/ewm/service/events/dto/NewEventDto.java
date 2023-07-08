package ru.ewm.service.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.ewm.service.events.model.Location;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    @Positive
    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private Location location;

    @Value("false")
    private Boolean paid;

    @PositiveOrZero
    @Value("0")
    private Integer participantLimit;

    @Value("true")
    private Boolean requestModeration;

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
}
