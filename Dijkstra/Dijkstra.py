#!/usr/bin/env python
# -*- coding: utf-8 -*-
# ecole polytechnique - modal prog. eff. - c.durr - 2013

# Dijkstra - plus court chemins
# complexité (|V| log |V| + |E|)

from MinHeap import minHeap

INF = []


def Dijkstra(G, w, source, target=None):
    """G[u] = adj list of vertex u, w[u][v] = weight of arc (u,v)"""
    dist = [ INF for v in range(len(G))]
    dist[source] = 0

    H = minHeap(dist)
    while H.size()>0:
        u = H.pop()
        if dist[u]==INF or u==target:
            break
        for v in G[u]:
            alt = dist[u] + w[u][v]
            if dist[v]>alt:
                dist[v] = alt
                H.up(v)
    return dist

        


# ------------ test

# same example in http://upload.wikimedia.org/wikipedia/commons/5/57/Dijkstra_Animation.gif
edges = [(1,2,7), (1,3,9), (1,6,14), (2,3,10), (2,4,15), (3,4,11), (3,6,2), (4,5,6), (5,6,9)]
V = range(6)
G = [[] for u in V]
w = [[INF for u in V] for v in V]

for u,v,wuv in edges:
    u -= 1
    v -= 1
    G[u].append(v)
    G[v].append(u)
    w[u][v] = w[v][u] = wuv

print Dijkstra(G, w, 0, 4)
