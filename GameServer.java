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
    private int expectedPlayers;

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
                broadcastMessage("PLAYER_COUNT:" + clients.size() + "/" + expectedPlayers);
            }

            System.out.println("All players connected. Starting game setup...");
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
            broadcastMessage("ERROR:Server encountered an error. Game ending.");
        } finally {
            stop();
        }
    }

    private boolean waitForPlayersReady() {
        System.out.println("Waiting for all players to confirm they're ready...");
        
        // Send ready check to all clients
        broadcastMessage("READY_CHECK:Are you ready to start? (yes/no)");

        // Set a timeout for responses
        long timeout = System.currentTimeMillis() + 30000; // 30 second timeout
        Map<String, Boolean> playerReadyStatus = new HashMap<>();
        
        // Wait for responses with timeout
        while (playerReadyStatus.size() < clients.size() && System.currentTimeMillis() < timeout) {
            for (ClientHandler client : clients) {
                if (!playerReadyStatus.containsKey(client.getPlayerName())) {
                    String response = client.receiveMessage();
                    if (response != null) {
                        playerReadyStatus.put(client.getPlayerName(), response.equalsIgnoreCase("yes"));
                        broadcastReadyStatus(playerReadyStatus);
                    }
                }
            }
            
            try {
                Thread.sleep(100); // Small delay to prevent CPU spinning
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        // Check if all players responded and are ready
        boolean allReady = playerReadyStatus.size() == clients.size() && 
                          playerReadyStatus.values().stream().allMatch(ready -> ready);
        
        if (allReady) {
            broadcastMessage("ALL_READY:All players are ready! Game starting...");
            return true;
        } else {
            broadcastMessage("CANCELED:Not all players ready or timeout occurred. Please reconnect to try again.");
            return false;
        }
    }

    private void runGameLoop() {
        try {
            int time = 1;
            int maxRounds = 100; // Maximum number of rounds as safety
            
            while (time <= maxRounds) {
                broadcastMessage("GAME_START:Round " + time + " starting!");
                System.out.println("Starting round " + time);
                
                int blind = time % clients.size();
                game.startGame(blind);
                
                // Send cards to each player
                ArrayList<Player> players = game.getPlayers();
                for (int i = 0; i < clients.size(); i++) {
                    Player player = (Player) players.get(i);
                    ClientHandler client = clients.get(i);
                    client.sendMessage("CARDS:" + player.getHand().toString());
                    System.out.println("Sent cards to " + player.getName() + ": " + player.getHand().toString());
                }
                
                time++;
                game.resetGame();
                
                // Ask to continue with timeout
                if (!askToContinue()) {
                    break;
                }

                // Ready check for next round with timeout
                if (!waitForPlayersReady()) {
                    break;
                }
            }
            
            if (time > maxRounds) {
                broadcastMessage("GAME_END:Maximum number of rounds reached.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            broadcastMessage("ERROR:An error occurred. Game ending.");
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
        
        // Remove the trailing comma and space if exists
        if (status.length() > 12) {
            status.setLength(status.length() - 2);
        }
        
        broadcastMessage(status.toString());
    }

    private void broadcastMessage(String message) {
        System.out.println("Broadcasting: " + message); // Server-side logging
        for (ClientHandler client : clients) {
            try {
                client.sendMessage(message);
            } catch (Exception e) {
                System.err.println("Failed to send message to " + client.getPlayerName() + ": " + e.getMessage());
            }
        }
    }

    private boolean askToContinue() {
        broadcastMessage("CONTINUE:Do you want to continue? (y/N)");

        // Set a timeout for responses
        long timeout = System.currentTimeMillis() + 30000; // 30 second timeout
        Map<String, Boolean> responses = new HashMap<>();
        
        while (responses.size() < clients.size() && System.currentTimeMillis() < timeout) {
            for (ClientHandler client : clients) {
                if (!responses.containsKey(client.getPlayerName())) {
                    String response = client.receiveMessage();
                    if (response != null) {
                        responses.put(client.getPlayerName(), !response.equalsIgnoreCase("n"));
                    }
                }
            }
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        boolean continueGame = responses.size() == clients.size() && 
                             !responses.containsValue(false);

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
                if (in.ready()) {
                    return in.readLine();
                }
                return null;
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
                    System.out.println("Received from " + playerName + ": " + inputLine);
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