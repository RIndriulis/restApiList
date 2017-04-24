package raimundasindriulis.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ItemORM {

    private static final String TABLE_NAME = "item";

    private static final String COMMA_SEP = ", ";

    private static final String COLUMN_ID_TYPE = "INTEGER PRIMARY KEY";
    private static final String COLUMN_ID = "id";

    private static final String COLUMN_TITLE_TYPE = "TEXT";
    private static final String COLUMN_TITLE = "title";


    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " " + COLUMN_ID_TYPE + COMMA_SEP +
                    COLUMN_TITLE  + " " + COLUMN_TITLE_TYPE + 
                    ")";

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static void dropTable(Context context){
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(context);
        SQLiteDatabase database = databaseWrapper.getWritableDatabase();
        database.execSQL(SQL_DROP_TABLE);
        database.execSQL(SQL_CREATE_TABLE);
        database.close();
    }

    public static void insertItem(Context context, User item) {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(context);
        SQLiteDatabase database = databaseWrapper.getWritableDatabase();

        ContentValues values = postToContentValues(item);
        long itemId = database.insert(ItemORM.TABLE_NAME, "null", values);


        database.close();
    }

    private static ContentValues postToContentValues(User item) {
        ContentValues values = new ContentValues();
        values.put(ItemORM.COLUMN_ID, item.id);
        values.put(ItemORM.COLUMN_TITLE, item.vardas);

        return values;
    }

    public static List<User> getItems(Context context) {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(context);
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + ItemORM.TABLE_NAME, null);


        List<User> itemList = new ArrayList<>();

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                User item = cursorToPost(cursor);
                itemList.add(item);
                cursor.moveToNext();
            }
        }

        database.close();

        return itemList;
    }

    private static User cursorToPost(Cursor cursor) {
        User item = new User();
        item.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        item.vardas = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));


        return item;
    }
}
