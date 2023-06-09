package Tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import com.example.family_map_client.DataCache;
import com.example.family_map_client.ServerProxy;

import java.io.IOException;
import java.net.URL;

import Fragments.LoginFragment;
import Request.LoginRequest;
import Result.EventsResult;
import Result.LoginResult;
import Result.PersonsResult;

public class LoginTask implements Runnable {
    private final Handler messageHandler;
    private final LoginRequest loginRequest;
    private final ServerProxy serverProxy;
    private final String serverHost;
    private final String serverPortNumber;
    private final static String SUCCESS_KEY = "success";
    private final static String FIRST_NAME = "firstname";
    private final static String LAST_NAME = "lastname";
    private final DataCache dataCache;

    private String firstName;
    private String lastName;
    private boolean isSuccess = false;


    public LoginTask(Handler messageHandler, LoginRequest loginRequest, String serverHost, String serverPortNumber) {
        // Initialize
        serverProxy = new ServerProxy();
        dataCache = DataCache.getInstance();

        this.messageHandler = messageHandler;
        this.loginRequest = loginRequest;

        // Server Host and Port Number
        this.serverHost = serverHost;
        this.serverPortNumber = serverPortNumber;
    }

    @Override
    public void run() {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPortNumber + "/user/login");
            LoginResult loginResult = serverProxy.login(loginRequest, url);

            if (loginResult.isSuccess()) {
                PersonsResult personsResult = serverProxy.getPersons(loginResult.getAuthtoken(), serverHost, serverPortNumber);
                EventsResult eventsResult = serverProxy.getEvents(loginResult.getAuthtoken(), serverHost, serverPortNumber);

                dataCache.initializeData(loginResult.getPersonID(), personsResult, eventsResult);
                firstName = dataCache.getUser().getFirstName();
                lastName = dataCache.getUser().getLastName();

                isSuccess = true;
            }

            sendMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage() {
        Message message = Message.obtain();

        Bundle messageBundle = new Bundle();
        if (isSuccess) {
            messageBundle.putString(FIRST_NAME, firstName);
            messageBundle.putString(LAST_NAME, lastName);
        }
        messageBundle.putBoolean(SUCCESS_KEY, isSuccess);

        messageBundle.putSerializable("taskType", LoginFragment.TaskType.LOGIN);
        message.setData(messageBundle);
        messageHandler.sendMessage(message);
    }
}