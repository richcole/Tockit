/*
 * Author: andreasplueschke@web.de
 * Date  : July 12, 2002
 */
public class test_sarl
{

  // Load libsarl.so (for Linux)
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

  // Proxy class for the sarl set interface
  class SarlSet {

    long setRef;

    public SarlSet() {
      this.setRef = sarl.sarl_set_create();
    }

    private SarlSet(long setRef) {
      this.setRef = setRef;
    }
        
    protected void finalize() throws Throwable{
      super.finalize();
      sarl.sarl_set_decr_ref(setRef);
    }

    public void insert(long i) {
      sarl.sarl_set_insert(setRef, i);
    }

    public void remove(long i) {
      sarl.sarl_set_insert(setRef, i);
    }

    public Object clone() {
      SarlSetIterator it = new SarlSetIterator(setRef);
      return new SarlSet(
	sarl.sarl_set_copy(it.itRef)
      );
    }

  }

  // Proxy class for the sarl set iterator interface
  class SarlSetIterator {
        
    public long itRef;
        
    public SarlSetIterator(SarlSet set) {
      itRef = sarl.sarl_set_iterator_create(set.setRef);
    }

    private SarlSetIterator(long itRef) {
      this.itRef = itRef;
    }

    protected void finalize() throws Throwable {
      super.finalize();
      sarl.sarl_set_iterator_decr_ref(itRef);
    }

    public void reset() {
      sarl.sarl_set_iterator_reset(itRef);
    }

    public boolean at_end() {
      return (sarl.sarl_set_iterator_at_end(itRef) != 0);
    }

    public void next() {
      sarl.sarl_set_iterator_next(itRef);
    }

    public void next_gte(int i) {
      sarl.sarl_set_iterator_next_gte(itRef, i);
    }

    public long val() {
      return sarl.sarl_set_iterator_val(itRef);
    }

    public Object clone() {
      return new SarlSetIterator(
	sarl.sarl_set_iterator_copy(itRef)
      );
    }
  }

  // The same test as for test_sarl.py
  void runTest() {

    SarlSet set = new SarlSet();

    int i;

    for (i=1; i < 10; i++) {
      set.insert(i);
    }

    SarlSetIterator it = new SarlSetIterator(set);

    while(!it.at_end()) {
      System.out.println(it.val());
      it.next();
    }

    it.reset();

    while(!it.at_end()) {
      System.out.println(it.val());
      it.next();
    }

    // enforce finalization of objects, otherwise it might be, that we do not
    // see the effect of finalization since the Java specification does not 
    // guarantees the finalization directly after the last reference on an object
    // has been lost
    try {
      it.finalize();
      set.finalize();
    } catch(Throwable any) {};

  }


  public static void main(String argv[]) {

    test_sarl test = new test_sarl();

    test.runTest();

  }
     
}
