#include <sarl/cpp/Lattice.h>
#include <sarl/cpp/ContextReader.h>
#include <sarl/cpp/OutputStream.h>
#include <sarl/cpp/InputStream.h>

#include <iostream>

using namespace std;

void usage()
{
  cerr << "proto_concepts <context>" << endl;
  exit(-1);
};

void print_set(SetIterator& S, Dictionary& D, ostream& out)
{
  S.reset();
  while( ! S.at_end() ) {
    out << D.get_string(S.value()).get_chars();
    S.next();
    if (! S.at_end()) {
      out << ", ";
    }
  };
};

main(int argc, char **argv)
{
  if ( argc != 2 ) {
    usage();
  };

  Dictionary D;
  Context    K;
  String     title;

  char const* filename = argv[1];
  InputFileStream in = String(filename);

  String          errors;
  OutputStream    err_stream = errors;
  
  if ( ContextReader::read_cxt(in, K, title, D, D, err_stream) != SARL_OK ) {
    cerr << "Error, unable to read '" << filename << "' there were errors:";
    cerr << endl;
    cerr << "  " << errors.get_chars() << endl;
    exit(-1);
  };

  ContextIterator K_it = K;
  SetIterator     A, B;
  SetIterator     G    = K_it.objects();
  SetIterator     M    = K_it.attributes();
  
  A = SetIterator::empty();
  do {
    SetIterator A_dash = K_it.intent(A);
    cout << "A={"; print_set(A, D, cout); cout << "}" << endl;
    cout << "A'={"; print_set(A_dash, D, cout); cout << "}" << endl;

    A = lectic_next(A, G);
  } while ( ! A.is_empty() );
  
  B = SetIterator::empty();
  do {
    SetIterator B_dash = K_it.extent(B);
    cout << "B={"; print_set(B, D, cout); cout << "}" << endl;
    cout << "B'={"; print_set(B_dash, D, cout); cout << "}" << endl;

    B = lectic_next(B, G);
  } while ( ! B.is_empty() );
};

    
    
    
  
  

