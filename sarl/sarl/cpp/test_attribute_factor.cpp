#include <sarl/cpp/Lattice.h>
#include <sarl/cpp/LatticeIterator.h>
#include <sarl/cpp/ContextReader.h>
#include <sarl/test.h>

main()
{
  String B3_filename = "b3.cxt";
  String B2_filename = "b2.cxt";
  Lattice B3 = ContextReader::read_cxt_lattice_from_file(B3_filename);
  Lattice B2 = ContextReader::read_cxt_lattice_from_file(B2_filename);

  LatticeIterator B3_it = B3;

  SARL_TEST_ASSERT_EQ(B3_it.count(), 8);

  B3_it.reset_last();

  SetIterator M = B3_it.intent();
  SARL_TEST_ASSERT_EQ(M.count(), 3);

  Set A;
  int i;
  for(i=0;i<2;i++) {
    A.insert(M.value());
    M.next();
  }

  SARL_TEST_ASSERT_EQ(SetIterator(A).count(), 2);

  LatticeIterator F_it = B3_it.attribute_factor(SetIterator(A));
  SARL_TEST_ASSERT_EQ(F_it.count(), 4);

};
  
