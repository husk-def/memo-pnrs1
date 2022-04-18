package aleksandar.petrovski.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
            if (id == R.id.mb0) return 0;
            else if (id == R.id.mb1) return 1;
            else if (id == R.id.mb2) return 2;
            else if (id == R.id.mb3) return 3;
            else if (id == R.id.mb4) return 4;
            else if (id == R.id.mb5) return 5;
            else if (id == R.id.mb6) return 6;
            else if (id == R.id.mb7) return 7;
            else if (id == R.id.mb8) return 8;
            else if (id == R.id.mb9) return 9;
            else if (id == R.id.mb10) return 10;
            else if (id == R.id.mb11) return 11;
            else if (id == R.id.mb12) return 12;
            else if (id == R.id.mb13) return 13;
            else if (id == R.id.mb14) return 14;
            else if (id == R.id.mb15) return 15;
            else return -1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mMatrixButtons = new Button[16];
        mMatrixImages = new ImageView[16];
        mImageHolder = new int[16];
        mSkipPicture = new ArrayList<>();
        mSkipButton = new ArrayList<>();
        mWhich = new int[2];
        mQueryPicture = new ArrayList<>();
        mQueryButton = new ArrayList<>();
        mScore = 0;

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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.startrestartbutton) {
            /* reset query */
            mQueryPicture.clear();
            mQueryButton.clear();
            /* set buttons */
            for (Button button : mMatrixButtons) {
                if (mSkipButton.contains(button)) {
                    /* correct guess should not be cleared */
                    Log.i("moje", "onClick: skip this button");
                    /* button is already disabled, change only visibility */
                    button.setVisibility(View.INVISIBLE);
                } else {
                    button.setEnabled(true);
                    button.setVisibility(View.VISIBLE);
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
            if (onceStartRestart) { /* only once */
                /* randomize images */
                randomizeImages(30);
                mStartRestartButton.setText(R.string.restart_buttontext);
                mStartRestartButton.setBackgroundColor(Color.BLUE);
                onceStartRestart = false;
            }
            mMemoPress = 0;
        } else if (id == R.id.statbutton) {
            /* jump to statistics activity */
            Intent it = new Intent(GameActivity.this, StatisticsActivity.class);
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
                } else { /* incorrect guess */
                    mScore -= 1;
                }
            }
            ++mMemoPress;
        }
    }
}