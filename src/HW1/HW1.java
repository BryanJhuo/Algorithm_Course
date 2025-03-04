package HW1;
import java.util.LinkedList;
import java.util.HashMap;
import java.lang.Math;

class Deck {
    // field
    LinkedList<Integer> cards;

    // Constructor
    // for empty deck
    Deck() {
        this.cards = new LinkedList<>();
    }
    // for fields
    Deck(LinkedList<Integer> cards) {
        this.cards = cards;
    }
    // for completed sorted deck
    Deck(int nbVals) {
        this.cards = new LinkedList<>();
        for (int i=1; i<=nbVals; i++)
            for (int j=0; j<4; j++) 
                this.cards.add(i);
    }

    // Methods
    @Override
    public String toString(){
        return this.cards.toString();
    }

    @Override
	public int hashCode() {
		return 0;
	}
	
	@Override
	public boolean equals(Object o) {
		Deck d = (Deck) o;
		return cards.equals(d.cards);
	}

    Deck copy() {
        Deck copyDeck = new Deck();
        for (Integer card : this.cards)
            copyDeck.cards.addLast(card);
        return copyDeck;
    }

    int pick(Deck d){
        if (d.cards.isEmpty())
            return -1;
        int pickNum = d.cards.removeFirst();
        this.cards.addLast(pickNum);
        return pickNum;
    }

    void pickAll(Deck d){
        while (!d.cards.isEmpty()){
            int pick = d.cards.removeFirst();
            this.cards.addLast(pick);
        }
    }

    boolean isValid(int nbVals){
        HashMap<Integer, Integer> countMap = new HashMap<>();

        for (int card: this.cards) {
            // check if the values is not out of the range
            if (card < 1 || card > nbVals)
                return false;
            // count the card times
            countMap.put(card, countMap.getOrDefault(card, 0) + 1);
            // check if the card of times is correct
            if (countMap.get(card) > 4)
                return false;
        }
        return true;
    }

    int cut() {
        int count = 0;
        if (this.cards.size() == 0)
            return count;
        for (int i=0; i < this.cards.size(); i++){
            double n = Math.random();
            // count plus one if the coin is head 
            if (n < 0.5) count += 1;
        }
        return count;
    }

    Deck split() {
        int c = this.cut();
        Deck newDeck = new Deck();
        for (int i = 0; i < c; i++){
            if (!this.cards.isEmpty())
                newDeck.cards.addLast(this.cards.removeFirst());
        }
        return newDeck;
    }

    void riffleWith(Deck d) {
        // the probability: a / (a + b) a -> current deck b -> the other deck
        Deck f = new Deck();
        int a = this.cards.size(), b = d.cards.size();
        while (a > 0 || b > 0) {
            double p = (double)a / (a + b);
            if (Math.random() < p && a > 0){
                f.cards.add(this.pick(this));
                a--;
            }
            else {
                f.cards.add(d.pick(d));
                b--;
            }
        }
        this.cards = f.cards;
    }

    void riffleShuffle(int m) {
        for (int i=0; i < m; i++) {
            riffleWith(this.split());
        }
    }
}

class Battle {
    // fields 
    Deck player1;
    Deck player2;
    Deck trick;
    boolean turn;

    // Constructors
    // for the empty
    Battle() {
        this.player1 = new Deck();
        this.player2 = new Deck();
        this.trick = new Deck();
        this.turn = true;
    }
    // for the param.
    Battle(Deck player1, Deck player2, Deck trick) {
        this.player1 = player1;
        this.player2 = player2;
        this.trick = trick;
        this.turn = true;
    }
    // for initiate the Battle game
    Battle(int nbVals) { 
        // initial the fields
        this.player1 = new Deck(); 
        this.player2 = new Deck(); 
        this.trick = new Deck();
        // create a new deck
        Deck battleDeck = new Deck(nbVals);
        // do 7 times riffle
        battleDeck.riffleShuffle(7);
        // dealing cards
        int index = 0;
        while (!battleDeck.cards.isEmpty()) {
            if (index % 2 == 0) // give player1 when index is odd
                this.player1.cards.addLast(battleDeck.cards.removeFirst());
            else                // give player2 when index is even
                this.player2.cards.addLast(battleDeck.cards.removeFirst());
            index++;
        }
        this.turn = true;
    }

