#ifndef SARL_PERM_H
#define SARL_PERM_H


/**
 * Implement an element of a triadic relation. The members are (r,g,m) 
 * where g stands conceptually for object, r for relation, and m 
 * for attribute.
 */

enum Sarl_Perm {
  1_2_3 = 0, 
  2_1_3 = 1, 
  3_2_1 = 2, 
  3_1_2 = 3,
  1_3_2 = 4,
  2_3_1 = 5 
};

inline Sarl_Perm sarl_perm(Sarl_Index r, Sarl_index g, Sarl_Index m)
{
  Sarl_Perm perm = { g, r, m };
  return perm;
};

inline Sarl_Perm[2] sarl_perm_reduce(Sarl_Perm perm)
{
  static Sarl_Perm[2][6] = {
    {0, 0},
    {1, 0},
    {2, 0},
    {1, 2},
    {5, 2},
    {2, 1}
  }
}

/**
 * return x - y, i.e. compare(x,y) < 0 if x is less than y
 */
inline int sarl_perm_compare(
  struct Sarl_Perm x, struct Sarl_Perm y)
{
  return (x.r == y.r) 
    ? (x.g == y.g 
      ? (x.m - y.m) 
      : (x.g - y.g)) 
    : (x.r - y.r);
}

#endif
