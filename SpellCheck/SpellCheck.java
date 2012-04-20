// modal programmation efficace - ecole polytechnique - c.durr - 2011

/* comment faire un correcteur orthographique

   usage: java SpellCheck < /usr/share/dict/words taable cheir candlle
*/

import java.awt.geom.*;
import java.util.*;

//snip{ NoeudSpellCheck
// noeud de l'arbre des prefixes
class Node {
    boolean isWord; // indique que le mot qui a mene' de la racine vers ce noeud est dans le dictionnaire
    Node son[];
    Node () {son = new Node[27];}
}
//snip}

class SpellCheck {

    //snip{ SpellCheck
    // ajoute un mot s dans l'arbre dont la racine est n, retourne le remplacement pour n
    static Node add(Node n, String s) {
	if (n==null)
	    n = new Node();
	if (s.equals(""))
	    n.isWord = true;
	else {
	    //                              --- decomposer s
	    char   c = s.charAt(0);
	    String r = s.substring(1);
	    if ('a'<=c && c<='z') 
		n.son[c-'a'] = add(n.son[c-'a'], r);
	}
	return n;
    }
	    
    // cherche un mot dans le dico n qui soit a distance au plus dist de s
    static String find(Node n, String s, int dist) {
	if (dist<0 || n==null)        return null;  // cas de base
	if (s.length()==0) 
	    if (n.isWord) 
		return "";
	    else 
		return null;	   
	//                              --- decomposer s
	char   c = s.charAt(0);
	int    i = c-'a';
	String r = s.substring(1);
	//                              --- chercher
	String f = find(n.son[i], r, dist);  // direct
	if (f!=null)  return c+f;
	for (c='a'; c<='z'; c++) {
	    f = find(n.son[i], s, dist-1);   // insertion
	    if (f!=null)  return c+f;
	    f = find(n.son[i], r, dist-1);   // substitution
	    if (f!=null)  return c+f;
	}
	return find(n, r, dist-1);           // suppression
    }
    

    // trouve un mot le plus proche du dictionnaire
    static String find(Node root, String s) {
	String f;
	// augmenter distance jusqu'a succes
	for (int dist=0; (f=find(root,s,dist))==null; dist++) {}
	return f;
    }
    //snip}

    public static void main(String args[]) {
	Scanner in = new Scanner(System.in);
	Node root = null;
	while (in.hasNext()) 
	    root = add(root, in.next());
	
	for (String s: args)
	    System.out.println(find(root, s));
    }
}
	    
