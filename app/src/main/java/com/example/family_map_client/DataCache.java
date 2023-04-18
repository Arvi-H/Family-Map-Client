package com.example.family_map_client;

import java.util.HashMap;
import java.util.Map;

import Model.Event;
import Model.Person;
import Result.PersonsResult;

public class DataCache {
    private static DataCache instance;
    private Map<String, Person> people;
    private Person user;

    private DataCache() {}

    public static DataCache getInstance() {
        if (instance == null) {instance = new DataCache();}
        return instance;
    }

    public void setData(String personID, PersonsResult personsResult) {
        if (personID != null && personsResult != null) {
            people = new HashMap<>();

            for (Person value : personsResult.getData()) {
                user = new Person(value.getPersonID(), value.getAssociatedUsername(), value.getFirstName(), value.getLastName(), value.getGender(), value.getFatherID(), value.getMotherID(), value.getSpouseID());
                people.put(value.getPersonID(), user);
            }

            user = people.get(personID);
        }
    }

    public Person getUser() {
        return user;
    }
}
