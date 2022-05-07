package aleksandar.petrovski.memorygame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class User {
    String              mUserName;
    String              mUserEmail;
    Integer             mBestScore;
    Integer             mWorstScore;
    Integer             mCurrentScore;
    ArrayList<Integer>  mScore;

    private void updateBestScore() {
        this.mBestScore = Collections.max(this.mScore);
    }

    private void updateWorstScore() {
        this.mWorstScore = Collections.min(this.mScore);
    }

    public Serializable getNBestResults(int howMany) {
        /* sort a list in a descending order */
        Collections.sort(this.mScore, Collections.reverseOrder());
        /* add best 10 results to return */
        ArrayList<Integer> arrayList = new ArrayList<>();
        int i = 0;
        for (Integer integer : this.mScore) {
            if (i < 10) {
                arrayList.add(integer);
                ++i;
            }
        }
        return arrayList;
    }

    public void updateBestWorstScore() {
        this.updateBestScore();
        this.updateWorstScore();
    }

    public User(String mUserName, String mUserEmail) {
        mScore = new ArrayList<>();
        this.mUserName = mUserName;
        this.mUserEmail = mUserEmail;
    }

    public User(String mUserName, String mUserEmail, Integer mCurrentScore) {
        this.mUserName = mUserName;
        this.mUserEmail = mUserEmail;
        this.mCurrentScore = mCurrentScore;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getmUserEmail() {
        return mUserEmail;
    }

    public void setmUserEmail(String mUserEmail) {
        this.mUserEmail = mUserEmail;
    }

    public Integer getmBestScore() {
        return mBestScore;
    }

    public Integer getmCurrentScore() { return mCurrentScore; }

    public void setmCurrentScore(Integer mCurrentScore) { this.mCurrentScore = mCurrentScore; }
//    public void setmBestScore(Integer mBestScore) {
//        this.mBestScore = mBestScore;
//    }

    public void addScore(Integer score) {
        this.mScore.add(score);
        /* not a smart thing to do, slow */
        //updateBestScore();
    }

    public Integer getmWorstScore() {
        return mWorstScore;
    }

//    public void setmWorstScore(Integer mWorstScore) {
//        this.mWorstScore = mWorstScore;
//    }
}
