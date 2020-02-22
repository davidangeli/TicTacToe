package tictactoe.opponent;

import javafx.application.Platform;
import tictactoe.AbstractGame;
import tictactoe.Main;
import tictactoe.Opponent;
import tictactoe.Player;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * Implements an opponent that receives moves from a server.
 */
public class RemoteOpponent implements Opponent {
    private final static String SERVER = "localhost";
    private final static int SERVERPORT = 42014;
    private volatile boolean initDone = false;
    private Player whoserves;
    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectOutputStream objectOut;
    private ObjectInputStream objectIn;
    final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

    public RemoteOpponent (Player whoserves) {
        this.whoserves = whoserves;
    }

    public <T> void sendGameInfo (AbstractGame<T> game) throws IOException {
        if (!initDone) return;

        objectOut.writeObject(game.getClass());
        objectOut.flush();
    }

    public Class receiveGameInfo () throws IOException, ClassNotFoundException {
        if (!initDone) return null;

        return (Class) objectIn.readObject();
    }

    public void discard () {
        System.out.println("Discard called.");
        System.out.println("Discard continues. Open tasks: " + executorService.shutdownNow().size());
        try {
            if (serverSocket != null) serverSocket.close();
            if (initDone) {
                objectIn.close();
                objectOut.close();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Executor shutdown: " + executorService.isShutdown());
        System.out.println("Executor terminated: " + executorService.isTerminated());
        System.out.println("Discard done.");
    }

    /**
     * Tries to set up connection to the remote opponent for 10 seconds.
     */
    public void init(Main main){
        Future<?> fsocket = executorService.submit(() -> {
            initTask();
        });

        executorService.submit(()->{
            try {
                fsocket.get(10, TimeUnit.SECONDS);
                initDone = true;
                Platform.runLater(()->main.gotRemoteOpponent(whoserves));
            } catch (Exception e) {
                fsocket.cancel(true);
                Platform.runLater(()->main.failedRemoteOpponent());
                e.printStackTrace();
            }
        });
    }

    private void initTask () {
        try {
            if (whoserves == Player.PLAYER) {
                serverSocket = new ServerSocket(SERVERPORT);
                System.out.println("Serversocket created.");
                socket = serverSocket.accept();
                System.out.println("Socket created by serverSocket.");
                serverSocket.close();
            }
            else {
                socket = new Socket(SERVER, SERVERPORT);
                System.out.println("Socket created");
            }
            objectOut = new ObjectOutputStream(socket.getOutputStream());
            objectIn = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Exception in init task.");
            e.printStackTrace();
        }
    }

    public boolean isInitialized(){
        return initDone;
    }

    @Override
    public <T> Optional<T> getNextStep(AbstractGame<T> state) throws IOException, ClassNotFoundException {
        if (!initDone) return null;

        if (!state.getSteps().isEmpty()) {
            objectOut.writeObject(state.getSteps().getLast().getValue());
            objectOut.flush();
        }
        return (Optional<T>) objectIn.readObject();
    }


}
