import SetIteratorFunctions;

class TestSarl extends SetIteratorFunctions
{
  static {
    try {
      System.loadLibrary("sarl_java");
    } catch (UnsatisfiedLinkError e) {
      System.err.println(
          "Native code library failed to load. See "
          + "the chapter on Dynamic Linking Problems "
          + "in the SWIG Java documentation for help.\n" + e
      );
      System.err.println("java.library.path=" 
         + System.getProperty("java.library.path"));
      System.exit(1);
    }
  }

  public static void test() {
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

    SetIterator it_a = new SetIterator(A);
    SetIterator it_b = new SetIterator(B);
    
    SetIterator it = meet(it_a, it_b);

    if ( it.count() != LEN/6 ) {
      System.out.println("Error, it.count() != " + LEN/6);
    }

  }

  public static void main(String argv[])
  {
    test();
    System.gc();
  }

}
