package summerland.twilight.tamadoggy;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainGameActivity extends AppCompatActivity {

    SharedPreferences sPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);

        sPref = getSharedPreferences(Const.SHARED_BASIC_GAME_DATA, Context.MODE_PRIVATE);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }
    @Override
    protected void onPause(){
        super.onPause();
    }

    private int calculateStatValue(Const.Stat type, int currValue, Date time){
        Date currDate = new Date();
        long timeDiff = currDate.getTime() - time.getTime();
        timeDiff = TimeUnit.MILLISECONDS.toMinutes(timeDiff);
        int intTimeDiff = (int)timeDiff;
        int points = 0;
        switch (type){
            case FUN:
                points = intTimeDiff/10;
                break;
            case HUNGER:
                points = intTimeDiff/10;
                break;
            case FITNESS:
                points = intTimeDiff/15;
                break;
            case HYGIENE:
                points = intTimeDiff/30;
                break;
        }
        return points;
    }
}
