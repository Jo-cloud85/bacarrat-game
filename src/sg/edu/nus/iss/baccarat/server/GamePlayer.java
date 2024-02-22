package sg.edu.nus.iss.baccarat.server;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

// 1. This is where we set/get the gamePlayer's name so that we can let DbHandler create/retrieve the data file
// 2. This is where we also know the gamePlayer's 'money' status, including what he started with 
// when he logs in, after betting get how much or lose how much, whether he has sufficient amount
// before betting i.e. can he continue to bet
// 3. This is also where there is an outputstream for this 'money' status to be updated/saved into
// the data file via the DbHandler

public class GamePlayer {

    private String gamePlayerName;
    private int walletSum;


    public GamePlayer() {

    }

    public GamePlayer(String gamePlayerName) {
        this.gamePlayerName = gamePlayerName;
    }
    
    public GamePlayer(String gamePlayerName, int walletSum) {
        this.gamePlayerName = gamePlayerName;
        this.walletSum = walletSum;
    }

    public String getGamePlayerName() {
        return gamePlayerName;
    }

    public void setGamePlayerName(String gamePlayerName) {
        this.gamePlayerName = gamePlayerName;
    }

    public Integer getWalletSum() {
        return walletSum;
    }

    public void setWalletSum(int walletSum) {
        this.walletSum = walletSum;
    }

    public void saveBets(OutputStream os) {
        OutputStreamWriter writer = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(writer);
        try{
            // now writing each item of the list from console to the external file
            try {
                bw.write(walletSum + ""); //write cannot accept double so we convert to String
                bw.newLine();
            } catch(Exception e){
                e.printStackTrace();
            }
        }finally{
            try{
                writer.flush();
                bw.flush();
                writer.close();
                bw.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
