package ru.ewm.service.events;

import ru.ewm.service.categories.Category;
import ru.ewm.service.events.dto.FullEventDto;
import ru.ewm.service.events.dto.NewEventDto;
import ru.ewm.service.events.dto.ShortEventDto;
import ru.ewm.service.events.model.Event;
import ru.ewm.service.users.UserMapper;

import static ru.ewm.service.categories.CategoryMapper.toCategoryDto;

public class EventMapper {
    public static Event toEvent(NewEventDto newEventDto) {
        Event event = new Event();
        Category category = new Category();

        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(category);
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDto.getEventDate());
        event.setLocation(newEventDto.getLocation());
        event.setPaid(newEventDto.getPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setTitle(newEventDto.getTitle());

        return event;
    }

    public static FullEventDto toEventFullDto(Event event) {
        FullEventDto fullEventDto = new FullEventDto();

        fullEventDto.setAnnotation(event.getAnnotation());
        fullEventDto.setCategory(toCategoryDto(event.getCategory()));
        fullEventDto.setCreatedOn(event.getCreatedOn());
        fullEventDto.setDescription(event.getDescription());
        fullEventDto.setEventDate(event.getEventDate());
        fullEventDto.setId(event.getId());
        fullEventDto.setInitiator(UserMapper.toShortUserDto(event.getInitiator()));
        fullEventDto.setLocation(event.getLocation());
        fullEventDto.setPaid(event.getPaid());
        fullEventDto.setParticipantLimit(event.getParticipantLimit());
        fullEventDto.setPublishedOn(event.getPublishedOn());
        fullEventDto.setRequestModeration(event.getRequestModeration());
        fullEventDto.setState(event.getState());
        fullEventDto.setTitle(event.getTitle());
        fullEventDto.setViews(0L);

        return fullEventDto;
    }

    public static ShortEventDto toEventShortDto(Event event) {
        ShortEventDto shortEventDto = new ShortEventDto();

        shortEventDto.setAnnotation(event.getAnnotation());
        shortEventDto.setCategory(toCategoryDto(event.getCategory()));
        shortEventDto.setEventDate(event.getEventDate());
        shortEventDto.setId(event.getId());
        shortEventDto.setInitiator(UserMapper.toShortUserDto(event.getInitiator()));
        shortEventDto.setPaid(event.getPaid());
        shortEventDto.setTitle(event.getTitle());

        return shortEventDto;
    }
}
