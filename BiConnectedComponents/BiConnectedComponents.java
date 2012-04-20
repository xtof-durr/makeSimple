// modex programmation efficace - ecole polytechnique - c.durr - 2012

/** calcul 
    - des points d'articulation
    - des ponts
    - des componsantes bi-connexes

    en temps lineaire par deux parcours DFS.
*/

import java.util.*;

@SuppressWarnings("serial")
    class Liste extends ArrayList<Integer> {}

class Main {
    static int V;            // nombre de sommets
    static Liste N[];        // voisins d'un sommet

    static int   d[];        // marque de premiere visite par DFS
    static int   T[];        // pere dans l'arbre DFS
    static final int NEW=-1; // pas encore visite
    static int time;         // ordre de visite
    static int low[];        // pow point
    static boolean deja[];   // marque pour 2eme DFS

    static boolean isArtPoint[]; // point d'articulation
    static int bcc[][];          // bi-connected component number, -1 for bridge
    static final int BRIDGE=-1;
    static int B;                // number of bi-connected components

    // lire un graphe en format <nb sommets> <nb aretes> 
    // puis <u> <v> pour chaque arete (u,v)
    static void read(Scanner in) {
        V = in.nextInt();
        int m = in.nextInt();
        N = new Liste[V];
        for (int u=0; u<V; u++)
            N[u] = new Liste();

        while (m-->0) {
            int u=in.nextInt(), v=in.nextInt();
            N[u].add(v);
            N[v].add(u);
        }
    }

    // afficher le graphe en format DOT, 
    // les aretes sont etiquetees avec numero de composante bi-connexe
    static void printBCC() {
        System.out.println("graph G {\nnode [shape=point]\n[rankdir=LR]");
        for (int u=0; u<V; u++)
            if (isArtPoint[u])
                System.out.println(u+" [color=grey]");
            else
                System.out.println(u);
        for (int u=0; u<V; u++)
            for (int v: N[u])
                if (u<v) {// don't print twice
                    int c = bcc[u][v];
                    if (c==BRIDGE)
                        System.out.println(u +" -- "+v+" [color=grey]");
                    else
                        System.out.println(u +" -- "+v+" [label="+c+"]");           
                }
        System.out.println("}");
    }

    // afficher le graphe en format DOT, 
    // les sommets sont etiquetees d[u] et low[u]
    static void printLOW() {
        System.out.println("graph G {\nnode [shape=circle]\n[rankdir=LR]");
        for (int u=0; u<V; u++)
            if (low[u]<Integer.MAX_VALUE)
                System.out.println(u+" [label=\""+d[u]+"("+low[u]+")\"]");
            else
                System.out.println(u+" [label=\""+d[u]+"(+infty)\"]");
        for (int u=0; u<V; u++)
            for (int v: N[u])
                if (d[u]<d[v]) {// don't print twice
                    if (T[v]==u)
                        System.out.println(u +" -- "+v);
                    else
                        System.out.println(u +" -- "+v+" [color=grey]");            
                }
        System.out.println("}");
    }

    //snip{ BiConnectedComponents
    static void solve() {
        dfs1();
        dfs2();
    }
        
    // premier parcours DFS pour calculer low 
    static void dfs1() {
        d          = new int[V];
        T          = new int[V];
        low        = new int[V];
        Arrays.fill(d,NEW);
        time = 0;
        for (int u=0; u<V; u++)
            if (d[u]==NEW)
                dfs1(u, NEW);  
        // initier un nouvel arbre pour composante connexe contenant u
    }
        
    // p = pere de u dans l'arbre. NEW si u = racine
    static void dfs1(int u, int p) {
        T[u]=p;             // noter le pere du u (optionnel)
        d[u]=time++;        // marquer ordre premiere visite
        // low = destination minimale arete retour 
        low[u]=Integer.MAX_VALUE;      
        for (int v:N[u])
            if (d[v]==NEW) {
                dfs1(v, u);
                if (low[v]<low[u]) 
                    low[u] = low[v];
            }
            else 
                if (v!=p && d[v]<low[u]) 
                    low[u] = d[v];
    }

    static void dfs2() {
        B    = 0; // nb composantes bi-connexes
        deja = new boolean[V];
        isArtPoint = new boolean[V];
        bcc        = new int[V][V];
        for (int u=0; u<V; u++)
            if (!deja[u])
                dfs2(u, NEW, NEW);
    }

    // c = numero de composante, si c=NEW alors composante encore vide
    // p = pere de u dans l'arbre DFS, si p=NEW alors u est une racine
    static void dfs2(int u, int p, int c) {
        deja[u] = true;
        int nbSonsDisc = 0; // nombre de fils qui seraient deconnectes sans u
        for (int v: N[u])
            if (!deja[v]) {
                if (low[v]>=d[v]) {                // (u,v) est un pont
                    bcc[u][v] = bcc[v][u] = BRIDGE;
                    nbSonsDisc++;
                    dfs2(v, u, NEW);               
                    // ici commence une nouvelle comp.
                }
                else if (low[v]>=d[u] || p==NEW) { 
                    // nouvelle composante bi-connexe
                    int b = bcc[u][v] = bcc[v][u] = B++;
                    nbSonsDisc++;
                    dfs2(v,u,b);
                } else {                           // meme composante
                    assert(c!=NEW && p!=NEW);
                    bcc[u][v] = bcc[v][u] = c;
                    dfs2(v,u,c);
                }
            }
            else if (v!=p && d[v]<d[u]) {          // arete retour
                assert(c!=NEW && p!=NEW);
                bcc[u][v] = bcc[v][u] = c;
            }
        isArtPoint[u] = p==NEW && nbSonsDisc>=2 || p!=NEW && nbSonsDisc>=1;
    }
    //snip}

    public static void main(String args[]) throws Exception {
        Scanner      in = new Scanner(System.in);
        read(in);
        solve();
        printLOW();
    }
}
