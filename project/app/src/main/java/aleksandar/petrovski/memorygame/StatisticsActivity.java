package aleksandar.petrovski.memorygame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class StatisticsActivity extends AppCompatActivity {
    private Button          refreshButton;
    String                  mSQLiteName = "memory_game.db";
    PlayerDBHelper          mDB;
    private final String    URL = "http://192.168.43.69:3000";
    UserAdapter             userAdapter;
    ArrayList<User>         users;
    private static String   me;
    ServiceRefresher        serviceRefresher;
    Receiver                receiver;
    //public static Context thisContext;


    HttpHelper httpHelper;

    public static String getMe() {
        Log.i("moje", "getMe: " + me);
        return me;
    }

    private void refreshLocalDatabaseAndUpdateAdapter() {
        new Thread(() -> {
            userAdapter.mUsers.clear();
            mDB.deleteAll();
        try {
            JSONArray jsonArray = httpHelper.getJSONArrayFromURL(URL + "/score");
            for (int i = 0; i < jsonArray.length(); ++i) {
                String userName = jsonArray.getJSONObject(i).getString("username");
                int score = jsonArray.getJSONObject(i).getInt("score");
                Log.i("moje", "run: username " + userName + " score " + score);
                User user = new User(userName);
                user.setmCurrentScore(score);
                runOnUiThread(() -> mDB.fInsert(user));
            }
            runOnUiThread(() -> {
                updateAdapter();
                userAdapter.notifyDSC();
            });
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void updateAdapter() {
        userAdapter.mUsers.clear();
        ArrayList<User> temp = mDB.getDistinctUsers();
        if (temp == null) {
            Log.i("moje", "au brate");
        } else {
            if (temp.isEmpty()) {
                Log.i("moje", "empty temp arraylist wtf");
            } else {
                for (User user : temp) {
                    Log.i("moje", "mesaga " + user.getmUserName());
                }
            }
            /* get their scores */
            for (User user : temp) {
                users.add(mDB.readUser(user.mUserName));
            }
            /* put them in adapter */
            for (User user : users) {
                Log.i("moje", "user: " + user.getmUserName());
                for (int i : user.getmScore()) {
                    Log.i("moje", "\t\t" + i);
                }
                userAdapter.addUser(user);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Bundle bundle = getIntent().getExtras();
        me = bundle.getString("username");

        Log.i("moje", "onCreate: me = " + me);

        mDB = new PlayerDBHelper(this, mSQLiteName, null, 1);
        mDB.setId();

        httpHelper = new HttpHelper();

        receiver = new Receiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ServiceRefresher.MY_ACTION);
        registerReceiver(receiver, intentFilter);

        Intent intent = new Intent(StatisticsActivity.this, ServiceRefresher.class);
        startService(intent);

        refreshButton = findViewById(R.id.refreshstatistics);
        refreshButton.setOnClickListener(view -> {
            userAdapter = new UserAdapter(getApplicationContext());
            refreshLocalDatabaseAndUpdateAdapter();
        });

        refreshLocalDatabaseAndUpdateAdapter();

        userAdapter = new UserAdapter(this);
        users = new ArrayList<>();



        ListView listView = findViewById(R.id.listvju);
        listView.setAdapter(userAdapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            User user = (User) userAdapter.getItem(i);
            Intent intent1 = new Intent(StatisticsActivity.this, DetailsActivity.class);
            Bundle arguments = new Bundle();
            arguments.putSerializable("arraylist", user.getNBestResults(10));
            for (int res : user.getNBestResults(10)) {
                Log.i("moje", "hop: " + res);
            }
            intent1.putExtra("nBestResults", arguments);
            intent1.putExtra("userName", user.getmUserName());
            startActivity(intent1);
        });
    }

    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            userAdapter.mUsers.clear();
            updateAdapter();
            userAdapter.notifyDSC();
            // todo add push notification
            NotificationChannel channel = new NotificationChannel("c", "channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(StatisticsActivity.this, "c")
                    .setSmallIcon(R.drawable.p1)
                    .setContentTitle("notification cool")
                    .setContentText("cool notifikacija");

            Notification notification = builder.build();
            NotificationManagerCompat notificationManagerCompat =
                    NotificationManagerCompat.from(StatisticsActivity.this);
            notificationManagerCompat.notify(1, notification);
        }
    }
}
