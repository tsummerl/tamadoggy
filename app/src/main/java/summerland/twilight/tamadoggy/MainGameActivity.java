package summerland.twilight.tamadoggy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainGameActivity extends AppCompatActivity {

    SharedPreferences sPref;

    Date lastDate;
    ProgressBar progHunger, progFitness, progHygiene, progFun;
    TextView textHunger, textFitness, textHygiene, textFun;
    Handler handleStat;
    int valHunger, valFitness, valHygiene, valFun, nextUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);

        sPref = getSharedPreferences(Const.SHARED_BASIC_GAME_DATA, Context.MODE_PRIVATE);
        progFitness = findViewById(R.id.barFitness);
        progHunger = findViewById(R.id.barHunger);
        progHygiene = findViewById(R.id.barHygiene);
        progFun = findViewById(R.id.barFun);
        textFitness = findViewById(R.id.textFitness);
        textFun = findViewById(R.id.textFun);
        textHunger = findViewById(R.id.textHunger);
        textHygiene = findViewById(R.id.textHygiene);
    }

    @Override
    protected void onResume(){
        super.onResume();
        valHunger = sPref.getInt(Const.SHARED_HUNGER, -1);
        valFitness = sPref.getInt(Const.SHARED_FITNESS, -1);
        valHygiene = sPref.getInt(Const.SHARED_HYGIENE, -1);
        valFun = sPref.getInt(Const.SHARED_FUN, -1);
        lastDate = new Date(sPref.getLong(Const.SHARED_DATE_LAST_GAME_UPDATE, 0));
        nextUpdate = calculateStatValue();

        setProgress();
        handleStat = new Handler();
        handleStat.postDelayed(runnable, TimeUnit.MINUTES.toMillis(nextUpdate));
    }
    @Override
    protected void onPause(){
        super.onPause();
        SharedPreferences.Editor editor = sPref.edit();
        editor.putLong(Const.SHARED_DATE_LAST_GAME_UPDATE, lastDate.getTime());
        editor.putInt(Const.SHARED_FUN, valFun);
        editor.putInt(Const.SHARED_HYGIENE, valHygiene);
        editor.putInt(Const.SHARED_FITNESS, valFitness);
        editor.putInt(Const.SHARED_HUNGER, valHunger);
        editor.commit();
    }

    private int calculateStatValue() {
        Date currDate = new Date();
        long timeDiff = currDate.getTime() - lastDate.getTime();
        int intTimeDiff = (int) TimeUnit.MILLISECONDS.toMinutes(timeDiff);
        if (intTimeDiff >= 60) {
            valHunger = valHunger - (2 * (intTimeDiff / 60));
            valFun = valFun - (3 * (intTimeDiff / 60));
            valFitness = valFitness - (2 * (intTimeDiff / 60));
            valHygiene = valHygiene - (1 * (intTimeDiff / 60));

            lastDate = new Date(lastDate.getTime() + TimeUnit.HOURS.toMillis(intTimeDiff / 60));
        }
        return 60 - intTimeDiff; //remaining time until next update;
    }
    private void setProgress()
    {
        progFitness.setProgress(valFitness);
        progFun.setProgress(valFun);
        progHygiene.setProgress(valHygiene);
        progHunger.setProgress(valHunger);

        textHygiene.setText("" + valHygiene);
        textFun.setText("" + valFun);
        textHunger.setText("" + valHunger);
        textFitness.setText("" + valFitness);
    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            valHunger = valHunger -2;
            valFun = valFun - 3;
            valFitness = valFitness -2;
            valHygiene = valHygiene -1;

            setProgress();
            lastDate = new Date();
            handleStat.postDelayed(this, TimeUnit.HOURS.toMillis(1));
        }
    };
}
