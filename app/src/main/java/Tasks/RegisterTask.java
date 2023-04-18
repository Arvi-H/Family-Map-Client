package Tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.family_map_client.DataCache;
import com.example.family_map_client.ServerProxy;

import java.net.MalformedURLException;
import java.net.URL;

import Fragments.LoginFragment;
import Request.RegisterRequest;
import Result.PersonsResult;
import Result.RegisterResult;
public class RegisterTask implements Runnable{
    private final Handler messageHandler;
    private final RegisterRequest registerRequest;
    private final ServerProxy server;
    private final String serverHost;
    private final String serverPortNumber;
    private final static String SUCCESS_KEY = "success";
    private final static String FIRST_NAME = "firstname";
    private final static String LAST_NAME = "lastname";
    private final DataCache dataCache;

    private String firstName;
    private String lastName;
    private boolean isSuccess = false;

    public RegisterTask(Handler messageHandler, RegisterRequest registerRequest, String serverHost, String serverPortNumber) {
        // Initialize
        server = new ServerProxy();
        dataCache = DataCache.getInstance();

        this.messageHandler = messageHandler;
        this.registerRequest = registerRequest;

        // Server Host and Port Number
        this.serverHost = serverHost;
        this.serverPortNumber = serverPortNumber;
    }

    @Override
    public void run() {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPortNumber + "/user/register");
            RegisterResult response = server.register(registerRequest, url);

            if (response.isSuccess()) {
                PersonsResult personsResult = server.getPersons(response.getAuth_token(), serverHost, serverPortNumber);

                dataCache.setData(response.getPersonID(), personsResult);
                firstName = dataCache.getUser().getFirstName();
                lastName = dataCache.getUser().getLastName();

                isSuccess = true;
            }

            sendMessage();
        } catch (MalformedURLException e) {
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

        messageBundle.putSerializable("taskType", LoginFragment.TaskType.REGISTER);
        message.setData(messageBundle);
        messageHandler.sendMessage(message);
    }
}