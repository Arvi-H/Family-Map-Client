package com.example.family_map_client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Model.Event;
import Model.Person;
import Result.EventsResult;
import Result.PersonsResult;

public class DataCache {
    private static DataCache instance;

    private Map<String, Person> people;
    private Map<String, Event> events;
    private Map<String, Person> children;
    private Map<String, ArrayList<Event>> peopleEventsList;
    private List<String> connections;
    private List<Event> maleEvents;
    private List<Event> femaleEvents;
    private Set<Person> userPeople;
    private List<Event> userEvents;
    private Person user;
    private Event selectEvent;
    private boolean isLifeEvent;
    private boolean isSpouseEvent;
    private boolean isFamilyEvent;
    private boolean isMale;
    private boolean isFemale;

    private DataCache() {}

    public static DataCache getInstance() {
        if (instance == null) {
            instance = new DataCache();
        }
        return instance;
    }

    public void initializeData(String personID, PersonsResult personsResult, EventsResult eventsResult) {

        // Check that inputs are not null
        if (personID != null && personsResult != null && eventsResult != null) {

            // Set initial values of boolean flags
            isLifeEvent = true;
            isSpouseEvent = true;
            isFamilyEvent = true;
            isMale = true;
            isFemale = true;

            // Initialize data structures
            people = new HashMap<>();
            events = new HashMap<>();
            children = new HashMap<>();
            peopleEventsList = new HashMap<>();
            femaleEvents = new ArrayList<>();
            maleEvents = new ArrayList<>();
            connections = new ArrayList<>();
            userPeople = new HashSet<>();
            userEvents = new ArrayList<>();

            // Loop through the Person objects in the PersonsResult
            for (Person res : personsResult.getData()) {
                // Create a new Person object based on the PersonResult
                Person person = new Person(res.getPersonID(), res.getAssociatedUsername(), res.getFirstName(), res.getLastName(), res.getGender(), res.getFatherID(), res.getMotherID(), res.getSpouseID());

                // If the Person has a father ID, add the Person to the childrenById map with the father ID as the key
                // Otherwise, if the Person has a mother ID, add the Person to the childrenById map with the mother ID as the key
                if (res.getFatherID() != null) {
                    this.children.put(res.getFatherID(), res);
                } else if (res.getMotherID() != null) {
                    this.children.put(res.getMotherID(), res);
                }

                // Add the Person to the people map with the person ID as the key
                // Add the Person to the userPeople set
                this.people.put(person.getPersonID(), person);
                this.userPeople.add(res);
            }

            // Get the User's Person object from the people map using the personID parameter
            this.user = people.get(personID);
            // Initialize the userEvents list
            this.userEvents = new ArrayList<>();

            // Loop through the Event objects in the EventsResult
            for (Event res : eventsResult.getData()) {
                // Create a new Event object based on the EventResult
                Event event = new Event(res.getEventID(), res.getAssociatedUsername(), res.getPersonID(), res.getLatitude(), res.getLongitude(), res.getCountry(), res.getCity(), res.getEventType(), res.getYear());

                // Add the Event to the eventsFromPeople map with the person ID as the key
                addEvent(res);
                // Add the Event to the events map with the event ID as the key
                this.events.put(event.getEventID(), event);

                // If the associated Person is male, add the Event to the maleEvents list
                // Otherwise, if the associated Person is female, add the Event to the femaleEvents list
                if (people.get(res.getPersonID()).getGender().equalsIgnoreCase("m")) {
                    this.maleEvents.add(res);
                } else if (people.get(res.getPersonID()).getGender().equalsIgnoreCase("f")) {
                    this.femaleEvents.add(res);
                }

                // Add the Event to the userEvents list
                this.userEvents.add(res);
            }
        }
    }

    /**
     * Adds an event to the list of events for a given person, sorting the events
     * by type (birth, death, other) and then by year and event type.
     *
     * @param event the event to add
     */
    private void addEvent(Event event) {
        // Get the list of events for the person
        ArrayList<Event> eventList = peopleEventsList.getOrDefault(event.getPersonID(), new ArrayList<>());

        // Determine the appropriate index to insert the new event
        int index = 0;
        for (Event compareEvent : eventList) {
            // Sort by birth event first
            if (compareEvent.getEventType().equalsIgnoreCase("birth")) {
                break;
            }
            // Sort by death event last
            if (compareEvent.getEventType().equalsIgnoreCase("death")) {
                index++;
                continue;
            }
            // Sort by year and then by event type
            if (compareEvent.getYear() < event.getYear()
                    || (compareEvent.getYear() == event.getYear()
                    && compareEvent.getEventType().compareToIgnoreCase(event.getEventType()) < 0)) {
                index++;
            } else {
                break;
            }
        }

        // Insert the new event at the determined index
        eventList.add(index, event);
        // Update the map of events for the person
        peopleEventsList.put(event.getPersonID(), eventList);
    }

