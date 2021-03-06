package com.bristech.service;

import com.bristech.entities.Event;
import com.bristech.entities.User;

import java.util.List;

public interface EventService {

    List<Event> getAllEvents();

    List<Event> getUpcomingEvents();

    List<Event> getPastEvents();

    Event getEventById(long eventId);

    void updateEvents(List<Event> events);

}
