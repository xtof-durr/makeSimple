/* Modex ACM c.durr (2008)

   Max weighted bi-partite matching

   Kunh-Munkres algorithm (the hungarian algorithm)
   cette implementation est en O(n^3).

main:
   lit <n> qui decrit une matrice M de dim n*n
   puis suivent autant de lignes que nessaire de la forme
   <u> <v> <M[u,v]>
   ici M[u,v] est le coût de l'arête (u,v) et il est de type double
   affiche la liste des arêtes de la solution optimale et son coût

KuhnMunkres:
   constructeur prend le nombre de sommets n, 
   et construit un graphe complet G(U,V,E) avec |U|=|V|=n
   puis on peut modifier les poids directement dans la matrice
   des poids M, et appeler M.solve() pour calculer l'optimum.

   les tableaux m0, m1 encoderont le couplage, 
   m0[u] = le sommet v auquel u est couplé
   m1[v] = le sommet u auquel v est couplé
*/

import java.util.*;

//snip{
class KuhnMunkres {

    // ------------------------------------- le graphe
    
    // taille du graphe
    int n;

    // poids des aretes
    double[][] w;

    // ------------------------------------- le couplage

    //couplage : m0[i] = sommet j dans V auquel i est couple, m1=inverse
    int[] m0, m1;
    final static int LIBRE=-1;
    
    // cardinalite du couplage
    int m;

    // l'arbre alternant
    // racine est un sommet libre de S
    int racine;
    // path : T -> V definit avec m0 l'arbre alternant
    // path[v] = predecesseur de v dans l'arbre
    int[] path;

    // ------------------------------------- l'etiquetage, etc.
    
    //etiquetage
    double[] l0, l1;

    //ensembles S,T
    boolean[] S, T;

    // slack en anglais, 
    // pour tout v pas dans T : 
    // margeVal[v] = min l0[u]+l1[v]-w[u][v] sur u dans S
    // et marge0[v] est u qui minimise
    double[] margeVal;
    int[]    marge0;

    
    KuhnMunkres(int _n) {
        n = _n;
        w        = new double[n][n];
        m0       = new int[n];
        m1       = new int[n];
        path     = new int[n];
        l0       = new double[n];
        l1       = new double[n];
        S        = new boolean[n];
        T        = new boolean[n];
        margeVal = new double[n];
        marge0   = new int[n];
    }

    // ------------------------------------- parties de l'algorithme
    
   // chercher dans U un sommet non couple'
    int sommetLibre() {
        for(int u=0; u<n; u++)
            if (m0[u]==LIBRE) 
                return u;
        throw new Error("recherche de sommet libre sur couplage parfait");
   }

    // creer un etiquetage trivial initial
    void initEtiquettes() {
        double sup;
        // etiquetage initial 
        for (int u=0; u<n; u++) {
            sup = w[u][0];
            for (int v=1; v<n; v++)
                if (sup<w[u][v])
                    sup = w[u][v];
            l0[u] = sup;
        }
        Arrays.fill(l1, 0.0);
        // et couplage vide
        Arrays.fill(m0, LIBRE);
        Arrays.fill(m1, LIBRE);
        m = 0;
    }

    // chercher a augmenter le couplage
    void augmenterCouplage() {
        int y0=0, y1, next;
        double a=0.0, marge_uv;
        do {
            // chercher (y0,y1) dans S*!T avec la plus petite marge
            y1 = LIBRE;
            for (int v=0; v<n; v++) 
                if (! T[v]) {
                    if (y1==LIBRE || a > margeVal[v]) {
                        y1 = v;
                        y0 = marge0[v];
                        a  = margeVal[v];
                    }
                }
    
            // si a>0, alors N_l(S)=T et 
            // on peut ameliorer l par a = marge[y]
            if (a>0) {
                for (int u=0; u<n; u++)
                    if (S[u]) 
                        l0[u]  -= a;
                for (int v=0; v<n; v++)
                    if (T[v])
                        l1[v]  += a;
		    else
			margeVal[v] -= a;
                a = 0;
            }

            // desormais a==0 et N_l(S) =/= T, et y1 est de N_l(S)-T.
            // on augmente l'arbre
            if (m1[y1]!=LIBRE) {
                int u = m1[y1];
                // on ajoute un nouveau sommet dans S, il faut mettre a jour la marge
                S[u] = true;
                for (int v=0; v<n; v++) 
                    if (! T[v]) {
                        marge_uv = l0[u] + l1[v] - w[u][v];
                        if (margeVal[v]> marge_uv) {
                            margeVal[v] = marge_uv;
                            marge0[v]   = u;
                        }
                    }
            }
            T[y1]     = true;
            path[y1]  = y0;
        } while (m1[y1]!=LIBRE);
    
        // y1 est libre, on peut augmenter le couplage 
        // remonter le chemin de y1 vers la racine
        do {
            y0    = path[y1];
            next  = m0[y0];
            // changer le couplage
            m1[y1] = y0;
            m0[y0] = y1;
            // iterer plus loin sur le chemin vers la racine
            y1 = next;
        } while (y0!=racine);
        m++;
    }

    void solve() {
        initEtiquettes();
        // chercher a rendre le coupage parfait
        while (m<n) {
            // chercher sommet libre u et poser S={u}, T={}
            Arrays.fill(S, false);
            Arrays.fill(T, false);
            racine = sommetLibre();
            S[racine] = true;
            // intialiser la marge
            for(int v=0; v<n; v++) {
                margeVal[v] = l0[racine]+l1[v]-w[racine][v];
                marge0[v]   = racine;
            }
            augmenterCouplage();
        }
    }

    // ------------------------------------- programme principal

    public static void main(String[] args) {
        int u,v, n;
        double wuv;
        Scanner in = new Scanner(System.in);
        in.useLocale(Locale.US);
        
        n = in.nextInt();
        KuhnMunkres M = new KuhnMunkres(n);
        
        while (in.hasNext()) {
            u   = in.nextInt();
            v   = in.nextInt();
            wuv = in.nextDouble();
            M.w[u][v] = wuv;
        }
        M.solve();
        
        // afficher solution
        double total = 0.0;
        for (u=0; u<n; u++) {
            System.out.println(""+u+" -- "+M.m0[u]
                               +" ["+M.w[u][M.m0[u]]+"]");
            total += M.w[u][M.m0[u]];
        }
        System.out.println("\ttotal weight="+ total);
    }
}
//snip}
