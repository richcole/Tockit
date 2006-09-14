package multivalent.std.adaptor.pdf;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.*;

import multivalent.ParseException;



/**
	Encryption handler: instantiate subclass according to Filter.
	Encryption applies to all strings and streams in the document's PDF file, but not to other object types.

	@see SecurityHandlerStandard
	@see SecurityHandlerPublicKey
	@version $Revision$ $Date$
*/
public class Encrypt {
  private static Map handlers_ = new HashMap(7);
  static {
	registerSecurityHandler("Standard", "multivalent.std.adaptor.pdf.SecurityHandlerStandard");
	//registerSecurityHandler("PublicKey", "multivalent.std.adaptor.pdf.EncryptPublicKey");
  }

  /**
	Registers the security handler <var>classname</var>, which must be a subclass of Encrypt, for <var>filter</var>.
	For example, Adobe's standard security handler is automatically registered,
	with the call <code>registerFilter("Standard", "multivalent.std.adaptor.pdf.SecurityHandlerStandard")</code>.
  */
  public static void registerSecurityHandler(String filterName, String classname) {
	handlers_.put(filterName, classname);
	// classname as String so class loaded on demand
  }

  private static Class[] SH_SIG = { Map.class, PDFReader.class };


  private String Filter_ = null;
  private String SubFilter_ = null;
  private Map CF_ = null;
  private CryptFilter StmF_ = CryptFilter.IDENTITY;
  private CryptFilter StrF_ = CryptFilter.IDENTITY;
  private int V_ = -1;

  private Map cache_ = null;


  /**
	Constructs a new encryption object from parameters in an encryption dictionary.
	The <var>trailer</var> is needed for <code>/ID</code>, which must exist.
  */
  public Encrypt(Map edict, PDFReader pdfr) {
	if (edict == null) {
		// no encryption

	} else {

		Object o = edict.get("V");
		V_ = o!=null? ((Number)o).intValue(): 0; assert V_>=0 && V_<=4: V_;

		Filter_ = (String)edict.get("Filter");
		SubFilter_ = (String)edict.get("SubFilter");
		if (V_<4) {
			SecurityHandler sh = getSecurityHandler(Filter_, edict, pdfr);
			StrF_ = StmF_ = new CryptFilter(null, sh, pdfr);

		} else if (V_>=4) {
			CF_ = (o = edict.get("CF")) != null? (Map)o: new HashMap(5);
			/*for (Iterator i = CF_.entrySet().iterator(); i.hasNext(); ) {
				// collect parts before turning on encryption
			}*/

			cache_ = new HashMap(7);
			cache_.put("Identity", CryptFilter.IDENTITY);     // set standard filters and at the same time disallow redefinition of standard
			StmF_ = getCryptFilter((String)edict.get("StmF"), pdfr);
			StrF_ = getCryptFilter((String)edict.get("StrF"), pdfr);
		}
	}

// forall f in CF: if DocOpen then
	//getStrF().authOwner(""); getStrF().authUser("");

  }

  /**
	Returns Filter name.
	@return null iff no encryption
  */
  public String getFilter() { return Filter_; }
  /** Returns SubFilter name. */
  public String getSubFilter() { return SubFilter_; }
  public CryptFilter getStmF() { return StmF_; }
  public CryptFilter getStrF() { return StrF_; }

  /** Returns algorithm code. */
  public int getV() { return V_; }


  /**
	Returns crypt filter of given <var>name</var>.
  */
  public CryptFilter getCryptFilter(String name, PDFReader pdfr) /*throws IOException*/ {
	//String filter = (String)/*not IRef/getObject since those objects would be encrypted!*/edict.get("Filter"); if (filter==null) filter="Standard";
	if (null==name) name="Identity";
	CryptFilter cf = (CryptFilter)cache_.get(name); if (cf!=null) return cf;

	Map cfdict = (Map)CF_.get(name);
	//if (cfdict==null) throw new Exception();
	SecurityHandler sh = getSecurityHandler(getFilter(), cfdict, pdfr);
	return new CryptFilter(cfdict, sh, pdfr);
  }


  /**
	{@link java.lang.reflect.Constructor#newInstance(Object[])} for a description of the exceptions thrown.
	@throws UnsupportedOperationException if filter not registered
	@throws ClassNotFoundException if class name registered to filter is not the CLASSPATH
	@throws ParseException if can't read parameters for filter
  */
  private SecurityHandler getSecurityHandler(String filter, Map shdict, PDFReader pdfr) {
	if ("Identity".equals(filter) || null==filter) return SecurityHandler.IDENTITY;

	SecurityHandler sh = null;
	String className = (String)handlers_.get(filter);
	//if (className==null) { System.out.println("unknown filter: "+filter); System.exit(1); }
	Exception e = null; String emsg = null;

	if (className == null) { emsg="Unregistered filter"; e = new UnsupportedOperationException(); }
	else try {
		Class cl = Class.forName(className);
		Constructor con = cl.getConstructor(SH_SIG);
		sh = (SecurityHandler)con.newInstance(new Object[] { shdict, pdfr});

// ParseException UnsupportedOperationException
	} catch (ClassNotFoundException cnfe) { emsg = "class "+className+" not found -- is it in CLASSPATH?"; e=cnfe; }
	catch (NoSuchMethodException nsme) { emsg = "need constructor "+className + "(Map, PDFReader)"; e=nsme; }
	catch (IllegalAccessException iae) { emsg = className+" must be public"; e=iae; }
	catch (InstantiationException ie) { emsg = className+" must be public and non-abstract"; e=ie; }
	catch (InvocationTargetException ite) { emsg = "error in "+className+"'s constructor"; e=ite; }

	if (emsg!=null) {
		System.err.println("couldn't make security handler "+filter+": "+emsg);
		e.printStackTrace();
		//throw e;
	}

	//sh.Filter_ = filter;
	//sh.init(edict, pdfr);

	// null password is automatic
	// fast enough to automatically try all 1- and, if 40-bit, 2-letter passwords, without needing to ask
	//for (int letter=0; letter<256 && !encrypt_.isAuthorized(); letter++) setPassword(Character.toString((char)letter));

	return sh;
  }

  /**
	Returns a representation of this object as a PDF dictionary.
	Used by PDFWriter to write out unfamiliar security handlers.
  */
  public Map toDictionary() {
	if (getFilter()==null) return null;
	Map dict = new HashMap(13);

	dict.put("Filter", Filter_);
	if (SubFilter_!=null) dict.put("SubFilter", SubFilter_);
	if (V_!=0) dict.put("V", new Integer(V_));
	if (V_<4) {
		//if ((V_==2 || V_==3) && Length_!=40) dict.put("Length", new Integer(Length_)); => done in merge
		// merge sh.toDictionary() with own
		dict.putAll(getStrF().toDictionary());

	} else if (V_>=4) {
		if (CF_.size() > 1/*Identity*/) {
			dict.put("CF", CF_);
			// foreach sh in CF_ { sh.toDictionary }
		}

		if (!"Identity".equals("StmF")) dict.put("StmF", StmF_.toDictionary());
		if (!"Identity".equals("StrF")) dict.put("StrF", StmF_.toDictionary());
	}

	return dict;
  }
}
