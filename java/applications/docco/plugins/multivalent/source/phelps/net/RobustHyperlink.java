/*

Robust Hyperlinks
Written by Tom Phelps (phelps@ACM.org)

Open Source software.
Permission to use, copy, modify, and distribute this software and its
documentation for any purpose, without fee, and without a written
agreement, is hereby granted.

*/

package phelps.net;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.HttpURLConnection;
import java.util.*;
import java.io.*;

// need to parse HTML
import multivalent.std.adaptor.HTML;
import multivalent.Node;
import multivalent.INode;
import multivalent.Document;
import multivalent.Multivalent;
//import multivalent.Browser;



/*
class SigningException extends Exception {
  SigningException() { super(); }
  SigningException(String s) { super(s); }
}*/


/**
	Augment URL with information that can be used to find content of URL in case link breaks.
	See the <a href='http://www.cs.berkeley.edu/~phelps/Robust'>Robust Home Page</a>.
	Runnable as an application to crawl site and rewrite HTML pages to make A HREF URLs robust automatically
	(see {@link #main(java.lang.String[]) instructions}).

	<p>Strategy:
		Inverse word frequency: find top n most common words in document that are uncommon in web overall.
		<ol>
		<li>count words in page (either from tree or, while testing, HTML text)
		<li>look up relative frequency counts from web search engine, cacheing new ones to disk
		<li>pick locally frequent-globally infrequent
		</ol>

	<p>Web word freqencies are obtained by screen scraping the results of a search engine.
	Unfortunately, search engines change the URL of search submissions and the template of the results regularly,
	so we need up update {@link #Engine} and {@link #EngineHook} regularly.

	<p>TO DO
	<ul>
	<li>Don't make everything static so can to speed up singings by doing in parallel.
	<li>Checkpoint rewrites (Mark, write rest, back to Mark, but no marks on write) so don't have to sign your huge
		bookmarks list on one go.  Report bad links to specified file.
	<li>extract frequency from page tree as constructed by HTML media adaptor, so fewer distracting numbers
	</ul>

	@author T.A. Phelps
	@version $Revision$ $Date$
*/
public class RobustHyperlink /* not a Behavior--utility, like Location *//*implements EventListener*/ {
  public static boolean DEBUG = true;

  static final String USAGE =
	"Usage: java phelps.net.RobustHyperlink [<options>] <URL> [<filename>]\n" +
	"Options: -force, -siglen <num>, -stdout\n" +
	"\t-algorithm (tdidf|tdidf+|rarest|random|random100k)\n" +
	"\t-preservecase, -minwordlen <num>\n"+
	"\t-verbose, -quiet, -studydata, -help, -version\n"+
	"More information at http://www.cs.berkeley.edu/~phelps/Robust/";
  public static final double VERSION = 0.2;


  public static boolean Verbose = true;
  public static boolean SignQueries = false;
  public static boolean firstNew = false;

  /** Canonical definition of parameter used for lexical signatures. */
  public static final String PARAMETER = "lexical-signature=";
  static final int DBVERS = 3;	// bump up if need to make changes to format, change frequency source, and so on

  static File wordCache = null;
  /**
	Client can set the file to use as the user's supplemental word frequency cache.
	The Multivalent client places this in user's private cache directory, as public placement can reveal personal information.
	Defaults to a file named "wordfreq.txt" in the Java temp directory.
  */
  public static void setWordCache(File cache) { wordCache=cache; }


  // take Engine and EngineHook from preferences or command line option
  //static String Engine = "http://www.google.com/search?num=1&q=";
	//"http://www.google.com/search?q=";
  //static String EngineHook = "about";	//"approximately"; //"About"
  /** URL of search engine search request, to which the search term can be appended. */
  public static String Engine =
   // "http://www.altavista.com/cgi-bin/query?pg=q&kl=XX&stype=stext&sc=on&q=";
	//"http://www.altavista.com/cgi-bin/query?pg=q&kl=XX&Translate=on&q=";
	"http://www.altavista.com/web/results?kgs=0&kls=1&avkw=qtrp&q=";

  // should search for search word and take closest number in text, within some limit
  /** Text to find in search engine results page next to word frequency. */
  public static String EngineHook =
	//from "pages found."; on 2001 June 9
	// changed from "WEB PAGES" on 2000 June 27
	// changed from "Web Pages" by 2000 September 21
	//on 5Jul ... //"found about";
	//"We found";
	"AltaVista found "; // 2002 Nov 17

  /** Amount of search engine results page guaranteed to include word frequency. */
  static int HUNK_LEN =
	//13 * 1024;
	30 * 1024;  // 2002 Nov 17 / AltaVista

  /** Set the search engine and key text fragment that signals the start of the web frequency information. */
  public static void setEngine(String prefix, String freqkey) { Engine=prefix; EngineHook=freqkey; }

  /** Ignore case in collecting words? */
  public static boolean FoldCase = true;
  public static boolean IgnoreBuiltin = false;
  public static int MinWordLength = 4;
  /** A good signature length (in words). */
  public static int SignatureLength = 10;
  /** List of implemented algorithms */
  public static final String[] ALGORITHMS = { "tfidf+", "tfidf", "rarest", "random", "random100k" };
  static String Algorithm = ALGORITHMS[0];
  //static boolean Concise = false;
  /** Set algorithm to use (nb: static). */
  public static boolean setAlgorithm(String alg) {
	// validate
	alg = alg.toLowerCase();
	boolean valid = false;
	for (int i=0,imax=ALGORITHMS.length; i<imax; i++) {
		if (ALGORITHMS[i].equals(alg)) { Algorithm = alg; valid=true; break; }
	}
	return valid;
  }
  public static String getAlgorithm() { return Algorithm; }

/*  public static final String TrimChars;   // "/ .!?,();:$%`'\"@#^=[]{}<>|+*-0123456789"
  static {
	StringBuffer tcsb = new StringBuffer(200);
	//for (int i=' '+1; i<'0'; i++) tcsb.append((char)i);
	//for (int i='9'+1; i<'A'; i++) tcsb.append((char)i);
	for (int i=0; i<'A'; i++) tcsb.append((char)i);
	for (int i='Z'+1; i<'a'; i++) tcsb.append((char)i);
	for (int i='z'+1; i<128; i++) tcsb.append((char)i);
	// also high bit versions?	interferes with accented characters, I bet
	//tcsb.append((char)' '+128);
	for (int i=146; i<148+1; i++) tcsb.append((char)i);
	tcsb.append((char)160);  // &nbsp;
	TrimChars = tcsb.substring(0);
  }*/


  // should compute good values for the following based on searches for words of high, medium, and low frequency
  // 1..MEDFREQ = round to 1000, MEDFREQ..BIGINT = round to 10000, > BIGFREQ = MAXINT
  static final int MEDFREQ =
	//1000000;    // million pretty lax as of Feb 2000, with web at 1 billion pages
	5000000;    // 2002 Nov 17
  static final int BIGFREQ = MEDFREQ*5;
  static final int LILROUND=1000;
  static final int MEDROUND=10000;
  static final Integer MAXINT = new Integer(Integer.MAX_VALUE);
  // shared frequency count objects
  static Integer[] LILINTS = new Integer[MEDFREQ/LILROUND];
  static Integer[] MEDINTS = new Integer[(BIGFREQ-MEDFREQ)/MEDROUND];
  static {
	for (int i=0,imax=LILINTS.length; i<imax; i++) LILINTS[i]=new Integer((i+1)*LILROUND);
	for (int i=0,imax=MEDINTS.length; i<imax; i++) MEDINTS[i]=new Integer((i+1)*MEDROUND+MEDFREQ);
  }

