#include <sarl/cpp/Lattice.h>
#include <sarl/cpp/Context.h>
#include <sarl/cpp/ContextIterator.h>

main()
{
  /* load a test lattice */

  Context K1;
  ContextIterator K1_it = K1;
  LatticeIterator L_it = K1_it;
  Lattice L1 = L_it;
  Lattice L2 = L1.copy();  /* should recalculate the lattice */

  /* assert that L1 and L2 have the same structure */
};
