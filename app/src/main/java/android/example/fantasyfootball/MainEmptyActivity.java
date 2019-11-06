package android.example.fantasyfootball;

import android.content.Intent;
import android.example.fantasyfootball.util.TokenAccess;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainEmptyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent activityIntent;
        // go straight to main if a token is stored
        if (!TokenAccess.hasTokenExpired(getApplicationContext())){
//        if (Util.getToken() != null) {
            activityIntent = new Intent(this, MainActivity.class);
        } else {
            activityIntent = new Intent(this, LoginActivity.class);
        }
        startActivity(activityIntent);
        startActivity(getIntent());
        //finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent activityIntent;
        // go straight to main if a token is stored
        if (!TokenAccess.hasTokenExpired(getApplicationContext())){
//        if (Util.getToken() != null) {
            activityIntent = new Intent(this, MainActivity.class);
        } else {
            activityIntent = new Intent(this, LoginActivity.class);
        }
        startActivity(activityIntent);
        startActivity(getIntent());
    }

}