  static Map word2cnt = null;
  static List newwords = new ArrayList(50);

  // words that may not be in word frequency list that we want to be sure to ignore
  static final String[] extras = {
	"lt", "gt", "nbsp", "quot", "meta", "script", "style",
	//"i", "ii", "iii", "iv", "v", "vi", "vii", "viii", "ix", -- too short to be included anyhow
  };


  private RobustHyperlink() { super(); }

  /** Given a URL in String form, return URL with signature, if any, stripped off. */
  public static String stripSignature(String surl) {
	String sig = getSignature(surl);
	if (sig!=null) {
		int inx = surl.indexOf(sig);
		//inx--;	// strip out preceding '?' or '&'
		return surl.substring(0,inx)+surl.substring(inx+sig.length());
	} else return surl;
  }


  /**
	Return signature as found in string.
	Signature <!--can either use priviledged position or be-->is introduced by "lexical-signature=".
  */
  public static String getSignature(String surl) {
	if (surl==null) return null;
	String sig = null;

	int inx = surl.indexOf(PARAMETER);
/*
	if (inx==-1) {
		// implicit?  some scripts have optional names in name-value pairs
		int qinx = surl.indexOf('?');
		if (qinx!=-1) {
			int einx=surl.indexOf('=',qinx), ainx=surl.indexOf('&',qinx);
			if (einx==-1 || (einx>ainx && einx+1<surl.length())) inx=qinx+1;	// priviledged position -- but not "?abstract="
		}
	}*/
	if (inx!=-1) {
		int inx2 = surl.indexOf('&', inx+PARAMETER.length());
		if (inx2==-1) inx2 = surl.indexOf('#', inx+PARAMETER.length());
		inx--; // return preceeding '?' or '&'
		if (inx2==-1) sig=surl.substring(inx); else sig=surl.substring(inx,inx2);
	}

	return sig;
  }


  /** Return signature as plain words: no "?lexical-signature=", no meta characters. */
  public static String getSignatureWords(String surl) {
	String sig = getSignature(surl);
	if (sig!=null) {
		int inx=sig.indexOf(PARAMETER);
		if (inx!=-1) sig = sig.substring(inx+PARAMETER.length());
	}
	if (sig!=null) sig = URIs.decode(sig);

	return sig;
  }


  /** Upon first use (not at system startup), read in cached frequencies. */
  static void readCaches() {
	if (word2cnt!=null) return;
	word2cnt = new HashMap(20000);	// LATER: use weak ref

	// some extras
	//for (int i=-10; i<10; i++) word2cnt.put(Integer.toString(i), MAXINT); -- too short anyhow
	for (int i=1900; i<2050; i++) word2cnt.put(Integer.toString(i), MAXINT);	// years
	for (int i=0,imax=extras.length; i<imax; i++) word2cnt.put(extras[i].toLowerCase(), MAXINT);

	// system word list
	BufferedReader r;
	if (!IgnoreBuiltin) try {
		//long start = System.currentTimeMillis();	// takes 330ms on my machine
		InputStream is = new byRelFreq().getClass().getResourceAsStream("/sys/words.txt");
		if (is!=null) {
			r = new BufferedReader(new InputStreamReader(is));
			r.readLine();	// eat version line
			readCache(r);
			r.close();
		}
		//System.out.println("Took "+(System.currentTimeMillis()-start)+"ms to read system word list");
	} catch (IOException ioe) {
		System.err.println("RobustHyperlink readCache: can't read system word frequency list.   "+ioe);
		// bad, but don't System.exit(1)
	}

	// user word list
	try {
		if (wordCache==null) {
			File tmpfile = File.createTempFile("xxx","yyy");
			wordCache = new File(tmpfile.getParent(), "wordfreq.txt");
//System.out.println("wordCache = "+wordCache);
		}
		//if (wordCache.lastModified() -  would like to refresh the cache annually, but can't get creation date from Java
		r = new BufferedReader(new FileReader(wordCache));

		String line = r.readLine();
		int vers = 0;
		if (line.startsWith("v")) try { vers = Integer.parseInt(line.substring(1)); } catch (NumberFormatException nfe) {}
		if (vers!=DBVERS) {
			r.close();
			wordCache.delete();
			if (DEBUG) System.out.println("old version of user word list: v"+vers+" => deleted");
		} else {
			readCache(r);
			r.close();
		}

	} catch (IOException ioe) {
		//System.err.println("readCache "+ioe); -- OK if user's word list doesn't exist (which it won't the first time)
	}
  }


  static void readCache(BufferedReader r) {
	try {
//System.out.println("reading user cache");
	  String word;
	  while ((word=r.readLine())!=null) {	 // exclusive of '\n'
		int inx = word.indexOf(' '); //if (inx==-1) continue;
		Integer cnto = MAXINT;

		if (inx!=-1) {
			String snum=word.substring(0,inx);
			word=word.substring(inx+1).trim();
			// count
			//if ("!".equals(snum)) cnto = MAXINT;
			//else if ("+".equals(num)) cnto = BIGINT;
			//else {
			if (!"!".equals(snum)) {
				try {
					int num = Integer.parseInt(snum);
					num *= LILROUND; // save space in file
//System.out.println(""+num+", "+LILINTS.length+", "+MEDINTS.length);
					if (num<MEDFREQ) cnto=LILINTS[num/LILROUND]; else cnto=MEDINTS[(num-MEDFREQ)/MEDROUND];
				} catch (NumberFormatException nnfe) {}
			}

//System.out.println(word+"="+cnto);
			word2cnt.put(word, cnto);
		  }
		}
	} catch (IOException ioe) {
		//System.err.println("readCache "+ioe); -- OK if user's word list doesn't exist (which it won't the first time)
		System.err.println(ioe);
	}
  }


  /**
	Update cache by appending new information.  (Updates user list; master list not touched.)
	May want to periodically refresh cache, for words that become popular and therefore no longer good distinguishers.
  */
  static void writeCache() {
	if (DEBUG && newwords.size()>0) System.out.println("writing to "+wordCache);
	//if ((wordCache.exists() && wordCache.canWrite()) return;
	try {
		BufferedWriter out = new BufferedWriter(new FileWriter(wordCache.getAbsolutePath(), true));
		if (wordCache.length()==0) {
			out.write('v');
			String vers=Integer.toString(DBVERS); out.write(vers,0,vers.length());
			out.newLine();
		}
		for (Iterator i=newwords.iterator(); i.hasNext(); ) {
			String word = (String)i.next();
			Integer num = (Integer)word2cnt.get(word);
			if (num==MAXINT) out.write('!');
			//else if (num==BIGINT) out.write('+');
			else {
				String snum = num.toString();
				out.write(snum, 0,snum.length()-3); // num/1000 to save space
			}
			out.write(' ');
			out.write(word, 0,word.length());
			out.newLine();
		}
		out.close();
		newwords.clear();
	} catch (IOException ioe) {
		System.err.println("writeCache "+ioe);
	}
  }


