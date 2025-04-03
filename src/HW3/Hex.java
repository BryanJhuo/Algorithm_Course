package HW3;

class UnionFind {
    private int[] parent;
    private int[] rank;

    UnionFind(int size) {
        this.parent = new int[size];
        this.rank = new int[size];

        for (int i = 0; i < size; i++) {
            parent[i] = i;
            rank[i] = 1;
        }
    }

    int find(int x) {
        if (this.parent[x] != x) 
            this.parent[x] = find(parent[x]);
        return parent[x];
    }

    void union(int x, int y) {
        int px = find(x);
        int py = find(y);

        if (px == py) return ;

        // merge
        if (rank[px] < rank[py])
            parent[px] = py;
        else if (rank[px] > rank[py])
            parent[py] = px;
        else {
            parent[py] = px;
            rank[px]++;
        }
    }

    boolean isConnected(int x, int y) {
        return find(x) == find(y);
    }
}

public class Hex {
    enum Player {
        NOONE, BLUE, RED
    }

    // members
    private int n;
    private Player[][] grid;
    private UnionFind ufRed, ufBlue;
    private Player currentPlayer;
    private Player winner;

    private int index(int i, int j) {
        return i + (n + 2) * j;
    }

    Hex(int n) {
        this.n = n;
        this.grid = new Player[n + 2][n + 2];   // range: 0 ~ n+1

        // initial all the grids are Player.NOONE
        for (int i = 0; i <= n + 1; i++) {
            for (int j = 0; j <= n + 1; j++) {
                this.grid[i][j] = Player.NOONE;
            }
        }

        // set Top and Button are RED
        for (int j = 1; j <= n; j++) {
            grid[0][j] = Player.RED;    // Top
            grid[n + 1][j] = Player.RED;  // Button
        }

        // Set Left and Right are BLUE
        for (int i = 1; i <= n; i++) {
            grid[i][0] = Player.BLUE;   // Left
            grid[i][n + 1] = Player.BLUE; // Right
        }

        // initial Union-Find, include (n + 2) * (n + 2) points
        int total = (n + 2) * (n + 2);
        ufRed = new UnionFind(total);
        ufBlue = new UnionFind(total);

        // RED border
        for (int j = 1; j <= n; j++) {
            ufRed.union(index(0, j), index(0, 1));
            ufRed.union(index(n + 1, j), index(n + 1, 1));
        }

        // BLUE border
        for (int i = 1; i <= n; i++) {
            ufBlue.union(index(i, 0), index(1, 0));
            ufBlue.union(index(i, n + 1), index(1, n + 1));
        }

        currentPlayer = Player.RED;
        winner = Player.NOONE;
    }
    
    Player get(int i, int j) {
        if (i < 0 || i > n + 1 || j < 0 || j > n + 1)
            return Player.NOONE;
        return grid[i][j];
    }
    
    boolean click(int i, int j) {
        if (winner != Player.NOONE) return false;
        if (i < 1 || i > n || j < 1 || j > n) return false;
        if (grid[i][j] != Player.NOONE) return false;

        grid[i][j] = currentPlayer;
        int currIndex = index(i, j);

        int[][] dirs = {{-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}};
        for (int[] d: dirs) {
            int ni = i + d[0], nj = j + d[1];
            if (ni < 0 || ni > n + 1 || nj < 0 || nj > n + 1) continue;
            if (grid[ni][nj] == currentPlayer) {
                int neighborIndex = index(ni, nj);
                if (currentPlayer == Player.RED)
                    ufRed.union(currIndex, neighborIndex);
                else if (currentPlayer == Player.BLUE)
                    ufBlue.union(currIndex, neighborIndex);
            }
        }

        // winner 
        if (currentPlayer == Player.RED && ufRed.isConnected(index(0, 1), index(n + 1, 1))) {
            winner = Player.RED;
        }
        else if (currentPlayer == Player.BLUE && ufBlue.isConnected(index(1,0), index(1, n + 1))) {
            winner = Player.BLUE;
        }
        
        // change
        if (winner == Player.NOONE) {
            currentPlayer = (currentPlayer == Player.RED) ? Player.BLUE : Player.RED;
        }
        return true;
    }
    
    Player currentPlayer() {
        if (winner == Player.NOONE)
            return currentPlayer;
        return Player.NOONE;
    }
    
    Player winner() {
        if (winner != Player.NOONE)
            return winner;
        return Player.NOONE;
    }
    
    int label(int i, int j) {
        if (i < 0 || i > n + 1 || j < 0 || j > n + 1) return -1;
        if (grid[i][j] == Player.RED)
            return ufRed.find(index(i, j));
        else if (grid[i][j] == Player.BLUE)
            return ufBlue.find(index(i, j));
        else
            return -1;
    }
    
    
    public static void main(String[] args) {
        HexGUI.createAndShowGUI();
    }
}