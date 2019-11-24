package android.example.fantasyfootball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.example.fantasyfootball.util.network.RestApiCalls;
import android.example.fantasyfootball.util.TokenAccess;
import android.example.fantasyfootball.util.network.VolleyCallback;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText emailText,passText;
    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

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

    void checkFieldsForEmptyValues(){
        Button b = (Button) findViewById(R.id.login_button);
        String s1 = emailText.getText().toString();
        String s2 = passText.getText().toString();

        if(s1.equals("")|| s2.equals("")){
            b.setEnabled(false);
        } else {
            b.setEnabled(true);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailText = findViewById(R.id.login_form_email);
        passText = findViewById(R.id.login_form_password);
        emailText.addTextChangedListener(mTextWatcher);
        passText.addTextChangedListener(mTextWatcher);
        checkFieldsForEmptyValues();
    }

    public void login(View view) {
        RestApiCalls.loginService(getApplicationContext(), emailText.getText().toString(), passText.getText().toString(), new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response){
                Toast.makeText(getApplicationContext(), "Logged in!", Toast.LENGTH_SHORT);
                int i = 0;
                while (TokenAccess.hasTokenExpired(getApplicationContext()) && i < 3) {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;
                }
                finish();
            }
        });
    }

    public void goToRegister(View view) {
        Intent activityIntent = new Intent(this, Register.class);
        startActivity(activityIntent);
    }
}