  /** Determine web page frequency of <tt>word</tt>.  If not in cache, looks up in web search engine. */
  public static int getFreq(String word) {
	Integer cnto = (Integer)word2cnt.get(word);
	//if (DEBUG && cnto!=null && cnto!=MAXINT) System.out.println(word+"	"+cnto);
	if (cnto==null) {
		cnto = MAXINT;	// assume very common on WWW
		// look up in search engine -- want to batch these, but Google won't report frequency if more than one word at a time
		try {
			// read in search results
			URL url = new URL(Engine+word);
//System.out.println("url = "+url+", DEBUG="+DEBUG);
			Reader in = new BufferedReader(new InputStreamReader(url.openStream()));
			char[] buf = new char[HUNK_LEN];  // Google needs 2K, Alta Vista 11K
			// frequency information in first 1K chars, but be robust if this changes a bit
			int len=0,hunk;
			while (len<HUNK_LEN && (hunk=in.read(buf, len,buf.length-len))>0) len+=hunk; // should collect entire page, but OK if doesn't
			in.close();
//System.out.println("len = "+len);

			// find count in HTML
			// Google: "<center><font color=darkgray>About <font color=black><b>2720</b></font> matches for <font color=black><b>Multivalent</b></font><br>Showing results <font color=black>1-10</font>,"
			String result = new String(buf, 0,len);
			int inx0=result.indexOf(EngineHook), imax=Math.min(inx0+200,result.length());
//System.out.println(result); System.exit(0);
//System.out.println(inx0+" .. "+imax);
			if (inx0!=-1) {
//System.out.println(word+"   "+result.substring(Math.max(0,inx0-100),inx0)+" *** "+result.substring(inx0,inx0+100));
				char ch; boolean skip=false;
				int num=0, inx=inx0;

				// number after key?
				for ( ; inx<imax && (!Character.isDigit((ch=result.charAt(inx))) || skip); inx++) {
					//if (ch=='"') skip=!skip; // skip over attribute settings (as in Alta Vista)
					//else if (skip && !Character.isDigit(ch) && ch!='#') skip=false;
					if (ch=='<') skip=true; // skip over tags
					else if (skip && ch=='>') skip=false;
				}
				// not after, hope number is before key
				if (inx==imax) {
					inx = inx0; skip=false;
					int imin=Math.max(0,inx0-200);
					for (; inx>imin && (!Character.isDigit((ch=result.charAt(inx))) || skip); inx--) {
						if (ch=='<') skip=true; else if (skip && ch=='>') skip=false;
					}
					imax = inx+1;
					while (inx>imin && (Character.isDigit(ch=result.charAt(inx)) || ch==',')) inx--;
					inx++;	// get back to a digit!
				}
//System.out.println("before? "+inx+".."+imax+": |"+result.substring(inx,imax)+"|");

				// text (possibly with commas) => number
				for (int inx2=inx; inx2<imax; inx2++) {
					ch = result.charAt(inx2);
					if (Character.isDigit(ch)) num = num*10+(((int)ch)-'0');	//isb.append(ch);
					else if (ch==',') { /*skip commas*/ }
					else break;
				}

				if (DEBUG) System.out.print("new word "+word+" => "+num);
				else if (Verbose) {
					if (firstNew) { System.out.print("Fetching words not in caches."); firstNew=false; }
					System.out.print(".");
				}
				if (num==0) cnto=MAXINT; else if (num<MEDFREQ) cnto=LILINTS[num/LILROUND]; else if (num<BIGFREQ) cnto=MEDINTS[(num-MEDFREQ)/MEDROUND]; else cnto=MAXINT;
				//if (cnto.compareTo(BIGINT)==1) cnto=MAXINT;
				//else if (cnto.intValue()>MEDFREQ) cnto=BIGINT;
				if (DEBUG) System.out.println("->"+cnto);
			}
//		} catch (IOException e) {
//		} catch (NumberFormatException nfe) {
		} catch (Exception e) { System.err.println(e);
		}

		// add to caches, both memory and disk
		word2cnt.put(word, cnto);
		newwords.add(word); if (newwords.size()>50) writeCache();
	}

	return cnto.intValue();
  }




  static class WordFreq {
	String word;
	int pagecnt;
	int webcnt; // for debugging.  relatively few words < MEDFREQ, so space not a consideration
	WordFreq(String w, int pagecnt, int webcnt) { word=w; this.pagecnt=pagecnt; this.webcnt=webcnt; }
	public String toString() { return word+"="+pagecnt+"/"+webcnt; }
  }

  static class byRelFreq implements Comparator {
	public int compare(Object o1, Object o2) {
		WordFreq wf1=(WordFreq)o1, wf2=(WordFreq)o2;
		double r1=((double)wf1.webcnt)/((double)wf1.pagecnt), r2=((double)wf2.webcnt)/((double)wf2.pagecnt);
		//int r1=((WordFreq)o1).rel, r2=((WordFreq)o2).rel;
		return (r1==r2? 0: (r1<r2? -1: 1));
	}
   }

  // temporary, until have more sophisticated robust selection of terms
  static class byRoFreq implements Comparator {
	public int compare(Object o1, Object o2) {
		WordFreq wf1=(WordFreq)o1, wf2=(WordFreq)o2;
		double r1=((double)wf1.webcnt)/((double)Math.min(3,wf1.pagecnt)), r2=((double)wf2.webcnt)/((double)Math.min(3,wf2.pagecnt));
		//int r1=((WordFreq)o1).rel, r2=((WordFreq)o2).rel;
		return (r1==r2? 0: (r1<r2? -1: 1));
	}
   }

  static class byWebFreq implements Comparator {
	public int compare(Object o1, Object o2) {
		return ((WordFreq)o2).pagecnt - ((WordFreq)o1).pagecnt;
	}
  }


  /** Sorts words by frequency in document, return top <i>n</i> most frequent. */
  public static String computeSignature(URL url) {
//System.out.println("computeSignature for "+url);

	// X RobustHyperlink fails to using Swing HTML parser (get links, get words functions)
	try {
		// LATER: ROUTINIZE through cache so can handle types other than HTML... even if web search engines don't (except for .txt)
		URLConnection con = url.openConnection();
		if (con==null) return "(can't open connection)"; // must be new protocol that's not supposed to be opened

		con.connect();
		if (con instanceof HttpURLConnection) {
			HttpURLConnection hcon = (HttpURLConnection)con;
			int code = hcon.getResponseCode();
			if (code!=200) return "(bad connection "+code+")";
			if (StudyOut!=null) {
				long lastmod = hcon.getLastModified();
				if (lastmod>0) StudyOut.print("m"+lastmod+" ");
			}

			HTML html = new HTML();
			try { html.docURI = new URI(url.toExternalForm()); } catch (URISyntaxException canthappen) {}
			//Multivalent m = Multivalent.getInstance();
			//Browser br = m.getBrowser("STARTUP");
			//Root root = new Root(null, br);
			//Document docroot = new Document("_DOCUMENT", null, br.getDocRoot());
			Document docroot = new Document("doc",null, null);
			if (Verbose) System.out.println("Reading "+url);
			//html.parse(con.getInputStream(), doc);
			html.setInputStream(new BufferedInputStream(con.getInputStream()));
			html.parse(docroot);
//System.out.println("dumping "+url);
//root.dump();

			String sig = computeSignature(docroot, url);
			assert sig!=null: "signatures should return error message rather than null";
			//if (sig.startsWith("(")) { System.err.println(sig); return null; }
			return sig;

		} else System.err.println("problem computing signature");
	} catch (Exception e) {
		System.err.println(e.toString());	// bail out
	}
		// ROUTINIZE

	return null;
  }

