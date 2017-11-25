package summerland.twilight.tamadoggy;

/**
 * Created by Jake on 11/1/2017.
 */

public final class Const {

    /// Shared Preferences
    public static final String SHARED_BASIC_GAME_DATA = "BASIC_GAME_DATA";
    public static final String SHARED_NULL = "NULL_VALUE";

    public static final String SHARED_DATE_LAST_GAME_UPDATE = "LAST_GAME";

    public static final String SHARED_HUNGER = "HUNGER";
    public static final String SHARED_FUN = "FUN";
    public static final String SHARED_FITNESS = "FITNESS";
    public static final String SHARED_HYGIENE = "HYGIENE";
    public static final String SHARED_DOGNAME = "DOGNAME";
    public static final String SHARED_CASH = "CASH";
    /// SQLite Database

    public static final String DATABASE_NAME = "dogDatabase";
    public static final String DOG_TABLE_NAME = "DOGINFOTABLE";
    public static final String ITEM_TABLE_NAME = "ITEMINFOTABLE";
    public static final String CURRENT_ITEMS_TABLE_NAME = "CURRENTITEMSTABLE";


    public static final String UID = "_id";
    public static final String ITEM_NAME = "Name";
    public static final String ITEM_DESCRIPTION = "Type";
    public static final String ITEM_HUNGER = "Hunger";
    public static final String ITEM_FUN = "Fun";
    public static final String ITEM_FITNESS = "Fitness";
    public static final String ITEM_HYGIENE = "Hygiene";
    public static final String ITEM_COST = "Cost";

    public static final String CURRENT_ITEMS_AMOUNT = "Amount";
    public static final String CURRENT_ITEMS_ID = "ItemID";

    public static final String DOG_TYPE = "Type";
    public static final String DOG_PICTURE_PREFIX = "Picture_Prefix";

    public static final int DATABASE_VERSION = 3;

    public enum Stat {
        HUNGER,
        FUN,
        FITNESS,
        HYGIENE
    }

    public enum databaseView {
        CURRENT_ITEMS,
        SHOP_ITEMS,
        ALL_ITEMS
    }

    public static final class CurrentItems{
        int id;
        String itemName;
        int hunger, fun, fitness, hygiene, amount;
    }

    public static final class Items{
        int id;
        String itemName;
        int hunger, fun, fitness, hygiene, cost, amount;
    }
    private Const(){
        throw new AssertionError();
    }
}
