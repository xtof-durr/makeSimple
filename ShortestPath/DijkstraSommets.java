/* c.durr - ecole polytechnique - 2012
   Plus courts chemins par Dijkstra
   a besoin que les poids des arcs soient non-negatifs.
   complexité O((n + m) log n)

   format entrée: 
   nombre de sommets <n> nombre d'aretes <m> 
   puis pour chacun des m aretes
   <u> <v> <w>
   source est le sommet 0
   destination le sommet n-1
*/

import java.util.*;

class DijkstraSommets {
    static int arcSrc[], arcDest[];
    static int arcWght[];
    static int d[]; // degree sortants
    static int adj[][];// arcs sortants
    
    static int n;     // nb de sommets

    static int dist[];   // distance

    // ajoute d'un arc (u,v) de poids w
    static void addArc(int k, int u, int v, int w) {
	arcSrc[k] = u;
	arcDest[k] = v;
	arcWght[k] = w;
	d[u]++;
    }

    static void read(Scanner in)  {
	n = in.nextInt();
	int m = 2*in.nextInt();
	d = new int[n];
	arcSrc  = new int[m];
	arcDest = new int[m];
	arcWght = new int[m];
	//                            -- lire les aretes
	for (int i=0; i<m; i+=2) {
	    int u=in.nextInt(), v=in.nextInt();
	    int w=in.nextInt();
	    addArc(i,   u,v,w);
	    addArc(i+1, v,u,w);
	}
	//                            -- créer les tables d'adjacence
	adj = new int[n][];
	for (int u=0; u<n; u++)
	    adj[u] = new int[d[u]];
	//                            -- et les remplir
	int pos[] = new int[n];
	for (int j=0; j<m; j++) {
	    int u = arcSrc[j];
	    adj[u][pos[u]++] = j;
	}
    }

    //snip{
    static void Dijkstra(int source) {
	dist = new int[n];
	// infini/2 pour eviter debordement lors d'une addition
	Arrays.fill(dist,Integer.MAX_VALUE/2); 
	dist[source] = 0;

	boolean deja[] = new boolean[n];
	
	// cet ordre retire voisin de S a plus courte distance
	TreeSet<Integer> Q 
	    = new TreeSet<Integer>(new Comparator<Integer>() {
		    public int compare(Integer u, Integer v) {
			return dist[u]-dist[v];
		    }
		});
	
	Q.add(source);

	while (!Q.isEmpty()) {
	    int u = Q.pollFirst();
	    if (!deja[u]) {
		deja[u] = true;      // ajouter u a S
		for (int i: adj[u]) {       // mettre a jour d pour les voisins
		    int v = arcDest[i];
		    if (!deja[v]) {
			int w = arcWght[i];
			int alt = dist[u]+w;
			if (alt<dist[v]) { // amelioration
			    Q.remove(v);
			    dist[v] = alt;
			    Q.add(v);
			}
		    }
		}
	    }
	}
    }
    //snip}
    
    public static void main(String args[]) {
	Scanner in = new Scanner(System.in);
	read(in);
	Dijkstra(0);
	for (int du: dist)
	    System.out.print(du+" ");
	System.out.println();
    }
}
