#ifndef SARL_LATTICE_IMPL_H
#define SARL_LATTICE_IMPL_H

extern "C" {
  #include <sarl/ref_count.h>
}
  
struct Sarl_Lattice  {
  Sarl_RefCount ref_count;

  struct Sarl_Relation*           intent;
  struct Sarl_Relation*           extent;
  struct Sarl_Relation*           intent_contingent;
  struct Sarl_Relation*           extent_contingent;
  struct Sarl_TransitiveRelation* order;
};

inline void sarl_lattice_init(struct Sarl_Lattice* it)
{
  sarl_ref_count_init(&it->ref_count);
  it->intent = 0;
  it->extent = 0;
  it->order  = 0;
};

#endif
