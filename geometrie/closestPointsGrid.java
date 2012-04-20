// modex programmation efficace - c.durr - 2011

/** Quoit Design
    http://acm.zju.edu.cn/onlinejudge/showProblem.do?problemId=1107
    
 *  Trouver les points les plus proches en temps espéré O(n) avec une
 *  table de hashage.
 

 * Idée: si d est la plus petite distance trouvé jusqu'à là, on
 * définit une grille de côté (d/2)*(d/2), et on place un par un les
 * points dans cette grille. Il y aura au plus un seul point par
 * cellule.  Pour chaque point p on va comparer la distance entre p et
 * les autres points q déjà placés, mais il suffit de considérer les
 * points q dans les 5*5 cases autour de celle de p.  Donc ce test
 * coûte un temps constant. Si on a amélioré la distance d, on
 * recommence tout.  

 * Pour la complexité en moyenne l'argument clé est que si les entrées
 * sont permutées uniformément au hasard, alors au moment du
 * traitement du i-ème point (i=1..n), on a amélioré la distance d
 * avec probabilité 1/(i-1).  Donc au total la complexité espéré est
 * de l'ordre de $\sum_{i=1}^n i/(i-1)$, donc linéaire en n.
 */

import java.awt.geom.*;
import java.util.*;

import java.io.*;


//snip{
class Cell {
    int a,b; 
    Cell(int _a, int _b) {a=_a; b=_b;}
    public int hashCode() {
	return a*1001 + b;
    }
    public boolean equals(Object obj) {
	Cell c = (Cell)obj;
	return c.a==a && c.b==b;
    }
}

class Main {
    static int    n;
    static double x[], y[];
    
    //...{
    static boolean read(BufferedReader in) throws IOException {
	n = Integer.parseInt(in.readLine());
	if (n==0) return false;
	//                  -- Scanner est trop lent, lire avec BufferedReader
	x = new double[n];
	y = new double[n];
	for (int i=0; i<n; i++) {
	    String  s = in.readLine();
	    int     p = s.indexOf(' ');
	    x[i] = Double.parseDouble(s.substring(0,p));
	    y[i] = Double.parseDouble(s.substring(p+1,s.length()));
	}

	// shuffle
	for (int i = 1; i < n; i++) {
	    int r = (int) (Math.random() * (i+1));     // int between 0 and i
	    double swap;
	    swap = x[r]; x[r] = x[i]; x[i] = swap;
	    swap = y[r]; y[r] = y[i]; y[i] = swap;
	}

	return true;
    }

    //...}
    
    static double dist(int i, int j) { // distance euclidienne
	return Math.hypot(x[i]-x[j], y[i]-y[j]);
    }

    static HashMap<Cell,Integer> H;

    static double d;

    static boolean improve() {
	H = new HashMap<Cell,Integer>();
	for (int i=0; i<n; i++) {
	    //             -- (a,b) coordonnees de la cellule du i-eme point
	    int a = (int)Math.floor(x[i]*2/d); 
	    int b = (int)Math.floor(y[i]*2/d);
	    //             -- chercher dans les 25 cases autour
	    for (int a2=a-2; a2<=a+2; a2++)
		for (int b2=b-2; b2<=b+2; b2++) {
		    Cell k2 = new Cell(a2,b2);
		    if (H.containsKey(k2)) {
			int j = H.get(k2);
			double d2 = dist(i,j);
			if (d2<d) {
			    d = d2;
			    return true;
			}
		    }
		}
	    Cell k = new Cell(a,b);
	    H.put(k, i);
	}
	return false;
    }
	    	
    static double solve() {
	d = dist(0,1);
	while (d>0 && improve()) {
	    /* do nothing */
	}
	return d;
    }

    //snip}

    public static void main(String args[])  throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	while (read(in))  {
	    System.out.format(Locale.US,"%.2f\n",solve()/2);
	}
    }
}
