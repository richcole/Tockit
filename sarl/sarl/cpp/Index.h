#ifndef SARL_CPP_INDEX_H
#define SARL_CPP_INDEX_H

extern "C" {
#include <sarl/index.h>
}

#if SWIG // Swig is quite dumb so we have to tell it explicitly about Index
typedef unsigned int Index;
#else
typedef Sarl_Index Index;
#endif

#endif
