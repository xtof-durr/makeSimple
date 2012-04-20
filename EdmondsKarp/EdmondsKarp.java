// ecole polytechique - modex acm - c.durr (2008)

/** 
    algorithm EdmondsKarp
    
    trouve un flot maximal dans un graphe en temps O(n * m^2).
    il y a mieux, mais c'est plus long a programmer.

    l'entree est composee de 4 entiers
    <n> <m> <s> <t> 
    ou n est le nb de sommets numer. de 0 a n-1, 
    m le nb d'arcs,
    s, et t la source et la destination
    suivent alors m lignes de la forme
    <u> <v> <c>
    ou c est la capacite de l'arc (u,v).
*/

import java.util.*;

//snip{
class Sommet {
    ArrayList<Integer> out = new ArrayList<Integer>();
    // mettre des etiquettes des sommets ici :
}

class EdmondsKarp {
    int        n;
    Sommet[]   V;
    double[][] C; // capacites
    double[][] F; // flot, capacite residuelle de u a v est C[u][v] - F[u][v]

    EdmondsKarp(int _n) {
	n = _n;
	V = new Sommet[n];
	for (int u=0; u<n; u++)
	    V[u] = new Sommet();
	C = new double[n][n];
    }

    // retourne la valeur du flot maximal de s a t. Le flot est dans F ensuite.
    double maxFlow(int s, int t) {
	F = new double[n][n];
	double f = 0.0;
	while (true) {
	    int[]  P = new int[n]; // contiendra un chemin: P[u]=parent de u
	    double m; // valeur du chemin augmentant
	    m = BFS(s, t, P);
	    // est-ce que le flot ne peut plus etre augmente ?
	    if (m==0.0)
		break;
	    f += m;
	    // retracer le chemin augmentant P a partir de la fin
	    // et mettre a jour le flot le long de P
	    for (int v=t; v!=s; v=P[v]) {
		int u = P[v];
		F[u][v] += m;
		F[v][u] -= m;
	    }
	}
	return f;
    }
    
    // cherche un chemin augmentant par parcours en largeur
    // retourne la capacte residuelle minimal le long du chemin
    double BFS(int s, int t, int[] P) {
	// P[u]==-1 indique le u n'est pas dans un chemin
	Arrays.fill(P, -1); 
	
	P[s] = -2; // pour s'assure que la source n'est pas redecouverte
	// M[u] : capacite trouve d'un chemin de s a u
	double[] M = new double[n];
	M[s] = Double.MAX_VALUE;
	
	// pour un parcours en largeur on utilise une file
	LinkedList<Integer> Q = new LinkedList<Integer>();
	Q.addLast(s);
	while (!Q.isEmpty()) {
	    int u = Q.removeFirst();
	    for(int v : V[u].out) {
		double residual = C[u][v] - F[u][v];
		// s'il y a de la capacite disponible, et que v est vu
		// pour la premiere fois
		if (residual > 0 && P[v] == -1) {
		    P[v] = u;		    
		    M[v] = Math.min(M[u], residual);
		    if (v != t)
			Q.addLast(v);
		    else
			return M[t];
		}
	    }
	}
	return 0.0;
    }

    public static void main(String[] args) {
	Scanner in = new Scanner(System.in);
	in.useLocale(Locale.US);
	
	int n = in.nextInt();
	EdmondsKarp G = new EdmondsKarp(n);

	int m = in.nextInt();
	int s = in.nextInt();
	int t = in.nextInt();
	while (m-- >0) {
	    int    u = in.nextInt();
	    int    v = in.nextInt();
	    double c = in.nextDouble();
	    G.V[u].out.add(v);
	    G.C[u][v] = c;
	    /* enlever commentaires si graphe lu est non-dirige
	       G.V[v].out.add(u);
	       G.C[v][u] = c;
	    */
	}
	System.out.println("max flow="+G.maxFlow(s,t));
	for (int u=0; u<n; u++)
	    for (int v: G.V[u].out)
		System.out.println(""+u+" -> "+v+" : "
                                   +G.F[u][v] +" out of "+G.C[u][v]);
    }    
}
//snip}
