// modex programmation efficace - ecole polytechnique - c.durr - 2009

/** Calculer l'enveloppe convexe par le balayage d'Andrew.
    Complexite O(n log n).
    voir http://www.algorithmist.com/index.php/Monotone_Chain_Convex_Hull
*/

import java.util.*;
import java.awt.geom.*;

class ConvexHull {

    //snip{

    /** Considere le Triangle a-b-c
        et retourne une valeur positive si oriente normal
        retourne une valeur negative si oriente dans le sens contraire
        et zero si a,b,c sont co-lineaires.
    */
    static double cross(Point2D.Double a, 
                  Point2D.Double b, 
                  Point2D.Double c) {
        return (b.getX() - a.getX()) * (c.getY() - a.getY())
            -  (b.getY() - a.getY()) * (c.getX() - a.getX());
    }

    /** Calcule l'enveloppe convexe. Va changer l'ordre de t.
     */
    static Vector<Point2D.Double> convexHull(Point2D.Double t[]) {
        Arrays.sort(t, new Comparator<Point2D.Double>() {
                public int compare(Point2D.Double a, Point2D.Double b) {  
                    int dx = (int)Math.signum(a.getX()-b.getX());
                    int dy = (int)Math.signum(a.getY()-b.getY());
                    return dx!=0 ? dx : dy;
                }
            });
        //                         -- parties haute et base de l'enveloppe
        Point2D.Double [] top = new Point2D.Double[t.length];
        Point2D.Double [] bot = new Point2D.Double[t.length];
        int ntop = 0, nbot = 0; // -- longueurs respectives
        for (Point2D.Double p: t) {
            while (ntop>=2 && cross(top[ntop-2], top[ntop-1], p)>=0)
                ntop--;
            top[ntop++] = p;
            
            while (nbot>=2 && cross(bot[nbot-2], bot[nbot-1], p)<=0)
                nbot--;
            bot[nbot++] = p;
        }
        // enlever premier element de top et dernier de bot 
        // concatener bot et top, c'est l'enveloppe convexe
        Vector<Point2D.Double> ret = new Vector<Point2D.Double>();
        for (int i=0; i<nbot-1; i++)
            ret.add(bot[i]);
        for (int i=ntop-1; i>0; i--)
            ret.add(top[i]);
        return ret;     
    }
    //snip}

    /** lit <n> puis <n> points de coordonnees flotantes
        affiche les points de l'enveloppe convexe
    */
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        in.useLocale(Locale.US);
        
        //                                  -- lire les n points
        int n = in.nextInt();
        Point2D.Double t[] = new Point2D.Double[n];
        for (int i=0; i<n; i++)
            t[i] = new Point2D.Double(in.nextDouble(), in.nextDouble());        
        
        for (Point2D.Double p : convexHull(t))
            System.out.println(p);
    }
}