  public static String computeSignature(Node root, URL url) {
	return computeSignature(computeSignature(root), url);
  }
  public static String computeSignature(String words, URL url) {
	String surl = url.toString();
	if (words==null) return surl;

	char sep = (surl.indexOf('?')==-1? '?': '&');
	return surl + ("".equals(url.getFile())?"/":"")+ sep + /*((sep=='&' || !Concise)?*/ PARAMETER/*: "")*/ + URIs.encode(words);
  }

  /** Glean document tree for words - maybe move this to class Node. */
  public static String computeSignature(Node root) {
//System.out.println("root="+root+"/"+((INode)root).childAt(0)+", firstleaf="+root.getFirstLeaf());
	List words = new ArrayList(1000);
	for (Node n=root.getFirstLeaf(); n!=null; n=n.getNextLeaf()) {
		String pname = n.getParentNode().getName();
		if (pname=="script" || pname=="style") continue;

		String sword = n.getName();
		if (sword.indexOf('@')!=-1) continue;   // no e-mail--not indexed (and probably would change if page moved?)

		// strings of letters only
		for (int i=0,j=0, imax=sword.length(); i<imax; i=j) {
			while (j<imax && Character.isLetter(sword.charAt(j))) j++;
			if (j-i > MinWordLength) {
				String word = sword.substring(i,j);
				words.add(FoldCase? word.toLowerCase(): word);
			}
			while (j<imax && !Character.isLetter(sword.charAt(j))) j++;
		}


/*		StringTokenizer st = new StringTokenizer(sword, TrimChars);
		while (st.hasMoreTokens()) {
			String word = st.nextToken();	//aw, that's Tcl's trim:  .trim(TrimChars);   // trim from edges only--not embedded
			// disqualify phone numbers since if person moves and page goes away, phone numbers change too
			if (word.length()>=MinWordLength && !Character.isDigit(word.charAt(0))) {
				words.add(FoldCase? word.toLowerCase(): word);
//System.out.println("word = "+word);
			}
		}*/
	}
	return computeSignature(words);
  }

  public static String computeSignature(List words) {
	if (words.size()==0) return "(empty word list)";

	readCaches();
	firstNew = true;

	// sort
	int len = words.size() + 1;
	String[] word = new String[len];
	words.toArray(word);
	word[len-1]=String.valueOf((char)0xffff); // sentinal to flush -- sorts last + can't match other words
	Arrays.sort(word);

	// count + arrange by count
	//SortedSet ss = new TreeSet(new byRelFreq()); => no!  if have same rel freq, then only one kept in *set*
	List list = new ArrayList();
	String prev=word[0];
	int c=0;
	for (int i=0,imax=len; i<imax; i++) {
		String w = word[i];
		if (w.equals(prev)) c++;
		else {
			int freq = getFreq(prev);
//System.out.println("word = "+w+", freq="+freq);
			//if (/*c>1 &&*/ /*rel*/freq<=MEDFREQ) list.add(new WordFreq(prev, c, freq));
			if (freq<Integer.MAX_VALUE) list.add(new WordFreq(prev, c, freq));
//System.out.println(prev+" "+freq);
//if (freq<=MEDFREQ) System.out.println("added "+prev);
//if (relfreq<100) System.out.println(prev+" "+c+"/"+freq+" => "+relfreq);
			prev=w; c=1;
		}
	}
	// last one flushed by sentinal
	if (list.size()==0) return "(no valid words)";

	WordFreq[] bogus = new WordFreq[0];
	WordFreq[] wordfreq = (WordFreq[])list.toArray(bogus);


	int validlen = Math.min(SignatureLength,wordfreq.length);
	// could OO this with an interface and classes...
	if ("tfidf".equals(Algorithm)) {
		Arrays.sort(wordfreq, new byRelFreq());

	} else if ("rarest".equals(Algorithm)) {
		Arrays.sort(wordfreq, new byWebFreq());

	} else if ("random".equals(Algorithm)) {
		Random rand = new Random();
		for (int i=0,imax=validlen; i<imax; i++) {
			int swapi=rand.nextInt(imax);
			WordFreq tmp=wordfreq[i]; wordfreq[i]=wordfreq[swapi]; wordfreq[swapi]=tmp;
		}

	} else if ("random100k".equals(Algorithm)) {
		// any random combination of words this rare works great
		Random rand = new Random();
		validlen=0;
		// march through, building up random list in 0..validlen
		for (int i=0,imax=wordfreq.length; i<imax; i++) {
			WordFreq tmp=wordfreq[i];
			if (tmp.webcnt < 100000) {
				int swapi=rand.nextInt(validlen+1); // returns 0..validlen
				wordfreq[i]=wordfreq[validlen]; wordfreq[validlen]=wordfreq[swapi]; wordfreq[swapi]=tmp;
				validlen++;
			}
		}
		validlen = Math.min(validlen, SignatureLength);

	} else /*default == tfidf+*/ {
		//System.out.println("robustrare");
		Arrays.sort(wordfreq, new byRoFreq());
		// refinements for robustness here
	}


	// dump top 20 words, page freq, web freq, so can see what you might want, and figure out a formula to choose them
	if (DEBUG) {
		System.out.println("* Rankings *");
		for (int i=0;i<Math.min(25, wordfreq.length); i++) System.out.println(wordfreq[i]);
	}


	// return top n
//if (debug) System.out.println(ss.size()+" qualifying words");
	//if (words.size()<100 && validlen<=2) return "(page too short)"; -- want to do this, but sometimes single word good enough


	StringBuffer sigsb = new StringBuffer(100);
	for (int i=0,imax=validlen; i<imax; i++) {
		//WordFreq wf = wordfreq[i];
		// need some flag to dump pagecnt and webcnt here
		if (i>0) sigsb.append(' '); // '+'
		sigsb.append(wordfreq[i].word);

		if (StudyOut!=null) StudyOut.print(wordfreq[i].pagecnt+"/"+wordfreq[i].webcnt+" ");
	}
	if (StudyOut!=null) StudyOut.println(/*validlen*/);

	if (Verbose && newwords.size()>0) {
		System.out.println();  // newline after string of dot-per-new-word
	}
	writeCache();

	return sigsb.substring(0);
  }


