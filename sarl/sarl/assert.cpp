
extern "C" {
  #include "assert.h"
}

#include <stdlib.h> // include abort()
#include <stdio.h>  // fprintf and stderr

void sarl_assert(
  bool expr, int line, char const* file, char const* expr_string
)
{
  fprintf(
    stderr, 
    "%s:%d:\n    Sarl Assertion Failure:%s", 
    file, line, expr_string
  );
  abort();
};
