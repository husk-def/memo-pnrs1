package aleksandar.petrovski.memorygame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
                new String [] {COLUMN_ID},
                null,
                null,
                null,
                null,
                null
        );
        if (cursor.getCount() <= 0) {
            Log.i("moje", "getMaxId: empty database. return 0.");
        } else {
            /* return the last value (the highest one) + 1 */
            cursor.moveToLast();
            ret = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)) + 1;
        }
        cursor.close();
        db.close();
        return ret;
    }

    public PlayerDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void setId() {
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
        User retUser = null;
        SQLiteDatabase db = getReadableDatabase();
        //////////////////////////////////////////////////////////////////////////////
        /////////////////////////ako ista ne valja gledaj ovde////////////////////////
        //////////////////////////////////////////////////////////////////////////////
//        Cursor cursor = db.query(
//                TABLE_NAME,
//                null,
//                COLUMN_USERNAME + " =? ",
//                new String[] {userName},
//                null,
//                null,
//                null
//        );
        String query = "SELECT * FROM " + TABLE_NAME + " where " + COLUMN_USERNAME + "=\"" + userName + "\"";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            Log.i("moje", "no such user: " + userName);
        } else {
            cursor.moveToFirst();
            retUser = createUser(cursor);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                retUser.addScore(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POINTS)));
            }
        }
        db.close();
        cursor.close();
        return retUser;
    }

    public ArrayList<User> readAllGames() {
        ArrayList<User> games = null;
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
        if (cursor.getCount() == 0) {
            Log.i("moje", "readAllGames: no games.");
        } else {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                games = new ArrayList<>();
                games.add(createUser(cursor));
            }
        }
        db.close();
        return games;
    }

    public ArrayList<User> getDistinctUsers() {
        ArrayList<User> distinct = null;
        int cnt = 0;
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT DISTINCT Username, Email FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() == 0) {
            Log.i("moje", "getDistinctUsers: no users.");
        } else {
            distinct = new ArrayList<>();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                distinct.add(createUser(cursor));
                ++cnt;
            }
        }
        Log.i("moje", "getDistinctUsers: " + Integer.toString(cnt) + " distinct users.");
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
