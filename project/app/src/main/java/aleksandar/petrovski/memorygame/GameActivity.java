package aleksandar.petrovski.memorygame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    private Button[]                mMatrixButtons;
    private Button                  mStartRestartButton;
    private ImageView[]             mMatrixImages;
    private int[]                   mImageHolder;
    private boolean                 onceStartRestart = true;
    private int                     mMemoPress;
    private ArrayList<ImageView>    mSkipPicture;
    private ArrayList<Button>       mSkipButton;
    private ArrayList<Button>       mQueryButton;
    private ArrayList<ImageView>    mQueryPicture;
    private int[]                   mWhich;
    private int                     mScore;
    public PlayerDBHelper           mDB;
    private User                    mCurrentUser;
    private boolean                 mDone;
    private final String            mSQLiteName = "memory_game.db";
    private final String            URL = "http://192.168.43.69:3000";

    private void randomizeImages(int howMuch) {
        int randX, randY;
        int x_temp;
        Random random = new Random();
        for (int i = 0; i < howMuch; ++i) {
            // indexes to swap
            randX = random.nextInt(15);
            randY = random.nextInt(15);
            // swap pictures
            // temp <= x
            Drawable drawable = mMatrixImages[randX].getDrawable();
            // x <= y
            mMatrixImages[randX].setImageDrawable(mMatrixImages[randY].getDrawable());
            // y <= temp
            mMatrixImages[randY].setImageDrawable(drawable);
            // remember this in imageHolder
            x_temp = mImageHolder[randX];
            mImageHolder[randX] = mImageHolder[randY];
            mImageHolder[randY] = x_temp;
        }
    }

    private int getIndexFromID(int id) {
            if (id == R.id.mb0)         return 0;
            else if (id == R.id.mb1)    return 1;
            else if (id == R.id.mb2)    return 2;
            else if (id == R.id.mb3)    return 3;
            else if (id == R.id.mb4)    return 4;
            else if (id == R.id.mb5)    return 5;
            else if (id == R.id.mb6)    return 6;
            else if (id == R.id.mb7)    return 7;
            else if (id == R.id.mb8)    return 8;
            else if (id == R.id.mb9)    return 9;
            else if (id == R.id.mb10)   return 10;
            else if (id == R.id.mb11)   return 11;
            else if (id == R.id.mb12)   return 12;
            else if (id == R.id.mb13)   return 13;
            else if (id == R.id.mb14)   return 14;
            else if (id == R.id.mb15)   return 15;
            else                        return -1;
    }

    private void addGameToServer(String userName, int score) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                HttpHelper httpHelper = new HttpHelper();
                try {
                    jsonObject.put("username", userName);
                    jsonObject.put("score", (score == 0) ? -1 : score);
                    Log.i("moje", "run: add score from " + userName + " = " + score);
                    int i = httpHelper.postJSONObjectFromURL(URL + "/score", jsonObject);
                    switch (i) {
                        case 201:
                            /* everything is good */
                            Log.i("moje", "run: game added successfully");
                            break;
                        case 400: // FALLTHROUGH
                        case -1:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(GameActivity.this, "CONNECTION_FAILED " + i, Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mDB = new PlayerDBHelper(this, mSQLiteName, null, 1);
        mDB.setId();
        String name = bundle.getString("username");
        mCurrentUser = new User(name);

        Log.i("debag", "onCreate: mCurrentUser = " + name);

        Log.i("moje", "ime = " + name);

        mMatrixButtons = new Button[16];
        mMatrixImages = new ImageView[16];
        mImageHolder = new int[16];
        mSkipPicture = new ArrayList<>();
        mSkipButton = new ArrayList<>();
        mWhich = new int[2];
        mQueryPicture = new ArrayList<>();
        mQueryButton = new ArrayList<>();
        mScore = 0;
        mDone = false;

        /* da li ovo moze drugacije ??? */
        mMatrixButtons[0] = findViewById(R.id.mb0);
        mMatrixButtons[1] = findViewById(R.id.mb1);
        mMatrixButtons[2] = findViewById(R.id.mb2);
        mMatrixButtons[3] = findViewById(R.id.mb3);
        mMatrixButtons[4] = findViewById(R.id.mb4);
        mMatrixButtons[5] = findViewById(R.id.mb5);
        mMatrixButtons[6] = findViewById(R.id.mb6);
        mMatrixButtons[7] = findViewById(R.id.mb7);
        mMatrixButtons[8] = findViewById(R.id.mb8);
        mMatrixButtons[9] = findViewById(R.id.mb9);
        mMatrixButtons[10] = findViewById(R.id.mb10);
        mMatrixButtons[11] = findViewById(R.id.mb11);
        mMatrixButtons[12] = findViewById(R.id.mb12);
        mMatrixButtons[13] = findViewById(R.id.mb13);
        mMatrixButtons[14] = findViewById(R.id.mb14);
        mMatrixButtons[15] = findViewById(R.id.mb15);
        mMatrixImages[0] = findViewById(R.id.img0);
        mMatrixImages[1] = findViewById(R.id.img1);
        mMatrixImages[2] = findViewById(R.id.img2);
        mMatrixImages[3] = findViewById(R.id.img3);
        mMatrixImages[4] = findViewById(R.id.img4);
        mMatrixImages[5] = findViewById(R.id.img5);
        mMatrixImages[6] = findViewById(R.id.img6);
        mMatrixImages[7] = findViewById(R.id.img7);
        mMatrixImages[8] = findViewById(R.id.img8);
        mMatrixImages[9] = findViewById(R.id.img9);
        mMatrixImages[10] = findViewById(R.id.img10);
        mMatrixImages[11] = findViewById(R.id.img11);
        mMatrixImages[12] = findViewById(R.id.img12);
        mMatrixImages[13] = findViewById(R.id.img13);
        mMatrixImages[14] = findViewById(R.id.img14);
        mMatrixImages[15] = findViewById(R.id.img15);


        mStartRestartButton = findViewById(R.id.startrestartbutton);
        Button mStatisticsButton = findViewById(R.id.statbutton);

        for (Button butt : mMatrixButtons) {
            butt.setEnabled(false);
            butt.setOnClickListener(this);
        }
        for (ImageView img : mMatrixImages) {
            img.setVisibility(View.INVISIBLE);
        }
        for (int i = 0; i < 16; ++i) {
            mImageHolder[i] = i / 2;
        }
        mStartRestartButton.setOnClickListener(this);
        mStatisticsButton.setOnClickListener(this);
    }

    public void autoRestart(@NonNull View view) {
        int id = view.getId();
        /* set done if done */
        if (mSkipButton.size() == 16) {
            mDone = true;
        }
        /* reset query */
        mQueryButton.clear();
        mQueryPicture.clear();
        /* set buttons */
        for (Button butt : mMatrixButtons) {
            if (mSkipButton.contains(butt)) {
                /* correct guess should not be cleared */
                Log.i("moje", "onClick: skip this button");
                /* button is already disabled, change only visibility */
                butt.setVisibility(View.INVISIBLE);
            } else {
                butt.setEnabled(true);
                butt.setVisibility(View.VISIBLE);
            }
        }
        /* hide images */
        for (ImageView img : mMatrixImages) {
            if (mSkipPicture.contains(img)) {
                /* correct guess should not be cleared */
                Log.i("moje", "onClick: skip this picture");
            } else {
                img.setVisibility(View.INVISIBLE);
            }
        }
        /* reset click counter */
        mMemoPress = 0;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.startrestartbutton) {
            /* randomize images every time the restart button is pressed */
            randomizeImages(30);
            if (onceStartRestart) { /* only once */
                mStartRestartButton.setText(R.string.restart_buttontext);
                mStartRestartButton.setBackgroundColor(Color.BLUE);
                onceStartRestart = false;
            } else {
                /* save the game here */
//                mDB.fInsert(new User(
//                        mCurrentUser.getmUserName(),
//                        mCurrentUser.getmUserEmail(),
//                        ((mDone)? mScore : 0)   /* set score 0 if the game is prematurely finished */
//                ));
                addGameToServer(mCurrentUser.getmUserName(), ((mDone)? mScore : 0));
                /* clear out skips */
                mSkipButton.clear();
                mSkipPicture.clear();
                /* clear out score */
                mScore = 0;
                /* reset gone signal */
                mDone = false;
            }
            autoRestart(view);
        } else if (id == R.id.statbutton) {
            /* if the game is done, push the result to DB, if not - do nothing */
            if (mDone) {
                /* save the game here */
//                mDB.fInsert(new User(
//                        mCurrentUser.mUserName,
//                        mCurrentUser.mUserEmail,
//                        mScore
//                ));
                addGameToServer(mCurrentUser.getmUserName(), mScore);
                /* clear out skips*/
                mSkipButton.clear();
                mSkipPicture.clear();
                /* clear out score */
                mScore = 0;
                /* reset done sig */
                mDone = false;
                /* reset oncestartrestart */
                onceStartRestart = true;
            }
            /* jump to statistics activity */
            Intent it = new Intent(GameActivity.this, StatisticsActivity.class);
            it.putExtra("username", mCurrentUser.getmUserName());
            startActivity(it);
        } else {
            /* this part takes care of other clickables - matrix buttons */
            if (mMemoPress <= 1) {
                /* disable button */
                findViewById(id).setEnabled(false);
                findViewById(id).setVisibility(View.INVISIBLE);
                /* visible image */
                mMatrixImages[getIndexFromID(id)].setVisibility(View.VISIBLE);
                /* remember which picture in query */
                mWhich[mMemoPress] = mImageHolder[getIndexFromID(id)];
                /* add button to skip query */
                mQueryButton.add(findViewById(id));
                /* add picture to skip query */
                mQueryPicture.add(mMatrixImages[getIndexFromID(id)]);
            }
            if (mMemoPress == 1) {
                /* disable all buttons */
                for (Button button : mMatrixButtons) {
                    button.setEnabled(false);
                }
                /* check if memo guess is correct */
                if (mWhich[0] == mWhich[1]) { /* correct guess */
                    Log.i("moje", "onClick: correct guess");
                    /* add queried pictures and buttons to skip section */
                    mSkipPicture.addAll(mQueryPicture);
                    mSkipButton.addAll(mQueryButton);
                    mScore += 5;
                    /* autorestart other buttons now */
                    autoRestart(view);
                } else { /* incorrect guess */
                    mScore -= 1;
                    /* autorestart buttons with delay of 1 second */
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            autoRestart(view);
                        }
                    }, 1000);
                }
                return;
            }
            /* increase click count */
            ++mMemoPress;
        }
    }
}