  static Map url2sig = new HashMap(10);
  static String[] rtag = { "base", "a", "frame" };
  static String[] rattr = { "href", "href", "src" };
  static PrintWriter StudyOut = null;
  /**
	Walk file system tree, rewriting pages to make links robust.
	Useful for converting one's old bookmarks or, called recursively, all links in a site.
  */
  public static void rewrite(URL url, File f, boolean force, boolean stdout) {
	File f0 = f;
	String canf0 = f0.getAbsolutePath().replace('\\','/');
	if (StudyOut!=null) {
		StudyOut.println("# m=page last mod, c=bookmark creation, v=bookmark last visitied,");
		StudyOut.println("# page cnt/web cnt or error message");
	}

	LinkedList q = new LinkedList();	// holds File's for rewrite, URLs for signing
	q.add(f);
	while (q.size()>0) {
		f = (File)q.removeFirst();

		if (!f.exists()) { System.err.println(f+" doesn't exist"); continue; }
		else if (!f.canRead()) { System.err.println(f+" unreadable"); continue; }
		else if (!f.canWrite()) { System.err.println(f+" unwriteable"); continue; }

		if (Verbose) System.out.println("making robust "+f);
		if (f.isDirectory()) {
			if (Verbose) System.out.println(" => directory.  Recursing");
			String[] files = f.list();
			for (int i=0,imax=files.length; i<imax; i++) {
				String lc = files[i].toLowerCase();
				if (lc.endsWith(".html") || lc.endsWith(".htm")) q.add(new File(f, files[i]));
			}

		} else {
			try {
				String canf = f.getAbsolutePath().replace('\\','/');
				String relf = "";
				// strip out commonality with base file, at directory boundary
				for (int i=0,imax=canf.length(); i<imax; i++) {
					if (canf.charAt(i)!=canf0.charAt(i)) {
						while (i>0 && canf.charAt(i)!='/') i--;
						relf = canf.substring(i);
						break;
					}
				}
//System.out.println("canf0 = "+canf0+"\ncanf = "+canf);

				URL base = new URL(url, relf);
//System.out.println(url+" + "+relf+" = "+base);
				//try { base = new URL(base, f.getCanonicalPath()); } catch (MalformedURLException male) {}
				if (Verbose) System.out.println("aka "+base);

				PrintWriter out;
				if (stdout) {
					boolean oldverbose = Verbose;
					Verbose=false;
					rewrite1(base, f, force, out=new PrintWriter(System.out));
					Verbose = oldverbose;
					out.close();	// close System.out wrapper?
				}
				//else if (Verbose==-1) rewrite1(base, f, force, out = new PrintWriter(new FileWriter("studydata.txt", true)));
				else {
					// first write to temporary file
					File robustf = File.createTempFile("xxx", null);
					out = new PrintWriter(new FileWriter(robustf));
					if (StudyOut!=null) StudyOut.println();
					rewrite1(base, f, force, out);
					out.close();

					// with rewritten version successful, delete original and rename temporary
					if (f.delete()) {
						if (!robustf.renameTo(f)) { System.err.print(" -- can't rename "); }
						robustf.renameTo(f);
					} else System.err.print(" -- can't delete");
				}
			} catch (MalformedURLException male) {
				if (Verbose) System.out.println("BAD"+male);
			} catch (IOException e) {
				System.err.println(e);
			}
		}

		if (Verbose && !firstNew) System.out.println();
	}
  }

  /**
	For given URL, write new version of page minimally changed from that read,
	with each HREF's URL replaced by a robust one.
  */
  //static int rewritecnt = 0;
  static int SkipCnt = 0;
  static String SkipTo = null;
  public static void rewrite1(URL base, File f, boolean force, PrintWriter out) {
	try {
		// first make big string of whole page
		char[] buf = new char[(int)f.length()];
		Reader in = new FileReader(f);
		int len = in.read(buf);
		in.close();
		String str = new String(buf, 0, len);
		int sinx=0;


		// handle A HREF, FRAME SRC, BASE HREF, ...
		int lasti=0; char ch;
		for (int i=0,imax=str.length(),ie; (i=str.indexOf('<', i))!=-1; i++) {
			// dump text up to start of tag
			i++;
			if (i>lasti) { out.print(str.substring(lasti,i)); lasti=i; }

			// find end of tag
			int iend = str.indexOf('>',i);
			if (iend==-1) break;
			if (str.charAt(i)=='/') continue;	// ignore close tag

			// tag were interested in?
			String findattr=null, attrval=null; // href for A and BASE, src for FRAME (and IMG)
			ie=i;
			while ((ch=str.charAt(ie))!='>' && !Character.isWhitespace(ch) && ie<iend) ie++;
			String tag = str.substring(i,ie).toLowerCase();
			//for (int k=0,kmax=rtag.length; k<kmax; k++) if (tag.equals(rtag[k])) { findattr = rattr[k]; break; }
			int hit = Arrays.asList(rtag).indexOf(tag);
//System.out.println("tag="+tag+" => "+findattr);
			//if (findattr==null) continue;
			if (hit!=-1) findattr = rattr[hit]; else continue;

			// find selected attribute, take value
			for (i=ie; i<iend; i=ie) {
				// name
				while (i<iend && Character.isWhitespace(str.charAt(i))) i++;	// start of attr
				ie=i;
				while (ie<iend && (ch=str.charAt(ie))!='=' && !Character.isWhitespace(ch)) ie++;   // attr itself
				boolean match = (ie-i==findattr.length() && findattr.regionMatches(true, 0, str,i,findattr.length()));
//System.out.println(findattr+" =? |"+str.substring(i,ie)+"|, match="+match);

				// val
				i=ie; while (i<iend && Character.isWhitespace(str.charAt(i))) i++;
				if (i<iend && str.charAt(i)=='=') i++; else continue;
				while (i<iend && Character.isWhitespace(str.charAt(i))) i++;
				char eoa = str.charAt(i);
				if (eoa=='"' || eoa=='\'') {
					i++; ie=i;
					while (ie<iend && str.charAt(ie)!=eoa) ie++;
				} else {
					ie=i;
					while (ie<iend && !Character.isWhitespace(str.charAt(ie))) ie++;
					if (ie>i) { eoa=str.charAt(ie-1); if (eoa=='"' || eoa=='\'') ie--; }  // close quote without open quote
				}

				if (match && ie>i) {
					if (str.charAt(i)!='#') attrval = str.substring(i,ie);	// don't sign intra-page
					if (SkipCnt>0) { SkipCnt--; attrval=null; }
//System.out.println("\n"+findattr+" = "+attrval);
					break;
				}
			}

			// tag-specific action
			if (tag.equals("base")) {
				try { base = new URL(base, attrval); } catch (MalformedURLException e) {}
//System.out.print(str.substring(lasti,iend+1));
				if (Verbose) System.out.println("\tBASE tag => setting URL base to "+base);
			} else if (attrval!=null && attrval.length()>0) {  // rewrite given attribute value
				//if (Verbose) System.out.print("\trewriting \""+tag+" "+findattr+" => ");
				if (StudyOut!=null && sinx!=-1 && "a".equals(tag)) {
//+System.out.println("sinx="+sinx+", i="+i+", |"+str.substring(i,ie)+"|, "+str.indexOf("ADD_DATE",i));
					if ((sinx=str.indexOf("ADD_DATE", i))!=-1 && sinx<iend) StudyOut.print("c"+str.substring(sinx+8+2,sinx+8+2+9)+" ");
					if ((sinx=str.indexOf("LAST_VISIT", i))!=-1 && sinx<iend) StudyOut.print("v"+str.substring(sinx+10+2,sinx+10+2+9)+" ");
				}

				// if no "lex-sig" or forced recompute, compute now
				String strip=stripSignature(attrval), sig=getSignature(attrval);
				String norm = strip.toLowerCase();
				int queryi = norm.indexOf('?'); if (queryi!=-1) norm=norm.substring(0,queryi);
				URL relurl = new URL(base, strip);
				if (SkipTo!=null) {
					if (SkipTo.indexOf(norm)!=-1) SkipTo=null;	// just have to specify partial, not full for .equals()
					else {
						if (Verbose) System.out.println("skipping "+norm);
						continue;
					}
				}
//System.out.println("norm = |"+norm+"|");
//System.out.println("|"+base+"| + |"+attrval+"| = |"+relurl+"|");
//System.out.println(base+" + "+attrval+" => "+relurl);
//System.out.println("relurl = |"+relurl+"|, queryi="+queryi);
				String path = relurl.getFile(); if ("".equals(path)) path="/";
				String file = (path.indexOf('/')!=-1? path.substring(path.lastIndexOf('/')+1): path);
				int sfx = file.lastIndexOf('.');
				// if host-only, add '/' before signature so that Netscape 4.x doesn't screw up
				// http://sunsite.berkeley.edu:80/Dienst/UI/2.0/Describe/ncstrl.ucb%2fCSD-82-104
//System.out.println("sig="+sig+", force="+force+", norm="+norm);
				if ((sig==null || force) && (queryi==-1 || SignQueries) && (norm.endsWith(".html") || norm.endsWith(".htm") || queryi!=-1 || relurl.getFile()=="" || sfx==-1 || sfx<file.length()-5)) try {
					if (Verbose) System.out.print("\t"+attrval+" => ");

					if (i>lasti) { out.print(str.substring(lasti,i)); lasti=ie; }
					//System.out.print("|STRIP="+strip+"|");
					out.print(strip); lasti=ie;

					//if (Verbose) System.out.println("\tsigning "+attrval);
//System.err.println("\n"+attrval+" => ");
					if (StudyOut!=null) StudyOut.flush();

					String signedurl = computeSignature(relurl);
//System.err.println("\n"+attrval+" => "+signedurl);
					sig = (signedurl.startsWith("(")? signedurl: getSignature(signedurl));
					if (StudyOut!=null && sig.startsWith("(")) StudyOut.println(sig);	// if ordinary signature, already written out

//System.err.println(attrval+" => "+signedurl+" => "+sig);
					if (Verbose) System.out.println(sig);
					if (sig!=null && !sig.startsWith("(")) {
						url2sig.put(relurl.toString(), sig);
						//relurl = new URL(sig);
						//if (i>lasti) { out.print(str.substring(lasti,ie)); lasti=ie; }
						//out.print(URIs.relativeURL(base, new URL(sig)));
						out.print(sig);
						//lasti=ie; // replaced attr value so skip old value
					} //else if (Verbose) System.out.println("can't sign (maybe page too short)");
				 //else if (sig!=null && Verbose) System.out.println("already signed");

				} catch (Exception e) {
					// skip it
					if (Verbose) System.out.println("error during signing (may not be HTML)");
					if (StudyOut!=null) StudyOut.println("("+e+")");
				}
				//else if (Verbose) System.out.println("doesn't appear to be HTML");
			}
			//if (StudyOut!=null) break;	// while debugging
		}

		if (lasti<str.length()) out.print(str.substring(lasti));

	} catch (IOException ioe) {
	}
  }



