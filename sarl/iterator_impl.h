#ifndef SARL_ITERATOR_IMPL_H
#define SARL_ITERATOR_IMPL_H

extern "C" {
  #include <sarl/ownership.h>
  #include <sarl/ref_count.h>
}

struct Sarl_Iterator
{
  Sarl_RefCount                  ref_count;
  Sarl_Ownership                 ownership;
};

#endif
