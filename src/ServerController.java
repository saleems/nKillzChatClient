import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;


/**
 * Created by Sadruddin on 12/15/2014.
 */
public class ServerController implements Initializable, Runnable {
    @FXML
    private TextArea idListTextArea;
    @FXML
    private TextArea chatTextArea;
    @FXML
    private Button kickButton;
    @FXML
    private Button sendButton;
    @FXML
    private Button startButton;
    @FXML
    private TextField kickIdTextField;
    @FXML
    private TextField chatTextField;
    @FXML
    private TextField portTextField;

    private ArrayList<ChatServerThread> clients = new ArrayList<ChatServerThread>();
    private ServerSocket server = null;
    private Thread thread = null;
    private int clientCount = 0;

    private ArrayList<Integer> IDs = new ArrayList<Integer>();
    private HashMap<Integer, String> nicknameTable = new HashMap<Integer, String>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sendServerMessage(chatTextField.getText());
                chatTextField.setText("");
            }
        });

        chatTextField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sendServerMessage(chatTextField.getText());
                chatTextField.setText("");
            }
        });

        portTextField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (portTextField.getText().equals("")) {
                    startServer(8181);
                } else {
                    startServer(Integer.parseInt(portTextField.getText()));
                }
                portTextField.setText("");
                startButton.setDisable(true);
                portTextField.setDisable(true);
            }
        });

        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (portTextField.getText().equals("")) {
                    startServer(8181);
                } else {
                    startServer(Integer.parseInt(portTextField.getText()));
                }
                portTextField.setText("");
                startButton.setDisable(true);
                portTextField.setDisable(true);
            }
        });

        kickIdTextField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    remove(Integer.parseInt(kickIdTextField.getText()));
                } catch (NumberFormatException e) {
                    println("Invalid ID : " + kickIdTextField.getText());
                }
                kickIdTextField.setText("");
            }
        });

        kickButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    remove(Integer.parseInt(kickIdTextField.getText()));
                } catch (NumberFormatException e) {
                    println("Invalid ID : " + kickIdTextField.getText());
                }
                kickIdTextField.setText("");
            }
        });
    }

    private void startServer(int port) {
        try {
            println("Binding to port " + port + ", please wait  ...");
            server = new ServerSocket(port);
            println("Server started: " + server);
            start();
        } catch (IOException ioe) {
            println("Can not bind to port " + port + ": " + ioe.getMessage());
        }
    }

    public void run() {
        while (thread != null) {
            try {
                addThread(server.accept());
            } catch (IOException ioe) {
                println("Server accept error: " + ioe);
                stop();
            }
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    private int findClient(int ID) {
        for (int i = 0; i < clientCount; i++)
            if (clients.get(i).getID() == ID)
                return i;
        return -1;
    }

    public synchronized void handle(int ID, String input) {
        if (input.equals(".bye")) {
            clients.get(findClient(ID)).send(".bye");
            remove(ID);
        } else if (input.startsWith("nick~"))
            handleNicknameMessage(ID, input);
        else {
            for (int i = 0; i < clientCount; i++)
                clients.get(i).send(nicknameTable.get(ID) + ": " + input);
            println(nicknameTable.get(ID) + ": " + input);
            if (input.length() > 3 && input.substring(0, 3).equalsIgnoreCase("add"))
                handleAddMessage(input, ID);
        }
    }

    private void handleNicknameMessage(int id, String input) {
        String[] parts = input.split(" ");
        String nickname = parts[1];

        nicknameTable.put(id, nickname);
        updateIDList();
    }

    private void println(String s) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chatTextArea.appendText(s + "\n");
            }
        });
    }

    /**
     * this helper method is called when a message that begins with "add" is
     * received from a client. Simply adds the numbers following "add" and sends
     * the result to all clients.
     * <p>
     * If an invalid command is sent, the server will send a message to all
     * clients saying so.
     *
     * @param input should be formatted so that there are only numbers following
     *              "add" and each number is separated by a " "
     * @param ID    the ID of the client who made the request
     */
    private void handleAddMessage(String input, int ID) {
        int count = 0;
        int startAt;
        //skips all the spaces after "add" in input
        for (startAt = 3; startAt < input.length(); startAt++)
            if (input.charAt(startAt) != ' ')
                break;

        String workWith = input.substring(startAt, input.length());
        String[] numbers = workWith.split(" ");

        try {
            for (String s : numbers) {
                count += Integer.parseInt(s);
            }

            sendServerMessage("The result of " + ID + "'s ADD request is: " + count);
        } catch (NumberFormatException e) {
            sendServerMessage("Invalid add command from " + ID);
        }
    }

    /**
     * sends message s to all clients prepended by "SERVER: "
     *
     * @param s
     */
    private void sendServerMessage(String s) {
        println("SERVER" + ": " + s);
        for (int i = 0; i < clientCount; i++)
            clients.get(i).send("SERVER" + ": " + s);
    }

    public synchronized void remove(int ID) {
        int pos = findClient(ID);
        if (!IDs.contains((Integer) ID)) {
            println(ID + " is not a valid client port!");
        }
        if (pos >= 0) {
            sendServerMessage(nicknameTable.get(ID) + " has been kicked!");
            ChatServerThread toTerminate = clients.get(pos);
            toTerminate.send("kicked");
            IDs.remove((Integer) toTerminate.getID());
            nicknameTable.remove(ID);
            println("Removing client thread " + ID);
            clients.remove(pos);
            clientCount--;
            try {
                toTerminate.close();
            } catch (IOException ioe) {
                println("Error closing thread: " + ioe);
            }
            toTerminate.interrupt();
            updateIDList();
        }
    }

    private void addToIDList(String s) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                idListTextArea.appendText(s + "\n");
            }
        });
    }

    private void updateIDList() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                idListTextArea.clear();
            }
        });

        for (int i : IDs) {
            addToIDList("Nickname: " + nicknameTable.get(i) + " ID: " + String.valueOf(i));
        }
    }

    private void addThread(Socket socket) {
        println("Client accepted: " + socket);
        clients.add(new ChatServerThread(this, socket));
        IDs.add(socket.getPort());
        addToIDList(String.valueOf(socket.getPort()));
        try {
            clients.get(clientCount).open();
            clients.get(clientCount).start();
            clientCount++;
        } catch (IOException ioe) {
            println("Error opening thread: " + ioe);
        }

    }


    class ChatServerThread extends Thread {
        private ServerController server = null;
        private Socket socket = null;
        private int ID = -1;
        private DataInputStream streamIn = null;
        private DataOutputStream streamOut = null;

        public ChatServerThread(ServerController _server, Socket _socket) {
            super();
            server = _server;
            socket = _socket;
            ID = socket.getPort();
        }

        public void send(String msg) {
            try {
                streamOut.writeUTF(msg);
                streamOut.flush();
            } catch (IOException ioe) {
                println(ID + " ERROR sending: " + ioe.getMessage());
                server.remove(ID);
                stop();
            }
        }

        public int getID() {
            return ID;
        }

        public void run() {
            println("Server Thread " + ID + " running.");
            while (true) {
                try {
                    server.handle(ID, streamIn.readUTF());
                } catch (IOException ioe) {
                    //println(ID + " ERROR reading: " + ioe.getMessage());
                    //server.remove(ID);
                    stop();
                }
            }
        }

        public void open() throws IOException {
            streamIn = new DataInputStream(new
                    BufferedInputStream(socket.getInputStream()));
            streamOut = new DataOutputStream(new
                    BufferedOutputStream(socket.getOutputStream()));
        }

        public void close() throws IOException {
            if (socket != null) socket.close();
            if (streamIn != null) streamIn.close();
            if (streamOut != null) streamOut.close();
        }
    }
}
