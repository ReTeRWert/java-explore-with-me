package ru.ewm.service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ewm.service.categories.Category;
import ru.ewm.service.categories.CategoryRepository;
import ru.ewm.service.compilations.Compilation;
import ru.ewm.service.compilations.CompilationRepository;
import ru.ewm.service.events.EventRepository;
import ru.ewm.service.events.model.Event;
import ru.ewm.service.exception.NotFoundException;
import ru.ewm.service.requests.Request;
import ru.ewm.service.requests.RequestRepository;
import ru.ewm.service.users.User;
import ru.ewm.service.users.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExistValidator {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CompilationRepository compilationRepository;

    public User getUserIfExist(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.orElseThrow(() -> new NotFoundException(userId, User.class.getSimpleName()));
    }

    public Event getEventIfExist(Long eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        return event.orElseThrow(() -> new NotFoundException(eventId, Event.class.getSimpleName()));
    }

    public Category getCategoryIfExist(Long catId) {
        Optional<Category> category = categoryRepository.findById(catId);
        return category.orElseThrow(() -> new NotFoundException(catId, Category.class.getSimpleName()));
    }

    public Request getRequestIfExist(Long requestId) {
        Optional<Request> request = requestRepository.findById(requestId);
        return request
                .orElseThrow(() -> new NotFoundException(requestId, Request.class.getSimpleName()));
    }

    public Compilation getCompilationIfExist(Long compId) {
        Optional<Compilation> compilation = compilationRepository.findById(compId);
        return compilation.orElseThrow(() -> new NotFoundException(compId, Compilation.class.getSimpleName()));
    }
}
