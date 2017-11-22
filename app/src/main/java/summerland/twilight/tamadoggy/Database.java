package summerland.twilight.tamadoggy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Jake on 11/5/2017.
 */

public class Database {
    private SQLiteDatabase db;
    private Context context;
    private final SQLiteHelper dbHelper;

    public Database (Context c){
        context = c;
        dbHelper = new SQLiteHelper(context);
    }

    public void saveItems(int id, int amount)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "UPDATE " + Const.CURRENT_ITEMS_TABLE_NAME + " SET " + Const.CURRENT_ITEMS_AMOUNT + " = " + amount
                + " WHERE " + Const.CURRENT_ITEMS_ID + "=" + id + "; INSERT INTO " +
                Const.CURRENT_ITEMS_TABLE_NAME + " (" + Const.CURRENT_ITEMS_ID + ", "
                + Const.CURRENT_ITEMS_AMOUNT + ") VALUES(" + id + ", " + amount + ") WHERE changes() = 0";
        db.execSQL(query);
    }

    public Cursor getData(Const.databaseView view)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = null;
        switch (view)
        {
            case ALL_ITEMS:
                String[] allItemsColumns = {Const.UID, Const.ITEM_NAME, Const.ITEM_HUNGER, Const.ITEM_FITNESS, Const.ITEM_FUN, Const.ITEM_COST, Const.ITEM_HYGIENE};
                cursor = db.query(Const.ITEM_TABLE_NAME, allItemsColumns, null, null, null, null, null);
                break;
            case CURRENT_ITEMS:
//                String[] currItemsColumns = {Const.UID, Const.CURRENT_ITEMS_ID, Const.CURRENT_ITEMS_AMOUNT};
//                cursor = db.query(Const.CURRENT_ITEMS_TABLE_NAME, currItemsColumns, null, null, null, null, null);
                cursor = db.rawQuery("SELECT * FROM " + Const.CURRENT_ITEMS_TABLE_NAME + " as currentItems, " + Const.ITEM_TABLE_NAME
                        +" as  items WHERE items._id = currentItems.ItemID" , null);
                break;
        }
        return cursor;
    }
    public void deleteDatabase()
    {
        try{
            context.deleteDatabase(Const.DATABASE_NAME);
        }
        catch (Exception e)
        {
            Log.d("DATABASE", e.toString());
        }
    }
}
