package summerland.twilight.tamadoggy;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainGameActivity extends AppCompatActivity {

    SharedPreferences sPref;

    ProgressBar progHunger, progFitness, progHygiene, progFun;
    int valHunger, valFitness, valHygiene, valFun;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);

        sPref = getSharedPreferences(Const.SHARED_BASIC_GAME_DATA, Context.MODE_PRIVATE);

    }

    @Override
    protected void onResume(){
        super.onResume();
        valHunger = sPref.getInt(Const.SHARED_HUNGER, -1);
        valFitness = sPref.getInt(Const.SHARED_FITNESS, -1);
        valHygiene = sPref.getInt(Const.SHARED_HYGIENE, -1);
        valFun = sPref.getInt(Const.SHARED_FUN, -1);
        Date lastDate = new Date(sPref.getLong(Const.SHARED_DATE_LAST_GAME, 0));
        valHunger = valHunger - calculateStatValue(Const.Stat.HUNGER, lastDate);
        valFitness = valFitness - calculateStatValue(Const.Stat.FITNESS, lastDate);
        valHygiene = valHygiene - calculateStatValue(Const.Stat.HYGIENE, lastDate);
        valFun = valFun - calculateStatValue(Const.Stat.FUN, lastDate);
    }
    @Override
    protected void onPause(){
        super.onPause();
        SharedPreferences.Editor editor = sPref.edit();
        editor.putLong(Const.SHARED_DATE_LAST_GAME, new Date().getTime());
        editor.putInt(Const.SHARED_FUN, valFun);
        editor.putInt(Const.SHARED_HYGIENE, valHygiene);
        editor.putInt(Const.SHARED_FITNESS, valFitness);
        editor.putInt(Const.SHARED_HUNGER, valHunger);
        editor.commit();
    }

    private int calculateStatValue(Const.Stat type, Date time){
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
