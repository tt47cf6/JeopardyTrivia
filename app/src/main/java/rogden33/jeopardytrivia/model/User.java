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

    public User (String serialized) {
        int pipeIndex = serialized.indexOf('|');
        int lengthOfUsername = Integer.parseInt(serialized.substring(0, pipeIndex));
        String username = serialized.substring(pipeIndex + 1, pipeIndex + lengthOfUsername + 1);
        String highScore = serialized.substring(pipeIndex + lengthOfUsername + 1);
        myUsername = username;
        myHighScore = Integer.parseInt(highScore);
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

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(myUsername.length());
        sb.append('|');
        sb.append(myUsername);
        sb.append(myHighScore);
        return sb.toString();
    }
}
