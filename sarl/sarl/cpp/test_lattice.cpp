#include <sarl/cpp/Lattice.h>

main()
{
  /* load a test lattice */

  Lattice L1;
  Lattice L2 = L1->copy();  /* should recalculate the lattice */

  /* assert that L1 and L2 have the same structure */
};
