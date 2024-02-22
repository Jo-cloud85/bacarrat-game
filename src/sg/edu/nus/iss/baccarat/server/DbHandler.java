package sg.edu.nus.iss.baccarat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DbHandler {

    private File dbFolder; // directory folder that holds cards.db and all the gameplayer.db
    private static final String cardsDBFilename = "cards.db"; 
    private static final String dbFoldername = "database"; 

    // constructor - once called, it will create a new folder
    public DbHandler() {
        this.dbFolder = new File(dbFoldername);
    }

    public File getDBFolder() {
        return dbFolder;
    }

    public void setDBFolder(File dbFolder) {
        this.dbFolder = dbFolder;
    }

    // locking in data from the cards deck list which comes via the card deck outputstream into the db folder -> file
    public void saveCardDeckListToDB(CardDeck cardDeck) throws IOException {
        // Assuming dir exists first, we are setting up the path and file 'space' for cards.db, we are NOT create the cards.db YET
        String cardsDBFilepath = dbFolder.getPath() + File.separator + cardsDBFilename; // "database/cards.db"
        File cardsDB = new File(cardsDBFilepath); // Potentially creating file which is "database/cards.db"

        // Preparing to get 'connected' with the outputstream from the CardDeck
        OutputStream os = null; 

        // Checking if the dir really exist, if no, create the dir, if yes, continue
        Path dirPath = Paths.get(dbFolder.getPath());
        if (!Files.exists(dirPath)) {
            Files.createDirectory(dirPath);
        } 

        try {
            if (!cardsDB.exists()) {
                cardsDB.createNewFile();
            } 
            os = new FileOutputStream(cardsDBFilepath); 
            cardDeck.saveCards(os);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            os.flush();
            os.close();
        }
    }

    // Locking in data from the gameplayer info, incl bets, which comes via the gameplayer outputstream into the db folder
    public void saveSumToDB(GamePlayer gamePlayer) throws IOException {
        // Already assuming directory exist coz saving card deck data should always happen first
        String gamePlayerDBFilepath = dbFoldername + File.separator + gamePlayer.getGamePlayerName() + ".db"; // "database/kenneth.db"
        File gamePlayerDB = new File(gamePlayerDBFilepath);

        // Preparing to get 'connected' with the outputstream from the gameplayer's bets data
        OutputStream os = null; 

        try {
            if (!gamePlayerDB.exists()) {
                gamePlayerDB.createNewFile();
            } else {
                // If exists, empty the contents inside the existing cards.db
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(gamePlayerDBFilepath));
                writer.write("");
                writer.flush();
                writer.close();
            }
            // Setting the outputstream variable to File type of output stream and w/ using the kenneth.db 
            os = new FileOutputStream(gamePlayerDBFilepath); 
            // Save that outputstream w/ the bets data into the gamePlayer db file
            gamePlayer.saveBets(os);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            os.flush();
            os.close();
        }
    }

    public int loadSumFrDB(GamePlayer gamePlayer) {
        String gamePlayerDBFilepath = dbFoldername + File.separator + gamePlayer.getGamePlayerName() + ".db"; 
        File gamePlayerDB = new File(gamePlayerDBFilepath);

        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(gamePlayerDB));
            String currentAmountStr = reader.readLine();
            int currentAmount = Integer.parseInt(currentAmountStr.trim());
            reader.close();
            return currentAmount;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // if reader is not wrapped with a BufferedReader, and you used reader.read(), you are reading
    // a single character from the file, not the entire contents. So when you're reading from the file, you're getting 
    // the ASCII value of the first character, which might not be what you expect

    public void withdrawSumFrDB(GamePlayer gamePlayer, int betAmount) {
        String gamePlayerDBFilepath = dbFoldername + File.separator + gamePlayer.getGamePlayerName() + ".db"; 
        File gamePlayerDB = new File(gamePlayerDBFilepath);

        BufferedReader reader;
        FileWriter writer;

        try {
            reader = new BufferedReader(new FileReader(gamePlayerDB));
            String currentAmountStr = reader.readLine();
            int currentAmount = Integer.parseInt(currentAmountStr.trim());

            if (betAmount > currentAmount) {
                System.out.println("Insufficient amount");
            } else {
                currentAmount -= betAmount;
            }

            writer = new FileWriter(gamePlayerDB);
            writer.write(Integer.toString(currentAmount));
            writer.close();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void depositSumToDB(GamePlayer gamePlayer, int winAmount) {
        String gamePlayerDBFilepath = dbFoldername + File.separator + gamePlayer.getGamePlayerName() + ".db"; 
        File gamePlayerDB = new File(gamePlayerDBFilepath);

        BufferedReader reader;
        FileWriter writer;

        try {
            reader = new BufferedReader(new FileReader(gamePlayerDB));
            String currentAmountStr = reader.readLine();
            int currentAmount = Integer.parseInt(currentAmountStr.trim());

            currentAmount += winAmount;

            writer = new FileWriter(gamePlayerDB);
            writer.write(Integer.toString(currentAmount));
            writer.close();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
