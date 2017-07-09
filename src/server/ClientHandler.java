package server;

import java.net.Socket;

public interface ClientHandler {
	void handleClient(int clientId,Socket socket);
}
