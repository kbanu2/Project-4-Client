import javafx.scene.control.Button;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Client extends Thread{
    ObjectInputStream in;
    ObjectOutputStream out;
    Consumer<Serializable> callback;
    String ip;
    int port;
    Socket socket;

    Client( Consumer<Serializable> callback, String ip, int port) {
        this.callback = callback;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run(){
        try {
            socket = new Socket(ip, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            socket.setTcpNoDelay(true);

        } catch (Exception e){
            System.out.println("Could not create connection with server");
            e.printStackTrace();
        }

        try {
            out.writeObject("b b X X b b O O b");
            callback.accept(convertStringToCharArray(in.readObject().toString()));
            //out.writeObject("b b X X b b O O O");
        }catch (Exception e){
            e.printStackTrace();
        }


//        while(true){
//            //Read in input and update board
//        }
    }

    private static char[] convertStringToCharArray(String input) {
        // Remove unwanted characters from the input string
        String cleanedInput = input.replaceAll("[^a-zA-Z]", "");

        // Convert the cleaned string to a char array
        char[] charArray = cleanedInput.toCharArray();

        return charArray;
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
            out.writeObject(boardAsString.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
