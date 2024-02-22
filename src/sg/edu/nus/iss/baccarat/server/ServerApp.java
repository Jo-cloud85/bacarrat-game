package sg.edu.nus.iss.baccarat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ServerApp {

    private static GamePlayer currentGamePlayer;
    private static int currentBetAmount;

    public static void main(String[] args) throws IOException {        

        if (args.length < 2)  {
            System.out.println("Missing argument(s)... Please key in both port number and number of card decks required");
            System.exit(1);
        } else if (Integer.parseInt(args[0]) <= 0 || Integer.parseInt(args[1]) <= 0){
            System.out.println("Invalid port number and/or number of card decks required");
            System.exit(1);
        } 

        int serverPort = Integer.parseInt(args[0]);
        int numOfDecks = Integer.parseInt(args[1]);

        Socket socket = null;
        InputStream is = null;
        OutputStream os = null;
        ServerSocket serverSocket = null;
        boolean finished = false;

        try {
            // create the server
            serverSocket = new ServerSocket(serverPort);
            System.out.println("Server started on " + serverPort);

            GameSession gameSession = new GameSession();
            gameSession.initializeGame(numOfDecks);

            DbHandler dbFolder = new DbHandler();

            while (!finished) {
                socket = serverSocket.accept();

                is = socket.getInputStream();
                DataInputStream dis = new DataInputStream(is);

                os = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);

                System.out.println("Waiting command from client...");

                try {
                    String requestFromClient = dis.readUTF();
                    String[] requestFromClientArr = requestFromClient.split("\\|");  
                    
                    if (requestFromClient.startsWith("end")) {
                        dos.writeUTF("Ending game...");
                        dos.flush();
                    }

                    if (requestFromClient.startsWith("login")) {
                        currentGamePlayer = new GamePlayer(requestFromClientArr[1], Integer.parseInt(requestFromClientArr[2]));
                        dbFolder.saveSumToDB(currentGamePlayer);
                        dos.writeUTF("Login success_" + requestFromClientArr[1]);
                        dos.flush();
                        System.out.println("Logging in Game Player: " + requestFromClientArr[1]);
                    } 
                    
                    if (requestFromClient.startsWith("bet")) {
                        int walletSum = dbFolder.loadSumFrDB(currentGamePlayer);
                        int betAmount = Integer.parseInt(requestFromClientArr[1]);
                        if (walletSum > betAmount) {
                            dbFolder.withdrawSumFrDB(currentGamePlayer, betAmount);
                            currentBetAmount = betAmount;
                            dos.writeUTF("Placed bet_" + betAmount);
                            dos.flush();
                            System.out.println("Receiving bet of " + betAmount + " from Game Player: " + currentGamePlayer.getGamePlayerName());
                        } else {
                            System.out.println("You have insufficient funds.");
                            break;
                        } 
                    } 
                    
                    if (requestFromClient.startsWith("deal")) {
                        if (currentBetAmount <= 0) {
                            dos.writeUTF("Please place a bet first.");
                            System.out.println("Game Player " + currentGamePlayer.getGamePlayerName() + " has insufficient funds to start the game.");
                        } else {
                            String betSide = requestFromClientArr[1].toUpperCase();
                            
                            System.out.println("Game Player betting on " + betSide);

                            List<Double> listOfDrawnCardsToPlayer = new LinkedList<>();
                            List<Double> listOfDrawnCardsToBanker = new LinkedList<>();
                            CardDeck cardDeck = new CardDeck();
                            listOfDrawnCardsToPlayer.addAll(cardDeck.drawCard(2));
                            listOfDrawnCardsToBanker.addAll(cardDeck.drawCard(2));
                            dbFolder.saveCardDeckListToDB(cardDeck);

                            BaccaratEngine bacarratEngine = new BaccaratEngine(listOfDrawnCardsToPlayer, listOfDrawnCardsToBanker, betSide, currentBetAmount);
                            int winAmount = 0;

                            String winner = bacarratEngine.determineWinner();
                            String[] winnerArr = winner.split("_");

                            if (betSide.equals(winnerArr[0])) { 
                                winAmount = bacarratEngine.determineBetAmountResult();
                                dbFolder.depositSumToDB(currentGamePlayer, winAmount);
                                currentBetAmount += winAmount;
                            } else if (winnerArr[1].equals("Game ending soon")) {
                                finished = true;
                            } else {
                                currentBetAmount = 0;
                            }

                            String playedCards = bacarratEngine.printAllPlayedCards(listOfDrawnCardsToPlayer, listOfDrawnCardsToBanker);

                            String combinedString = "Betting on_" + betSide + "_" + winnerArr[0] + "_" + winnerArr[1]; 
                            
                            dos.writeUTF(combinedString);
                            dos.flush();
                            System.out.println(playedCards);
                        }
                    } else {
                        dos.writeUTF("Invalid request");
                }
                    dos.flush();
                } catch (EOFException e) {
                    //System.out.println("1. Closing socket...");
                    socket.close();
                    break;
                }
            }
        } catch (NumberFormatException | IOException e) {
            //System.out.println("Exception caught Number|IO");
            e.printStackTrace();
        }finally{
            try {
                //System.out.println("2. Closing socket...");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
