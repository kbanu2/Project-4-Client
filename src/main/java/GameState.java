import java.io.Serializable;
import java.util.ArrayList;

public class GameState implements Serializable {
    public String[] gameBoard = new String[]{"b", "b", "b", "b", "b", "b", "b", "b", "b"};
    public boolean gameOver = false;
    public boolean clientWon = false;
    public boolean draw = false;
    ArrayList<String> topScoreNames = new ArrayList<>();
    ArrayList<String> topScorePoints = new ArrayList<>();
}