  static String[] ENGINES = {
	"  http://www.google.com/search?num=10&q=",
	"1 http://www.altavista.com/cgi-bin/query?pg=q&sc=on&kl=XX&stype=stext&q=",
	"1+http://www.altavista.com/cgi-bin/query?pg=q&sc=on&kl=XX&stype=stext&q=",
	"  http://ink.yahoo.com/bin/query?z=2&hc=0&hs=0&p=",
	" +http://ink.yahoo.com/bin/query?z=2&hc=0&hs=0&p=",
	//"o+http://www.lycos.com/srch/?lpv=1&loc=searchhp&x=33&y=9&query=", -- redirects links, confusing rank scraper
	"1 http://hotbot.lycos.com/?SM=MC&DV=0&LG=any&DC=10&DE=2&BT=L&x=25&y=9&MT=",
	"1+http://hotbot.lycos.com/?SM=MC&DV=0&LG=any&DC=10&DE=2&BT=L&x=25&y=9&MT=",
	"1 http://infoseek.go.com/Titles?col=WW&svx=home_searchbox&sv=IS&lk=noframes&qt=",
	"1+http://infoseek.go.com/Titles?col=WW&svx=home_searchbox&sv=IS&lk=noframes&qt=",
	// ...
  };
  /* to determine rankings, probably easier, if perhaps more fragile, to associate unique hunk of text
  Google - <p><A HREF=
Alta Vista - . </b>
<a href=
Yahoo - <li><a href="
HotBot - <A href=/director.asp
infoseek - <b><a href="/redirect
  */

