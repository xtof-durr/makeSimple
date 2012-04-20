import java.util.*;

class BellmanFord {
    int E[][]; // la liste d'adjacence
    int w[][]; // poids des arcs
    
    int n;     // nb de sommets

    int s;     // la source
    int d[];   // distance

    //snip{
    // retourne vrai s'il n'y a pas de cycle negatif
    boolean BellmanFord() {
	d = new int[n];
	// infini/2 pour eviter debordement lors d'une addition
	Arrays.fill(d,Integer.MAX_VALUE/2); 
	d[s] = 0;

	for (int i=1; i<n; i++)
	    for(int u=0; u<n; u++) // pour chaque arc (u,v)
		for (int v: E[u])
		    d[v] = Math.min(d[v], d[u]+w[u][v]); // relaxer
	// detecter cycle negatif
	for(int u=0; u<n; u++) // pour chaque arc (u,v)
	    for (int v: E[u])
		if (d[v] > d[u]+w[u][v])
		    return false;  // on pourrait relaxer a l'infini
	return true;
    }
    //snip}
}
