package cocochat.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;


/**
 * Created by Sadruddin on 12/15/2014.
 */
public class ClientController implements Initializable {
    @FXML //  fx:id="myButton"
    private TextArea mainTextArea;
    @FXML
    private TextField sendTextField;
    @FXML
    private Button sendButton;
    @FXML
    private TextField ipTextField;
    @FXML
    private TextField nicknameTextField;
    @FXML
    private Button connectButton;
    @FXML
    private TextField portTextField;
    @FXML
    private Button playButton;

    private Socket socket = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private ChatClientThread client = null;
    private String serverName = "localhost";
    private int serverPort = 8181;

    String nickname = "";

    Media hit = new Media(ClientController.class.getResource("coco.mp3").toString());
    MediaPlayer mediaPlayer = new MediaPlayer(hit);


    @Override
    // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert sendButton != null : "fx:id=\"myButton\" was not injected: check your FXML file 'simple.fxml'.";

        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                send();
            }
        });

        connectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pullAndConnect();
            }
        });

        sendTextField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                send();
            }
        });

        portTextField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pullAndConnect();
            }
        });

        playButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (playButton.getText().equalsIgnoreCase("play")) {
                    mediaPlayer.play();
                    playButton.setText("Stop");
                } else {
                    mediaPlayer.pause();
                    playButton.setText("Play");
                }
            }
        });
    }

    private void pullAndConnect() {
        if (ipTextField.getText().equals("")) {
            connect("localhost", 8181);
        } else {
            String server = ipTextField.getText();
            ipTextField.clear();
            int port = Integer.parseInt(portTextField.getText());
            portTextField.clear();
            connect(server, port);
        }
        setAndSendNickname();

        sendTextField.requestFocus();
    }

    private void setAndSendNickname() {
        if (nicknameTextField.getText().equals(""))
            nickname = String.valueOf(socket.getPort());
        else
            nickname = nicknameTextField.getText();

        sendNicknameToServer();
    }

    private void sendNicknameToServer() {
        try {
            streamOut.writeUTF("nick~ " + nickname);
            streamOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void send() {
        try {
            streamOut.writeUTF(sendTextField.getText());
            streamOut.flush();
            sendTextField.setText("");
            sendTextField.setPromptText(nickname + ", please enter a message.");
        } catch (IOException ioe) {
            mainTextArea.appendText("Sending error: " + ioe.getMessage() + "\n");
            close();
        }
    }

    public void handle(String msg) {
        if (msg.equals(".bye")) {
            println("Good bye. Press RETURN to exit ...");
            close();
        } else if (msg.equals("kicked")) {
            println("You've been kicked by the server.");
            close();
        } else println(msg);
    }

    private void println(String s) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainTextArea.appendText(s + "\n");
            }
        });
    }

    public void connect(String serverName, int serverPort) {
        println("Establishing connection. Please wait ...");
        try {
            socket = new Socket(serverName, serverPort);
            println("Connected: " + socket);
            open();
        } catch (UnknownHostException uhe) {
            println("Host unknown: " + uhe.getMessage());
        } catch (IOException ioe) {
            println("Unexpected exception: " + ioe.getMessage());
        }
    }

    public void open() {
        try {
            streamOut = new DataOutputStream(socket.getOutputStream());
            client = new ChatClientThread(this, socket);
        } catch (IOException ioe) {
            println("Error opening output stream: " + ioe);
        }
    }

    public void close() {
        try {
            if (streamOut != null) streamOut.close();
            if (socket != null) socket.close();
        } catch (IOException ioe) {
            println("Error closing ...");
        }
        client.close();
        client.stop();
    }

    class ChatClientThread extends Thread {
        private Socket socket = null;
        private ClientController client = null;
        private DataInputStream streamIn = null;

        public ChatClientThread(ClientController _client, Socket _socket) {
            client = _client;
            socket = _socket;
            open();
            start();
        }

        public void open() {
            try {
                streamIn = new DataInputStream(socket.getInputStream());
            } catch (IOException ioe) {
                System.out.println("Error getting input stream: " + ioe);
                client.close();
            }
        }

        public void close() {
            try {
                if (streamIn != null) streamIn.close();
            } catch (IOException ioe) {
                System.out.println("Error closing input stream: " + ioe);
            }
        }

        public void run() {
            while (true) {
                try {
                    client.handle(streamIn.readUTF());
                } catch (IOException ioe) {
                    System.out.println("Listening error: " + ioe.getMessage());
                    client.close();
                }
            }
        }
    }
}



