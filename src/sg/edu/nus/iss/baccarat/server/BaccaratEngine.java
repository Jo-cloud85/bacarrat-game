package sg.edu.nus.iss.baccarat.server;

import java.util.List;
import java.util.stream.Collectors;

// 1. Calculates the last digit of drawn cards for Banker and Player
// 2. Determines the winner
// 3. Determines the winnings (or losses) for the gamePlayer based on his initalbetAmount
// (this is assuming that the gamePlayer has enough money, we check this in the GamePlayer class)
// 4. Game stops or get ready to stop when dealer picks the black card OR gamePlayer has no more money
// 5. Prints all the cards that have been played e.g. "P|1|10|3,B|10|10|7"

public class BaccaratEngine {

    private List<Double> bankerCards;
    private List<Double> playerCards;
    private int cardValue;
    private int cardRank;
    private String gamePlayerBetSide;
    private int gamePlayerBetAmount;

    public BaccaratEngine() {

    }

    public BaccaratEngine(List<Double> playerCards, List<Double> bankerCards) {
        this.playerCards = playerCards;
        this.bankerCards = bankerCards;
    }


    public BaccaratEngine(List<Double> playerCards, List<Double> bankerCards, String gamePlayerBetSide,
            int gamePlayerBetAmount) {
        this.playerCards = playerCards;
        this.bankerCards = bankerCards;
        this.gamePlayerBetSide = gamePlayerBetSide;
        this.gamePlayerBetAmount = gamePlayerBetAmount;
    }


    public int extractCardValue(Double card) {
        cardRank = (int) Math.round(card);
        if (cardRank == 11 || cardRank == 12 || cardRank == 13) {
            cardValue = 10;
        } else {
            cardValue = cardRank;
        }
        return cardValue;
    }


    public int lastDigitFromSumOfCards(List<Double> listOfDrawnCards) {
        int sum = 0;
        int lastDigit = 0;

        for (double card : listOfDrawnCards) {
            cardValue = extractCardValue(card);
            sum += cardValue;
        }
        
        lastDigit = sum % 10;
        return lastDigit;
    }


    public String determineWinner() {
        String winner = "";
        String message = "";
        int playerDigit = lastDigitFromSumOfCards(playerCards);
        int bankerDigit = lastDigitFromSumOfCards(bankerCards);
    
        CardDeck cardDeck = new CardDeck();
    
        // Loop until both player's and banker's digits are greater than 5
        while (playerDigit <= 5 || bankerDigit <= 5) {
            if (bankerDigit <= 5) {
                Double card = cardDeck.drawCard(1).get(0);
                if (card == 0.0) {
                    winner = "";
                    message = "Game ending soon";
                }
                bankerCards.add(card);
                bankerDigit = lastDigitFromSumOfCards(bankerCards);
            }
            if (playerDigit <= 5) {
                Double card = cardDeck.drawCard(1).get(0);
                if (card == 0.0) {
                    winner = "";
                    message = "Game ending soon";
                }
                playerCards.add(card);
                playerDigit = lastDigitFromSumOfCards(playerCards);
            }
        }
    
        // Now both player's and banker's digits are greater than 5
        if (bankerDigit > 5 && playerDigit > 5) {
            if (bankerDigit > playerDigit) {
                message = "Banker wins with " + bankerDigit + " points.";
                winner = "B";
            } else if (playerDigit > bankerDigit) {
                message = "Player wins with " + playerDigit + " points.";
                winner = "P";
            } else {
                message = "It is a draw.";
                winner = "D";
            }
        }

        return (winner + "_" + message);
    }
    

    public int determineBetAmountResult() {
        int betAmountResult = gamePlayerBetAmount;
        int bankerDigit = lastDigitFromSumOfCards(bankerCards);
        String[] winner = determineWinner().split("_");

        // means gameplayer bets on banker and banker wins
        if (gamePlayerBetSide.equalsIgnoreCase(winner[0]) && winner[0].equals("B")) {
            // if bankerDigit is 6, apply 6-card rule
            if (bankerDigit == 6) {
                betAmountResult = (int) (gamePlayerBetAmount * 1.5);
            } else {
                betAmountResult = gamePlayerBetAmount * 2;
            }
        // means gameplayer bets on player and player wins
        } else if (gamePlayerBetSide.equalsIgnoreCase(winner[0]) && winner[0].equals("P")) {
            betAmountResult = gamePlayerBetAmount * 2;
        // means gameplayer bets on either and got a draw
        } else {
            betAmountResult = gamePlayerBetAmount; 
        }

        return betAmountResult;
    }


    public String printAllPlayedCards(List<Double> listOfPlayerCards, List<Double> listOfBankerCards) {
        String joinedPlayerValues = listOfPlayerCards.stream()
            .map(pc -> String.valueOf(extractCardValue(pc)))
            .collect(Collectors.joining("|"));
        
        String joinedBankerValues = listOfBankerCards.stream()
            .map(bc -> String.valueOf(extractCardValue(bc)))
            .collect(Collectors.joining("|"));
        
        String combined = "P|" + joinedPlayerValues + ",B|" + joinedBankerValues;

        return combined;
    }
}
