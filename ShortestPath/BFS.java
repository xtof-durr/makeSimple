import java.util.*;

class BFS {
    //snip{
    int E[][]; // la liste d'adjacence
    
    int n;     // nb de sommets

    int s;     // la source
    int d[];   // distance
    boolean visited[]; // marque pour parcours BFS

    void BFS() {
	d = new int[n];
	Arrays.fill(d,Integer.MAX_VALUE);
	d[s] = 0;

	visited = new boolean[n];
	Queue<Integer> Q = new LinkedList<Integer>();
	Q.add(s);
	
	while (!Q.isEmpty()) {
	    int u = Q.poll();
	    if (!visited[u]) {
		visited[u] = true;
		for (int v: E[u]) {
		    d[v] = Math.min(d[v], d[u]+1);
		    Q.add(v);
		}
	    }
	}
    }
    //snip}
}
