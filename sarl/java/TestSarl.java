import SetIterator;
import Set;
import sarl;

class TestSarl
{
  static {
    try {
      System.loadLibrary("sarl");
    } catch (UnsatisfiedLinkError e) {
      System.err.println(
	"Native code library failed to load. See "
	+ "the chapter on Dynamic Linking Problems "
	+ "in the SWIG Java documentation for help.\n" + e
      );
      System.exit(1);
    }
  }

  public static void main(String argv[]) 
  {
    Set A = new Set();
    Set B = new Set();

    int i;
    int LEN=100;

    for (i=1; i < LEN; i++) {
      if ( i % 2 == 0 ) {
	A.insert(i);
      }
      if ( i % 3 == 0 ) {
	B.insert(i);
      }
    }

    SetIterator tmp_it_A = new SetIterator(A);
    SetIterator tmp_it_B = new SetIterator(B);
    SetIterator it = tmp_it_A.iterator_meet(
      tmp_it_B
    );
    
    if ( it.count() != LEN/6 ) {
      System.out.println("Error, it.count() != " + LEN/6);
    }

    // enforce finalization of objects, otherwise it might be, that we do not
    // see the effect of finalization since the Java specification does not
    // guarantees the finalization directly after the last reference on an
    // object has been lost

    try {
      it.finalize();
      A.finalize();
      B.finalize();
      tmp_it_A.finalize();
      tmp_it_B.finalize();
    } catch(Throwable any) {};


  }
  
}
