package Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.family_map_client.R;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Request.LoginRequest;
import Request.RegisterRequest;
import Tasks.LoginTask;
import Tasks.RegisterTask;

public class LoginFragment extends Fragment {
    private final static String FIRST_NAME = "firstname";
    private final static String LAST_NAME = "lastname";
    private final static String SUCCESS_KEY = "success";

    private EditText serverHost;
    private EditText serverPortNumber;
    private EditText username;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private RadioGroup genderGroup;
    private Button loginButton;
    private Button registerButton;

    private Listener listener;

    private View view;
    RegisterRequest registerRequest;

    public LoginFragment() {}

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void notifyDone();
    }

    public enum TaskType {
        LOGIN, REGISTER
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.login_fragment, container, false);

        // Get references to all the views in the layout
        findViews();

        // Set up the UI
        setUpUi();

        // Return the inflated view
        return view;
    }

    private void findViews() {
        // Get references to all the views in the layout
        serverHost = view.findViewById(R.id.server_host);
        serverPortNumber = view.findViewById(R.id.server_port);
        username = view.findViewById(R.id.user_name);
        password = view.findViewById(R.id.password);
        firstName = view.findViewById(R.id.first_name);
        lastName = view.findViewById(R.id.last_name);
        email = view.findViewById(R.id.email);
        genderGroup = view.findViewById(R.id.gender_group);
        loginButton = view.findViewById(R.id.log_in_button);
        registerButton = view.findViewById(R.id.register_button);
    }

    private void setUpUi() {
        // Create a text watcher to enable/disable buttons when text changes
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) { enableButtons(); }
        };

        // Add the text watcher to all the EditText views
        List<EditText> editTexts = Arrays.asList(serverHost, serverPortNumber, username, password, firstName, lastName, email);
        editTexts.forEach(editText -> editText.addTextChangedListener(textWatcher));

        // Set the onCheckedChangeListener for the gender radio group
        genderGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Create a new RegisterRequest object
            registerRequest = new RegisterRequest();

            // Enable/disable buttons based on the text in the EditText views
            enableButtons();

            // Set the gender in the RegisterRequest based on the selected radio button
            switch (checkedId) {
                case R.id.radio_male:
                    registerRequest.setGender("m");
                    break;
                case R.id.radio_female:
                    registerRequest.setGender("f");
                    break;
            }
        });

        // Disable the login and register buttons until text is entered into the EditText views
        loginButton.setEnabled(false);
        loginButton.setOnClickListener(v -> onLoginClick());
        registerButton.setEnabled(false);
        registerButton.setOnClickListener(v -> onRegisterClick());
    }

    private void onLoginClick() {
        // New Request + Set Fields
        LoginRequest loginRequest = new LoginRequest(username.getText().toString(), password.getText().toString());

        LoginTask login = new LoginTask(uiThreadMessageHandler, loginRequest, serverHost.getText().toString(), serverPortNumber.getText().toString());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(login);
    }

    private void onRegisterClick() {
        // Set Request Fields
        registerRequest.setUsername(username.getText().toString());
        registerRequest.setPassword(password.getText().toString());
        registerRequest.setFirstName(firstName.getText().toString());
        registerRequest.setLastName(lastName.getText().toString());
        registerRequest.setEmail(email.getText().toString());

        RegisterTask register = new RegisterTask(uiThreadMessageHandler, registerRequest, serverHost.getText().toString(), serverPortNumber.getText().toString());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(register);
    }

    /**
     * Enables or disables the login and register buttons based on the text entered in the EditText views.
     */
    private void enableButtons() {
        // Determine if the login button should be enabled based on whether the required fields have text
        boolean loginEnabled = serverHost.length() > 0 && serverPortNumber.length() > 0 && username.length() > 0 && password.length() > 0;
        loginButton.setEnabled(loginEnabled);

        // Determine if the register button should be enabled based on whether the required fields have text and a gender is selected
        boolean registrationEnabled = loginEnabled && firstName.length() > 0 && lastName.length() > 0 && email.length() > 0 && genderGroup.getCheckedRadioButtonId() != -1;
        registerButton.setEnabled(registrationEnabled);
    }

    private final Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();
            boolean success = bundle.getBoolean(SUCCESS_KEY);
            String firstName = bundle.getString(FIRST_NAME);
            String lastName = bundle.getString(LAST_NAME);

            String taskName = bundle.getSerializable("taskType") == TaskType.LOGIN ? "Login" : "Registration";

            if (success) {
                Toast.makeText(view.getContext(), taskName + " Successful for " + firstName + " " + lastName, Toast.LENGTH_SHORT).show();
                if (taskName.equals("Login")) {
                    listener.notifyDone(); // Switch to MapFragment
                }
            } else {
                Toast.makeText(view.getContext(), taskName + " Failed", Toast.LENGTH_SHORT).show();
            }
        }
    };
}