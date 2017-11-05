package summerland.twilight.tamadoggy;

import android.content.Context;
import android.database.ContentObservable;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Jake on 11/5/2017.
 */

public class SQLiteHelper extends SQLiteOpenHelper {
    private Context context;

    private static final String CREATE_ITEM_INFO_TABLE =
            "CREATE TABLE " +
            Const.ITEM_TABLE_NAME + " (" +
            Const.UID + " INTEGER PRIMARY KEY AUTOINCREMENET, " +
            Const.ITEM_NAME + "TEXT, " +
            Const.ITEM_HUNGER + "INTEGER, " +
            Const.ITEM_FITNESS + "INTEGER, " +
            Const.ITEM_FUN + "INTEGER, " +
            Const.ITEM_COST + "INTEGER, " +
            Const.ITEM_HYGIENE + "INTEGER);";

    private static final String CREATE_CURRENT_ITEMS_TABLE =
            "CREATE TABLE " +
                    Const.CURRENT_ITEMS_TABLE_NAME + " (" +
                    Const.UID + " INTEGER PRIMARY KEY AUTOINCREMENET, " +
                    Const.CURRENT_ITEMS_AMOUNT + "INTEGER" +
                    Const.CURRENT_ITEMS_ID + "INTEGER" +
                    " FOREIGN KEY (" + Const.CURRENT_ITEMS_ID + ") REFERENCES " +
                    Const.ITEM_TABLE_NAME + "(" + Const.UID + "));";
    public SQLiteHelper(Context context){
        super (context, Const.DATABASE_NAME, null, Const.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_ITEM_INFO_TABLE);
        sqLiteDatabase.execSQL(CREATE_CURRENT_ITEMS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private void insertJSONData(SQLiteDatabase db){
        try {
            InputStream in = context.openFileInput("items.json");
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (FileNotFoundException e) {
            Log.e("SQLITEHELPER", "file not found exception");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.e("SQLITEHELPER", "IO error");
            e.printStackTrace();
        }
    }
}
