#ifndef SARL_PAIR_H
#define SARL_PAIR_H

#include <sarl/index.h>

struct Pair {
  Index dom;
  Index rng;
};

/**
 * return x - y, i.e. compare(x,y) < 0 if x is less than y
 */
inline int sarl_pair_compare(struct Pair x, struct Pair y)
{
  return (x.dom == y.dom) ? (x.rng - y.rng) : (x.dom - y.dom);
}

#endif
