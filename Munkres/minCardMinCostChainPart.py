#!/usr/bin/python
# -*- coding: utf-8 -*-
# ecole polytechnique - c.durr - 2012

""" partitionner un ordre partiel en un nombre minimal de chaînes et
    de coût minimal.  en temps O(n^3) par réduction au problème de
    couplage parfait à coût minimal dans un graphe bi-parti.
    la réduction est expliquée dans le poly.
    
    minCardMinCostChainPart(n,M) retourne p
    n = nombre de sommets d'un DAG
    DAG = graphe dirigé sans cycle, ou ordre partiel
    M[u,v] = coût de l'arc (u,v) ou None si inexistant.
    hypothese: les sommets sont dans l'ordre topologique,
    donc si l'arc (u,v) existe alors u<v.
    p est un tableau qui décrit le partionnement,
    p[u] = indice de la chaîne contenant sommet u
    les indices de sommets seront successifs de 0,1,..,k-1
    où k est le nombre de chaînes de la solution.
"""

from kuhnMunkres import *

def minCardMinCostChainPart(M, Empty=None):
    #                           --- vérifier hypothèses
    n = len(M)
    for u in range(n):
        assert len(M[u])==n
        assert M[u][u]==Empty
    for v in range(n):
        for u in range(v):
            assert M[v][u]==Empty
    #                           --- construire graphe bi-parti complet
    #                           --- extraire max poids des arcs
    maxWeight = 0
    for v in range(n):
        for u in range(v):
            if M[u][v]>maxWeight:
                maxWeight = M[u][v]
    #                           --- big = poids pour completer le graphe
    big = 1+n*maxWeight
    #                           --- B = graphe complet, inverser signe poids
    B = [[(-big if M[u][v]==Empty else -M[u][v]) for v in range(n)] for u in range(n)]
    Mout,Min,val = maxWeightMatching(B)
    #                           --- enlever du couplage les arcs de poids big
    for u in range(n):
        v = Mout[u]
        if M[u][v]==Empty:
            del Mout[u]
            del Min[v]
    #                           --- extraire les chemins
    p = [0 for u in range(n)]
    k=0
    for u in range(n):
        if u not in Min:
           #                    --- ou a trouvé un début de chaîne
           p[u] = k
           v = u
           while v in Mout: #   --- suivre la chaîne
              v = Mout[v]
              p[v]=k
           k += 1
    return p


def printDot(M,p):
    f = open("tmp.dot", "w")
    n = len(M)
    f.write("digraph G{\n")
    for u in range(n):
        f.write('%u [label="%i[%i]"]\n'%(u,u,p[u]))
    for u in range(n):
        for v in range(n):
            if M[u][v]!=None:
                f.write('%i -> %i [label="%g"]\n'%(u,v,M[u][v]))
    f.write("}\n");
    f.close()


if __name__=='__main__':
    """lit dans le format <n> <m>
    puis m lignes du format <u> <v> <weight of arc (u,v)>
    """
    n,m = map(int,raw_input().split())
    M = [[None for v in range(n)] for u in range(n)]
    for i in range(m):
        t = raw_input().split()
        u       = int(t[0])
        v       = int(t[1])
        M[u][v] = float(t[2])
    p = minCardMinCostChainPart(M)
    printDot(M,p)
    
