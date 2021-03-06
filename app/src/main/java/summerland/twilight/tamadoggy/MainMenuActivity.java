package summerland.twilight.tamadoggy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Date;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener{

    private SharedPreferences sPref;

    Button buttonResume;
    Button buttonNew;
    Button buttonSettings;
    Button buttonCredits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        buttonResume = findViewById(R.id.buttonResume);
        buttonNew = findViewById(R.id.buttonNewGame);
        buttonSettings = findViewById(R.id.buttonSettings);
        buttonCredits = findViewById(R.id.buttonCredits);

        sPref = getSharedPreferences(Const.SHARED_BASIC_GAME_DATA, Context.MODE_PRIVATE);
        Date lastGame = new Date(sPref.getLong(Const.SHARED_DATE_LAST_GAME_UPDATE, 0));
        if (lastGame.getTime() == 0L){
            buttonResume.setEnabled(false);
            buttonResume.setAlpha(0.5f);
        }
        else{
            buttonResume.setEnabled(true);
            buttonResume.setAlpha(1);
        }
        buttonResume.setOnClickListener(this);
        buttonNew.setOnClickListener(this);
        buttonCredits.setOnClickListener(this);
        buttonSettings.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId())
        {
            case R.id.buttonCredits:
                i = new Intent(this, CreditsActivity.class);
                startActivity(i);
                break;
            case R.id.buttonNewGame:
                i = new Intent(this, NewGameActivity.class);
                startActivity(i);
                break;
            case R.id.buttonResume:
                i = new Intent(this, MainGameActivity.class);
                startActivity(i);
                break;
            case R.id.buttonSettings:
                break;
        }
    }
}
