/* Modex ACM c.durr (2008)
   
   resoudre une formule booleenne 2-SAT par Tarjan en temps lineaire.
   Mais cette implementation utilise une matrice d'adjacence est sera
   donc en temps quadratique (en nombre de variables).

   format de l'entree:
        chaque instance est code par
        <n> <m> 
        m fois <x> <y>
        et la fin est marquee par
        0 0
        Dans l'instance il y a n variables, et m clauses
        x,y sont des literaux. 1..n sont les literaux positifs 
        et -1..-n les literaux negatifs.
*/

import java.util.*;

class Main {    
//snip{ snipSccp
    static int n; // nb literaux, variable xi=2*i, literal non(xi)=2*i+1
    //        pour un literal y, non(y)=y^1    
    static boolean adj[][];    // adj[x][y] = il existe un arc x=>y

    static int o[];            // o[i] = i-eme sommet traite par dfs
    static boolean deja[];     // marqueur pour dfs
    static int time;
    static int sccp[];         // sccp[u]=numero composante fort. connex. de u

    // parcours dfs normal, noter dans o l'ordre fin visite
    static void dfs(int u) {
        deja[u] = true;
        for (int v=0; v<n; v++) 
            if (adj[u][v] && !deja[v])
                dfs(v);
        o[time++] = u;    
    }

    // parcours dfs en inversant les arcs, mettre tout le monde dans meme comp.
    static void dfsInv(int u, int comp) {
        deja[u] = true;
        sccp[u] = comp;
        for (int v=0; v<n; v++) 
            if (adj[v][u] && !deja[v])
                dfsInv(v, comp);
    }

    // calculer les composantes fortement connexes, et retourner leur nombre
    static int sccp() {
        time = 0;
        deja = new boolean[n];
        o    = new int[n];
        for (int u=0; u<n; u++)
            if (!deja[u])
                dfs(u);

        int comp=0;
        deja = new boolean[n];
        sccp = new int[n];
        for (int i=n-1; i>=0; i--) {
            int u = o[i];
            if (!deja[u])
                dfsInv(u, comp++);
        }
        return comp;
    }
    //snip}

    //snip{ snip2sat
    static int nc;           // nb composantes fortement connexes
    static int val[];        // valeur d'une comp. +1=vrai, -1=faux, 0=indef.
    static int neg[];        // neg[j]=composante opposee de j

    // retourne true si la formule est satisfiable
    // dans ce cas, pour chque literal x sa valeur est val[sccp[x]]
    static boolean satisfiable() {
        nc  = sccp();
        neg = new int[nc];

        // est-ce qu'il existe une variable x et sa negation non(x)
        // qui soient dans la meme componsante ?
        for (int x=0; x<n; x+=2) 
            if (sccp[x] == sccp[x^1])
                return false;      
            else {
                neg[sccp[x]] = sccp[x^1];
                neg[sccp[x^1]] = sccp[x];
            }

        // on utilise le fait que les compsantes portent un numero dans 
        // l'ordre topologique inverse
        val = new int[nc];
        for (int j=0; j<nc; j++)
            if (val[j]==0) {
                val[j]      = -1;
                val[neg[j]] = +1;
            }
        return true;
    }
    //snip}

    public static void main(String[] args) {
        int m, x,y;
        Scanner in = new Scanner(System.in);
        for (int t=1; true; t++) {      
            n = 2*in.nextInt(); 
            m = in.nextInt();
            if (n==0)
                return;
            adj = new boolean [n][n];
            while (m-->0) {
                x = in.nextInt();
                x = (x>0) ? 2*(x-1) : 1-2*(x+1);
                y = in.nextInt();
                y = (y>0) ? 2*(y-1) : 1-2*(y+1);
                adj[x^1][y] = adj[y^1][x] = true;
            }
            if (!satisfiable())
                System.out.println("Formule "+t+" non satisfiable");
            else {
                System.out.println("Formule "+t+" satisfiable");
                for (x=0; x<n; x+=2) 
                    System.out.println("  X"+(x/2+1)+"="+(val[sccp[x]]==+1));
            }
        }
    }
}
