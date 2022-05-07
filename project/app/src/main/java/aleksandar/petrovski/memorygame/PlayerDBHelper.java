package aleksandar.petrovski.memorygame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class PlayerDBHelper extends SQLiteOpenHelper {
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
        /* return that value + 1 */
        int ret = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)) + 1;
        close();
        cursor.close();
        return ret;
    }

    public PlayerDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
            "CREATE TABLE " + TABLE_NAME
            + " (" +
            COLUMN_ID +         " INT PRIMARY KEY, " +
            COLUMN_USERNAME +   " TEXT, " +
            COLUMN_EMAIL +      " TEXT, " +
            COLUMN_POINTS +     " TEXT" +
            ");"
        );
        idOffset = getMaxId();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insert(User user) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, idOffset++);                   /* this is where idOffset increases */
        values.put(COLUMN_USERNAME, user.getmUserName());
        values.put(COLUMN_EMAIL, user.getmUserEmail());
        values.put(COLUMN_POINTS, user.getmCurrentScore());

        db.insert(
                TABLE_NAME,
                null,
                values
        );
        close();
    }

    public void delete(String userName) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                TABLE_NAME,
                COLUMN_USERNAME + " =?",
                new String[] {userName}
        );
        close();
    }

    public User readUser(String userName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                COLUMN_USERNAME + " =?",
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

        close();
        cursor.close();
        return user;
    }

    private User createUser(Cursor cursor) {
        String userName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
        int points = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POINTS));

        return new User(userName, email, points);
    }
}
