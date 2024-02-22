package sg.edu.nus.iss.baccarat.client;

import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientApp {

    public static void main(String[] args) {
        
        String[] connectionInfo = args[0].split(":");

        List<String> listOfWinners = new ArrayList<>();

        try {
            while (true) {
                Socket socket = new Socket(connectionInfo[0], Integer.parseInt(connectionInfo[1]));

                InputStream is = socket.getInputStream();
                DataInputStream dis  = new DataInputStream(is);

                OutputStream os = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);

                Console console = System.console();

                String input = console.readLine("> ");
                input = input.replace(" ", "|");

                // Writes the input from console to the output stream to be sent back to the server
                dos.writeUTF(input);
                dos.flush();

                String responseFromServer = dis.readUTF();

                TrackWins trackWins = new TrackWins(listOfWinners);

                String winner = "";

                if (responseFromServer.startsWith("Login success_")) {
                    String[] responseFromServerArr = responseFromServer.split("_");
                    System.out.println("Login success! Welcome " + responseFromServerArr[1] + "!");
                } 
                
                if (responseFromServer.startsWith("Placed bet_")) {
                    String[] responseFromServerArr = responseFromServer.split("_");
                    System.out.println("You have placed a bet of " + responseFromServerArr[1]);
                } 

                if (responseFromServer.startsWith("Betting on_")) {
                    String[] responseFromServerArr = responseFromServer.split("_");

                    System.out.println("You have bet on " + responseFromServerArr[1]);
                    System.out.println(responseFromServerArr[3]);

                    winner = responseFromServerArr[2];
                    listOfWinners.add(winner);
                    
                    //System.out.println(listOfWinners);
                    trackWins.saveWinningGamesToCSV();
                }

                if (responseFromServer.startsWith("Ending")) {
                    System.out.println("Goodbye!");
                    break;
                }

                //System.out.println("Closing socket...");
                is.close();
                os.close();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
}