  /** Report rank of signature in various search engines.
	  Can be fooled, so check the excerpted hit with the URL you expect.
  */
  // LATER: check with all terms required, as in "+phelps +tkman +multivalent"
  public static String checkSignature(URL targeturl, String signature) { return checkSignature(targeturl, signature, ENGINES); }
  public static String checkSignature(URL targeturl, String signature, String[] engines) {
	//StringBuffer sb = new StringBuffer(100);
	char[] buf = new char[20*1024];

	String qwords = URIs.encode(signature);
	URL url=null;

	// will server accept a signed version of that URL?  YES!
	/*
	try {
		url = new URL(targeturl, (targeturl.toString().indexOf('?')==-1? '?': '&') + PARAMETER + qwords);
		System.out.println("signed URL = "+url);
		URLConnection urlc = url.openConnection();
		// get server and error code
		for (int i=1; ; i++) {	// field 0 is return code, with key==null
			String key=urlc.getHeaderFieldKey(i);
			if (key==null) break;
			String val = urlc.getHeaderField(i);
			//System.out.println("key = "+key+", val="+val);
		}
		urlc.getInputStream().close();
	} catch (MalformedURLException male) {
	} catch (Exception e) {
			System.err.println(url.toString()+": "+e.toString());
	}*/

	for (int i=0,imax=engines.length; i<imax; i++) {
		String engine = engines[i];
		char ranking=engine.charAt(0), allwords=engine.charAt(1);
		if (allwords!=' ') {
			StringBuffer awsb = new StringBuffer(100);
			StringTokenizer awst = new StringTokenizer(signature); while (awst.hasMoreTokens()) awsb.append(allwords).append(awst.nextToken()).append(' ');
			qwords = URIs.encode(awsb.toString().trim());
		}

		engine = engine.substring(2);
		try {
			url = new URL(engine+qwords);
//System.out.println("checking "+url.getHost()+"\t"+qwords);

			Reader in = new BufferedReader(new InputStreamReader(url.openStream()));
			// frequency information in first 1K chars, but be robust if this changes a bit
			int len=0,hunk;
			while (len<buf.length && (hunk=in.read(buf, len,buf.length-len))>0) len+=hunk;	// should collect entire page, but OK if doesn't
			in.close();
			String result = new String(buf, 0,len);
//System.out.println("result len = "+len);

			String quality = "exact match";
			String hit = "not indexed (yet?)";
			String searchfor = URIs.encode(targeturl.getHost()+targeturl.getFile());
//System.out.println("search for |"+searchfor+"|");
			String filename = targeturl.getFile();
			if (filename.indexOf('/')!=-1) filename=filename.substring(filename.lastIndexOf(',')+1);
			int inx=result.indexOf(searchfor);
			if (inx==-1) {
				quality="moved on same site"; searchfor=targeturl.getHost();
				inx=result.indexOf(searchfor);
			}
			if (inx==-1 && filename.length()>=5) {
				quality="same file on different site";
				searchfor = filename;
				inx=result.indexOf(searchfor);
			}
			if (inx==-1) { quality="not indexed (yet?)"; }
//System.out.println("\tfound "+searchfor+" @ "+inx);

			// rank
				// first try to find numbers immediately preceeding
			int rank=-1;
			//if (inx==-1) System.out.println(result);
			if (inx!=-1) {
				int endtag = result.indexOf('>', inx);
				hit = result.substring(inx, endtag);

				if (ranking=='1') {
					int digend=-1;
					for (int j=inx; j>=2; j--) {
						char ch = result.charAt(j);
						if (digend==-1 && (ch==' '||ch=='&') && result.charAt(j-1)=='.' && Character.isDigit(result.charAt(j-2))) { j-=2; digend=j; }
						else if (digend!=-1 && !Character.isDigit(ch)) {
							try {
								rank = Integer.parseInt(result.substring(j+1, digend+1));
//System.out.println("numbered: "+result.substring(j+1, endtag));
							} catch (NumberFormatException nfe) {}
							break;
						}
					}
				}

				if (rank==-1) {
					rank=1;
					// count links(!)
					String curhost = url.getHost();
					int inx2 = curhost.indexOf('.');
					if (inx2!=-1) curhost = curhost.substring(inx2+1);
//if (ranking=='o') System.out.println("<OL > = "+result.indexOf("<OL")+" => "+result.substring(result.indexOf("<OL "),inx+50));
					for (int j=(ranking=='o'?result.indexOf("<OL"):0),jmax=inx-20; j<jmax; j++) {
						int newj = result.indexOf("<a href", j);
						if (newj==-1) newj = result.indexOf("<A HREF=", j);
						if (newj==-1 || newj>=jmax) break;
						j=newj;
//System.out.println("link @ "+j+result.substring(j,endtag));
						newj = result.indexOf(curhost, j);
//if (newj==-1 || newj<endtag) System.out.println("ignored");
						if (newj!=-1 && newj>endtag) rank++;
					}
				}
			}

			// backtrack to find rank number
			String report = url.getHost();
			if (allwords!=' ') report+= "  (strict)";
			if (rank!=-1 && rank<=10) report += "  rank="+rank;
			report += "\t"+hit;
			System.out.println(report);

		} catch (MalformedURLException male) {
		} catch (IOException ioe) {
			System.err.println(url.toString()+": "+ioe.toString());
		}
	}
	return null;
  }

/*
  public boolean event(AWTEvent e, Point scrn) {
	// if "computeSigs" semantic event... => augment on Document.MSG_OPENED?
	return false;
  }*/


