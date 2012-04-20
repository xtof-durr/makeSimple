/* Modex ACM c.durr (2008)

   maximum bi-partite matching.
   complexité O(n m^2).
   n'est pas le meilleur algorithme, mais est simple.

   Lit de l'entree standard les entiers n0 n1, qui sont les cardinalites des ensembles U et V
   Puis une liste d'entiers i j avec 0<=i<n0, 0<=j<n1 specifiant les arretes.
   Le couplage est ecrit dans un fichier tmp.dot.
*/

#include <iostream>
#include <fstream>

using namespace std;

// nb maximal de sommets de part et d'autre
const int MAX = 200; 
// matrice d'adjacence	
bool M[MAX][MAX];

// nb sommets a gauche et a droite
int n0, n1; 
// couplage, m0[i] est le sommet a droite auquel i est couple'
int m0[MAX],m1[MAX]; 
// utilise' si sommet libre (pas couple')
const int LIBRE=-1;   

// marquage des sommets deja visites pour le dfs
bool deja[MAX]; 

// retourne si un chemin augmentant a pu etre trouve,
// dans ce cas ce chemin est applique au couplage
// commence a explorer de i qui est a gauche
bool dfs(int i) {
  assert (!deja[i]);
  deja[i] = true;
  for (int j=0; j<n1; j++)
    if (M[i][j] && (m1[j]==LIBRE || !deja[m1[j]] && dfs(m1[j]))) {
      m0[i] = j; // coupler i avec j
      m1[j] = i;
      return true;
    }
  return false;
}

// cherche un chemin augmentant a partir de u
// retourne 1 si succes, 0 sinon
bool cheminAugmentant() {
  for(int i=0; i<n0; i++)
    deja[i] = false;
  for(int i=0; i<n0; i++) {
    if (m0[i]==LIBRE)
      if (dfs(i))
	return true;
  }
  return false;
}

int maxBipartiteMatching() {
  int val=0;
  // partir du couplage trivial vide
  for (int i=0; i<n0; i++)
    m0[i] = LIBRE;
  for (int j=0; j<n1; j++)
    m1[j] = LIBRE;
  // chercher a augmenter tant que possible
  while (cheminAugmentant())
    val ++;
  return val;
}


// affiche dans le format DOT
void printGraph(const char *filename) {
  ofstream out(filename);
  out << "graph G {" << endl
      << "  rankdir=LR" << endl
      << "  subgraph cluster0 {" << endl;
  for (int i=0; i<n0; i++)
    out << i << " [label=\"u" << i << "\"]" << endl;
  out << "}" << endl
      << "  subgraph cluster1 {" << endl;
  for (int j=0; j<n1; j++)
    out << n0+j << " [label=\"v" << j << "\"]" << endl;
  out << "}" << endl;
  for (int i=0; i<n0; i++)
    for (int j=0; j<n1; j++) 
      if (M[i][j]) {
	out << i << " -- " << n0+j;
	if (m0[i]==j)
	  out << " [style=bold]";
	out << endl;
      }
  out << "}" << endl;
}

int main() {
  int i,j;
  cin >> n0 >> n1;
  assert(0<= n0 && n0<MAX & 0<= n1 && n1<MAX);
  // commencer par le graphe vide
  for (int i=0; i<n0; i++)
    for (int j=0; j<n1; j++)
      M[i][j] = false;
  // lire les aretes
  while (cin >> i >> j) {
    assert(0<=i && i<n0 && 0<=j && j<n1);
    M[i][j] = true;
  }
  cout << maxBipartiteMatching() << endl;
  printGraph("tmp.dot");
  /*
    for (int i=0; i<n0; i++) 
    if (m0[i]!=LIBRE)
      cout << i << " -- " << m0[i] << endl;
  */
  return 0;
}
