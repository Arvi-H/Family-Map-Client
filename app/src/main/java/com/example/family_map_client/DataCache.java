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
    private Map<String, Person> childrenById;
    private Map<String, ArrayList<Event>> eventsFromPeople;
    private List<String> relationships;
    private List<Event> maleEvents;
    private List<Event> femaleEvents;
    private Set<Person> userPeople;
    private List<Event> userEvents;
    private Person user;
    private Person selectPerson;
    private Event selectEvent;
    private boolean isLifeEvent;
    private boolean isSpouseEvent;
    private boolean isFamilyEvent;
    private boolean isMale;
    private boolean isFemale;

    private DataCache() {}

    public static DataCache getInstance() {
        if (instance == null) {instance = new DataCache();}
        return instance;
    }

    public void setData(String personID, PersonsResult personsResult, EventsResult eventsResult) {
        if (personID != null && personsResult != null && eventsResult != null) {
            isLifeEvent = true;
            isSpouseEvent = true;
            isFamilyEvent = true;
            isMale = true;
            isFemale = true;


            this.people = new HashMap<>();
            this.events = new HashMap<>();
            this.childrenById = new HashMap<>();
            this.eventsFromPeople = new HashMap<>();
            this.femaleEvents = new ArrayList<>();
            this.maleEvents = new ArrayList<>();
            this.relationships = new ArrayList<>();
            this.userPeople = new HashSet<>();
            this.userPeople = new HashSet<>();

            for (Person res : personsResult.getData()) {
                Person person = new Person(res.getPersonID(), res.getAssociatedUsername(),
                        res.getFirstName(), res.getLastName(), res.getGender(),
                        res.getFatherID(), res.getMotherID(), res.getSpouseID());
                if (res.getFatherID() != null) {
                    this.childrenById.put(res.getFatherID(), res);
                } else if (res.getMotherID() != null){
                    this.childrenById.put(res.getMotherID(), res);
                }
                this.people.put(person.getPersonID(), person);
                this.userPeople.add(res);
            }

            this.user = people.get(personID);
            this.userEvents = new ArrayList<>();

            for (Event res : eventsResult.getData()){
                Event event = new Event(res.getEventID(), res.getAssociatedUsername(), res.getPersonID(), res.getLatitude(),
                        res.getLongitude(), res.getCountry(), res.getCity(), res.getEventType(), res.getYear());
                addEvent(res);
                this.events.put(event.getEventID(), event);
                if (people.get(res.getPersonID()).getGender().toLowerCase().equals("m")) {
                    this.maleEvents.add(res);
                } else if (people.get(res.getPersonID()).getGender().toLowerCase().equals("f")) {
                    this.femaleEvents.add(res);
                }
                this.userEvents.add(res);
            }
        }
    }

    private void addEvent(Event event){
        ArrayList<Event> eventList;
        if (eventsFromPeople.containsKey(event.getPersonID())){
            eventList = eventsFromPeople.get(event.getPersonID());
        } else{
            eventList = new ArrayList<>();
        }
        if (event.getEventType().toLowerCase().equals("birth")) {
            eventList.add(0, event);
        } else if (event.getEventType().toLowerCase().equals("death")) {
            eventList.add(eventList.size(), event);
        } else {
            if (eventList.size() == 0) {
                eventList.add(eventList.size(), event);
            } else {
                for (int i = 0; i < eventList.size(); i++) {
                    Event compareEvent = eventList.get(i);
                    if (compareEvent.getEventType().toLowerCase().equals("birth")) {
                        if (eventList.size() == 1) {
                            eventList.add(1, event);
                            break;
                        }
                    } else if (compareEvent.getEventType().toLowerCase().equals("death")) {
                        eventList.add(i, event);
                        break;
                    } else {
                        if (compareEvent.getYear() > event.getYear()) {
                            eventList.add(i, event);
                            break;
                        } else if (compareEvent.getYear() == event.getYear()) {
                            if (compareEvent.getEventType().toLowerCase().
                                    compareTo(event.getEventType().toLowerCase())
                                    > 0) {
                                eventList.add(i, event);
                                break;
                            }
                        }
                        if (i == eventList.size() - 1) {
                            eventList.add(eventList.size(), event);
                            break;
                        }
                    }
                }
            }
        }
        this.eventsFromPeople.put(event.getPersonID(), eventList);
    }


    public ArrayList<Person> findPeopleWithText(String text) {
        ArrayList<Person> peopleWithText = new ArrayList<>();
        for (Person person : userPeople) {
            if (person.getFirstName().toLowerCase().contains(text.toLowerCase()) ||
                    person.getLastName().toLowerCase().contains(text.toLowerCase())) {
                peopleWithText.add(person);
            }
        }
        return peopleWithText;
    }

    public ArrayList<Event> findEventsWithText(String text) {
        ArrayList<Event> eventsWithText = new ArrayList<>();
        for (Event event : userEvents) {
            if (event.getCountry().toLowerCase().contains(text.toLowerCase()) ||
                    event.getCity().toLowerCase().contains(text.toLowerCase()) ||
                    event.getEventType().toLowerCase().contains(text.toLowerCase()) ||
                    String.valueOf(event.getYear()).contains(text.toLowerCase())) {
                eventsWithText.add(event);
            }
        }
        return eventsWithText;
    }

    public Person getUser() {
        return user;
    }

    public Map<String, Event> getEvents() {
        return events;
    }

    public Map<String, Person> getPeople() { return people; }

    public void setSelectPerson(Person selectPerson) {
        this.selectPerson = selectPerson;
    }

    public Person getSelectPerson() {
        return selectPerson;
    }

    public Event getSelectEvent() {
        return selectEvent;
    }

    public void setSelectEvent(Event selectEvent) {
        this.selectEvent = selectEvent;
    }

    public List<String> getRelationships() {
        return relationships;
    }

    public Map<String, ArrayList<Event>> getEventsFromPeople() {
        return eventsFromPeople;
    }

    public List<Person> family(String id) {
        Person currPerson = getPeople().get(id);
        List<Person> personList = new ArrayList<>();

        if(getPeople().get(currPerson.getSpouseID()) != null){
            personList.add(getPeople().get(currPerson.getSpouseID()));
            relationships.add("Spouse");
        }
        if(getPeople().get(currPerson.getMotherID()) != null){
            personList.add(getPeople().get(currPerson.getMotherID()));
            relationships.add("Mother");
        }
        if(getPeople().get(currPerson.getFatherID()) != null){
            personList.add(getPeople().get(currPerson.getFatherID()));
            relationships.add("Father");
        }
        if(getPeople().get(currPerson.getPersonID()) != null){
            personList.add(getPeople().get(currPerson.getPersonID()));
            relationships.add("Child");
        }

        return personList;
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

    public void logout(){
        this.people.clear();
        this.events.clear();
        this.childrenById.clear();
        this.eventsFromPeople.clear();
        this.relationships.clear();
        this.maleEvents.clear();
        this.femaleEvents.clear();
        this.userPeople.clear();
        this.userEvents.clear();
        this.user = null;
        this.selectPerson = null;
        this.selectEvent = null;
        this.isLifeEvent = true;
        this.isSpouseEvent = true;
        this.isFamilyEvent = true;
        this.isMale = true;
        this.isFemale = true;
    }

}
