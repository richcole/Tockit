#ifndef SARL_CPP_PAIR_H
#define SARL_CPP_PAIR_H

typedef Sarl_Pair Pair;

inline bool operator!=(Pair const& s, Pair const& t) {
  return (s.dom != t.dom) || (s.rng != t.rng);
};

#endif
