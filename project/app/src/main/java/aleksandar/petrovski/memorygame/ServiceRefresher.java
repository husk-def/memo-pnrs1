package aleksandar.petrovski.memorygame;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class ServiceRefresher extends Service {
    PlayerDBHelper          playerDBHelper;
    HttpHelper              httpHelper;
    final static String     MY_ACTION = "MY_ACTION";
    private final String    URL = "http://192.168.43.69:3000";
    String                  mSQLiteName = "memory_game.db";



    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        playerDBHelper = new PlayerDBHelper(this, mSQLiteName, null, 1);
        httpHelper = new HttpHelper();

        MyThread myThread = new MyThread();
        myThread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    public class MyThread extends Thread {
        @Override
        public void run() {
            httpHelper = new HttpHelper();
            for (;;) {
                try {
                    Thread.sleep(5000);
                    Intent intent = new Intent();
                    intent.setAction(MY_ACTION);

                    // intent.putExtra("DATAPASSED", i);
                    // todo update
                    JSONArray jsonArray = httpHelper.getJSONArrayFromURL(URL + "/score/");
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        String userName = jsonArray.getJSONObject(i).getString("username");
                        int score = jsonArray.getJSONObject(i).getInt("score");
                        Log.i("moje", "run: username " + userName + " score " + score);
                        User user = new User(userName);
                        user.setmCurrentScore(score);
                        playerDBHelper.fInsert(user);
                    }
                    sendBroadcast(intent);
                } catch (InterruptedException | IOException | JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }
}