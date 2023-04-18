package com.example.family_map_client;

import java.util.HashMap;
import java.util.Map;

import Model.Event;
import Model.Person;
import Result.EventsResult;
import Result.PersonsResult;

public class DataCache {
    private static DataCache instance;
    private Map<String, Person> people;
    private Map<String, Event> events;
    private Person user;
    private Event event;
    private DataCache() {}

    public static DataCache getInstance() {
        if (instance == null) {instance = new DataCache();}
        return instance;
    }

    public void setData(String personID, PersonsResult personsResult, EventsResult eventsResult) {
        if (personID != null && personsResult != null && eventsResult != null) {
            people = new HashMap<>();
            events = new HashMap<>();

            for (Person value : personsResult.getData()) {
                user = new Person(value.getPersonID(), value.getAssociatedUsername(), value.getFirstName(), value.getLastName(), value.getGender(), value.getFatherID(), value.getMotherID(), value.getSpouseID());
                people.put(value.getPersonID(), user);
            }

            user = people.get(personID);

            for (Event value : eventsResult.getData()){
                event = new Event(value.getEventID(), value.getAssociatedUsername(), value.getPersonID(), value.getLatitude(), value.getLongitude(), value.getCountry(), value.getCity(), value.getEventType(), value.getYear());
                events.put(event.getEventID(), event);
            }
        }
    }

    public Person getUser() {
        return user;
    }

    public Map<String, Event> getEvents() {
        return events;
    }
}
