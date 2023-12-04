import javafx.animation.PauseTransition;
import javafx.scene.control.Button;
import javafx.util.Duration;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class Client extends Thread{
    ObjectInputStream in;
    ObjectOutputStream out;
    Consumer<Serializable> callback;
    String gameDifficulty;
    String username;
    String ip;
    int port;
    Socket socket;
    static GameState gameState = new GameState();

    Client( Consumer<Serializable> callback, String ip, int port, String username) {
        this.callback = callback;
        this.ip = ip;
        this.port = port;
        this.username = username;
    }

    @Override
    public void run(){
        try {
            socket = new Socket(ip, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            socket.setTcpNoDelay(true);
            out.writeObject(username);
        } catch (Exception e){
            System.out.println("Could not create connection with server");
            e.printStackTrace();
        }

        try {


            while (true){
                if (gameState.gameOver){ //Update clientGUI or something
                    if (gameState.clientWon){
                        System.out.println("Client Won");
                    }
                    else if (gameState.draw){
                        System.out.println("Draw");
                    }
                    else{
                        System.out.println("Server Won");
                    }
                     //New gameState with only score board updated

                    //Reset game back to difficulty scene

                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public GameState getGameState(){
        return gameState;
    }

    public GameState interceptNextGameState(){
        try{
            return (GameState) in.readObject();
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private static char[] convertStringToCharArray(String input) {
        // Remove unwanted characters from the input string
        String cleanedInput = input.replaceAll("[^a-zA-Z]", "");

        // Convert the cleaned string to a char array
        char[] charArray = cleanedInput.toCharArray();

        return charArray;
    }
    public void sendGameDifficulty(String gameDifficulty){
        try{
            out.reset();
            out.writeObject(gameDifficulty);

            gameState = (GameState) in.readObject(); //Receive blank board with score board
            callback.accept(gameState);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendGameBoard(ArrayList<Button> gameBoard){
        StringBuilder boardAsString = new StringBuilder();

        for (int i = 0; i < 9; i++){
            if (gameBoard.get(i).getText().equals("_")){
                boardAsString.append("b");
            }
            else{
                boardAsString.append(gameBoard.get(i).getText());
            }
            boardAsString.append(" ");
        }

        try{
            out.reset();
            out.writeObject(boardAsString.toString());

            gameState = (GameState) in.readObject();


            PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2));

            pauseTransition.setOnFinished(event -> callback.accept(gameState));

            pauseTransition.play();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
