#ifndef SARL_RELATION_IMPL_H
#define SARL_RELATION_IMPL_H

#include <sarl/pair.h>
#include <sarl/relation_iterator.h>

#include <set>

struct Sarl_Pair_less
{
  inline bool operator()(Sarl_Pair const& x, Sarl_Pair const& y) {
    return sarl_pair_compare(x,y) < 0;
  }
};

struct Sarl_Relation
{
  Sarl_RefCount m_ref_count;

  typedef std::set<Sarl_Pair,Sarl_Pair_less> Sarl_RelationImpl;

  Sarl_RelationImpl forward;
  Sarl_RelationImpl reverse;
};

#endif
