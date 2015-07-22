package rogden33.jeopardytrivia.model;

/**
 * Created by Robert on 7/22/2015.
 */
public class User {

    private final String myUsername;

    private int myHighScore;

    public User(String username, int highScore) {
        myUsername = username;
        myHighScore = highScore;
    }

    public String getUsername() {
        return myUsername;
    }

    public int getHighScore() {
        return myHighScore;
    }

    public void setHighScore(int score) {
        myHighScore = score;
    }
}
