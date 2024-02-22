package sg.edu.nus.iss.baccarat.server;

import java.io.IOException;

public class GameSession {

    private DbHandler dbFolder;
    private CardDeck cardDeck;

    public GameSession() {
    }

    public void initializeGame(int numOfDecks) throws IOException {
        cardDeck = new CardDeck(numOfDecks);
        cardDeck.getShuffledCardDeck();     

        dbFolder = new DbHandler();
        dbFolder.saveCardDeckListToDB(cardDeck);
    }
}
