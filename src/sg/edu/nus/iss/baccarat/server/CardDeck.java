package sg.edu.nus.iss.baccarat.server;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

// 1. Creates and shuffles card deck list
// 2. Saves card deck list to an outputstream so that dbhandler can manage
// 3. Draws card(s) and remove drawn cards from the card deck list

public class CardDeck {

    private static int numOfCardsinSingleDeck = 52;
    private static int typesOfCardsinSingleDeck = 13;
    private int numOfDecks;
    private int totalNumOfCards;
    private static Integer[] suits = new Integer[4];
    private static List<Double> allCardsList;

    public CardDeck() {}

    public CardDeck(int numOfDecks) {
        this.numOfDecks = numOfDecks;
    }

    
    
    public List<Double> getAllCardsList() {
        return allCardsList;
    }

    public List<Double> getShuffledCardDeck() {
        totalNumOfCards = numOfCardsinSingleDeck * numOfDecks;
        allCardsList = new ArrayList<>(totalNumOfCards);
        
        for (int n = 0; n < numOfDecks; n++) {
            for (int i = 0; i < typesOfCardsinSingleDeck; i++) {
                for (int j = 0; j < suits.length; j++) {
                    allCardsList.add(Double.parseDouble((i+1) + "." + (j+1)));
                }
            }
        }

        // Shuffle before adding blackcard
        Collections.shuffle(allCardsList);

        // Put the blackcard randomly in the 10th to 30th percentile of the combinedDeck
        // So 30-10 = 20 so it is 20% of the card size, chose a random num and we start from the 10th percentile
        Random random = new Random();
        int bound = Math.round(totalNumOfCards / 5);
        int start = (totalNumOfCards / 10);
        int position = random.nextInt(bound) + start;
        // Letting blackCard be this value since it should be represented as a double and no card has this value
        Double blackCard = 0.0;
        allCardsList.add(position, blackCard);

        return allCardsList;
    }

    // Since you are saving/writing shuffled card deck list to an external resource, you need OutputStream
    public void saveCards(OutputStream os) {
        OutputStreamWriter writer = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(writer);
        
        try{
            // Writing each item of the list from console to the external file
            for(Double card : allCardsList){
                try {
                    bw.write(card + ""); // write() cannot accept double so we convert to String
                    bw.newLine();
                } catch(Exception e){
                    e.printStackTrace();
                }
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

    // Draw card(s) and delete them from cards.db
    public List<Double> drawCard(int numOfCardsToDraw) { // Always either 2 or 1
        List<Double> drawnCards = new LinkedList<>();
        int numOfAllCards = allCardsList.size();

        for (int i=0; i<numOfCardsToDraw; i++) {
            int cardIndex = numOfAllCards - 2; // Since you draw from the top
            if (allCardsList.get(cardIndex) == 0.0) {
                System.out.println("The black card is drawn. Game session ends after this round.");
                drawnCards.add(allCardsList.get(cardIndex));
                allCardsList.remove(cardIndex);
                cardIndex--;
            } else {
                drawnCards.add(allCardsList.get(cardIndex));
                allCardsList.remove(cardIndex);
            }
        }
        return drawnCards;
    }
}