#!/usr/bin/python
# -*- coding: utf-8 -*-
# google code jam - c.durr - 2012

# tas binaire
#


class minHeap:
    """ tas binaire
    heap est le tas, heap[1] = l'indice de l'élément le plus petit
    rank est l'inverse rank[x]=i tel que heap[i]=x
    prio est la priorité
    n la taille du tas
    """

    def up(s, x):
        """la valeur s.prio[x] vient de diminuer. Maintenir l'invariant du tas"""
        i = s.rank[x]
        while i>1 and s.prio[x]<s.prio[s.heap[i/2]]:
            s.heap[i] = s.heap[i/2] 
            s.rank[s.heap[i]]=i
            i/=2
        s.heap[i]=x   # --- found insertion point
        s.rank[x]=i

    def down(s, x):
        """la valeur s.prio[p] vient d'augmenter. Maintenir l'invariant du tas"""
        i = s.rank[p]
        while True:
            left = 2*i
            right= left+1
            if right<=s.n and s.prio[s.heap[right]] < s.prio[x] \
                          and s.prio[s.heap[right]] < s.prio[s.heap[left]]:
                s.heap[i] = s.heap[right]
                s.rank[s.heap[i]] = i
                i = right
            elif left<=s.n and s.prio[s.heap[left]] < s.prio[x]:
                s.heap[i] = s.heap[left]
                s.rank[s.heap[i]]=i
                i = left
            else:
                s.heap[i] = x  # --- found insertion point
                s.rank[x] = i
                return
    
    def push(s, x):
        s.n += 1
        # make sure that n does not exceed size of heap
        s.heap[s.n] = x
        s.rank[x]   = s.n
        s.up(x)

    def pop(s):
        ret = s.heap[1]
        s.heap[1] = s.heap[s.n]
        s.rank[s.heap[1]] = 1
        s.n -= 1
        s.down(s.heap[1])
        return ret
        
    def __init__(s, p):
        s.n = 0
        N = len(p)+1
        s.heap = [0] * N
        s.rank = [0] * N
        s.prio = p
        for x in range(len(p)):
            s.push(x)

    def size(s):
        return s.n


