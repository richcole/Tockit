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
    x = (count % 2 ? 1 : -1) * 50;
    y = (((count - 1) / 2) + 1) * 50;
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
      std::cerr << "m=" << M.value() << ", x=" << value_x();
      std::cerr << ", y=" << value_y() << std::endl;
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
      std::cerr << "c=" << C.value() << ", x=" << sum(intent, Mx);
      std::cerr << ", y=" << sum(intent, My) << std::endl;
      std::cerr << "c=" << C.value() << ", up_set=(";
      for(intent.reset(); !intent.at_end(); ) {
        std::cerr << intent.value();
        if ( intent.next(), ! intent.at_end() ) {
          std::cerr << ", ";
        }
      }
      std::cerr << ")" << std::endl;
    }

    return SARL_OK;
  }
};

    

#endif
