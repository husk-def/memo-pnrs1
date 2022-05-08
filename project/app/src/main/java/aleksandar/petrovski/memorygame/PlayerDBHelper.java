package aleksandar.petrovski.memorygame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

public class PlayerDBHelper extends SQLiteOpenHelper implements Serializable {
    static private int idOffset;
    private final String TABLE_NAME = "player";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_USERNAME = "Username";
    public static final String COLUMN_EMAIL = "Email";
    public static final String COLUMN_POINTS = "Points";

    public static void setIdOffset(int idOffset) {
        PlayerDBHelper.idOffset = idOffset;
    }

    private int getMaxId() {
        SQLiteDatabase db = getReadableDatabase();
        int ret = 0;
        /* select max(id) from player */
        Cursor cursor = db.query(
                TABLE_NAME,
                new String [] {"MAX(" + COLUMN_ID + ")"},
                null,
                null,
                null,
                null,
                null
        );
        if (cursor.getCount() <= 0) {
            ret = 0;
        } else {
            /* return that value + 1 */
            ret = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)) + 1;
        }
        cursor.close();
        db.close();
        return ret;
    }

    public PlayerDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void setid() {
        idOffset = getMaxId();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
            "CREATE TABLE " + TABLE_NAME
            + " (" +
            COLUMN_ID +         " INT, " +
            COLUMN_USERNAME +   " TEXT, " +
            COLUMN_EMAIL +      " TEXT, " +
            COLUMN_POINTS +     " INT" +
            ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void restore() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE " + TABLE_NAME);
        db.close();
    }

    public void fInsert(User user) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, idOffset++);                   /* this is where idOffset increases */
        values.put(COLUMN_USERNAME, user.getmUserName());
        values.put(COLUMN_EMAIL, user.getmUserEmail());
        values.put(COLUMN_POINTS, user.getmCurrentScore());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void delete(String userName) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                TABLE_NAME,
                COLUMN_USERNAME + " =?",
                new String[] {userName}
        );
        db.close();
    }

    public User readUser(String userName) {
        SQLiteDatabase db = getReadableDatabase();
        //////////////////////////////////////////////////////////////////////////////
        /////////////////////////ako ista ne valja gledaj ovde////////////////////////
        //////////////////////////////////////////////////////////////////////////////
        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                COLUMN_USERNAME + " =? ",
                new String[] {userName},
                null,
                null,
                null
        );
        if (cursor.getCount() <= 0) {
            return null;
        }
        cursor.moveToFirst();
        User user = createUser(cursor);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            user.addScore(cursor.getColumnIndexOrThrow(COLUMN_POINTS));
        }
        db.close();
        cursor.close();
        return user;
    }

    public ArrayList<User> readAllGames() {
        ArrayList<User> games = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        if(cursor.getCount() == 0) {
            return null;
        }
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            games.add(createUser(cursor));
        }
        db.close();
        return games;
    }

    public ArrayList<User> getDistinctUsers() {
        ArrayList<User> distinct = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT DISTINCT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            distinct.add(createUser(cursor));
        }

        db.close();
        cursor.close();
        return distinct;
    }

    private User createUser(Cursor cursor) {
        String userName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
        //String email = "tood";

        return new User(userName, email);
    }
}
