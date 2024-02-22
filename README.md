# Bacarrat Game

This is a solution to the [Bacarrat]. 

## How to run code
### 1. Compile class
javac --source-path src -d classes 

### 2. Launch the code
#### Server side
java -cp classes sg.edu.nus.iss.baccarat.server.ServerApp 12345 4

#### Client side 
java -cp classes sg.edu.nus.iss.baccarat.client.ClientApp localhost:12345

#### for test    	
javac -cp lib/junit-4.13.2.jar:src -d classes src/sg/edu/nus/iss/baccarat/AppTest.java


## Rules of the Game
Once both the server and client and connected to one another, GamePlayer will be prompted to input in the command line

Valid commands:
1. login <GamePlayerName> <walletSum> e.g. login john 500
   - Where walletSum is the total sum the GamePlayer wants to put in per Game Session

2. bet <betAmount> e.g. bet 50
   - Where betAmount is the amount the GamePlayer wants to bet in each Game

3. deal <betSide> e.g. deal p
   - Where betSide is either 'P' - the Player or 'B' - the Banker

4. end
   - If the the GamePlayer wants to end the game

The GamePlayer must always start of with a walletSum bigger than the betAmount. 

Every time the GamePlayer bets, the amount will be deducted from the walletSum.

The Dealer will draw the cards and determine the winner and winning bet return if GamePlayer wins.

If the GamePlayer wins/loses the game, the walletSum will be updated accordingly and the Game Session continues until:
1. GamePlayer decides to end the Game
2. Dealer picks a black card