#include <sarl/cpp/InputStringStream.h>
#include <sarl/cpp/Lattice.h>
#include <sarl/cpp/LatticeIterator.h>
#include <sarl/cpp/ContextReader.h>
#include <sarl/cpp/SetIterator.h>

extern "C" 
{
#include <sarl/test.h>
}


main()
{
  String b3_context = 
    "B\n"
    "B3\n"
    "3\n"
    "3\n"
    "A\n"
    "B\n"
    "C\n"
    "1\n"
    "2\n"
    "3\n"
    ".xx\n"
    "x.x\n"
    "xx.\n";

  String b2_context = 
    "B\n"
    "B2\n"
    "2\n"
    "3\n"
    "A\n"
    "B\n"
    "1\n"
    "2\n"
    "3\n"
    ".xx\n"
    "x.x\n";

  Context         K;
  LatticeIterator L_it(K);
  Lattice         B3   = L_it;
  Lattice         B2   = L_it;
  String     title;
  Set        G_s; 
  Dictionary Dict;
  
  String            err_string;
  OutputStream      err_stream(err_string);
  
  ContextReader     reader;

  String            b3_string = b3_context;
  InputStringStream b3_stream = b3_string;

  reader.read_cxt(
    b3_stream,
    B3, title, Dict, Dict, 
    err_stream
  );

  String            b2_string = b2_context;
  InputStringStream b2_stream = b2_string;

  reader.read_cxt(
    b2_stream,
    B2, title, Dict, Dict, 
    err_stream
  );

  G_s.insert(Dict.get_index("A"));
  G_s.insert(Dict.get_index("B"));
  SetIterator G_s_it = G_s;
  
  LatticeIterator F = object_factor(B3, G_s_it);
  LatticeIterator B2_it = B2;
  
  int count = 0;
  for(
    F.reset(), B2_it.reset(); 
    ! F.at_end() && ! B2_it.at_end(); 
    F.next(), B2_it.next()
  )
  {
    SetIterator B2_extent = B2_it.extent();
    SetIterator B2_intent = B2_it.intent();
    SARL_TEST_ASSERT(F.extent().eq(B2_extent));
    SARL_TEST_ASSERT(F.intent().eq(B2_intent));
    ++count;
  }
  SARL_TEST_ASSERT(F.at_end() == B2_it.at_end());

  // test that there were four concepts in the factor
  SARL_TEST_ASSERT_EQ(count, 4);

  return 0;
};

      
  
