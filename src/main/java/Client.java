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

    Client(Consumer<Serializable> callback, String ip, int port) {
        this.callback = callback;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run(){
        try (Socket socket = new Socket(ip, port)){
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            socket.setTcpNoDelay(true);
        } catch (Exception e){
            System.out.println("Could not create connection with server");
            e.printStackTrace();
        }

        while(true){
            //Read in input and update board
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
            out.writeObject(boardAsString.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
