import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 4444;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of players (2-8):");
        int numPlayers = scanner.nextInt();
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 1; i <= numPlayers; i++) {
            System.out.println("Enter name for Player " + i + ":");
            String name = scanner.next();
            players.add(new Player(name, 1000)); // Each player starts with 1000 chips
        }
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running and waiting for clients...");
            Game game = new Game(players,1000,5);
            while (true) {
                // Accept a client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                // Handle the client in a separate thread
                new Thread(() -> handleClient(clientSocket,game)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket, Game game) {
        
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        ) {
            // Receive the client's name
            String clientName = in.readLine();
            System.out.println("Received name from client: " + clientName);

            // Generate two random cards for the client
            ArrayList players = game.getActivePlayers();
            
            Player receiver = players.get(0);
            for (Player player: players){
                if (player.getName() == clientName){
                    System.err.println("");
                }
            }
            List<> cards = ;
            

            // Send the cards back to the client
            out.println("Your cards: " + cards);

            System.out.println("Sent cards to client: " + cards);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    