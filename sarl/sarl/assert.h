#ifndef SARL_ASSERT_H
#define SARL_ASSERT_H

/* 
 * call the function sarl_assert 
 */
#define SARL_ASSERT(expr) \
  if (! (expr) ) sarl_assert((expr), __LINE__, __FILE__, #expr)

/*! 
 * if expr is true then a programming error has occured.  
 */
void sarl_assert(
  bool expr, int line, char const* file, char const* expr_string
);


#endif
