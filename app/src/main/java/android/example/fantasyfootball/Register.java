package android.example.fantasyfootball;

import androidx.appcompat.app.AppCompatActivity;

import android.example.fantasyfootball.util.TokenAccess;
import android.example.fantasyfootball.util.network.RestApiCalls;
import android.example.fantasyfootball.util.network.VolleyCallback;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {
    EditText name;
    EditText username;
    EditText password;
    EditText email;

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // check Fields For Empty Values
            checkFieldsForEmptyValues();
        }
    };

    private void checkFieldsForEmptyValues() {
        Button b = (Button) findViewById(R.id.button2);
        String s1 = name.getText().toString();
        String s2 = username.getText().toString();
        String s3 = password.getText().toString();
        String s4 = email.getText().toString();
        if (s1.equals("") || s2.equals("") || s3.equals("") || s4.equals("")) {
            b.setEnabled(false);
        } else {
            b.setEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText) findViewById(R.id.name);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);
        name.addTextChangedListener(mTextWatcher);
        username.addTextChangedListener(mTextWatcher);
        password.addTextChangedListener(mTextWatcher);
        email.addTextChangedListener(mTextWatcher);

        Button b = (Button) findViewById(R.id.button2);
        b.setEnabled(false);
    }

    public void register(View view) {
        JSONObject json = new JSONObject();
        JSONObject roleJson = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            roleJson.put("roleName", "STANDARD_USER");
            roleJson.put("description", "none");
            array.put(roleJson);

            json.put("name", name.getText().toString());
            json.put("userName", username.getText().toString());
            json.put("password", password.getText().toString());
            json.put("email", email.getText().toString());
            json.put("roles", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RestApiCalls.signup(getApplicationContext(), json, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response){
                Toast.makeText(getApplicationContext(), "Successful Register!", Toast.LENGTH_SHORT);
                finish();
            }
        });
    }
}
