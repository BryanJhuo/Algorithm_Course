package HW2;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

class Row {
    private final int[] fruits;

    // constructor
    Row() { // empty
        this.fruits = new int[0];
    }

    Row(int[] fruits) { // by given fruits
        this.fruits = fruits.clone();
    }

    // methods
    @Override
    public boolean equals(Object o) {
        Row that = (Row) o;
        if (this.fruits.length != that.fruits.length)
            return false;
        for (int i=0; i<this.fruits.length; i++){
            if (this.fruits[i] != that.fruits[i])
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (int i=0; i<this.fruits.length; i++){
            hash = 2 * hash + this.fruits[i];
        }
        return hash;
    }

    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        for (int i=0; i<this.fruits.length; i++) {
            s.append(this.fruits[i]);
        }
        return s.toString();
    }

    Row extendedWith(int fruit) {
        int[] copyFruits = new int[this.fruits.length + 1];
        copyFruits[copyFruits.length - 1] = fruit;
        System.arraycopy(this.fruits, 0, copyFruits, 0, this.fruits.length);
        return new Row(copyFruits);
    }

    public boolean isStable() {
        for (int i=0; i < fruits.length - 2; i++){
            if (fruits[i] == fruits[i + 1] && fruits[i] == fruits[i + 2]) {
                return false;
            }
        }
        return true;
    }

    static LinkedList<Row> allStableRows(int width) {
        LinkedList<Row> stableRows = new LinkedList<>();

        // width = 0
        if (width == 0) {
            stableRows.add(new Row(new int[]{}));
            return stableRows;
        }
        // width = 1
        stableRows.add(new Row(new int[]{0}));
        stableRows.add(new Row(new int[]{1}));
        
        // iteration from 2 to width
        for (int currentWidth = 2; currentWidth <= width; currentWidth++){
            LinkedList<Row> newStableRows = new LinkedList<>();

            for (Row row : stableRows) {
                Row rowWithZero = row.extendedWith(0);
                Row rowWithOne = row.extendedWith(1);

                if (rowWithZero.isStable()) 
                    newStableRows.add(rowWithZero);
                if (rowWithOne.isStable())
                    newStableRows.add(rowWithOne);
            }
            stableRows = newStableRows;
        }
        return stableRows;
    }

    boolean areStackable(Row r1, Row r2) {
        if (this.fruits.length != r1.fruits.length || this.fruits.length != r2.fruits.length)
            return false;
        for (int i=0; i<this.fruits.length; i++){
            if (this.fruits[i] == r1.fruits[i] && this.fruits[i] == r2.fruits[i])
                return false;
        }
        return true;
    }
}

class CountConfigurationsNaive {
    // private static final Map<String, Long> memo = new HashMap<>(); 

    static long count(Row r1, Row r2, LinkedList<Row> rows, int height) {
        if (height == 2) return 1;
        // String key = r1.toString() + "|" + r2.toString() + "|" + height;
        
        // if (memo.containsKey(key)) return memo.get(key); 
        long totalCount = 0;
        for (Row r3 : rows) {
            if (r1.areStackable(r2, r3))
                totalCount += count(r2, r3, rows, height - 1);
        }

        // memo.put(key, totalCount);
        return totalCount;
    }

    static long count(int n) {
        if (n == 0) return 1;
        if (n == 1) return 2;

        LinkedList<Row> rows = Row.allStableRows(n);
        long totalCount = 0;

        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < rows.size(); j++) {
                Row r1 = rows.get(i);
                Row r2 = rows.get(j);

                totalCount += count(r1, r2, rows, n);
            }
        }
        
        return totalCount;
    }
}

class Quadruple { 
	Row r1;
	Row r2;
	int height;
	long result;

	Quadruple(Row r1, Row r2, int height, long result) {
		this.r1 = r1;
		this.r2 = r2;
		this.height = height;
		this.result = result;
	}
}

class HashTable {
    final static int M = 50000;
    Vector<LinkedList<Quadruple>> buckets = new Vector<>(M);

    // constructor
    HashTable() {
        for (int i=0; i < M; i++) {
            buckets.add(new LinkedList<>());
        }
    }

    // method
    static int hashCode(Row r1, Row r2, int height) {
        return 31 * r1.hashCode() + 17 * r2.hashCode() + height;
    }

    static int bucket(Row r1, Row r2, int height) {
        int hash = hashCode(r1, r2, height);
        return (hash % M + M) % M;  // ensure the range to be 0 <= result < M
    }

    void add(Row r1, Row r2, int height, long result) {
        int index = bucket(r1, r2, height);
        Quadruple quad = new Quadruple(r1, r2, height, result);
        buckets.get(index).add(quad);
    }

    Long find(Row r1, Row r2, int height) {
        int index = bucket(r1, r2, height);
        for (Quadruple quad: buckets.get(index)) {
            if (quad.r1.equals(r1) && quad.r2.equals(r2) && quad.height == height)
                return Long.valueOf(quad.result);
        }
        return null;
    }
}

class CountConfigurationsHashTable {
    static HashTable memo = new HashTable();

    static long count(Row r1, Row r2, LinkedList<Row> rows, int height) {
        if (height == 2) return 1;
        Long cached = memo.find(r1, r2, height);
        // return value if count is already in HashTable
        if (cached != null) return cached;

        long totalCount = 0;
        
        for (Row r3 : rows) {
            if (r1.areStackable(r2, r3))
                totalCount += count(r2, r3, rows, height - 1);
        }

        memo.add(r1, r2, height, totalCount);
        return totalCount;
    }

    static long count(int n) {
        if (n == 0) return 1;
        if (n == 1) return 2;

        LinkedList<Row> rows = Row.allStableRows(n);
        long totalCount = 0;

        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < rows.size(); j++) {
                Row r1 = rows.get(i);
                Row r2 = rows.get(j);

                totalCount += count(r1, r2, rows, n);
            }
        }
        return totalCount;
    }
}

class Triple {
    private Row r1;
    private Row r2;
    private final int height;

    Triple(Row r1, Row r2, int height) {
        this.r1 = r1;
        this.r2 = r2;
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Triple)) return false;
        Triple other = (Triple) o;
        return height == other.height &&
               r1.equals(other.r1) &&
               r2.equals(other.r2);
    }

    @Override
    public int hashCode() {
        return HashTable.hashCode(r1, r2, height);
    }
}

class CountConfigurationsHashMap  {
    static HashMap<Triple, Long> memo = new HashMap<Triple, Long>();

    static long count(Row r1, Row r2, LinkedList<Row> rows, int height) {
        if (height == 2) return 1;
        
        Triple key = new Triple(r1, r2, height);
        if (memo.containsKey(key)) return memo.get(key);

        long totalCount = 0;
        for (Row r3 : rows) {
            if (r1.areStackable(r2, r3))
                totalCount += count(r2, r3, rows, height - 1);
        }

        memo.put(key, totalCount);
        return totalCount;
    }

    static long count(int n) {
        if (n == 0) return 1;
        if (n == 1) return 2;

        LinkedList<Row> rows = Row.allStableRows(n);
        long totalCount = 0;

        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < rows.size(); j++) {
                Row r1 = rows.get(i);
                Row r2 = rows.get(j);

                totalCount += count(r1, r2, rows, n);
            }
        }
        return totalCount;
    }
}
