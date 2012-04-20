// Modal Informatique: programmation efficace
// ecole polytechnique 2011, c.durr + s.oudot

/* la classe ExactCover implémente l'algorithme de backtracking des liens dansants.
   On a un univers d'élements 0,1,2...,universe-1. 
   On a des ensembles de ces éléments.
   Le problème de ExactCover consiste à sélectionner une collection de ces ensembles,
   tel qu'ils partionnent l'univers.
   Le contructeur de ExactCover prend universe en argument, 
   puis les appels à add(r,c) indiquent que l'ensemble r contient l'élément c.
   finalement solve() retourne si une solution a pu être trouvé.
   Dans ce cas la pile sel contient les indices des ensembles de la solution.
   L'algorithme est exponentiel dans le pire des cas.

   Il est illustré ici sur l'exemple d'une résolution du Sudoku.

   http://acm.tju.edu.cn/toj/showp2546.html
   Sudoku

   Resoudre Sudoku 16*16 par backtracking (liens dansants)

   cette version utilise une classe pour les cellules, ce qui n'est
   pas beaucoup plus lent, et plus lisible.  Aussi les manipulation
   pour retirer ou rajouter dans une liste doublement chainee, sont
   mises dans des methodes pour plus de lisibilite.
*/


import java.util.*;

//snip{ Cell

class Cell {
    Cell L,R,U,D;  // double chainage vertical U,D, et horizontal L,R
    int S,C;       // deux informations, en general indices ligne, colonne

    // cree une cellule et l'insere evt dans une liste verticale et horizontale
    // la cellule sera mise a gauche de left. Si left est la cellule la plus a gauche,
    // la cellule sera insere a l'extremite droite. Idem pour below.
    Cell(Cell left, Cell below, int _S, int _C) {
	S=_S;
	C=_C;
	if (left==null) {L=R=this;}
	else {
	    L   = left.L;
	    R   = left;
	    L.R = this;
	    R.L = this;
	}
	if (below==null) {U=D=this;}
	else {
	    U   = below.U;
	    D   = below;
	    U.D = this;
	    D.U = this;
	}
    }
    //snip}
    // -------------------------------------- manipulation de cellules

    //snip{ hideVert
    void hideVert() {
	U.D = D;
	D.U = U;
    }
    //snip}
    
    //snip{ unhideVert
    void unhideVert() {
	D.U = this;
	U.D = this;
    }
    //snip}

    void hideHorz() {
	R.L = L;
	L.R = R;
    }

    void unhideHorz() {
	L.R = this;
	R.L = this;
    }

};

/* le constructeur de cette classe prend en argument la taille de l'univers.
   
 * ensuite les 1 de la matrice sont ajoutes par la methode add, en
 * respectant l'ordre ligne par ligne.

 * La methode solve resoud le probleme, et si elle retourne true,
 * alors la pile sel contient la liste des lignes de la solution.
*/     
class ExactCover {

    // -------------------------------------- donnees

    Stack<Integer> sel; // les lignes selectionnees, evt. la solution
    
    Cell col[]; // indice colonne -> cellule colonne
    Cell h;     // entete generale

    // -------------------------------------- construction
    
    ExactCover(int universe) {
        h   = new Cell(null, null, 0,0); 
	col = new Cell[universe];

	for (int c=0; c<universe; c++) 
	    col[c] = new Cell(h, null, 0, c); 

	sel = new Stack<Integer>();
    }

    int  last_row = -1;
    Cell first    = null;     // first cell in row
    
    /* les entrees (r,c) de la matrice doivent etre ajoutees groupees
     * par lignes r.  Le plus simple est alors de respecter l'ordre
     * lexicographique sur (r,c) des appels successifs a add.  Mais
     * pour un meme r, les appels ne doivent pas forcement arriver
     * dans l'ordre croissant de c.
    */
    void add(int r, int c) {
	if (r!=last_row)
	    first = new Cell(null, col[c], r, c);
	else
	    new Cell(first, col[c], r, c);
	col[c].S++;      // mettre a jour compteur dans l'entete de colonne
	last_row = r;    // se souvenir pour l'ajout suivant
    }



    // -------------------------------------- manipulation de lignes/colonnes
    //snip{ cover
    void cover(int ic) { // i doit etre une entete de colonne  
	Cell c=col[ic];
	c.hideHorz();
	for (Cell i=c.D; i!=c; i=i.D)
	    for (Cell j=i.R; j!=i; j=j.R) { //    -- boucer dans la ligne
		j.hideVert();
		col[j.C].S--; //   -- une entree de moins dans cette colonne
	    }
    }

