#ifndef SARL_TRIPLE_H
#define SARL_TRIPLE_H

#include <sarl/index.h>

/**
 * Implement an element of a triadic relation. The members are (r,g,m) 
 * where g stands conceptually for object, r for relation, and m 
 * for attribute.
 */

struct Sarl_Triple {
  Sarl_Index r; 
  Sarl_Index g; 
  Sarl_Index m; 
};

inline Sarl_Triple sarl_triple(Sarl_Index r, Sarl_index g, Sarl_Index m)
{
  Sarl_Triple triple = { g, r, m };
  return triple;
};

/**
 * return x - y, i.e. compare(x,y) < 0 if x is less than y
 */
inline int sarl_triple_compare(
  struct Sarl_Triple x, struct Sarl_Triple y)
{
  return (x.r == y.r) 
    ? (x.g == y.g 
      ? (x.m - y.m) 
      : (x.g - y.g)) 
    : (x.r - y.r);
}

#endif
