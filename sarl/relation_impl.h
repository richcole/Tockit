#ifndef SARL_RELATION_IMPL_H
#define SARL_RELATION_IMPL_H

#include <sarl/pair.h>
#include <sarl/relation_iterator.h>

#include <set>

struct Pair_less
{
  inline bool operator()(Pair const& x, Pair const& y) {
    return sarl_pair_compare(x,y) < 0;
  }
};

struct Relation
{
  RefCount m_ref_count;

  typedef std::set<Pair,Pair_less> RelationImpl;

  RelationImpl forward;
  RelationImpl reverse;
};

#endif