    // Methods
    Battle copy() {
        Battle newBattle = new Battle();
        newBattle.player1 = this.player1.copy();
        newBattle.player2 = this.player2.copy();
        newBattle.trick = this.trick.copy();
        return newBattle;
    }

    @Override
    public String toString() {
        return "Player 1 : " + player1.toString() + "\n" + "Player 2 : " + player2.toString() + "\nPli " + trick.toString();
    }

    boolean isOver() { // judge whether the game is over
        if (this.player1.cards.isEmpty() || this.player2.cards.isEmpty()) return true;
        return false;
    }

    boolean oneRound() {
        if (this.player1.cards.isEmpty() || this.player2.cards.isEmpty())
            return false;
        while (true) {
            if (this.player1.cards.isEmpty() || this.player2.cards.isEmpty())
                return false;
            // take one card to trick from every player
            // when turn equals true then player1 first, else player2 first 
            if (this.turn == true){
                this.trick.cards.add(this.player1.cards.removeFirst());
                this.trick.cards.add(this.player2.cards.removeFirst());    
            }
            else {
                this.trick.cards.add(this.player2.cards.removeFirst());
                this.trick.cards.add(this.player1.cards.removeFirst());    
            }
            int player1Num = this.trick.cards.get(this.trick.cards.size() - 2), player2Num = this.trick.cards.get(this.trick.cards.size() - 1);
            if (player1Num > player2Num){
                for (Integer card: this.trick.cards)
                    this.player1.cards.addLast(card);
                this.trick.cards.clear();
                break;
            }
            else if (player1Num < player2Num) {
                for (Integer card: this.trick.cards)
                    this.player2.cards.addLast(card);
                this.trick.cards.clear();
                break;
            }
            else {
                // return false if someone player's cards is empty 
                if (this.player1.cards.isEmpty() || this.player2.cards.isEmpty())
                    return false;
                // take one again but don't compare
                if (this.turn == true){
                    this.trick.cards.add(this.player1.cards.removeFirst());
                    this.trick.cards.add(this.player2.cards.removeFirst());    
                }
                else {
                    this.trick.cards.add(this.player2.cards.removeFirst());
                    this.trick.cards.add(this.player1.cards.removeFirst());    
                }
            }
            // you have to commen out when test4.2
            //this.turn = !this.turn;
        }
        return true;
    }

    int winner() {
        int player1Size = this.player1.cards.size(), player2Size = this.player2.cards.size();
        // current player1 win
        if (player1Size > player2Size)
            return 1;
        // current player2 win
        else if (player1Size < player2Size)
            return 2;
        else
            return 0;
    }

    int game(int turns) {
        boolean gameOver = false;
        while (turns > 0 && !gameOver) {
            if (this.oneRound()) turns--;
            else gameOver = true;
        }
        return this.winner();
    }

    // implement the infinite game
    int game() {
        // clone the deck for turtle game
        Battle b1_copy = this.copy();
        // Round game
        while (true) {
            this.oneRound();
            this.oneRound();
            b1_copy.oneRound();
            int hasWinner = this.winner();
            if (hasWinner == 1 || hasWinner == 2) 
                return hasWinner;
            else if (this.player1.cards.isEmpty() && this.player2.cards.isEmpty())
                return 0;
            if (b1_copy.toString().equals(this.toString()))
                return 3;
        }
    }

    static void stats(int nbVals, int nbGames) {
        int player1Wins = 0, player2Wins = 0, infiniteGames = 0, draws = 0;
        for (int i=0; i < nbGames; i++) {
            Battle battle = new Battle(nbVals);
            int result = battle.game();
            if (result == 1) player1Wins++;
            else if (result == 2) player2Wins++;
            else if (result == 3) infiniteGames++;
            else if (result == 0) draws++;
        }
        System.out.println("player1 Wins: " + String.format("%.10f", (double)player1Wins / nbGames));
        System.out.println("player2 Wins: " + String.format("%.10f", (double)player2Wins / nbGames));
        System.out.println("infiniteGames Num: " + String.format("%.10f", (double)infiniteGames / nbGames));
        System.out.println("Draws Num: " + String.format("%.10f", (double)draws / nbGames));
    }
}