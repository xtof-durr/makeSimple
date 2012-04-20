// modex programmation efficace - c.durr - 2009

/** Trouver les points les plus proches en temps O(n log n) par
 * balayage.
 */

import java.awt.geom.*;
import java.util.*;


/** si on travaille avec Point2D.Double a la place de Point, alors
    il faut calculer (int)Math.signum de la difference.
*/
class CompareXY implements Comparator<Point2D.Double> {
    public int compare(Point2D.Double p, Point2D.Double q) {
        int diffX =  (int)Math.signum(p.getX() - q.getX());
        int diffY =  (int)Math.signum(p.getY() - q.getY());
        if (diffX != 0)
            return diffX;
        else
            return diffY;
    }
}

class CompareYX implements Comparator<Point2D.Double> {
    public int compare(Point2D.Double p, Point2D.Double q) {
        int diffX =  (int)Math.signum(p.getX() - q.getX());
        int diffY =  (int)Math.signum(p.getY() - q.getY());
        if (diffY != 0)
            return diffY;
        else
            return diffX;
    }
}

    /** lit <n> puis les coordonnees flotantes de <n> points
        affiche la plus paire la plus proche.
     */
//snip{
class ClosestPoints {
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        in.useLocale(Locale.US);
        //                                    -- lire tous les points
        int n = in.nextInt();
        assert(n>=2);
        Point2D.Double tab[] = new Point2D.Double[n];
        for (int i=0; i<n; i++)
            tab[i] = new Point2D.Double(in.nextDouble(), in.nextDouble());
	
	long start = System.currentTimeMillis();
        //                                    -- trier par X
        Arrays.sort(tab, new CompareXY());
        //                          -- dist=meilleure distance vue jusqu'alors
        double dist = tab[0].distance(tab[1]);
        Point2D.Double  sol0 = tab[0], sol1 = tab[1]; // points realisant la distance

        //                                    -- B=bande verticale triee par Y
        TreeSet<Point2D.Double> B 
            = new TreeSet<Point2D.Double>(new CompareYX());
        // pour les classes des comparateurs voir chapitre 1

        //                                    -- balayer de gauche a droite
        for (Point2D.Double p: tab) {
            //                                -- avancer le balai `a p
            //     -- comparer p avec les points de B au dessus et en dessous
            Point2D.Double low = new Point2D.Double(p.getX(), p.getY()-dist);
            Point2D.Double up  = new Point2D.Double(p.getX(), p.getY()+dist);
            for (Point2D.Double q: B.subSet(low, up)) {
                if (q.getY() < p.getY()-dist)
                    B.remove(q);  //          -- point en dehors de la bande
                else {
                    double d = p.distance(q);
                    if (d<dist) {  //         -- on a trouve mieux
                        dist = d;
                        sol0 = q;
                        sol1 = p;
                    }
                }
            }           
            //                                -- mettre a jour la bande
            B.add(p);                   
        }
	long end = System.currentTimeMillis();
        System.out.println("closest pair "+sol0+", "+sol1+
                           " at distance "+dist);
	System.out.println("computation time (w/o reading) "+(end-start)+"ms");
    }
}
//snip}