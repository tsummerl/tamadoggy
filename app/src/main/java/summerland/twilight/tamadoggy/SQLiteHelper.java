package summerland.twilight.tamadoggy;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObservable;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
            Const.UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Const.ITEM_NAME + " TEXT, " +
            Const.ITEM_HUNGER + " INTEGER, " +
            Const.ITEM_FITNESS + " INTEGER, " +
            Const.ITEM_FUN + " INTEGER, " +
            Const.ITEM_COST + " INTEGER, " +
            Const.ITEM_HYGIENE + " INTEGER);";

    private static final String CREATE_CURRENT_ITEMS_TABLE =
            "CREATE TABLE " +
                    Const.CURRENT_ITEMS_TABLE_NAME + " (" +
                    Const.UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Const.CURRENT_ITEMS_AMOUNT + " INTEGER, " +
                    Const.CURRENT_ITEMS_ID + " INTEGER, " +
                    " FOREIGN KEY (" + Const.CURRENT_ITEMS_ID + ") REFERENCES " +
                    Const.ITEM_TABLE_NAME + "(" + Const.UID + "));";
    private static final String DROP_TABLE_ITEMS = "DROP TABLE IF EXISTS " + Const.ITEM_TABLE_NAME;
    public SQLiteHelper(Context context){
        super (context, Const.DATABASE_NAME, null, Const.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_ITEM_INFO_TABLE);
        sqLiteDatabase.execSQL(CREATE_CURRENT_ITEMS_TABLE);
        insertJSONData(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try {
            sqLiteDatabase.execSQL(DROP_TABLE_ITEMS);
            onCreate(sqLiteDatabase);
            insertJSONData(sqLiteDatabase);
            Toast.makeText(context, "onUpgrade called", Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            Toast.makeText(context, "exception onUpgrade() db", Toast.LENGTH_LONG).show();
        }
    }

    private void insertJSONData(SQLiteDatabase db){
        try {
            InputStream in = context.getResources().openRawResource(R.raw.items);
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject obj = new JSONObject(sb.toString());
            JSONArray jsonArray = obj.getJSONArray("items");
            ContentValues contentValues;
            for (int i=0; i<jsonArray.length(); i++){
                JSONObject jb = (JSONObject) jsonArray.get(i);
                contentValues = new ContentValues();
                contentValues.put(Const.ITEM_NAME, jb.getString(Const.ITEM_NAME));
                contentValues.put(Const.ITEM_HUNGER, jb.getString(Const.ITEM_HUNGER));
                contentValues.put(Const.ITEM_FUN, jb.getString(Const.ITEM_FUN));
                contentValues.put(Const.ITEM_FITNESS, jb.getString(Const.ITEM_FITNESS));
                contentValues.put(Const.ITEM_HYGIENE, jb.getString(Const.ITEM_HYGIENE));
                contentValues.put(Const.ITEM_COST, jb.getString(Const.ITEM_COST));
                long id = db.insert(Const.ITEM_TABLE_NAME, null, contentValues);
                Log.d("SQLITEHELPER", "insert data id" + id);
            }
        } catch (FileNotFoundException e) {
            Log.e("SQLITEHELPER", "file not found exception");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.e("SQLITEHELPER", "IO error");
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
