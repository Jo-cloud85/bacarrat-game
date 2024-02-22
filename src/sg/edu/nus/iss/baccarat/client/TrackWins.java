package sg.edu.nus.iss.baccarat.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class TrackWins {
    
    private static String csvFilename = "game_record/game_history.csv";
    private List<String> listOfWinningGames;
    
    public TrackWins (List<String> listOfWinningGames) {
        this.listOfWinningGames = listOfWinningGames;
    }
    
    public void saveWinningGames(OutputStream os) {
        int totalNumOfWinningGames = listOfWinningGames.size();

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
        
        try{
            if ((totalNumOfWinningGames % 6) != 0) {
                bw.write(listOfWinningGames.get(totalNumOfWinningGames - 1).toString());
                bw.write(",");
            } else {
                bw.write(listOfWinningGames.get(totalNumOfWinningGames - 1).toString());
                bw.newLine();
                //bw.close();
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
        
    public void saveWinningGamesToCSV() throws IOException {
        File gameHistoryCSV = new File(csvFilename);
        OutputStream os = null; 
        
        try {
            if (!gameHistoryCSV.exists()) {
                gameHistoryCSV.createNewFile();
            } 
            os = new FileOutputStream(csvFilename, true); 

            saveWinningGames(os);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            os.flush();
            os.close();
        }
    }
}
    