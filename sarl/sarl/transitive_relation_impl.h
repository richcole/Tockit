#ifndef SARL_TRANSITIVE_RELATION_IMPL_H
#define SARL_TRANSITIVE_RELATION_IMPL_H

#include <sarl/pair.h>
#include <sarl/relation_impl.h>

struct Sarl_TransitiveRelation 
{
  Sarl_RefCount ref_count;

  Sarl_Relation* ordering;
  Sarl_Relation* covering;
};

#endif
