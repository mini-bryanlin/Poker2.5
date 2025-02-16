import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class GameServer {
    private static final int PORT = 4444;
    private ServerSocket serverSocket;
    private List<ClientHandler> clients;
    private Game game;
    private ExecutorService pool;
    private int expectedPlayers;  // New field for expected number of players

    public GameServer() {
        clients = new ArrayList<>();
        pool = Executors.newFixedThreadPool(8);
        
        // Ask for number of expected players
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of players to wait for (2-8):");
        expectedPlayers = scanner.nextInt();
        while (expectedPlayers < 2 || expectedPlayers > 8) {
            System.out.println("Invalid number. Please enter a number between 2 and 8:");
            expectedPlayers = scanner.nextInt();
        }
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);
            System.out.println("Waiting for " + expectedPlayers + " players to connect...");

            // Wait for all players to connect
            while (clients.size() < expectedPlayers) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                pool.execute(clientHandler);
                System.out.println("Player connected: " + clients.size() + "/" + expectedPlayers);
            }

            // Wait for all players to be ready
            if (waitForPlayersReady()) {
                // Initialize and start the game
                ArrayList<Player> players = new ArrayList<>();
                for (ClientHandler client : clients) {
                    players.add(new Player(client.getPlayerName(), 1000));
                }
                game = new Game(players, 1000, 5);
                
                // Start game loop
                runGameLoop();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    private boolean waitForPlayersReady() {
        System.out.println("Waiting for all players to confirm they're ready...");
        
        // Send ready check to all clients
        for (ClientHandler client : clients) {
            client.sendMessage("READY_CHECK:Are you ready to start? (yes/no)");
        }

        // Wait for all responses
        Map<String, Boolean> playerReadyStatus = new HashMap<>();
        for (ClientHandler client : clients) {
            String response = client.receiveMessage();
            playerReadyStatus.put(client.getPlayerName(), response.equalsIgnoreCase("yes"));
            
            // Broadcast each player's ready status
            broadcastReadyStatus(playerReadyStatus);
        }

        // Check if all players are ready
        boolean allReady = playerReadyStatus.values().stream().allMatch(ready -> ready);
        
        if (allReady) {
            broadcastMessage("ALL_READY:All players are ready! Game starting...");
            return true;
        } else {
            broadcastMessage("CANCELED:Some players were not ready. Please reconnect to try again.");
            return false;
        }
    }

    private void broadcastReadyStatus(Map<String, Boolean> readyStatus) {
        StringBuilder status = new StringBuilder("READY_STATUS:");
        for (Map.Entry<String, Boolean> entry : readyStatus.entrySet()) {
            status.append(entry.getKey())
                  .append(": ")
                  .append(entry.getValue() ? "Ready" : "Not Ready")
                  .append(", ");
        }
        broadcastMessage(status.toString());
    }

    private void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    private void runGameLoop() {
        try {
            int time = 1;
            while (true) {
                broadcastMessage("GAME_START:Round " + time + " starting!");
                
                int blind = time % clients.size();
                game.startGame(blind);
                
                // Send cards to each player
                ArrayList<Player> players = game.getPlayers();
                for (int i = 0; i < clients.size(); i++) {
                    Player player = (Player) players.get(i);
                    ClientHandler client = clients.get(i);
                    client.sendMessage("CARDS:" + player.getHand().toString());
                }
                
                time++;
                game.resetGame();
                
                // Ask to continue
                boolean continuePlaying = askToContinue();
                if (!continuePlaying) {
                    break;
                }

                // Ready check for next round
                if (!waitForPlayersReady()) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean askToContinue() {
        // Broadcast continue question to all clients
        for (ClientHandler client : clients) {
            client.sendMessage("CONTINUE:Do you want to continue? (y/N)");
        }

        // Wait for all responses
        boolean continueGame = true;
        for (ClientHandler client : clients) {
            String response = client.receiveMessage();
            if (response.equalsIgnoreCase("n")) {
                continueGame = false;
                break;
            }
        }

        // Broadcast the decision
        broadcastMessage(continueGame ? "CONTINUING:Game will continue!" : "ENDING:Game will end.");
        return continueGame;
    }

    public void stop() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            pool.shutdown();
            for (ClientHandler client : clients) {
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String playerName;

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        public String getPlayerName() {
            return playerName;
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public String receiveMessage() {
            try {
                return in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        public void close() throws IOException {
            socket.close();
            out.close();
            in.close();
        }

        @Override
        public void run() {
            try {
                // Get player name
                playerName = in.readLine();
                System.out.println("Player connected: " + playerName);
                
                // Send welcome message
                sendMessage("WELCOME:Welcome " + playerName + "! Waiting for other players...");

                // Main client handler loop
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    // Handle client messages here
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        GameServer server = new GameServer();
        server.start();
    }
}