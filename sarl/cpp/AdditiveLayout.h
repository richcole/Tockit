#ifndef SARL_CPP_ADDITIVE_LAYOUT_H
#define SARL_CPP_ADDITIVE_LAYOUT_H

extern "C" {
#include "sarl/config.h"
#include "sarl/read_cxt_context.h"
}

#include <sarl/cpp/Index.h>
#include <sarl/cpp/InputStream.h>
#include <sarl/cpp/OutputStream.h>
#include <sarl/cpp/Dictionary.h>
#include <sarl/cpp/String.h>
#include <sarl/cpp/Context.h>
#include <sarl/cpp/Map.h>
#include <sarl/cpp/Lattice.h>

class AdditiveLayout {
public:
  AdditiveLayout() {
    reset();
  };

  Index sum(SetIterator& S, Map const& D) 
  {
    Index result = 0;
    SARL_FOR(S) {
      result += D.image(S.value());
    }
    return result;
  }

  int count;
  Index x, y;

  void reset() { 
    count = 0; x = 0; y = 10; 
  };

  void next() 
  {
    ++count;
    x = (count % 2 ? 1 : -1) * 10;
    y = ((count / 2) + 1) * 10;
  };

  Index value_x() 
  {
    return x;
  };
  
  Index value_y()
  {
    return y;
  };

  int layout(
    Lattice const& L, 
    Map& Dx, 
    Map& Dy
  ) 
  {
    SetIterator M = L.meet_irreducibles();
    Map Mx, My;

    reset();
    SARL_FOR(M) {
      Mx.insert(M.value(), value_x());
      My.insert(M.value(), value_y());
      next();
    }
      
    SetIterator C = L.concepts();
    SARL_FOR(C) {
      SetIterator intent = L.concept_intent(C.value());
      Dx.insert(C.value(), sum(intent, Mx));
      Dy.insert(C.value(), sum(intent, My));
    }

    return SARL_OK;
  }
};

    

#endif