    public ArrayList<Person> searchPeopleByName(String text) {
        // Create a new ArrayList to store matching people
        ArrayList<Person> peopleNames = new ArrayList<>();

        // Iterate over all people in the userPeople list
        for (Person person : userPeople) {
            // Check if the first name or last name of the person contains the search text (ignoring case)
            if (person.getFirstName().toLowerCase().contains(text.toLowerCase()) || person.getLastName().toLowerCase().contains(text.toLowerCase())) {
                // If there is a match, add the person to the peopleNames list
                peopleNames.add(person);
            }
        }

        // Return the list of matching people
        return peopleNames;
    }

    public ArrayList<Event> searchEventsByID(String text) {
        // Create a new ArrayList to store matching events
        ArrayList<Event> eventIDs = new ArrayList<>();

        // Iterate over all events in the userEvents list
        for (Event event : userEvents) {
            // Check if the event's country, city, event type, or year contain the search text (ignoring case)
            if (event.getCountry().toLowerCase().contains(text.toLowerCase()) ||
                    event.getCity().toLowerCase().contains(text.toLowerCase()) ||
                    event.getEventType().toLowerCase().contains(text.toLowerCase()) ||
                    String.valueOf(event.getYear()).contains(text.toLowerCase())) {
                // If there is a match, add the event to the eventIDs list
                eventIDs.add(event);
            }
        }

        // Return the list of matching events
        return eventIDs;
    }

    public Person getUser() {
        return user;
    }

    public Map<String, Event> getEvents() {
        return events;
    }

    public Map<String, Person> getPeople() {
        return people;
    }

    public Event getSelectEvent() {
        return selectEvent;
    }

    public void setSelectEvent(Event selectEvent) {
        this.selectEvent = selectEvent;
    }

    public List<String> getConnections() {
        return connections;
    }

    public Map<String, ArrayList<Event>> getPeopleEventsList() {
        return peopleEventsList;
    }

    public List<Person> getFamily(String id) {
        // Get the current person
        Person currPerson = getPeople().get(id);
        // Create a new list to hold the family members
        List<Person> personList = new ArrayList<>();

        addPersonIfNotNull(personList, getPeople().get(currPerson.getSpouseID()), "Spouse");
        addPersonIfNotNull(personList, getPeople().get(currPerson.getMotherID()), "Mother");
        addPersonIfNotNull(personList, getPeople().get(currPerson.getFatherID()), "Father");
        addPersonIfNotNull(personList, currPerson, "Child");

        // Return the list of family members
        return personList;
    }

    // Helper method to add a person to a list if the person is not null
    private void addPersonIfNotNull(List<Person> personList, Person person, String relationship) {
        if (person != null) {
            // Add the person to the list of family members
            personList.add(person);
            // Update the relationship
            connections.add(relationship);
        }
    }

    public boolean isLifeEvent() {
        return isLifeEvent;
    }

    public void setLifeEvent(boolean lifeEvent) {
        this.isLifeEvent = lifeEvent;
    }

    public boolean isSpouseEvent() {
        return isSpouseEvent;
    }

    public void setSpouseEvent(boolean spouseEvent) {
        this.isSpouseEvent = spouseEvent;
    }

    public boolean isFamilyEvent() {
        return isFamilyEvent;
    }

    public void setFamilyEvent(boolean familyEvent) {
        this.isFamilyEvent = familyEvent;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        this.isMale = male;
    }

    public boolean isFemale() {
        return isFemale;
    }

    public void setFemale(boolean female) {
        this.isFemale = female;
    }

    public List<Event> getUserEvents() {
        return userEvents;
    }

    public void logout() {
        // Clear all data structures
        people.clear();
        events.clear();
        children.clear();
        peopleEventsList.clear();
        connections.clear();
        maleEvents.clear();
        femaleEvents.clear();
        userPeople.clear();
        userEvents.clear();

        // Reset current user, selected person, and selected event
        user = null;
        selectEvent = null;

        // Reset filter options
        isLifeEvent = true;
        isSpouseEvent = true;
        isFamilyEvent = true;
        isMale = true;
        isFemale = true;
    }
}