package ru.ewm.service.events.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import ru.ewm.service.categories.Category;
import ru.ewm.service.users.User;
import ru.ewm.service.util.EventState;

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

    private LocalDateTime createdOn;
    private String description;
    private LocalDateTime eventDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @Embedded
    private Location location;

    private Boolean paid;
    private int participantLimit;
    private LocalDateTime publishedOn;
    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    private EventState state;

    private String title;

}
