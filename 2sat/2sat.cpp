/* Modex ACM c.durr (2008)
   
   resoudre une formule 2-SAT en temps linéaire.

   Formule(n) crée une formule sur n variables booléens x1,x2,...,xn.
   Les litéraux sont codés 1,..,n ou les variables positives 
   et -1,..,-n pour leurs négations.
   add(x,y) ajoute la clause x OU y, où x,y sont des litéraux.
   solve() résoud la forrmule. 
   et get permet alors de lire la valeur des variables dans la solution.
*/
#include <iostream>
#include <fstream>
#include <vector>
#include <stack>
#include <assert.h>

using namespace std;


// ---------------- le graphe oriente sur les literaux

struct Sommet {
  vector<int> out;
  vector<int> in;
  int         comp; // numero de la composante fortement connexe
  int         name; // nom pour affichage
};

struct Graphe : vector<Sommet> {

  Graphe(int n) :vector<Sommet>(n) {}

  // ajouter un arc (u,v)
  void add(int u, int v) {
    (*this)[u].out.push_back(v);
    (*this)[v].in.push_back(u);
  }

};
  
// ------------------ Formule


struct Formule {
  int n; // nb variables, les variables sont de 1 a n, leur negations de -1 a -n
  Graphe G; // graphe des implications

  vector<int> var; // valeur des variables, indices de 1 a n

  enum ValPossibles {FAUX=-1, UNDEF=0, VRAI=+1};


  // given a literal, returns the vertex number
  int vertex(int x) {
    assert(1<=abs(x) && abs(x)<=n);
    return x>0 ? x-1 : 2*n+x;
  }

  // given a literal, returns its value
  int get(int x) const {
    assert(1<=abs(x) && abs(x)<=n);
    return x>0 ? var[x] : -var[-x];
  }

  // set the value of a literal
  // dis-donc, pourquoi je me met a commenter en anglais ?
  void set(int x, int val) {
    assert(1<=abs(x) && abs(x)<=n);
    if (x>0)
      var[x]  =  val;
    else
      var[-x] = -val;
  }

  Formule (int _n) :G(2*_n) , var(_n+1, UNDEF){
    n=_n;
    for (int x=1; x<=n; x++) {
      G[vertex( x)].name =  x;
      G[vertex(-x)].name = -x;
    }
  }

  // ajoute la clause x || y, qui est equivalente a -x=>y et -y=>x
  void add(int x, int y) {
    assert(1<=abs(x) && abs(x)<=n);
    assert(1<=abs(y) && abs(y)<=n);
    G.add(vertex(-x),vertex( y));
    G.add(vertex(-y),vertex( x));
  }
  
#define PAS_ENCORE -1

  stack<int>  aTraiter;
  int         heure;
  
  /* V(v).comp contient soit PAS_ENCORE si sommet pas encore visite
     sinon la date de debut de traitement par dfs (decale par -size()-1).
     V(v).comp contient sinon le numero d'un sommet representatif de la
     composante.
  */
  int sccpFrom(int u) {
    int v;
    aTraiter.push(u);
    int m = G[u].comp = heure++;
    for (int i=0; i<G[u].out.size(); i++) {
      int v = G[u].out[i];
      int mv = (G[v].comp==PAS_ENCORE) ? sccpFrom(v) : G[v].comp;
      m = min(m, mv);
    }
    if (m == G[u].comp) {
      int uval = get(G[u].name);
      int compval = (uval==UNDEF) ? VRAI : uval;
      do {
	v = aTraiter.top();
	aTraiter.pop();
	G[v].comp = u;
	set(G[v].name, compval);
      } while (u != v);
    }
    return m;
  }

  // retourne true si la formule est satisfiable
  bool solve() {
    heure = -1-G.size();
    var.assign(var.size(), UNDEF);
    for (int u=0; u<G.size(); u++)
      G[u].comp = PAS_ENCORE;

    for (int u=0; u<G.size(); u++)
      if (G[u].comp==PAS_ENCORE)
	sccpFrom(u);

    // est-ce qu'il existe une variable x et -x qui sont dans la 
    // meme componsante ?
    for (int x=1; x<=n; x++) 
      if (G[vertex(x)].comp == G[vertex(-x)].comp)
	return false;

    print("tmp.dot");
    return true;
  }


  /** afficher en format DOT le graphe d'implication avec les composantes
      fortement connexes et en indiquant les valeurs booleennes de celles-ci
  */
  void print(const char *filename) const {
    ofstream o(filename, ios::out);
    vector<int>  c[G.size()];
    o << "digraph {" << endl
      << "  node [shape=circle]" << endl;
    // produire une liste de sommets par composante
    for (int u=0; u<G.size(); u++) 
      c[G[u].comp].push_back(u); 

    // afficher les composantes
    for (int j=0; j<G.size(); j++) 
      if (c[j].size() != 0) {
	o << "  subgraph cluster" << j << " {\n    ";
	if (get(G[c[j][0]].name)==VRAI)
	  o << "style=filled; color=lightgrey;\n    ";
	for (int i=0; i<c[j].size(); i++) {
	  int u = c[j][i];
	  o << u << " [label=" << G[u].name << "]; ";
	}
	o << endl << "  }" << endl;
      }
    // afficher les arcs
    for (int u=0; u<G.size(); u++) 
      for (int i=0; i<G[u].out.size(); i++) {
	int v = G[u].out[i];
	o << "  " << u << " -> " << v << endl;
      }
    o << "}" << endl;
  }
};

// ------------------ Programme principal
int main() {
  int n, x,y;
  cin >> n;
  Formule F(n);
  while (cin >> x >> y) 
    F.add(x, y);
  if (!F.solve())
    cout << "Formule non satisfiable";
  else {
    cout << "Formule satisfiable" << endl;
    for (int x=1; x<=n; x++)
      cout << "x"<<x<< "=" << (F.get(x)==Formule::VRAI) << " ";
  }
  cout << endl;
}
