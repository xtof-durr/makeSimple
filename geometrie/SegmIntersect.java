//snip{ snipSegment
//ecole polytechnique - modex acm - c.durr (2008)

/* lit une suite de n segments et affiche le nombre de segments qui
   intersectent, disons k.  utilise un algorithme de balayage pour cela.
   Complexite est O(nlogn + k).
*/
import java.util.*;
import java.awt.geom.*;


class Balai implements Comparator<Line2D.Double> {

    static double xt; // position balai

    /* calcule l'intersection de la droite x=xt (parallele a l'axe y)
       et du segment s (vue comme une droite)
       //...{
     :       s.p2
     :       /
     :      /:<-y
     :     / :
     :    /  :dy
     :   /   :
     :  s.p1....+
     :  dx  ^x
     //...}
    */
    static double y(Line2D.Double s, double xt) {
        return s.y1 + (s.y2-s.y1)*(xt-s.x1)/(s.x2-s.x1);
    }

    /* on trie les segments par rapport a l'intersection de la droite
     * x=xt (parallele a l'axe y).  Le premiers segments sont ceux
     * dont l'intersection est haute.  On a la promesse que les droite
     * ne sont pas paralleles a l'axe y.  Si deux segments
     * intersectent au meme endroit, on decale un peu la droite `a
     * x=xt-1.
     */
    static int compareTo(Line2D.Double a, Line2D.Double b) {
	double diff = y(a,xt) - y(b,xt);
        return (int)Math.signum(diff!=0 ? diff : y(a,xt-1)-y(b,xt-1));
    }
}

//------------------------------------ evenements

class  Event implements Comparable<Event> {
    static final int ARRIVEE=0, INTERSECT=1, DEPART=2;
    int           event;
    double        x;    // heure de l'evenement
    Line2D.Double seg;  // segment de l'evenement

    Event(int _event, double _x, Line2D.Double _seg) {
        event = _event;
        x     = _x;
        seg   = _seg;
    }

    // les evenements seront dans l'ordre du temps x et en priorite ARRIVE..
    public int compareTo(Event e) {
        int dx = (int)Math.signum(x - e.x);
        if (dx!=0)
            return dx;
        else
            return event-e.event;
    }
}

//------------------------------------ Main

class SegmIntersect {

    static double xt; // position du balai

    PriorityQueue<Event>   q= new PriorityQueue<Event>();

    TreeSet<Line2D.Double> b= new TreeSet<Line2D.Double>(new Balai());
							
    // retourne le point d'intersection ou null
    // considere les segments comme des droites
    Point2D.Double intersect(Line2D.Double a, Line2D.Double b) {
        if (!a.intersectsLine(b))
            return null;
        double adx  = a.x2-a.x1;
        double ady  = a.y2-a.y1;
        double bdx  = b.x2-b.x1;
        double bdy  = b.y2-b.y1;
        double x = (b.y1-a.y1+ady*a.x1/adx-bdy*b.x1/bdx)/(ady/adx - bdy/bdx);
        double y = ady*x/adx + a.x1;
        return new Point2D.Double(x,y);
    }

    // est-ce que le segment de a va intersecter avec le segment d'avant ?
    void detect(Arbre a) {
        if (a==null || a.prec==null)
            return;
        Point2D.Double p = intersect(a.prec.seg, a.seg);
        if (p==null)
            return;
        // intersection dans le futur ?
        if (p.x > Arbre.xt)
            q.add(new Event(Event.INTERSECT, p.x, a.seg));
    }

    void print(String msg, Line2D.Double s) {
        System.out.println(msg+"\t"+s.x1+" "+s.y1+" "+s.x2+" "+s.y2);
    }

    void  add(Line2D.Double seg) {
        print("add",seg);
        racine = Arbre.add(racine, seg, null, null);
        Arbre a = Arbre.find(racine, seg);
        detect(a);
        detect(a.succ);
    }

    void remove(Line2D.Double seg) {
        print("del",seg);
        Arbre a = Arbre.find(racine, seg).succ;
        racine = Arbre.remove(racine, seg);
        detect(a);
    }

    void intersect(Line2D.Double seg) {
        Arbre a = Arbre.find(racine, seg);
        print("inter ",seg);
        System.out.println(""+a);
        print("  avec",a.prec.seg);
        // echanger dans l'arbre
        Line2D.Double tmp = a.seg;
        a.seg = a.prec.seg;
        a.prec.seg = tmp;
        detect(a.succ);
        detect(a.prec);
    }

    int count(Vector<Line2D.Double> all) {
        int countIntersect = 0;
        // creer la file d'evenements
        for (Line2D.Double l : all) {
            q.add(new Event(Event.ARRIVEE, Math.min(l.x1, l.x2), l));
            q.add(new Event(Event.DEPART,  Math.max(l.x1, l.x2), l));
        }
        // traiter les evenements
        while (! q.isEmpty()) {
            Event  e = q.poll();
            Arbre.xt = e.x;   // on avance au moment de l'evenement
            Arbre.print(racine, "");
            System.out.println("-----");
            switch (e.event) {
            case Event.ARRIVEE:    
                add(e.seg);       break;
            case Event.DEPART:    
                remove(e.seg);    break;
            case Event.INTERSECT: 
                countIntersect++;
                intersect(e.seg); break;                
            }
        }
        return countIntersect;
    }


    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        in.useLocale(Locale.US);
        SegmIntersect s = new SegmIntersect();
        Vector<Line2D.Double> all = new Vector<Line2D.Double>();
        while (in.hasNext()) {
            double x1 = in.nextDouble();
            double y1 = in.nextDouble();
            double x2 = in.nextDouble();
            double y2 = in.nextDouble();
            all.add(new Line2D.Double(x1,y1,x2,y2));
        }
        System.out.println(""+s.count(all));
    }
}
//snip}