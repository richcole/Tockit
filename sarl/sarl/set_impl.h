#ifndef TOCKIT_FCA_SET_IMPL_H
#define TOCKIT_FCA_SET_IMPL_H

#include <set>

struct Sarl_Set 
{
  typedef std::set<Sarl_Index> Sarl_SetImpl;

  Sarl_SetImpl  set;
  Sarl_RefCount ref_count;
};

#endif
