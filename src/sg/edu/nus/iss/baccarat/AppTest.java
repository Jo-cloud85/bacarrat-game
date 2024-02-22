package sg.edu.nus.iss.baccarat;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import sg.edu.nus.iss.baccarat.server.BaccaratEngine;
import sg.edu.nus.iss.baccarat.server.CardDeck;

/**
 * Unit test for simple App.
 */

public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void testExtractCardValue() {
        BaccaratEngine baccaratEngine = new BaccaratEngine();
        Double input = 11.1;
        int expectedOutput = 10;
        assertEquals(expectedOutput, baccaratEngine.extractCardValue(input));        
    }

    @Test
    public void testGetShuffledCardDeck() {
        // Create an instance of CardDeck
        CardDeck cardDeck = new CardDeck(2);
        
        // Call the method to get the shuffled card deck
        List<Double> shuffledDeck = cardDeck.getShuffledCardDeck();
        
        // Verify that the shuffled deck is not null
        assertNotNull(shuffledDeck);
        
        // For example, you can check if the black card is present in the shuffled deck
        assertTrue(shuffledDeck.contains(0.0));
    }
}

