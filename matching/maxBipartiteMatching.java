/* Modex ACM c.durr (2008)

   maximum bi-partite matching.
   complexité O(n m^2).
   n'est pas le meilleur algorithme, mais est simple.

   MaxBipartiteMatching(n0,n1) prend le nombre de sommets des deux parties.
   puis dans M[u][v] on peut ajouter des arêtes.
   maxBipartiteMatching() retourne la taille du plus grand couplage,
   qui est ensuite codé dans les tableaux m0,m1.
   pour un sommet u m0[u] est soit LIBRE (-1) soit le sommet auquel u est couplé.
   idem pour m1.
   
   
   Lit de l'entree standard les entiers n0 n1, qui sont les
   cardinalites des ensembles U et V Puis une liste d'entiers i j avec
   0<=i<n0, 0<=j<n1 specifiant les aretes.
*/

import java.util.*;

//snip{
class MaxBipartiteMatching {

    // M = matrice d'adjacence	
    // n0,n1 = nb sommets a gauche et a droite
    // m0[i] est le sommet a droite auquel i est couple'
    // m0[i]==LIBRE si i n'est pas couple'
    boolean[][]  M;
    int n0, n1; 
    int[] m0, m1; 
    static final int LIBRE=-1;   

    // marquage des sommets deja visites pour le dfs
    boolean[] deja;
    
    MaxBipartiteMatching(int _n0, int _n1) {
	n0 = _n0;
	n1 = _n1;
	M  = new boolean[n0][n1];
	m0 = new int[n0];
	m1 = new int[n1];
	deja = new boolean[n0];
	Arrays.fill(m0, LIBRE);
	Arrays.fill(m1, LIBRE);
    }

    // retourne si un chemin augmentant a pu etre trouve,
    // dans ce cas ce chemin est applique au couplage
    // commence a explorer de i qui est a gauche
    boolean dfs(int i) {
	assert (!deja[i]);
	deja[i] = true;
	for (int j=0; j<n1; j++)
	    if (M[i][j] && (m1[j]==LIBRE || 
			    !deja[m1[j]] && dfs(m1[j]))) {
		m0[i] = j; // coupler i avec j
		m1[j] = i;
		return true;
	    }
	return false;
    }

    // cherche un chemin augmentant a partir de u
    // retourne 1 si succes, 0 sinon
    boolean cheminAugmentant() {
	Arrays.fill(deja, false);
	for(int i=0; i<n0; i++) {
	    if (m0[i]==LIBRE)
		if (dfs(i))
		    return true;
	}
	return false;
    }

    // trouve un couplage maximal et retourne sa cardinalite
    int maxBipartiteMatching() {
	int val=0;
	// partir du couplage trivial vide
	Arrays.fill(m0, LIBRE);
	Arrays.fill(m1, LIBRE);
	// chercher a augmenter tant que possible
	while (cheminAugmentant())
	    val ++;
	return val;
    }

    public static void main(String[] args) {
	int i,j, n0, n1;
	Scanner in = new Scanner(System.in);
	n0 = in.nextInt();
	n1 = in.nextInt();
	MaxBipartiteMatching g = new MaxBipartiteMatching(n0, n1);
	
	// lire les aretes
	while (in.hasNext()) {
	    i = in.nextInt();
	    j = in.nextInt();
	    assert(0<=i && i<n0 && 0<=j && j<n1);
	    g.M[i][j] = true;
	}
	System.out.println("max matching = "+g.maxBipartiteMatching());
	for (i=0; i<n0; i++)
	    if (g.m0[i] != LIBRE)
		System.out.println(" "+i+" -- "+g.m0[i]);
    }
}
//snip}