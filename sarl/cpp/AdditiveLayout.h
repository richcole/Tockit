#ifndef SARL_CPP_ADDITIVE_LAYOUT_H
#define SARL_CPP_ADDITIVE_LAYOUT_H

extern "C" {
#include "sarl/global.h"
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

#include <iostream>

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

  Index count;
  Index x, y;

  void reset() { 
    count = 0; x = 0; y = 50; 
  };

  void next() 
  {
    ++count;
    Index sign = ((count % 2)*2)-1;
    Index a    = ((count - 1) / 2);

    x = ((0x1 << a) + a) * 50 * sign; 
    y = (0x1 << a) * 50;
    std::cerr << "New vector: x=" << x << ", y=" << y << std::endl;
  };

  Index value_x() 
  {
    return x;
  };
  
  Index value_y()
  {
    return y;
  };

  Index layout(
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
      SetIterator intent = L.ordering().intent(C.value());
      Dx.insert(C.value(), sum(intent, Mx));
      Dx.insert(C.value(), Dx.image(C.value()) + Mx.image(C.value()));
      Dy.insert(C.value(), sum(intent, My));
      Dy.insert(C.value(), Dy.image(C.value()) + My.image(C.value()));
    }

    return SARL_OK;
  }
};

    

#endif
