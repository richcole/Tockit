#ifndef SARL_NARY_RELATION_IMPL_H
#define SARL_NARY_RELATION_IMPL_H

#include <vector>

struct Sarl_Map;

struct Sarl_NaryRelation
{
  Sarl_RefCount ref_count;

  std::vector<Sarl_Map*> maps;
  Sarl_Index arity;
  Sarl_Index count;
};

#endif
