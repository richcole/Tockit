#ifndef TOCKIT_FCA_SET_IMPL_H
#define TOCKIT_FCA_SET_IMPL_H

#include <set>

struct Set 
{
  typedef std::set<Index> SetImpl;

  SetImpl m_set;
  RefCount m_ref_count;
};

#endif
