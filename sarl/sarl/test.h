#ifndef SARL_TEST_H
#define SARL_TEST_H

#include <stdio.h>
#include <stdlib.h>

#define SARL_TEST_ASSERT_STRING(file, line, expr) \
  { \
    if ( ! (expr) ) { \
      fprintf(stderr, "%s:%d: Assert failed, '%s'\n", \
        __FILE__, __LINE__, #expr); \
        exit(-1); \
    }; \
  }; 

#define SARL_TEST_ASSERT(expr) \
  SARL_TEST_ASSERT_STRING(__FILE__, __LINE__, expr)

#define SARL_TEST_ASSERT_EQ(expr1, expr2) \
  { \
    if ( (expr1) != (expr2) ) { \
      fprintf(stderr, "%s:%d: Assert eq failed, " \
        #expr1 "=%d, " #expr2 "=%d\n", __FILE__, __LINE__, \
        (int)expr1, (int)expr2 \
      ); \
      exit(-1); \
    }; \
  }; \

#endif
