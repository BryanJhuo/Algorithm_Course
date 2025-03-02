package HW1;
import java.util.LinkedList;
import java.util.HashMap;
import java.lang.Math;

class Deck {
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
