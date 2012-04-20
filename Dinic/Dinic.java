// modex programmation efficace - ecole polytechnique - c.durr - 2009

/** Total Flow
    http://acm.tju.edu.cn/toj/showp3172.html
    
    trouver le flot maximal dans un graphe

    Flot maximal par Algorithme de Dinic en O(n^2m). Mais sur ce
    probleme n'est pas plus rapide que l'algo de Edmonds-Karp.
*/

import java.util.*;
import java.io.*;

class Main {

    //snip{ snipDinic
    static int n;        // nb sommets
    static int C[][];    // capacite initiale
    static int F[][];    // flot
    static int adj[][];  // adj list
    static int l[];      // niveau (l=level)

    //                                       initialiser les tableaux
    static void init(int _n) {                   
	n = _n;
	C = new int[n][n];
	F = new int[n][n];
	l = new int[n];
    }
    
    
    //  calcule et retourne flot maximal (qui sera dans F)
    static int dinic(int s, int t) {
	Queue<Integer> q = new LinkedList<Integer>();
	int total = 0;
	while (true) {
	    q.offer(s);
	    Arrays.fill(l,-1);      // construire graphe des niveaux par BFS
	    l[s]=0;
	    while (!q.isEmpty()) {
		int u = q.remove();
		for (int v: adj[u]) {
		    if (l[v]==-1 && C[u][v] > F[u][v]) {
			l[v] = l[u]+1;
			q.offer(v);
		    }
		}
	    }
	    if (l[t]==-1)               // plus de chemin s-t dans ce graphe
		return total;
	    total += dinic(s,t, Integer.MAX_VALUE);
	}			    
    }

    // trouver un flot bloquant et retourne sa valeur
    // suppose que dans p il y a le chemin de s a u
    static int dinic(int u, int t, int val) {
	if (val<=0)
	    return 0;
	if (u==t)  //                                       -- augment
	    return val;
	int a=0, av;        //                              -- advance
	for (int v: adj[u]) 
	    if (l[v]==l[u]+1 && C[u][v] > F[u][v]) {
		av = dinic(v, t, Math.min(val-a, C[u][v]-F[u][v]));
		F[u][v] += av;
		F[v][u] -= av;
		a += av;
	    }
	if (a==0) //                                         -- retreat
	    l[u] = -1;
	return a;
    }
    //snip}

    //                               initialiser la liste d'adjacence
    static void initAdj() {
	int d[] = new int[n];                    // degrees
	for (int u=0; u<n; u++)                  // calcul degree
	    for (int v=u+1; v<n; v++)
		if (C[u][v]>0 || C[v][u]>0) {
		    d[u]++;
		    d[v]++;
		}
	adj = new int[n][];
	for (int u=0; u<n; u++)
	    adj[u] = new int[d[u]];
	for (int u=0; u<n; u++)                 // remplir liste adj
	    for (int v=u+1; v<n; v++)
		if (C[u][v]>0 || C[v][u]>0) {
		    adj[u][--d[u]] = v;
		    adj[v][--d[v]] = u;
		}	
    }

    static void printDot() {
	System.out.println("digraph G {");
	for (int u=0; u<n; u++)
	    System.out.println(""+u+" [label=\""+(u+1)+" ["+l[u]+"]\"];");
	for (int u=0; u<n; u++)
	    for (int v: adj[u]) 
		System.out.println("  "+u+" -> "+v+
				   "[label=\"["+F[u][v]+"/"+C[u][v]+"]\"];");
	System.out.println("}");	
    }    

    // converti en le numero qui sera utilise en interne pour le sommet
    static int idx(String s) {
	char c = s.charAt(0);
	if (Character.isLowerCase(c))
	    return c-'a' + 26;
	else
	    return c-'A';
    }

    public static void main(String[] args) {
	Scanner in = new Scanner(System.in);
	
	init(2*26);
	    
	int m = in.nextInt();
	while (m-->0) {
	    int u = idx(in.next());
	    int v = idx(in.next());
	    int c = in.nextInt();
	    C[u][v] += c;
	    C[v][u] += c;
	}
	initAdj();
	System.out.println(dinic(idx("A"),idx("Z")));
    }    

}
