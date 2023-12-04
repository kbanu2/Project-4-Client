import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Consumer;

public class ClientGUI extends Application {
    private TextField ipTextField = new TextField();
    private TextField portTextField = new TextField();
    private TextField usernameTextFIeld = new TextField();
    private Button connectButton = new Button("Attempt Connection");
    private Button newGame = new Button("New Game");
    private Button quit = new Button("Quit");
    Button easy = new Button("Easy");
    Button medium = new Button("Medium");
    Button hard = new Button("Hard");
    private ListView<String> scoreBoard = new ListView<>();
    private final ArrayList<Button> gameBoard = new ArrayList<>(9);
    private EventHandler<ActionEvent> boardSelectionEvent;
    private String gameDifficulty;
    private Client client;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Connect To Server");
        primaryStage.setScene(createConnectionScene());
        primaryStage.show();

        Consumer<Serializable> callback = data -> {
            Platform.runLater(() -> {
                GameState gameState = (GameState) data;
                updatePlayScene(gameState.gameBoard, gameState.topScoreNames, gameState.topScorePoints);
            });
        };

        connectButton.setOnAction(event -> {
            try{
                primaryStage.setTitle("Choose Difficulty");
                client = new Client(callback, ipTextField.getText(), Integer.parseInt(portTextField.getText()), usernameTextFIeld.getText());
                client.start();

                primaryStage.setScene(createDifficultyScene());
            }catch (Exception e){
                System.out.println("Could not connect to server");
                e.printStackTrace();
            }
        });

        EventHandler<ActionEvent> difficultySelectionEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gameDifficulty = ((Button)event.getSource()).getText();
                primaryStage.setTitle("Play Game");
                primaryStage.setScene(createPlayScene());

                client.sendGameDifficulty(gameDifficulty);
            }
        };

        boardSelectionEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ((Button) event.getSource()).setText("O");
                client.sendGameBoard(gameBoard);
                GameState gameState = client.getGameState();

                if (gameState.gameOver){
                    for (int i = 0; i < 9; i++){
                        gameBoard.get(i).setDisable(true);
                    }

                    scoreBoard.getItems().clear();

                    if (gameState.clientWon){
                        scoreBoard.getItems().add("You Won!");
                    }
                    else if (gameState.draw){
                        scoreBoard.getItems().add("Draw!");
                    }
                    else{
                        scoreBoard.getItems().add("Server Won");
                    }

                    newGame.setVisible(true);
                    quit.setVisible(true);
                }
            }
        };

        easy.setOnAction(difficultySelectionEvent);
        medium.setOnAction(difficultySelectionEvent);
        hard.setOnAction(difficultySelectionEvent);

        newGame.setOnAction(event -> primaryStage.setScene(createDifficultyScene()));
        quit.setOnAction(event -> System.exit(1));
    }

    public Scene createConnectionScene(){
        HBox hBox1 = new HBox(new Label("Enter IP Address of Server: "), ipTextField);
        HBox hBox2 = new HBox(new Label("Enter Port of Server: " ), portTextField);
        HBox hBox3 = new HBox(new Label("Enter Your Username: " ), usernameTextFIeld);
        VBox vBox = new VBox(hBox1, hBox2, hBox3, connectButton, new Label("Note: Local host is 127.0.0.1 :)"));

        hBox1.setAlignment(Pos.CENTER);
        hBox1.setSpacing(10);
        hBox2.setAlignment(Pos.CENTER);
        hBox2.setSpacing(10);
        hBox3.setAlignment(Pos.CENTER);
        hBox3.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);

        return new Scene(vBox, 500, 500);
    }

    public Scene createPlayScene(){
        gameBoard.clear();
        newGame.setVisible(false);
        quit.setVisible(false);

        for (int i = 0; i < 3; i++){
            gameBoard.add(new Button("_"));
            gameBoard.add(new Button("_"));
            gameBoard.add(new Button("_"));
        }

        for (int i = 0; i < 9; i++){
            gameBoard.get(i).setOnAction(boardSelectionEvent);
        }

        HBox row1 = new HBox(gameBoard.get(0), gameBoard.get(1), gameBoard.get(2));
        HBox row2 = new HBox(gameBoard.get(3), gameBoard.get(4), gameBoard.get(5));
        HBox row3 = new HBox(gameBoard.get(6), gameBoard.get(7), gameBoard.get(8));
        HBox score = new HBox(new Label("Difficulty: " + gameDifficulty), scoreBoard, new VBox(newGame, quit));
        VBox vBox = new VBox(score, row1, row2, row3);
        score.setSpacing(10);
        vBox.setSpacing(30);
        vBox.setAlignment(Pos.CENTER);
        scoreBoard.setMaxHeight(80);
        score.setAlignment(Pos.CENTER);
        row1.setSpacing(10);
        row1.setAlignment(Pos.CENTER);
        row2.setAlignment(Pos.CENTER);
        row2.setSpacing(10);
        row3.setAlignment(Pos.CENTER);
        row3.setSpacing(10);

        return new Scene(vBox, 500, 500);
    }

    public Scene createDifficultyScene(){
        VBox vBox = new VBox(new Label("Select your difficulty"), easy, medium, hard);

        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);

        return new Scene(vBox, 500, 500);
    }

    public void updatePlayScene(String[] gameBoardString, ArrayList<String> topScoreNames, ArrayList<String> topScorePoints){
        for (int i = 0; i < 9; i++){
            if (gameBoardString[i].charAt(0) == 'b'){
                gameBoard.get(i).setText("_");
            }
            else{
                gameBoard.get(i).setText(gameBoardString[i]);
                gameBoard.get(i).setDisable(true);
            }
        }

        if (!topScoreNames.isEmpty() && !topScorePoints.isEmpty()){
            scoreBoard.getItems().clear();
            for (int i = topScoreNames.size() - 1; i >= 0; i--){
                scoreBoard.getItems().add(topScoreNames.get(i) + " " + topScorePoints.get(i));
            }
        }
    }
}
