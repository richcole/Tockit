#ifndef SARL_PAIR_H
#define SARL_PAIR_H

#include <sarl/index.h>

struct Sarl_Pair {
  Sarl_Index dom;
  Sarl_Index rng;
};

inline Sarl_Pair sarl_pair(Sarl_Index dom, Sarl_Index rng)
{
  Sarl_Pair pair = { dom, rng };
  return pair;
};

/**
 * return x - y, i.e. compare(x,y) < 0 if x is less than y
 */
inline int sarl_pair_compare(struct Sarl_Pair x, struct Sarl_Pair y)
{
  return (x.dom == y.dom) ? (x.rng - y.rng) : (x.dom - y.dom);
}

#endif
