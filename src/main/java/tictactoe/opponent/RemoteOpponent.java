package tictactoe.opponent;

import tictactoe.AbstractGame;
import tictactoe.Opponent;
import java.io.*;
import java.net.Socket;
import java.util.Optional;

/**
 * Implements an opponent that receives moves from a server.
 */
public class RemoteOpponent implements Opponent {
    private final static String SERVERNAME = "localhost";
    private final static int SERVERPORT = 42014;
    private final Socket socket;
    private final ObjectOutputStream objectOut;
    private final ObjectInputStream objectIn;

    public RemoteOpponent () throws IOException {
        socket = new Socket("localhost", 42030);
        objectOut = new ObjectOutputStream(socket.getOutputStream());
        objectIn = new ObjectInputStream(socket.getInputStream());
    }

    public <T> void sendGame(AbstractGame<T> game) throws IOException {
        objectOut.writeObject(game.getClass());
        objectOut.flush();
    }

    public Class receiveClass () throws IOException, ClassNotFoundException {
        return (Class) objectIn.readObject();
    }

    public <T> AbstractGame<T> receiveGame() throws IOException, ClassNotFoundException {
        return (AbstractGame<T>) objectIn.readObject();
    }

    public void discard () throws IOException {
        objectIn.close();
        objectOut.close();
        socket.close();
    }

    @Override
    public <T> Optional<T> getNextStep(AbstractGame<T> state) throws IOException, ClassNotFoundException {
        if (!state.getSteps().isEmpty()) {
            objectOut.writeObject(state.getSteps().getLast().getValue());
            objectOut.flush();
        }
        return (Optional<T>) objectIn.readObject();
    }


}
