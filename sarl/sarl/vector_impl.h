#ifndef SARL_VECTOR_IMPL_H
#define SARL_VECTOR_IMPL_H

extern "C" {
#include "index.h"
#include "ref_count.h"
}


#include <vector>


struct Sarl_Vector 
{
  Sarl_RefCount ref_count;

  Sarl_Index              arity;
  std::vector<Sarl_Index> elements;
};


#endif
