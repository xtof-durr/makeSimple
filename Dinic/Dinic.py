#!/usr/bin/python

# c.durr - 2009 - max flow by Dinic in time O(n^2 m)

from queue import Queue

def dinic(graph, cap, s,t):
    """ Find maximum flow from s to t. 
    returns value and matrix of flow.
    graph is list of adjacency lists, G[u]=neighbors of u
    cap is the capacity matrix
    """
    assert s!=t
    q = queue()
    #                                         -- start with empty flow
    total = 0
    flow = [[0 for _ in graph] for _ in graph]
    while True:                                # repeat until no augment poss.
        q.put(s)
        lev = [-1]*n                           # construct levels, -1=unreach.
        lev[s] = 0
        while not q.empty():
            u = q.get()
            for v in graph[u]:
                if lev[v]==-1 and cap[u][v] > flow[u][v]:
                    lev[v]=lev[u]+1
                    q.put(v)

        if lev[t]==-1:                         # stop if target not reachable
            return (total, flow)
        upperBound = sum([cap[s][v] for v in graph[s]])
        total += dinicStep(graph, lev, cap, flow, s,t,upperBound)

def dinicStep(graph, lev, cap, flow, u,t, limit):
    """ tries to push at most limit flow from u to t. Returns how much could
    be pushed.
    """
    if limit<=0:
        return 0
    if u==t:
        return limit
    val=0
    for v in graph[u]:
        res = cap[u][v] - flow[u][v]
        if lev[v]==lev[u]+1 and res>0:
            av = dinic(graph,lev, cap,flow, v,t, min(limit-val, res))
            flow[u][v] += av
            flow[v][u] -= av
            val += av
    if val==0:
        lev[u]=-1
    return val
