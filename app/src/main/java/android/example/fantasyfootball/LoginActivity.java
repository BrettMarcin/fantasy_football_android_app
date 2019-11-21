package android.example.fantasyfootball;

import androidx.appcompat.app.AppCompatActivity;

import android.example.fantasyfootball.util.network.RestApiCalls;
import android.example.fantasyfootball.util.TokenAccess;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
        RestApiCalls.loginService(getApplicationContext(), emailText.getText().toString(), passText.getText().toString());
        if (!TokenAccess.hasTokenExpired(getApplicationContext())) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Failed to login", Toast.LENGTH_LONG).show();
            Log.i(LOG_TAG, "It is null :(");
        }
    }
}
