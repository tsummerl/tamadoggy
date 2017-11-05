package summerland.twilight.tamadoggy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

public class NewGameActivity extends AppCompatActivity implements View.OnClickListener{

    EditText editDogName;
    TextView textFeedback;
    Button buttonNewGame;
    SharedPreferences sPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        editDogName = findViewById(R.id.editDogName);
        buttonNewGame = findViewById(R.id.buttonCreate);
        textFeedback = findViewById(R.id.textFeedback);

        sPref = getSharedPreferences(Const.SHARED_BASIC_GAME_DATA, Context.MODE_PRIVATE);
        buttonNewGame.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String dogName = editDogName.getText().toString();
        if(dogName.equals("")){
            textFeedback.setText("Please enter a name");
        }
        else{
            SharedPreferences.Editor sharedEdit = sPref.edit();
            Date currTime = new Date();
            sharedEdit.putLong(Const.SHARED_DATE_LAST_GAME_UPDATE, currTime.getTime());
//            sharedEdit.putLong(Const.SHARED_DATE_HUNGER, currTime.getTime());
//            sharedEdit.putLong(Const.SHARED_DATE_HYGIENE, currTime.getTime());
//            sharedEdit.putLong(Const.SHARED_DATE_FITNESS, currTime.getTime());
//            sharedEdit.putLong(Const.SHARED_DATE_FUN, currTime.getTime());

            sharedEdit.putInt(Const.SHARED_FITNESS, 100);
            sharedEdit.putInt(Const.SHARED_FUN, 100);
            sharedEdit.putInt(Const.SHARED_HYGIENE, 100);
            sharedEdit.putInt(Const.SHARED_HUNGER, 100);
            sharedEdit.putString(Const.SHARED_DOGNAME, dogName);
            sharedEdit.commit();
            Intent i = new Intent(this, MainGameActivity.class);
            finish();
            startActivity(i);
        }
    }
}
