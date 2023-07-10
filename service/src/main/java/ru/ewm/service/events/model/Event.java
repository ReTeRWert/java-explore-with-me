package ru.ewm.service.events.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ewm.service.categories.Category;
import ru.ewm.service.events.enums.EventState;
import ru.ewm.service.users.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String annotation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    @JsonFormat(pattern = "yyyy:MM:dd HH:mm:ss")
    private LocalDateTime createdOn;
    private String description;

    @JsonFormat(pattern = "yyyy:MM:dd HH:mm:ss")
    private LocalDateTime eventDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @Embedded
    private Location location;

    private Boolean isPaid;
    private int participantLimit;

    @JsonFormat(pattern = "yyyy:MM:dd HH:mm:ss")
    private LocalDateTime publishedOn;

    private Boolean isRequestModeration;

    @Enumerated(EnumType.STRING)
    private EventState state;

    private String title;

}