    void uncover(int ic) {
	Cell c=col[ic];
	for (Cell i=c.U; i!=c; i=i.U)
	    for (Cell j=i.L; j!=i; j=j.L) { //    -- boucer dans la ligne
		col[j.C].S++; //   -- une entree de plus de nouveau
		j.unhideVert();
	    }
	c.unhideHorz();
    }
    //snip}

    // -------------------------------------- resoudre

    //snip{ dlxSolve
    boolean solve() {
        if (h.R==h)         // matrice vide, on a trouve une solution
            return true;
        
        // choisir colonne c de plus petite taille
        int  s = Integer.MAX_VALUE;
	Cell c=null;
        for (Cell j=h.R; j!=h; j=j.R)
            if (j.S<s) {
                c = j;
                s = j.S;
            }
	
	cover(c.C);
        // essayer chaque ligne
        for (Cell r=c.D; r!=c; r=r.D) {
            sel.push(r.S); //              -- selectionner r
            for (Cell j=r.R; j!=r; j=j.R)
                cover(j.C);
            if (solve())
                return true;
            for (Cell j=r.L; j!=r; j=j.L)
                uncover(j.C);
	    sel.pop();
        }
        uncover(c.C);
	
        return false;   
    }
    //snip}
    

    // n'est utilise que pour tester la classe
    // en argument les lignes d'une matrice binaire
    public static void main(String args[]) {
        int r = args.length, c = args[0].length();
        ExactCover e = new ExactCover(c);
        for (int i=0; i<r; i++)
            for (int j=0; j<c; j++)
                if (args[i].charAt(j)=='1')
                    e.add(i,j);
        if (e.solve()) {
            for (int i: e.sel)
                System.out.print(" "+i);
            System.out.println();
        }           
    }
}

/* on reduit sudoku a exact cover
   
   Exact Cover: etant donnee une matrice binaire M, trouver un
   ensemble de lignes S, tel que l'intersection avec chaque colonne c
   contient un seul 1.
*/
class Main {

    //snip{ reducSudoko1
    /* une assignation est un entier a, qui code une ligne, une
     * colonne, un block et une valeur.  Les assignations sont les
     * lignes de M. */
    static int row(int a) {return a/16/16;                }
    static int col(int a) {return (a/16)%16;              }
    static int blk(int a) {return (row(a)/4)*4 + col(a)/4;}
    static int val(int a) {return a%16;                   }

    /* une contrainte est un couple, soit une ligne,colonne, soit
     * ligne,valeur soit colonne,valeur, soit block,valeur.  Les
     * contraintes sont les colonnes de M. */ 
    static int rc(int a)  {return           row(a)*16+col(a);}
    static int rv(int a)  {return   16*16 + row(a)*16+val(a);}
    static int cv(int a)  {return 2*16*16 + col(a)*16+val(a);}
    static int bv(int a)  {return 3*16*16 + blk(a)*16+val(a);}
    //snip}

    public static void main(String args[]) {
        char[] M = new char[256];
        
        Scanner in = new Scanner(System.in);
        int count=0;
        while (in.hasNext()) {
            // --- lire la grille
            for (int i=0; i<16; i++) {    
                String w = in.next();
                for (int j=0; j<16; j++) {
                    M[16*i+j] = w.charAt(j);
                }
            }

            // --- reduire vers ExactCover
            //snip{ reducSudoku2
            ExactCover e = new ExactCover(4*16*16);
            
            for (int a=0; a<16*16*16; a++) {
                e.add(a, rc(a));
                e.add(a, rv(a));
                e.add(a, cv(a));
                e.add(a, bv(a));
            }

            // les affectations initialles
            for (int p=0; p<16*16; p++)
                if (M[p] != '-') {
                    int a = 16*p + M[p]-'A';
                    e.cover(rc(a));
                    e.cover(rv(a));
                    e.cover(cv(a));
                    e.cover(bv(a));
		}
            //snip}

            if (!e.solve()) 
                System.err.println("** pas de solution");
            // --- extraire la solution
            for (int a : e.sel)
		M[a/16] = (char)('A' + a%16);
            
            // --- afficher
            if (count++ >0)
                System.out.println();
            for (int p=0; p<256; p++) {
                System.out.print(M[p]);
                if (p%16==15) 
                    System.out.println();
            }
        }
    }
}