  /**
	Make your web pages robust automatically!  Invoke this class as an Java application (see
	below) under <b>Java 2 v1.2.2</b> or later to rewrite your web pages, making all HREF URLs robust and leaving inaccessible URLs and all other text untouched.
	Or compute the signature of a non-local web page.
	Make your bookmarks robust, and you will be able to find those interesting pages after they move to a different site.
	If you're a webmaster, make your site robust now, then add this to your production process
	so that new HTML pages are made robust before being put on the web server.

	<blockquote><tt>java -jar Robust.jar [&lt;options&gt;] [&lt;URL&gt;] [&lt;filename&gt;]</tt><BR>
	with &lt;options&gt; described below.
	</blockquote>

<!--
	Or, if you're running it from an <i>unpacked</i> Multivalent.jar:
	<blockquote><tt>java phelps.net.RobustHyperlink [&lt;options&gt;] [&lt;URL&gt;] [&lt;filename&gt;]</tt><BR>
	with the root directory of that unpacked JAR in your CLASSPATH.
	</blockquote>
-->


	<p>Given a lone URL (no &lt;filename&gt;), report signature of &lt;URL&gt;
	and, optionally, check efficacy by looking up signature in various web search engines.

	<p>Otherwise, rewrite the single file &lt;filename&gt;
	or all files ending in ".html" or ".htm" in the directory &lt;filename&gt; and its subdirectories.
	&lt;URL&gt; gives the HTTP URL corresponding to &lt;filename&gt;, and it used to resolve links
	relative to the local site root (as in "http:/images/img.png").
	If the HTML contains no such links, as in a bookmarks file, &lt;URL&gt; may be omitted, but do so at your own risk.
	If you'd like, you can make a copy or revision control checkpoint of the file/directory tree first.
	Rewriting is done to a temporary file, and at completion of the process renamed to the original filename,
	so you can interrupt the process safely.


	<p>Command-line options
	<ul>
	<li>-force recomputes signatures for embedded HREF URLs even if they are already signed.
		Useful if you decide to use a different signing algorithm, or use more signature words.
	<li>-signqueries computes signatures for queries too
	<!--<li>-metadata all:MD5:timestamp:size:... - decide how to encode into URL
	-->
	<!-- conflicts with deprecated ISINDEX
	<li>-concise (or -short) signs URL in the consise form (no "lexical-signature=" text),
		if possible (if the URL already carries parameters to a cgi-bin script, the non-concise form is used).
	-->
	<li>-siglen &lt;num&gt; - maximum number of words to put in the signature (longer singatures are
		more robust, but after 4-5 words the marginal benefit decreases rapidly).  Defaults to 10.
	<li>-stdout shows the rewritten file on System.out and does not write to disk.

	<li>-textmode runs as a text application, without showing the GUI
	<li>-help shows the format of the command line and immediately exits
	<li>-version writes the version of this class and immediately exits
	<li>-verbose lists files as they are rewritten, lists linked-to pages as they are signed,
		and shows the signature or reason the page was not signed (host was not available,
		protocol was news, mailto, telnet, javascript).  -verbose is on by default.
	<li>-quiet is the opposite of -verbose
	</ul>

	<p>A few other options are useful in studies measuring the effectiveness of different algorithms.
	   The default provides the recommended settings for in ordinary use.
	<ul>
	<li>-algorithm (tfidf|tfidf+|rarest|random|random100k) - use the specified algorithm to compute the signature.
		tfidf ("term frequency-inverse document frequency") picks the most frequent words in the document that are the rarest in the web.
		tfidf+ refines tfidf by making sure words are in different sentences, among other things [not implemented presently].
		rarest picks the words rarest in the web.
		random picks words randomly.
		random100k picks words randomly from those that appear in fewer than 100,000 web pages.
		Defaults to the recommended tfidf+.
	<li>-ignorebuiltin ignores the built-in list of the most common words on the Internet,
		supplied courtesy of <a href="http://www.inktomi.com/">Inktomi</a>,
		and processed for web frequency counts against <a href="http://www.altavista.com/">Alta Vista</a>.
	<li>-preservecase counts words differing only in case as different words (defaults to OFF).
	<li>-minwordlen &lt;number&gt; prevents words of fewer letters from consideration as signature words (defaults to 4).
	<li>-check reports the rankings of the signature in various search engines
	<li>-raw reports information in an easily parsed format for further processing by other programs.
	<li>-debug dumps a great deal of information, including word frequency counts
	</ul>

	<!--
	<p>Examples
	<p>Sign the file (need not be HTML).
		... http://...
		... file:/...
	<p>Rewrite the file
		... URL d:/...
	<p>Rewrite the file (different from signing file above as file is given here, not a URL).
	(resolves site root-relative links assuming this page is the site root,
	   which is probably wrong).
		... /home/users/.../xxx.html
	<p>Rewrite the file to make embedded URLs robust
	<p>Rewrite the directory tree rewriting all ".html" and ".htm" files.
	-->

	<!--
	TO DO
	options to put more in URL, such as size of destination (read it all anyhow),
	or maybe round robin even on signing URL so everybody can add
	-->
  */
  public static void main(String[] argv) {
	// process command-line options
	int argi=0, argc=argv.length;
	boolean err=false;

	boolean force=false, stdout=false, check=false, raw=false, study=false, gui=true;
	Verbose=true;	// default for interactive use
	while (argi<argc && argv[argi].startsWith("-")) {
		String opt = argv[argi].toLowerCase().intern();
		if ("-force"==opt) force=true;
		else if ("-signqueries"==opt) SignQueries=true;
		else if ("-stdout"==opt) { stdout=true; Verbose=false; }
		else if ("-siglen"==opt) {
			try {
				SignatureLength = Integer.parseInt(argv[++argi]);
				if (SignatureLength<3) { System.err.println("Signature too short"); err=true; }
			} catch (NumberFormatException nfe) {
				System.err.println("Unable to parse "+argv[argi]+" as an integer");
				err = true;
			}

		// analysis
		} else if ("-algorithm"==opt) {
			String alg = argv[++argi];
			boolean valid = setAlgorithm(alg);
			if (!valid) { System.err.println("invalid algorithm "+alg); err=true; }
		} //else if ("-concise"==opt || "-short"==opt) Concise=true;
		else if ("-minwordlen"==opt) {
			try {
				MinWordLength = Integer.parseInt(argv[++argi]);
				if (MinWordLength<3) { System.err.println("Minimum word length too short"); err=true; }
			} catch (NumberFormatException nfe) {
				System.err.println("Unable to parse "+argv[argi]+" as an integer");
				err = true;
			}
		} else if ("-preservecase"==opt) FoldCase=false;
		else if ("-ignorebuiltin"==opt) IgnoreBuiltin=true;
		else if ("-check"==opt) check=true;
		else if ("-raw"==opt) raw=true;
		else if ("-debug"==opt) DEBUG=true;
		else if ("-studydata"==opt) study=true; // whether to append depends on -skip
		else if ("-skip"==opt) { String arg=argv[++argi]; try { SkipCnt = Integer.parseInt(arg); } catch (NumberFormatException nfe) { SkipTo=arg; } }
		else if ("-selftest"==opt) {
			// sig words on: http://sunsite.berkeley.edu:80/Dienst/UI/2.0/Describe/ncstrl.ucb/CSD-82-104?abstract=&lexical-signature=wilensky+carbonell+painless+dialogues+waltz
			System.exit(0);

		// standard
		} else if ("-help"==opt) { System.out.println(USAGE); System.exit(0); }
		else if ("-textmode"==opt) gui=false;
		else if ("-verbose"==opt) { Verbose=true; stdout=false; }
		else if ("-quiet"==opt) { Verbose=false; }
		else if (opt.startsWith("-v"/*ersion--after "verbose"*/)) { System.out.println("RobustHyperlink v"+VERSION); System.exit(0); }
		else { System.err.println("bad option: "+opt); err=true; }
		argi++;
	}

	if (study) try { StudyOut=new PrintWriter(new FileWriter("studydata.txt", (SkipCnt>0 || SkipTo!=null))); } catch (IOException ignore) {}


	URL url = null;
	if (argi<argc) {
		try {
			url=new URL(argv[argi]); argi++;
		} catch (MalformedURLException male) {
			// check for case of lone filename
			// => screws up on site root-relative paths though
//System.out.println("sole filename?  "+(argi+1==argc)+" && "+new File(argv[argi]).exists());
			if (argi+1==argc && new File(argv[argi]).exists()) {
				//File guessroot = new File(argv[argi]);
				//String guessroot = new File(argv[argi]);
				//try { url=new URL("file", "", guessroot.getCanonicalPath().replace('\\','/')); } catch (Exception shouldnthappen) {}
				try { url=new URL("http", "unknownhost", "/"); } catch (MalformedURLException shouldnthappen) {}
				System.out.println("WARNING: No corresponding URL given for file:  Site root-relative links, if any, resolved against "+argv[argi]);
			} else { System.err.println(male); err=true; }
		}
	}


	File f = null;
	if (argi<argc) {
		//if (url!=null && !"http".equals(url.getProtocol())) { System.err.println("URL protocol must be HTTP"); err=true; }
		f=new File(argv[argi]); argi++;
	}


	gui = false;    // taken out
	if (gui) {
		String[] argv2 = new String[1];
		argv2[0] = new INode(null,null,null).getClass().getResource("/util/robust/RobustUI.mvd").toString();
		Multivalent.main(argv2);	 // need interface without all the extras; used by help panes too
		return;
	}

	// is URL really a directory?  (I know, should ask the server)
	if (f!=null) {
		String basefile = url.getFile();
		//if (basefile.length()==0) basefile="/";
		int lastslashi = basefile.lastIndexOf('/');
		if (lastslashi==-1 || lastslashi+1 != basefile.length()) {
			if (lastslashi==-1) lastslashi=0;
			int doti = basefile.indexOf('.', lastslashi);
			String sfile = f.getAbsolutePath().replace('\\','/');
			int flastslash = sfile.lastIndexOf('/');
			if (flastslash!=-1) sfile = sfile.substring(flastslash);
//System.out.println("doti="+doti+", query="+basefile.indexOf('?')+", basefile="+basefile.substring(lastslashi)+", sfile="+sfile);
			if (doti==-1 && basefile.indexOf('?')==-1 && !basefile.substring(lastslashi).equals(sfile)) {
				try { url = new URL(url, url.getFile()+"/"); } catch (MalformedURLException canthappen) {}
			}
		}
//		System.out.println("basefile=|"+basefile+"|");
//		System.out.println("base = "+url);
	}


	if (err || url==null || argi!=argc) { System.err.println(USAGE); System.exit(1); }


	// just URL: sign and exit
	java.net.HttpURLConnection.setFollowRedirects(true);	// -- default
	if (f!=null) rewrite(url, f, force, stdout);
	else {
		String surl = computeSignature(url);
		String sig = (surl.startsWith("(")? surl: getSignatureWords(surl));
		//String sig = surl;
		if (raw) { System.out.println("SIGNATURE\t"+sig);
		} else {
			System.out.println("Use the following Robust Hyperlink in place of the old URL:\n\t"+surl+"\n");
			System.out.println("Try a web search for the following words, as in Google, Hotbot or Yahoo");
			System.out.println("\t"+sig);
			//System.out.println("(You can have this software automatically rewrite web pages to make);
		}
		if (check && "http".equals(url.getProtocol())) {
			if (!raw) System.out.println("Checking efficacy of signature by looking up in web search engines");
			// would like to warn about not expecting page to be regularly reindexed
			checkSignature(url, sig);
		}
	}

	if (StudyOut!=null) StudyOut.close();
	System.exit(0); // now that created some GUI thing someplace, have to exit
  }
}
