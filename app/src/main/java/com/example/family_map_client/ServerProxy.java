package com.example.family_map_client;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import Request.LoginRequest;
import Request.RegisterRequest;
import Result.EventsResult;
import Result.LoginResult;
import Result.PersonsResult;
import Result.RegisterResult;

public class ServerProxy {
    private static final Gson gson = new Gson();

    public RegisterResult register(RegisterRequest registerRequest, URL url) {
        try {
            RegisterResult registerResult;

            // HTTP Connection
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            // Request Body
            OutputStream requestBody = http.getOutputStream();
            writeData(gson.toJson(registerRequest), requestBody);
            requestBody.close();

            // Result Body
            InputStream resultBody;
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                resultBody = http.getInputStream();
            } else {
                resultBody = http.getErrorStream();
            }

            registerResult = gson.fromJson(readData(resultBody), RegisterResult.class);
            return registerResult;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LoginResult login(LoginRequest loginRequest, URL url) {
        try {
            LoginResult loginResult;

            // HTTP Connection
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            // Request Body
            OutputStream requestBody = http.getOutputStream();
            writeData(gson.toJson(loginRequest), requestBody);
            requestBody.close();

            // Result Body
            InputStream resultBody;
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                resultBody = http.getInputStream();
            } else {
                resultBody = http.getErrorStream();
            }

            loginResult = gson.fromJson(readData(resultBody), LoginResult.class);
            return loginResult;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PersonsResult getPersons(String authToken, String serverHost, String serverPortNumber) {
        try {
            PersonsResult personsResult;

            // HTTP Connection
            URL url = new URL("http://" + serverHost + ":" + serverPortNumber + "/person");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false); // No Request Body
            http.addRequestProperty("Authorization", authToken);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            // Result Body
            InputStream resultBody;
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                resultBody = http.getInputStream();
            } else {
                resultBody = http.getErrorStream();
            }

            personsResult = gson.fromJson(readData(resultBody), PersonsResult.class);
            return personsResult;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public EventsResult getEvents(String authToken, String serverHost, String serverPortNumber) throws IOException {
        try {
            EventsResult eventsResult;

            // HTTP Connection
            URL url = new URL("http://" + serverHost + ":" + serverPortNumber + "/person");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false); // No Request Body
            http.addRequestProperty("Authorization", authToken);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            // Result Body
            InputStream resultBody;
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                resultBody = http.getInputStream();
            } else {
                resultBody = http.getErrorStream();
            }

            eventsResult = gson.fromJson(readData(resultBody), EventsResult.class);
            return eventsResult;

    } catch (IOException e) {
        e.printStackTrace();
    }
        return null;
    }

    protected String readData(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    protected void writeData(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}