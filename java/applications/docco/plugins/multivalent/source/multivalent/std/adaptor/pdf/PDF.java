package multivalent.std.adaptor.pdf;

import java.io.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.color.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.*;

import multivalent.*;
import multivalent.node.LeafAscii;
import multivalent.node.FixedI;
import multivalent.node.FixedIHBox;
import multivalent.node.FixedLeafAscii;
import multivalent.node.FixedLeafAsciiKern;
import multivalent.node.FixedLeafShape;
import multivalent.node.FixedLeafImage;
import multivalent.node.FixedIClip;
import multivalent.gui.VCheckbox;
import multivalent.gui.VMenu;
import multivalent.std.span.StrokeSpan;

import phelps.awt.color.*;
import phelps.lang.Integers;
import phelps.lang.Booleans;



/**
	Parse a page of PDF and display with Java 2's 2D API.


	<h3 id='doctree'>Document Tree Construction</h3>

	<p>The PDF content stream is translated into a Multivalent document tree as follows.
	The tree is live: reformat.  Objects drawn as appear in content stream, which usually but not necessarily follows reading order,
	To see the document tree for any particular PDF page, turn on the Debug switch in the Help menu, then select Debug/View Document Tree.

	<ul>
	<li>Text blocks (<code>BT</code>..<code>ET</code>) have subtrees rooted at a {@link FixedI} with name "text".
	Under that can be any number of lines, which collect text that have been determined to share the same baseline in {@link FixedIHBox}s named "line".
	(Some PDF generators generate an inordinate number of BT..ET blocks, as for instance on version of pdfTeX generated a block
	for each dot in a table of contents between header and page number, but most generators use for meaningful blocks of text.)
	PDF text streams are normalized to word chunks in {@link FixedLeafAsciiKern}s, with special kerning between letters, whether from TJ or Tz or small TD/TM/...,  stored in the leaf.
	Text rendered in a Type 3 font are {@link FixedLeafType3}, with no kerning.
	Text is translated into Unicode, from whatever original encoding (Macintosh, Macintosh Expert, Windows, PDF, Adobe Standard).  However, if the encoding is nonstandard and found only in font tables, it is not translated.
	Text content is available from the node via {@link Node#getName()}.

	<li>Images are stored in {@link FixedLeafImage}s.  The {@link java.awt.image.BufferedImage} is available via {@link multivalent.node.LeafImage#getImage()},
	and the image's colorspace via {@link java.awt.image.BufferedImage#getColorModel()}.
	Images from XObjects have the reference <code>/Name</code> as the GI,
	and inline images (<code>BI</code>..<code>ID</code>..<code>EI</code>) have the GI "[inline]".

	<li>Paths are {@link FixedLeafShape}s, with fill and stroke flags.
	Paths are kept as simple {@link java.awt.geom.Line2D} with GI "line" or {@link java.awt.Rectangle} with GI "rect" if possible, else {@link java.awt.geom.GeneralPath} with GI "path".
	, paths as Rectangle "rect" if possible, else "line", else GeneralPath "path",

	<li>For all leaf types (text, image, path), positioning is available from {@link Node#bbox},
	but the command positioning it there (<code>cm</code>, <code>Td</code>, ...) is not maintained.
	Transformation matrices (<code>cm</code>, <code>Tm</code>) are reflected in final sizes and not maintained as separate objects.

	<li>Colors are maintained as {@link SpanPDF}s, and all colors are translated into RGB.
	Fonts (family, size, style), text rise (<code>Ts</code>), text rendering mode (<code>Tr</code>) are all maintained as {@link SpanPDF}s.
	Other attributes (line width, line cap style, line join style, miter limit, dash array, ...) are all maintained as {@link SpanPDF}s
	such that if several change at once they are batched in same span and if any of the group changes a new span is started,
	which means that only one span for these attributes is active at any point.
	Sometimes a PDF generator produces redundant color/font/attribute changes (pdfTeX sets the color to <code>1 1 1 1 K</code> and again immediately to <code>1 1 1 1 K</code)
	or useless changes (e.g., setting the color and then setting it to something else without drawing anything) --
	all redundent and useless changes are optimized away.

	<li>Marked points (<code>MP</code>/<code>DP</code>) are {@link Mark}s, with the point name as the Mark name.
	Marked regions (<code>BMC</code>/<code>BDC</code>..<code>EMC</code>) are simple {@link multivalent.Span}s, with the region name as the Span name and with any region attributes in span attributes.

	<li>Clipping regions (<code>W</code>/<code>W*</code>) are {@link FixedIClip}.
	Clipping regions cannot be enlarged (push the clip onto the graphics stack with <code>q</code>..<code>Q</code> to temporarily reduce it),
	but some PDF generators don't know this:  useless clipping changes are optimized away.

	<li>Shading patterns are {@link FixedLeafShade}.

	<li>If a large filled rectangle appears before any other drawing, its color extracted as the page background and put into the {@link Document} {@link StyleSheet}.

	<li>If the PDF is determined to be a scanned paper and has OCR (but hasn't replaced text with outline fonts), it is transformed.
	OCR text (which is drawn in invisible mode <code>Tr 3</code> or overdrawn with image)
	is associated with the corresponding image fragment and transformed into {@link multivalent.node.FixedLeafOCR}, and the independent image os removed.
	(This allows hybrid image-OCR PDFs to work as expected with other behaviors, such as select and paste and the Show OCR lens.)

	<li>Annotations such as hyperlinks, are sent as semantic events with message {@link Anno#MSG_CREATE}.
	Other behaviors can translate them into entities on the document tree, often spans.

	<!--li>PDF commands are not maintained explicitly: q..Q, BX..EX, comments, different text drawing commands (Tj, TJ, ', "), changes in transformation matrices (cm, Tm), Form XObject interpolated, -->

	</ul>


	<h3>See Also</h3>
	<ul>
	<li>{@link PDFReader}
	<li>{@link PDFWriter} to write new PDF data format from Java data structures
	</ul>

	<p>Other PDF viewers:
	<ul>
	<li><a href='http://www.adobe.com/products/acrviewer/main.html'>Adobe's Java version of Acrobat</a>.
		It was designed for Java 1.1.8
		It runs as an AWT component.
		Development has been abandoned with PDF 1.4 (Acrobat 5.0),
		so it can't read PDFs that use PDF 1.5's (Acrobat 6.0) the new fine-grained encryption
		or space-saving object streams or cross-reference streams.
	<li><a href='http://www.pdfgo.com/'>PDFGo</a> - another Java-based PDF viewer
		(no Type 3 fonts, slow, crashes, no text selection, costs US$450; but
		great font shaping and runs as applet or application)
	<li><a href='http://www.foolabs.com/xpdf/'>xpdf</a> (not Java)
	</ul>

	@version $Revision$ $Date$
*/
public class PDF extends multivalent.std.adaptor.MediaAdaptorRandom {
  static final boolean DEBUG = false && Multivalent.DEVEL;
  //static final boolean PERF = DEBUG;    //true;     // performance testing flag
  static boolean Dump_ = false;
  //public static final boolean DUMP = true;
  //int lcnt_ = 0;

  /** Message "pdfSetGoFast": faster rendering if sometimes less accurate: arg=boolean or null to toggle. */
  public static final String MSG_GO_FAST = "pdfSetGoFast";


  /** Message of semantic event to jump to a page (by number, name, ...) as specified by the event's <tt>arg</tt> field.
  public static final String MSG_GOTO = "pdfGoto";    // WRONG -- subset of action
*/

  /** Message of semantic event to set the user password so encrypted files can be read, with the password String passed in <tt>arg</tt>. */
  public static final String MSG_OWNER_PASSWORD = "pdfUserPassword";
  /** Message of semantic event to set the owner password so encrypted files can be read, with the password String passed in <tt>arg</tt>. */
  public static final String MSG_USER_PASSWORD = "pdfOwnerPassword";
  /** Message of semantic event to control dumping of uncompress and decrypted content stream to temporary file. */
  public static final String MSG_DUMP = "pdfDump";

  /**
	Optional content groups stored in {@link Document} under this key.
	The value there is a {@link java.util.Map} with names of optional content groups as keys and {@link #OCG_ON} and {@link #OCG_OFF} as values.
  */
  public static final String VAR_OCG = "PDFOptionalContentGroups";
  public static final String OCG_ON = "ON";
  public static final String OCG_OFF = "OFF";


  private static final Matcher ALL_WS = Pattern.compile("\\s+").matcher("");
  /** Metadata that may be in PDF and is useful to Multivalent. */
  private static final /*const*/ String[] METADATA = { "Author", "Title", "Keywords", "Subject", "Producer", "Creator" };

  static final String BLANK_PAGE = "";  // or "This page intentionally left blank"

  //static final double PIXEL_INVISIBLE = 0.1; => antialiasing

  //private static final FontRenderContext IDENTITY_RENDER = new FontRenderContext(new AffineTransform(), true, true);
  //int ppi_ = Toolkit.getDefaultToolkit().getScreenResolution(); => 72ppi + zoom

  // want constants importing from Java 1.5
  static final boolean[] WHITESPACE=PDFReader.WHITESPACE, WSDL=PDFReader.WSDL, OP=PDFReader.OP;
  static final Object OBJECT_NULL = PDFReader.OBJECT_NULL;
  static final Class CLASS_DICTIONARY=PDFReader.CLASS_DICTIONARY, CLASS_ARRAY=PDFReader.CLASS_ARRAY,
	CLASS_NAME=PDFReader.CLASS_NAME, CLASS_STRING=PDFReader.CLASS_STRING;


  static {  // Java's settings are same as PDF's, but verify
	assert BasicStroke.CAP_BUTT==0 && BasicStroke.CAP_ROUND==1 && BasicStroke.CAP_SQUARE==2;
	assert BasicStroke.JOIN_MITER==0 && BasicStroke.JOIN_ROUND==1 && BasicStroke.JOIN_BEVEL==2;
  }

  static Map streamcmds_;
  static {
	String[] cmds = (   // commands are case sensitive
		"w/1 J/1 j/1 M/1 d/2 ri/1 i/1 gs/1   q/0 Q/0 cm/6   m/2 l/2 c/6 v/4 y/4 h/0 re/4   S/0 s/0 f/0 F/0 f*/0 B/0 B*/0 b/0 b*/0 n/0   W/0 W*/0"
		+ " BT/0 ET/0   Tc/1 Tw/1 Tz/1 TL/1 Tf/2 Tr/1 Ts/1   Td/2 TD/2 Tm/6 T*/0   Tj/1 TJ/1 '/1 \"/3   d0/2 d1/6"
		+ " CS/1 cs/1 SC/+ SCN/+ sc/+ scn/+ G/1 g/1 RG/3 rg/3 K/4 k/4   sh/1   BI/0 ID/0 EI/0   Do/1"
		+ " MP/1 DP/2 BMC/1 BDC/2 EMC/0   BX/0 EX/0"
		+ " %/0").split("\\s+");
	streamcmds_ = new HashMap(cmds.length *2);
	for (int i=0,imax=cmds.length; i<imax; i++) {
		String token = cmds[i];  assert streamcmds_.get(token)==null && token.length()>=1+2 && token.length()<=3+2: token;
		int x = token.indexOf('/');
		int arity = (token.charAt(x+1)=='+'? Integer.MAX_VALUE: token.charAt(x+1)-'0');  // all arity single digit
		token = token.substring(0,x);
		assert "n".equals(token) || !token.startsWith("n");     // simple lookahead to see if need to copy clipping path in W/W*
		assert !Character.isDigit(token.charAt(0));     // simpler paths if lookahead not a point
		streamcmds_.put(token, Integers.getInteger(arity));
	}
  }

  /** Go fast or be exactly correct. */
  public static /*NOT final*/ boolean GoFast = true;

/*
*/

/*  static class Histogram {
	int cnt=0, sum=0, min=Integer.MAX_VALUE, max=Integer.MIN_VALUE;
	void update(int val) {
		cnt++;
		sum += val;
		if (val<min) min=val;
		if (val>max) max=val;
	}
	public String toString() { return cnt+"/"+min+".."+(sum/cnt)+".."+max; }
  }
  private Histogram[] hist = new Histogram[256];
*/



  //int pageheight_, pagewidth_;    // have to convert y-coords from PDF's from bottom-up to Java's from top-down
  private PDFReader pdfr_ = null;

  // per-page variables
  Rectangle cropbox_;
  private AffineTransform ctm_ = null;  // zoom + rotate, as for annotations
  /** If encounter error, exception message placed here. */
  String fail_ = null;

  /*
  public PDF() {
	//for (int i=0,imax=hist.length; i<imax; i++) hist[i] = new Histogram();
  }*/

  public boolean isAuthorized() { return getReader().isAuthorized(); }
  public void setPassword(String pw) { getReader().setPassword(pw); }

  public PDFReader getReader() { return pdfr_; }

  public Rectangle getCropBox() { return new Rectangle(cropbox_); }
  // make available to annotations
  public AffineTransform getTransform() { return new AffineTransform(ctm_); }



//+ Content stream: readObject(in), eatSpace(in), getDoubles(), getFloats(), buildPage, buildStream, parse

  /** Helper method used in parsing content stream. */
  /*private*/ static void getDoubles(Object[] ops, double[] d, int cnt) {
	assert ops!=null && d!=null && cnt>0 && cnt <= d.length;
	// if (d==null) d=new double[cnt];
	for (int i=0; i<cnt; i++) d[i] = ((Number)ops[i]).doubleValue();
	//return d;
  }

  /** Used by color space creation and color value filling. */
  /*private*/ static void getFloats(Object[] ops, float[] f, int cnt) {
	assert ops!=null && f!=null /*&& cnt>0 -- pattern*/ && cnt <= f.length;
	for (int i=0; i<cnt; i++) f[i] = ((Number)ops[i]).floatValue();
  }

  /**
	Parse content stream of operators for <var>pagenum</var> into document tree.
	Pages are numbered 1 .. {@link #getPageCnt()}, inclusive.
	See PDF Reference 1.4, page 134.
	Colors and fonts are transformed into Span's.
	@param ctm  initial transform with scaling/zoom, to which the method adds page rotation and conversion to Java coordinates (Y goes down)
  */
  void buildPage(int pagenum, INode pageroot, AffineTransform ctm/*initial zoom*/) throws IOException, ParseException {
	PDFReader pdfr = pdfr_;
	assert pagenum>=1 && pagenum <= pdfr.getPageCnt(): pagenum+ " >= "+pdfr.getPageCnt()+" (1-based)";
	assert pageroot!=null;

	if (Multivalent.MONITOR) System.out.print(pagenum+".  ");
	IRef pageref = pdfr.getPage(pagenum);
	Map page = (Map)pdfr.getObject(pageref);


	//double scale = Toolkit.getDefaultToolkit().getScreenResolution() / 72.0;
//System.out.println("ppi = "+Toolkit.getDefaultToolkit().getScreenResolution());
	//ctm.scale(scale, scale);

	Number rotate = (Number)page.get("Rotate");   // clockwise, multiple of 90
	int rot = rotate==null? 0: rotate.intValue() % 360;  if (rot<0) rot+=360;  assert rot%90==0 && rot>=0 && rot<360;
	ctm.rotate(Math.toRadians(-rot));
//System.out.println("rot="+rot+", ctm="+ctm);

	Rectangle mediabox = PDFReader.array2Rectangle((Object[])pdfr.getObject(page.get("MediaBox")), ctm, true);   // mediabox in zoomed, rotated space
	cropbox_ = (page.get("CropBox")!=null? PDFReader.array2Rectangle((Object[])pdfr.getObject(page.get("CropBox")), ctm, true): mediabox);  // transform here
	double pw = (double)cropbox_.width, ph = (double)cropbox_.height;
//System.out.println("cropbox = "+cropbox_);


	AffineTransform tmpat = new AffineTransform();
	if (rot==0) tmpat.setToIdentity();
	else if (rot==90) tmpat.setToTranslation(0.0, ph);  	// translations in PDF coordinates -- increasing y goes up
	else if (rot==180) tmpat.setToTranslation(pw, ph);
	else { assert rot==270: rot; tmpat.setToTranslation(pw, 0.0); }
	ctm.preConcatenate(tmpat);


	// transform PDF coordinates to Java coordinates
	AffineTransform pdf2java = new AffineTransform(1.0, 0.0, 0.0, -1.0, 0.0-cropbox_.x, ph + (rot==0 || rot==180? cropbox_.y: -cropbox_.y));
	ctm.preConcatenate(pdf2java);
	cropbox_.setLocation(0,0);     // normalize to (0,0) -- cropbox outside of any rotation
//System.out.println("cropbox "+cropbox_+", xform="+ctm);

	ctm_ = new AffineTransform(ctm);


	Object o = page.get("Contents");   // null if empty page
	CompositeInputStream in = (o!=null? pdfr.getInputStream(o, true): null);   // can be IRef or Object[] of IRef's
//System.out.println("building page "+pagenum);

	if (Dump_ && in!=null) try {
		System.out.println("Contents dict = "+o+"/"+pdfr.getObject(o));
		File tmpf = File.createTempFile("pdf", ".stream");
		tmpf.deleteOnExit();
		FileOutputStream out = new FileOutputStream(tmpf);
		for (int c; (c=in.read())!=-1; ) out.write(c);    // test char at a time
		//byte[] buf = new byte[4 * 1024];
		//int len = 0;
		//while (true) { int hunk=in.read(buf); if (hunk>0) { out.write(buf, 0, hunk); len+=hunk; } else if (hunk==-1) break; }  // test block read
		in.close(); out.close();
		System.out.println("wrote PDF content "+pageref+" to "+tmpf/*+".   len="+len*/);   // +" vs /Length="+pdfr.getObject(((HashMap--can be array)pdfr.getObject(o)).get("Length")));

		in = pdfr.getInputStream(o, true);    // restore eaten up stream
	} catch (IOException ignore) { System.err.println("error writing stream: "+ignore); }

	List ocrimgs = new ArrayList(10);   // collect FAX fragments for possible use in OCR
	if (in!=null) {
		Rectangle clipshape = new Rectangle(cropbox_);   // forcefully crop everything to cropbox
		clipshape.translate(-clipshape.x,-clipshape.y);  // already at (0,0)
		FixedIClip clipp = new FixedIClip("crop", null, pageroot, clipshape, new Rectangle(cropbox_));

		try {
//System.out.println("buildStream "+page+" "+clipp+" "+ctm+" "+in+" "+ocrimgs);
			buildStream(page, clipp/*pageroot*/, ctm, in, ocrimgs);
		} catch (IOException ioe) { throw ioe;
		} catch (/*Parse, NumberFormat, ...*/Exception pe) {
			pe.printStackTrace(); 
			throw new ParseException("corrupt content stream: "+pe.toString());
		} finally {
			in.close();
		}
	}

//for (int i=0,imax=hist.length; i<imax; i++) if (hist[i].cnt>0) System.out.print(((char)i)+" "+hist[i]+"   ");  System.out.println();
	//if (DEBUG) Span.dumpPending();  // should be empty list (except in embedded form)

	//if (pageroot.size()==0) new FixedLeafAscii(BLANK_PAGE,null, pageroot); => have clipping region at least
	if (pageroot.getFirstLeaf()==null) { pageroot.removeAllChildren(); new FixedLeafAscii(BLANK_PAGE,null, pageroot); }
	assert checkTree("content stream", pageroot);

	OCR.extractBackground(pageroot, this);
	if (pageroot.size()==0) new FixedLeafAscii("",null, pageroot);
	assert checkTree("bg", pageroot);

	OCR.transform(pageroot, ocrimgs, this);
	assert ocrimgs.size()==0 || checkTree("OCR", pageroot);

	createAnnots(page, pageroot);
	assert page.get("Annots")==null || checkTree("annos", pageroot);
  }



  /**
	Used by buildPage(), recursively by Form XObject, and by Type 3 fonts.
	<!--
	This is a very long method (over 1000 lines), but it's awkward to break parts into their own methods
	as for most PDF commands there is a lot of state to pass and not much computation on it.
	-->

	@return number of commands processed
  */
  int buildStream(Map page, FixedIClip clipp/*INode pageroot*/, AffineTransform ctm, CompositeInputStream in, List ocrimgs) throws IOException, ParseException {
	PDFReader pdfr = pdfr_;

	// PDF state
	Object[] ops = new Object[6];   // name, string, array.  cm, Tm, d1 need 6; SC, SCN, sc, scn variable but <= 5 (CMYK + 1).
	int opsi=0;
	GraphicsState gs = new GraphicsState();
	List gsstack=new ArrayList(10);
	AffineTransform Tm=new AffineTransform(), Tlm=new AffineTransform(), tmpat=new AffineTransform();//, Trm=null;
	double Tc=0.0, Tw=0.0, Tz=100.0, TL=0.0, Ts=0.0;
	int Tr=0;
	GeneralPath path = new GeneralPath();
	//boolean fcompat = false;  BX..EX have no function
	Color color=Color.BLACK, fcolor=Color.BLACK;  // some PDFs rely on defaults
	ColorSpace fCS=ColorSpace.getInstance(ColorSpace.CS_GRAY), sCS=ColorSpace.getInstance(ColorSpace.CS_GRAY);
	float[] cscomp = new float[4];  // up to 4 for CMYK
	List markedseq = new ArrayList(5);  // can be nested
	double curx=0.0, cury=0.0;      // current point

	Map resources = (Map)pdfr.getObject(page.get("Resources")!=null? page.get("Resources"): new HashMap(1)),   // "Resources" requires but can have content that doesn't need one
	 xores = (Map)pdfr.getObject(resources.get("XObject")),
	 fontres = (Map)pdfr.getObject(resources.get("Font")),
	 csres = (Map)pdfr.getObject(resources.get("ColorSpace")),
	 patres = (Map)pdfr.getObject(resources.get("Pattern")),
	 shres =  (Map)pdfr.getObject(resources.get("Shading")),
	 propres = (Map)pdfr.getObject(resources.get("Properties"));
//System.out.println("resources = "+page.get("Resources")+" / "+resources);


	// Multivalent state
	Document doc = clipp/*pageroot*/.getDocument();     // null for Type 3
	Layer scratchLayer = doc!=null? doc.getLayer(Layer.SCRATCH): null;
	Point2D srcpt=new Point2D.Double(), transpt=new Point2D.Double();
	double[] d = new double[6];
	//INode p = pageroot;  // a tiny bit of hierarchy according to graphics state push/pop => push/pop done randomly
//System.out.println("cropbox = "+cropbox_);

	INode textp = null;
	FixedIHBox linep = null;
	double baseline = Double.MIN_VALUE;
	FontPDF tf = null;
	Object[] Tja = new Object[1];
	double spacew = 0.0, concatthreshold = 0.0;     // width of space character in Tm coordinates, and in pixels
	Rectangle2D maxr = null;
	double lastX=0.0, totalW=0.0;   // right edge of last text hunk, and accurate word width without accumulation of rounding error
	boolean/*FixedLeafAscii*/ fconcat = false;  // additional condition for concatenating (set to true after characters, false after spaces)
	//String lastsub = null;

	SpanPDF fontspan=null, sspan=null, fillspan=null, Trspan=null;  // make sure that build in paint order so never need to swap endpoints, even if is drawn bottom-up
	StrokeSpan strokespan=null;
	Node lastleaf = clipp/*pageroot*/;//.getLastLeaf(); if (lastleaf==null) lastleaf=pageroot;   // set to last leaf created, but maybe no leaves when start or maybe in /Form
	boolean fnewfont=true, fnewline=true;   // flag (possible) change in xxx state.  E.g., if font family or size change, collect all changes at next text drawing, and likewise for line attribute changes
	boolean fstroke=false, ffill=false;
	boolean fvalidpath=false;
	Color newcolor=color, newfcolor=fcolor;
	int newTr = Tr;

	// gs.fontdict, gs.xxx = current PDF state
	Map fontdict = gs.fontdict = null;
	double pointsize = gs.pointsize = 1.0;
	float linewidth = gs.linewidth = Context.DEFAULT_STROKE.getLineWidth();
	int linecap = gs.linecap = Context.DEFAULT_STROKE.getEndCap(), linejoin = gs.linejoin = Context.DEFAULT_STROKE.getLineJoin();
	float miterlimit = gs.miterlimit = Context.DEFAULT_STROKE.getMiterLimit();  // if don't set in content stream, whatever defaults are acceptable
	float[] dasharray = gs.dasharray = Context.DEFAULT_STROKE.getDashArray(); float dashphase = gs.dashphase = Context.DEFAULT_STROKE.getDashPhase();
	//boolean fType3 = false;

	Rectangle pathrect = null;  // if shape is simple rectangle, use it rather than more complex GeneralPath.  would like to query GeneralPath, but added shaped immediately flattened into segments.
	Line2D pathline = null;
	//boolean lastS = false, firstS=true;

	boolean /*fshowtext = (getHints() & MediaAdaptor.HINT_NO_TEXT) == 0,*/ fshowshape = (getHints() & MediaAdaptor.HINT_NO_SHAPE) == 0;

	// metrics
	int cmdcnt=0, leafcnt=0, spancnt=0, vspancnt=0, concatcnt=0;
	int pathcnt=0, pathlen=0; int[] pathlens=new int[5000];
	long start = System.currentTimeMillis();


	// Pushes tokens onto stack, until operator, which uses operaands and clears stack.
	PDFReader.eatSpace(in);
	for (int c, peek=-1; (c=in.peek())!=-1; ) {
	//for (int ccnt=0, pcnt=0; true; ccnt++, pcnt++) {
		//if (pcnt > 5000 && clipp.size()>0) { pcnt=0; System.out.println(ccnt); br.repaintNow(); }
		if (OP[c]) {     // OPERAND
			if (opsi >= 6) throw new ParseException("too many operands: "+ops[0]+" "+ops[1]+" ... "+ops[5]+" + more");
			ops[opsi++] = PDFReader.readObject(in);
//if (DEBUG) System.out.print(ops[opsi-1]+" ");

		} else {    // OPERATOR
			c=in.read();    // only peek() above
			int c2=in.read(), c3=-1, c2c3;     // second and third characters
			if (c2==-1 || WSDL[c2] || c=='%') { peek=c2; c2c3=' '; }    // normalize whitespace AND delimiter immediately following BOTH to space
			else if ((c3=in.read())==-1 || WSDL[c3]) { peek=c3; c2c3=c2; }
			else { c2c3 = (c2<<8)+c3; peek=in.read();
				if (peek!=-1 && !WSDL[peek]) {
					if (c=='e' && c2=='n' && c3=='d' && peek=='s') break;   // "ends[tream]" -- assume edited uncompressed command stream to make it shorter
					else throw new ParseException("bad command or no trailing whitespace "+(char)c+(char)c2+(char)c3+" + "+peek);
				}
			}

			cmdcnt++; //if (++hunkcnt == 5000) { br.repaint(); hunkcnt=0; }
			if (DEBUG) { // => when user is running, want to let fail to an Exception, so caller can catch (can't catch failed assertions)
			StringBuffer scmd = new StringBuffer(3); scmd.append((char)c); if (c2c3!=' ') { scmd.append((char)c2); if (c2c3!=c2) scmd.append((char)c3); }
//for (int i=0; i<opsi; i++) System.out.print(" "+ops[i]);  System.out.println("  "+scmd);
			Integer arity = (Integer)streamcmds_.get(scmd.toString());
			boolean ok = arity!=null && (arity.intValue()==opsi || (arity.intValue()==Integer.MAX_VALUE && opsi>0));
			if (!ok) {
				System.out.print((arity==null? "unknown command": ("bad arity "+opsi+" not "+arity))+": |"+scmd+"| ["+c+" "+c2+"] ");
				for (int i=0; i<opsi; i++) System.out.println("\t"+ops[i]);
				if (DEBUG) assert false;
				return cmdcnt;
			}
			}
			if (c!='%'/*"%\n" bad*/) while (peek!=-1 && WHITESPACE[peek]) peek=in.read();    // not PDFReader.eatSpace(in) here
			in.unread(peek);



			switch (c) {
			case 'B':   // B, B*, BT, BI, BMC, BDC, BX
			if (c2c3==' ') {   // -- 'B' - fill and then stroke the path, using the nonzero winding number rule
				// NO path.closePath();
				path.setWindingRule(GeneralPath.WIND_NON_ZERO);
				ffill = fstroke = true;

			} else if (c2c3=='*') {    // -- 'B*' -- fill and then stroke the path, using the even-odd rule
				// NO path.closePath();
				path.setWindingRule(GeneralPath.WIND_EVEN_ODD);
				ffill = fstroke = true;

			} else if (c2c3=='T') {    // -- 'BT' -- begin text object
				// if new BT..ET very very close to last, don't start new block (pdfTex-0.13d very inefficient this way)
				if (clipp.size()>0 && textp == clipp.getLastChild() && Math.abs(ctm.getTranslateX() - Tm.getTranslateX()) < 5.0/*pixels*/ && Math.abs(ctm.getTranslateX() - Tm.getTranslateX()) < 0.001) {   // same baseline and close in X
					//System.out.print(" B"+Math.abs(ctm.getTranslateX() - Tm.getTranslateX()));  // should happen rarely, except on pdfTex table of contents

				} else { // assume new BT..ET means new structural block of text, although not necessarily true
					textp = new FixedI("text"/*+lcnt_++*/,null, clipp);
					linep = new FixedIHBox("line",null, textp); fconcat=false;
					baseline = Double.MIN_VALUE;
					//fontspan = new SpanPDF... => NO, not necessarily a Tf in each BT
				}

				Tm.setTransform(ctm);    // concat with identity...
				Tlm.setTransform(Tm);


			} else if (c2c3=='I') { // 'BI' - begin inline image
				BufferedImage img = Images.createScaledInline(in, csres, ctm, newfcolor, pdfr);
				lastleaf = appendImage("[inline]",clipp, img, ctm); leafcnt++;
//System.out.println("inline img "+lastleaf.getBbox());

//System.out.println("image @ x,"+ctm.getTranslateY()+", height="+img.getHeight()+", scale="+ctm.getScaleY());

			} else if (c2c3==('M'<<8)+'C'   //  tag 'BMC' - begin marked-content sequence
					|| c2c3==('D'<<8)+'C') { // tag properties 'BDC' - begin marked-content sequence
//System.out.println("marked: "+ops[0]+", "+ops[1]+" @ "+lastleaf);
				Map attrs = null;
				if (c2=='D') attrs = (Map)(ops[1].getClass()==CLASS_DICTIONARY? ops[1]: pdfr.getObject(propres.get(ops[1])));

				Span seq = (Span)Behavior.getInstance((String)ops[0], "multivalent.Span", attrs, scratchLayer);
				seq.open(lastleaf);
				markedseq.add(seq);     // set start point

			} else if (c2c3=='X') {    // -- 'BX' - begin a compatibility section: don't report unrecognized operators
				//fcompat = true;
			}
			break;

			case 'b':   // b, b*
			if (c2c3==' ') {   // -- 'b' - close, fill, and then stroke the path, using the nonzero winding number rule
				assert fvalidpath: "b";
				if (fvalidpath) {   // error to closePath() if empty
					if (pathrect==null) path.closePath();
					path.setWindingRule(GeneralPath.WIND_NON_ZERO);
					ffill = fstroke = true;
				}

			} else if (c2c3=='*') {    // -- 'b*' - close, fill, and then stroke the path, using the even-odd rule
				assert fvalidpath: "b*";
				if (fvalidpath) {
					if (pathrect==null) path.closePath();
					path.setWindingRule(GeneralPath.WIND_EVEN_ODD);
					ffill = fstroke = true;
				}
			}
			break;

			case 'C':   // CS
			if (c2c3=='S') {   // name 'CS' - set the color space to use for stroking operations
				sCS = pdfr.getColorSpace(ops[0], csres, patres); assert sCS!=null: "CS stroke "+pdfr.getObject(ops[0])+" in "+csres;
			}
			break;

			case 'c':   // c, cm, cs
			if (c2c3==' ') {    // (curpt-x, y-curpt) x1 y1 x2 y2 x3 y3 'c' - append a cubic Bezier curve to the current path: current point to x3 y3, with x1 y1 and x2 y2 as control points
				getDoubles(ops,d,6); ctm.transform(d,0, d,0, 3);
				/*if (pathlen==1 && peek=='S') simplepath = new CubicCurve2D.Double(d[0],d[1], d[2],d[3], d[4],d[5]);   // rare, so don't optimize
				else*/ path.curveTo((float)d[0],(float)d[1], (float)d[2],(float)d[3], (float)(curx=d[4]),(float)(cury=d[5]));
				pathlen+=100;    // probably more efficient than above

			} else if (c2c3=='m') {   // a b c d e f 'cm' - concatenate matrix to CTM
				getDoubles(ops,d,6); tmpat.setTransform(d[0], d[1], d[2], d[3], d[4], d[5]);
				if (!tmpat.isIdentity()) {
					ctm.concatenate(tmpat);
					if (tmpat.getType()!=AffineTransform.TYPE_TRANSLATION) fnewfont = true;
					// should recompute stroke attributes too
				}

			} else if (c2c3=='s') {    // name 'cs' - set the color space to use for nonstroking operations
				fCS = pdfr.getColorSpace(ops[0], csres, patres); assert fCS!=null: "cs fill "+pdfr.getObject(ops[0])+" in "+csres;
			}
			break;

			case 'D':   // Do, DP
			if (c2c3=='o') {    // name 'Do' -- paint XObject
				Leaf l = cmdDo((String)ops[0], xores, resources,  ctm, newfcolor, clipp,  d, ocrimgs);
				if (l!=null) { lastleaf=l; leafcnt++; }

			} else if (c2c3=='P') { // tag properties 'DP' - marked content point (see 'MP')
				// maybe have "Mark extends VObject" so can stuff properties
				if (lastleaf.isLeaf()) new Mark((Leaf)lastleaf, lastleaf.size());     // marked point lost if occurs before any content
			}
			break;

			case 'd':   // d, d0, d1
			if (c2c3==' ') {   // dash-array dash-phase 'd' - dash line (p.155; "[]0" or "[]n" to stop dashed, return to solid)
				Object[] oa = (Object[])ops[0];
				if (oa==OBJECT_NULL || oa.length==0) gs.dasharray = null; else getFloats(oa, gs.dasharray=new float[oa.length], oa.length);
				gs.dashphase = ((Number)ops[1]).floatValue();
				fnewline = true;

			} else if (c2c3=='0') { // wx wy 'd0' - set glyph width, and declare that color specified too
				clipp.bbox.width = ((Number)ops[0]).intValue() /* ctm.getScaleX()*/;     // unscaled in backdoor communication so Type 3 font can get more accurate, non-truncated-to-int value
				//fType3 = true;

			} else if (c2c3=='1') { // wx wy llx lly urx ury 'd1' - set glyph width and bounding box, and declare that color not specified
				clipp.bbox.width = ((Number)ops[0]).intValue();
				//fType3 = true;
			}
			break;

			case 'E':   // ET, EI, EMC, EX
			if (c2c3=='T') {    // -- 'ET'
				// text mode paramters retained across BT..ET blocks (font, Ts, Tr, ...)
//System.out.println("textp.size() = "+textp.size()+" @ ET");
				if (linep.size()==0) {
					if (linep!=lastleaf) linep.remove();
					else new FixedLeafAscii("",null, linep).getIbbox().setBounds((int)Math.round(Tm.getTranslateX()), (int)Math.round(Tm.getTranslateY()), 0,0);
				}
				//else if (linep.size()==1) { textp.appendChild(linep.childAt(0)); linep.remove(); } -- keep structure even though less efficient (already allocated so only space inefficient)

				if (textp.size()==0) textp.remove();
				//else sortY(textp);

				//textp=null; => recycled if next BT..ET very close

			} else if (c2c3=='I') { // 'EI' - end inline image -- handled in BI
				assert false;   // ignore if see out of context

			} else if (c2c3==('M'<<8)+'C') {    // 'EMC' - end marked-content sequence
				if (markedseq.size()>0) {
					Span seq = (Span)markedseq.remove(markedseq.size()-1);
					seq.close(lastleaf);
				}

			} else if (c2c3=='X') {    // -- 'EX' - end a compatibility section
				//fcompat = false;
			}
			break;

			case 'F':   // F
				assert c2c3==' ';   // 'F'=deprecated; identical to 'f'
				// fall through
			case 'f':   // f, f*
			if (c2c3==' ') {   // -- 'f' - fill the path, using the nonzero winding number rule
				path.setWindingRule(GeneralPath.WIND_NON_ZERO);
				ffill = true;

			} else if (c2c3=='*') {    // -- 'f*' - fill the path, using the even-odd rule to determine
				path.setWindingRule(GeneralPath.WIND_EVEN_ODD);
				ffill = true;
			}
			break;

			case 'G':   // G
			if (c2c3==' ') {   // gray 'G' - set DeviceGray color space and gray level for stroking
				sCS = ColorSpace.getInstance(ColorSpace.CS_GRAY);
				float gray=((Number)ops[0]).floatValue();
				newcolor = (gray==0f? Color.BLACK: gray==1f? Color.WHITE: new Color(gray, gray, gray, 1f)); // gray==0.5f: Color.GRAY... ?
			}
			break;

			case 'g':   // gs, g
			if (c2c3==' ') {   // gray 'g' - set DeviceGray color space and gray level for filling
				fCS = ColorSpace.getInstance(ColorSpace.CS_GRAY);
				float gray=((Number)ops[0]).floatValue();
				newfcolor = (gray==0f? Color.BLACK: gray==1f? Color.WHITE: new Color(gray, gray, gray, 1f));

			} else if (c2c3=='s') {    // dictName 'gs' - set the specified parameters in the graphics state.
				Map gsdicts = (Map)pdfr.getObject(resources.get("ExtGState"));
				Map gsdict = (Map)pdfr.getObject(gsdicts.get(ops[0]));
				cmdgs(gsdict, fontres, ctm, d, gs);

				if (gsdict.get("Font")!=null) fnewfont = true;
				fnewline = true;    // too many attributes to check
			}
			break;

			case 'h':   // h
			if (c2c3==' ') {   // -- 'h' - close the current subpath by appending a straight line segment from the current point to the starting point
				assert fvalidpath: "h";
				if (fvalidpath) {
					if (pathrect==null) path.closePath();  // rectangles already closed, but have seen "36 464.25 633.75 6.75 re h W n"
				}// catch (Exception e) { /*trying to close already-closed path*/ }
			}
			break;

			case 'I':   // ID
			if (c2c3=='D') {    // 'ID' - inline image data (between BI and EI)
				assert false;   //  handled in BI -- ignore if see out of context
			}
			break;

			case 'i':   // i
			if (c2c3==' ') {   // 0..100 'i' - flatness tolerance [not settable in Graphics2D]
				gs.flatness = ((Number)ops[0]).intValue();
			}
			break;

			case 'J':   // J
			if (c2c3==' ') {   // number 'J' - line cap style
				gs.linecap = ((Number)ops[0]).intValue();
				fnewline = true;
			}
			break;

			case 'j':   // j
			if (c2c3==' ') {   // number 'j' - line join style
				gs.linejoin = ((Number)ops[0]).intValue();
				fnewline = true;
			}
			break;

			case 'K':   // K
			if (c2c3==' ') {    // c m y k 'K' - set the color space to DeviceCMYK for stroking
				sCS = ColorSpaceCMYK.getInstance();
				getFloats(ops, cscomp, 4); float r=cscomp[0], g=cscomp[1], b=cscomp[2], k=cscomp[3];    // 'c' m y k - already used as variables -- and cmyk inverse of rgb so not so terrible
				newcolor = (r==0f && g==0f && b==0f && k==0f? Color.WHITE: r+k>=1f && g+k>=1f && b+k>=1f /*&& k==1f*/? Color.BLACK: new Color(sCS, cscomp, 1f));
			}
			break;

			case 'k':   // k
			if (c2c3==' ') {    // c m y k 'k' - set the color space to DeviceCMYK for nonstroking
				fCS = ColorSpaceCMYK.getInstance();
				getFloats(ops, cscomp, 4); float r=cscomp[0], g=cscomp[1], b=cscomp[2], k=cscomp[3];
				newfcolor = (r==0f && g==0f && b==0f && k==0f? Color.WHITE: r+k>=1f && g+k>=1f && b+k>=1f /*&& k==1f*/? Color.BLACK: new Color(fCS, cscomp, 1f));
			}
			break;

			case 'l':   // l
			if (c2c3==' ') {   // x y 'l' - append a straight line segment from the current point to the point (x, y)
//System.out.println(ops[0]+" "+ops[1]+" l");
				getDoubles(ops,d,2);
				assert pathline==null: d[0]+" "+d[1];
				ctm.transform(d,0, d,0, 1);
//if (lcnt_++ < 10) System.out.println("line to "+srcpt+" / "+transpt);
				if (pathlen==1 && (((peek=in.peek())<'0' || peek>'9') && peek!='.' && peek!='-'/*jdj200108*/)) pathline = new Line2D.Double(curx,cury, d[0],d[1]);    // JLS 15.7.4 Argument Lists are Evaluated Left-to-Right.  No need to set curx,cury because end of path
				else path.lineTo((float)(curx=d[0]), (float)(cury=d[1]));
				pathlen+=1000;
				//path.reset(); => keep initial 'm' in case bogus closePath
			}
			break;

			case 'M':   // M, MP
			if (c2c3==' ') {    // number 'M' - miter limit
				gs.miterlimit = ((Number)ops[0]).intValue();
				fnewline = true;

			} else if (c2c3=='P') { // tag 'MP' - marked content point (see 'DP')
				if (lastleaf.isLeaf()) new Mark((Leaf)lastleaf, lastleaf.size());     // marked point lost if occurs before any content
			}
			break;

			case 'm':   // m
			if (c2c3==' ') { // x y 'm' - begin a new subpath by moving the current point to coordinates (x, y)
				assert pathrect==null && pathline==null;
				//path.reset(); -- NO
				getDoubles(ops,d,2); ctm.transform(d,0, d,0, 1);
//if (lcnt_++ < 10) System.out.println("move to "+srcpt+" / "+transpt);
				// no special case for one 'm' only or 'm'/'m' as these are rare
				path.moveTo((float)(curx=d[0]), (float)(cury=d[1])); pathlen++;
				fvalidpath = true;  //-- move-only alone with nothing to paint shouldn't make valid, but it does
			}
			break;

			case 'n':   // n
			if (c2c3==' ') {   // -- 'n' - end the path object without filling or stroking it.
				//NO path.closePath();
				path.reset(); pathlen=0;  // clipping ops have already used path
				pathrect=null; pathline=null;
				fvalidpath = false;
			}
			break;

			case 'Q':   // Q
			if (c2c3==' ') {   // 'Q' - pop graphics stack
				if (gsstack.size()>0/*it happens*/) gs = (GraphicsState)gsstack.remove(gsstack.size()-1);

				// rather than closing all spans in pushed graphics state to reestablish previous state, we set the changes.
				// Some PDFs repeatedly push a graphics statck and set the exact same attributes within intervening drawing.
				// In this case, the redundancy does not create useless spans.
				fnewfont = true; newTr = gs.Tr;
				Tc = gs.Tc; Tw = gs.Tw; Tz = gs.Tz; TL = gs.TL; Ts = gs.Ts; //Tm = gs.Tm; Tlm = gs.Tlm;   // needed?
				fCS = gs.fCS; sCS = gs.sCS; newcolor = gs.strokecolor; newfcolor=gs.fillcolor;
//System.out.println("Q pop "+gs.fontdictkey+" @ "+pointsize+", tf="+tf);
				fnewline = true;
				ctm = gs.ctm;

				if (clipp!=gs.clip && clipp.size()==0) clipp.remove();      // "Serving PDFs on the Web" starts with "q Q"
				clipp = gs.clip;    // foolable: set clip, q, <nothing>, Q
				//clipr = clipp.getClip();
//if (lcnt_ < 10) System.out.println("Q: pop "+ctm);

				fvalidpath = false;     // "current path is not part of the graphics state"
			}
			break;

			case 'q':   // q
			if (c2c3==' ') {   // 'q' - push graphics stack
				// stuff state -- use possibly new settings, in case of sequence: <new-setting> 'q' ... 'Q', which would drop change in setting
				gs.Tr = newTr; gs.Tc = Tc; gs.Tw = Tw; gs.Tz = Tz; gs.TL = TL; gs.Ts = Ts; //gs.Tm = Tm; gs.Tlm = Tlm;
				gs.fCS = fCS; gs.sCS = sCS; gs.strokecolor = newcolor; gs.fillcolor = newfcolor;
				gs.ctm = ctm;
				gs.clip = clipp;

				gsstack.add(new GraphicsState(gs));     // push copy and keep using current

				//assert !fvalidpath;   // good idea, but don't enforce
//if (lcnt_ < 10) System.out.println("q: push "+ctm);
			}
			break;

			case 'R':   // RG
			if (c2c3=='G') {    // r g b 'RG' - set DeviceRGB color space and stroke color
				sCS = ColorSpace.getInstance(ColorSpace.CS_sRGB);   // CS_LINEAR_RGB? => worse
				getFloats(ops, cscomp, 3); float r=cscomp[0], g=cscomp[1], b=cscomp[2];
				newcolor = (r==0f && g==0f && b==0f? Color.BLACK: r==1f && g==1f && b==1f? Color.WHITE: new Color(r,g,b, 1f));  // r==1f && g==0f && b==0f: Color.RED: r==0f && g=1f && b==0f? Color.GREEN: r==0f && g==0f && b==1f: Color.BLUE ?
				//else newcolor=new Color(sCS, cscomp, 1f);  // during color space testing -- "alpha value of 1.0 or 255 means that the color is completely opaque"
			}
			break;

			case 'r':   // re, ri, rg
			if (c2c3=='e') {    // x y width height 're' - append a rectangle to the current path as a complete subpath, with lower-left corner (x, y) and dimensions width and height
				assert pathrect==null && pathline==null;    // Acrobat Core API Overview has rect after rect

				getDoubles(ops,d,4);
//System.out.print("re "+newfcolor+" "+d[0]+" "+d[1]+" "+d[2]+" "+d[3]+" => ");
//System.out.print("re "+d[2]+"x"+d[3]+" @ "+d[0]+","+d[1]+" => ");
				ctm.transform(d,0, d,0, 1); ctm.deltaTransform(d,2, d,2, 1);
				// FIX: doesn't pick up shear
				double x=curx=d[0], y=cury=d[1], w=d[2], h=d[3];
				if (w<0.0) { x+=w; w=-w; } /*else--negative and small*/ if (w<1.0) w=1.0;    // it happens!
				if (h<0.0) { y+=h; h=-h; } /*else*/ if (h<1.0) h=1.0;
//System.out.println("rectangle "+w+"x"+h+" @ "+x+","+y);
				Rectangle r = new Rectangle((int)x,(int)(y /*- h/*ll=>ul*/), (int)Math.round(w),(int)Math.round(h));    // upside down, so lower-left => upper-left -- but Java coordinates flip Y so OK.  Math.ceil() so clip gets bottom line.
				//assert r.width>0 && r.height>0: r.width+" "+r.height;
//System.out.println(" => "+phelps.text.Formats.pretty(r));
				if (!fvalidpath && (((peek=in.peek())<'0' || peek>'9') && peek!='.' && peek!='-')) { pathrect=r; pathlen=1; }
				//else if (pathlen==1 && ((peek<'0' || peek>'9') && peek!='.' && peek!='-')) { pathrect=r; pathlen=1; }  // ignore initial 'm'
				else { path.append(r, false); pathlen += 4; }
				fvalidpath = true;
				//if (!fvalidpath) { pathrect=r; fvalidpath=true; }      // if rectangle only shape in path, keep it simple and don't use GeneralPath

			} else if (c2c3=='g') {    // r g b 'rg' - set DeviceRGB color space and fill color
				fCS = ColorSpace.getInstance(ColorSpace.CS_sRGB);
				getFloats(ops, cscomp, 3); float r=cscomp[0], g=cscomp[1], b=cscomp[2];
				newfcolor = (r==0f && g==0f && b==0f? Color.BLACK: r==1f && g==1f && b==1f? Color.WHITE: new Color(r,g,b, 1f));     //new Color(fCS, cscomp, 1f);

			} else if (c2c3=='i') {   // name 'ri' - color rendering intent [no equivalent in Java 2D]
				gs.renderingintent = (String)ops[0];
			}
			break;

			case 'S':   // S, SC, SCN
			if (c2c3==' ') {   // ? -- 'S' - stroke the path
				fstroke = true;

			} else if (c2c3=='C' || c2c3==('C'<<8)+'N') {   // c1, ..., cn 'SC' - set stroking color; SCN "same as SC, but also supports Pattern, Separation, DeviceN, and ICCBased color spaces."
				if (opsi>0 && ops[opsi-1].getClass() == CLASS_NAME) {    // scn
					assert c2c3==('C'<<8)+'N';
					sCS = pdfr.getColorSpace(ops[opsi-1], csres, patres);
					opsi--;
				}

				if (opsi>0) {
					getFloats(ops, cscomp, Math.min(opsi,4));   // opsi can be 0 (Pattern color space)
					// scale to colorspace min..max
					//newcolor = new Color(sCS, cscomp, 1f);    // Java bug: Color(ColorSpace...) hardcodes component range to 0.0 .. 1.0
					float[] rgb = sCS.toRGB(cscomp);
					newcolor = new Color(rgb[0], rgb[1], rgb[2], 1f);
				}
			}
			break;

			case 's':   // s, sc, scn, sh
			if (c2c3==' ') {   // -- 's' - close and stroke the path
				assert fvalidpath: "s";
				if (fvalidpath) {
					if (pathrect==null) path.closePath();
					fstroke=true;
//System.out.println("s(troke) - path bounds = "+path.getBounds2D());
				}

			} else if (c2c3=='c' || c2c3==('c'<<8)+'n') {   // c1, ..., cn 'sc' - set nonstroking color; 'scn' "same as sc, but also supports Pattern, Separation, DeviceN, and ICCBased color spaces."
				if (opsi>0 && ops[opsi-1].getClass() == CLASS_NAME) {    // scn
					assert c2c3==('c'<<8)+'n';
//System.out.println("scn "+csname);
					fCS = pdfr.getColorSpace(ops[opsi-1], csres, patres);
					opsi--;
				}

				if (opsi>0) {
					getFloats(ops, cscomp, Math.min(opsi,4));
//System.out.println("sc/scn  "+opsi+": "+cscomp[0]+" "+cscomp[1]+" "+cscomp[2]+" in "+fCS);
					//newfcolor = new Color(fCS, cscomp, 1f);     // Java bug: see 'S' above
					float[] rgb = fCS.toRGB(cscomp);
//System.out.println(" => "+rgb[0]+" "+rgb[1]+" "+rgb[2]);
					newfcolor = new Color(rgb[0], rgb[1], rgb[2], 1f);
//System.out.println(" => "+newfcolor);
				}

			} else if (c2c3=='h') { // name 'sh' - shading pattern
				Map shdict = (Map)pdfr.getObject(shres.get(ops[0]));
				ColorSpace cs = pdfr.getColorSpace(shdict.get("ColorSpace"), csres, patres);
				Object[] oa = (Object[])pdfr.getObject(shdict.get("Bbox"));
				Rectangle bbox = (oa!=null? PDFReader.array2Rectangle(oa, ctm/*sh in user coords*/, false): clipp.getCrop());

				FixedLeafShade l=FixedLeafShade.getInstance(shdict, cs, bbox, clipp, pdfr/*this*/); lastleaf=l; leafcnt++;
				l.getBbox().setBounds(l.getIbbox()); l.setValid(true);
			}
			break;


			case '"':   // "
			if (c2c3==' ') {   // aw ac string " - move to the next line and show a text string
				getDoubles(ops,d,2);
				Tw=d[0]; Tc=d[1];
				//Tlm.translate(0.0, -TL);
				//Tm.setTransform(Tlm);
				ops[0]=ops[2];
			}
			//break; => fall through to "'"!

			case '\'':  // '
			if (c2c3==' ') {   // string ' - move to the next line and show a text string
				Tlm.translate(0.0, -TL);
				Tm.setTransform(Tlm);
				c2c3 = 'j'; // now pretend Tj
			}
			//break; => fall through to 'Tj'

			case 'T':   // Tc, Tw, Tz, TL, Tf, Tr, Ts, Td, TD, Tm, T*, Tj, TJ
			if (c2c3=='j' || c2c3=='J') {   // string 'Tj' / [string number ...] 'TJ' - show a text string
				// would like to split off into own method, but so much state to pass (two methods maybe, font setting and text drawing)
				if (lastleaf.isStruct()) lastleaf=linep;    // 'ET' will guarantee some text in initial BT..ET
				//lastleaf!=clipp? lastleaf: linep);    // lastleaf initially clipp, but since added textp and linep which bump Span.close() to next subtree, but can use linep here because know going to add some text (sigh)

				// 1. set font, maxr & spacew
//System.out.println("Tj "+fnewfont);   // +", tf="+tf);
				float newsize = 0f;
				if (fnewfont) {   // fscale is advisory now: verify that did indeed change -- pdfTex-0.13d generates redundant font changes
					// set new font and dependent state
					srcpt.setLocation(gs.pointsize,0.0); Tm.deltaTransform(srcpt, transpt);
					double zx=transpt.getX(), zy=transpt.getY();
					newsize = (float)Math.abs(zy==0.0? zx: zx==0.0? zy: Math.sqrt(zx*zx + zy*zy));  // rotated
					//newsize = (float)Math.abs(transpt.getX());    // until can handle rotated text
//if (newsize==0.0) System.out.println(newpointsize+" => "+transpt.getX()+"/"+transpt.getY());
//System.out.println("scaling "+tf.getFamily()+" to "+pointsize+" logical, "+newsize+" vs y="+transpt.getY());// +" within "+tmpat);
//System.out.println("scaling from "+(tf!=null? tf.getSize2D(): 0.0)+" to "+pointsize+"/"+newsize);

					if (fontdict==gs.fontdict && Math.abs(newsize - tf.getSize2D()) < 0.0001) { fnewfont=false; /*System.out.println("cancelled: "+newsize+" vs "+tf.getSize2D());*/ }
				}

				if (fnewfont) { // do here rather than at bottom with rest so to collect all changes immediately before drawing text (sometimes Tm set after Tf)
					if (fontspan!=null) {
						//System.out.println("close |"+lastleaf+"|");
						fontspan.close(lastleaf);
						assert !fshowshape || fontspan.isSet(): "can't add font span "+getName()+"  "+fontspan.getStart().leaf+" .. "+lastleaf;
						//assert spancnt>0 || fontspan.getStart().leaf == clipp.getFirstLeaf(): "attached "+fontspan.getStart().leaf+" vs "+clipp.getFirstLeaf();     // not true if start with graphics
						//System.out.println("font span "+fontspan.getStart()+" .. "+fontspan.getEnd());
						spancnt++;
					} //else System.out.println("first @ "+lastleaf+"/"+lastleaf.size()+"  is "+lastleaf.getLastLeaf());
						//assert fontspan.getStart().leaf==span.getStart().leaf && fontspan.getStart().offset==span.getStart().offset && fontspan.getEnd().leaf==span.getEnd().leaf && fontspan.getEnd().offset==span.getEnd().offset: fontspan.getStart()+".."+fontspan.getEnd()+"  vs  "+span.getStart()+".."+span.getEnd();

					fontdict=gs.fontdict; pointsize=gs.pointsize;
					tf = pdfr.getFont(fontdict, pointsize, newsize, page, Tm, this);
//System.out.println("new font "+tf.getFamily()+", "+pointsize+"/"+tf.getSize2D());
					maxr = tf.getMaxCharBounds(/*tfrc*/);

					//System.out.print("open |"+lastleaf+"| .. ");
					fontspan = (SpanPDF)Behavior.getInstance((DEBUG? tf.getFamily()+" "+/*pointsize*/tf.getSize2D(): tf.getFamily()), "multivalent.std.adaptor.pdf.SpanPDF", null, scratchLayer);
					fontspan.font = tf.getFont();
					fontspan.open(lastleaf);

					spacew = tf.measureText(' ') / Tm.getScaleX();
					concatthreshold = spacew * Tm.getScaleX() / 4.0;    // be conservative.  If wrong, bad spacing and long word-lines; if right, better word boundaries.

					fnewfont = false;     // set to false only here.  Other places can only signal a change that needs scaling.
				}
				if (!tf.canRender()) break;     // error or Type 0 (composite font -- no widths at toplevel)
				boolean fType3 = tf instanceof FontType3;   // needs special leaf type
				if (fType3) ((FontType3)tf).setPage(page);
//System.out.println("font="+tf+", tfrc="+tfrc/*+", space bounds "+tf.getStringBounds(" ", tfrc)*/);
//System.out.println("spacew (pixels) "+(spacew * Tm.getScaleX()));

				if (newTr != Tr /*|| c==-1*/) { // lesk-superbook has "0  Tr 21.3343 0  TD 3  Tr -0.085  Tc (add) Tj" for each word
//System.out.println("close "+Trspan+" @ "+lastleaf+"/"+lastleaf.size());
					if (Trspan!=null) { Trspan.close(lastleaf); spancnt++; /*System.out.println("Tr "+Trspan.Tr+" "+Trspan.getStart()+".."+Trspan.getEnd());*/ }

					Tr = newTr;

					if (Tr==0) { Trspan=null; vspancnt++; }
					else {
						Trspan = (SpanPDF)Behavior.getInstance("Tr"/*+Tr*/, "multivalent.std.adaptor.pdf.SpanPDF", null, scratchLayer);
						Trspan.Tr = Tr;
						Trspan.open(lastleaf);
					}
//Node openat=(lastleaf!=clipp? lastleaf: linep); /*if (lastleaf==clipp)*/ System.out.println("*** Tr = "+Tr+" => "+openat+"/"+openat.size()+", clipp="+clipp);
				}

				// 2. set up line
				double newbaseline = Tm.getTranslateY();
				if (/*Math.abs(subtract to compare?)*/newbaseline!=baseline && Math.abs(baseline-newbaseline) > 0.1) {     // if switch font, won't be exact
					if (linep.size()>0) {   // start new line?
						//if (linep.size()==1) { textp.appendChild(linep.childAt(0)); linep.remove(); }   // actually should keep structure
						linep = new FixedIHBox("line",null, textp/*clipp*/); fconcat = false;
					}
					baseline = newbaseline;     // for first word on line too
				}

				// 3. add text
				Object[] oa;
				if (c2c3=='j') { oa = Tja/*new Object[1]*/; oa[0] = ops[0]; }  // make Tj look like TJ
				else oa = (Object[])ops[0];    // array 'TJ' - show one or more text strings, allowing individual glyph positioning [often interword space, rather than kerning]

				double sTc = Tc * Tm.getScaleX(), kern1 = (sTc>=0.0? Math.floor(sTc): Math.ceil(sTc)); //assert kern1==(byte)sTc;
				boolean fspace1 = false;     // if just single space, retain, because can be part of marked sequence
				boolean frot = Tm.getShearX()!=0.0;
				FontRenderContext frc = (frot? new FontRenderContext(Tm/*new AffineTransform()/*.getShearInstance(1.0,1.0)*/, true, true): null);
				for (int i=0,imax=oa.length; i<imax; i++) {
					Object o = oa[i];

					if (o instanceof Number) { // count as word-separating space or kern within same word?  (less common than String, but shorter so more readable code)
						double kern = ((Number)o).doubleValue()/1000.0 * pointsize;     // not tf.getSize2D(), which incorporates Tm.getScaleX()
						Tm.translate(-kern, 0.0);   // regardless of possible concatenation adjust Tm as concat adjustment on leaf outside Tm translation due to hunk width

					} else { assert o.getClass()==CLASS_STRING;
						//if (sb.length()>0 && sb.charAt(sb.length()-1)==' ') sb.deleteCharAt(sb.length()-1);
						StringBuffer txt8 = (StringBuffer)o;
						String txt = tf.translate(txt8);
//System.out.println("Tj, |"+txt+"|, len="+txt.length());
//if (Math.abs(Tc * Tm.getScaleX()) > 1.0) System.out.print("Tc="+Tc+"=>"+(Tc * Tm.getScaleX())+"   ");
//if (DEBUG) System.out.println("Tj show text |"+txt+"|/"+txt.length()+" @ ("+Tm.getTranslateX()+","+(ph - Tm.getTranslateY())+")");//  vs  "+transpt);
//if (tf instanceof FontType3 /*&& txt8.charAt(0)<'a'*/) { for (int ii=0,iimax=txt8.length(); ii<iimax; ii++) System.out.print(Integer.toOctalString(txt8.charAt(ii))+" "); System.out.println(); }

						for (int s=0,smax=txt.length(), e; s<smax; s=e) {
							e = txt.indexOf(' ', s); if (e==-1) e=smax; else if (e==0 && i==0 && s==0 && ALL_WS.reset(txt).matches() /*&& markedseq.size()>0*/) { e=1; fspace1=true; }     // even if no markedseq, could be used for rendered effect or be sole child of clip, so have to keep

							// chop into word hunks at space character
							if (s<e) {  // could start with space
								// special case for 1-letter word?
								String sub = txt.substring(s,e);
//if (" ".equals(sub)) System.out.println("sub: |"+sub+"|, s="+s+", e="+e+", smax="+smax);

								double kern = kern1 * sub.length();
								double fw = tf.measureText(txt8, s, e) + kern;
								//double fw = (tf.canRender()/*can't render Type 0*/? tf.measureText(txt8, s, e): tf.getFont().getStringBounds(txt, IDENTITY_RENDER).getWidth()) + kern;
//System.out.println(sub+", fw="+fw+" @ x="+Tm.getTranslateX());
//+", spacew "+spacew+" vs "+(widths[32-firstch]*tf.getSize2D()/1000.0));

								// put text hunk in Leaf
								int bw = (int)Math.ceil(fw), bh = (int)Math.ceil(maxr.getHeight()), ascent = (int)Math.ceil(-maxr.getY());  // set baseline to ascent?
								//FixedLeafAsciiKern l;
								double dx = Tm.getTranslateX() - lastX;     // in pixels
//if (fconcat && c2c3=='J')
//System.out.println("concat: |"+/*l.getName()+*/"| + |"+txt+"|, "+dx+" <? "+concatthreshold));
//if ("S".equals(lastsub)) System.out.println("concat? S + |"+sub+"|, fconcat="+fconcat+", dx="+dx+" <? "+concatthreshold+", Ts="+Ts);
								if (frot /*or Tr special effects*/ && false/*until can handle*/) {
									GlyphVector gv = tf.getFont().createGlyphVector(frc, sub);
									Shape txtshp = gv.getOutline();
									Rectangle2D r2d = gv.getVisualBounds();
									//Rectangle r = new Rectangle((int)(r2d.getX()+Tm.getTranslateX()), (int)(r2d.getY()+Tm.getTranslateY()), (int)Math.ceil(r2d.getWidth()), (int)Math.ceil(r2d.getHeight()));
									Rectangle r = gv.getPixelBounds(frc, (float)Tm.getTranslateX(), (float)Tm.getTranslateY());
//if ("extend".equals(sub)) System.out.println("rotated |"+sub+"|, w="+fw+"/"+bw+"/"+bh+" in "+r);    //+" vs "+maxr);
									FixedLeafShape lshp = new FixedLeafShape("glyphs",null, linep/*clipp*/, txtshp, true, true); lastleaf=lshp; leafcnt++;
									lshp.getIbbox().setBounds(r); lshp.getBbox().setBounds(r); //lshp.setValid(true);
									//l=null;

								} else if (fType3) {
									FontType3 tf3 = (FontType3)tf;
									// => move to FixedLeafType3 format
									FixedLeafType3 l = new FixedLeafType3(sub,null, linep, tf3); lastleaf=l; leafcnt++;
									int w=0, minascent=0, maxdescent=0;
									for (int j=0,jmax=txt.length(); j<jmax; j++) {
										Node glyph = tf3.getGlyph(txt.charAt(j));
										Rectangle bbox = glyph.bbox;
										w += /*tf3.getAdvance(ch)--why not?*/bbox.width; minascent = Math.min(minascent, bbox.y); maxdescent = Math.max(maxdescent, bbox.height+bbox.y);
									}
									l.getIbbox().setBounds((int)Math.round(Tm.getTranslateX()), (int)Math.round(Tm.getTranslateY() + Ts * Tm.getScaleY() + minascent), w, maxdescent-minascent);
									l.getBbox().setBounds(l.getIbbox());
									l.baseline = -minascent;
									//l.setValid(true);

								} else if (fconcat /* && starts with letter?*/ /*&& baseline==lastBaseline -- guaranteed since same line*/ && Math.abs(dx) < concatthreshold /*-spacew < dx && dx < spacew/2.0*/ && /*for now*/Ts==0.0) {  // heuristic for determining if part of same word.  if so, concatenate with previous leaf
									//assert linep.size()>0 && linep.getLastChild().getName().length()>0;
									FixedLeafAsciiKern l = (FixedLeafAsciiKern)linep.getLastChild();  assert l==lastleaf;
									l.appendText(sub, (byte)kern1);  // updates kerning too
//System.out.println(l.getName()+" concat w/"+sub+", adjusting interword concat from "+l.getKernAt(l.size()-1)+" by "+kern1+" + "+dx+" = (byte)"+(byte)(l.getKernAt(l.size()-1)+dx));
									l.setKernAt(l.size()-1, (byte)(l.getKernAt(l.size()-1)+dx));  // adjust join point by inter-hunk kern from TJ number
									Rectangle ibbox = l.getIbbox();
									totalW += fw;
									ibbox.width = (int)Math.ceil(totalW); ibbox.height = Math.max(ibbox.height, bh);   // FIX: max height depends on ascent
									l.bbox.setSize(ibbox.width, ibbox.height); //l.baseline = Math.max(l.baseline, ascent); -- baseline is the same during TJ
									//l.setValid(true);   // be safe
									concatcnt++;

								} else {    // new leaf
//if (kern!=0) System.out.print("  kern="+kern);
// assert -128.0 < kern1 && kern1 < 127.0: kern1;

									FixedLeafAsciiKern l = new FixedLeafAsciiKern(sub,null, linep/*clipp*/, (byte)kern1); lastleaf=l; leafcnt++;     // should track kerning per character and bump by pixel when >= 1.0
									l.getIbbox().setBounds((int)Math.round(Tm.getTranslateX()), (int)Math.round(Tm.getTranslateY() + maxr.getY() + Ts * Tm.getScaleY()), bw,bh);   // FIX: Ts => span -- upside down
//System.out.println(sub+" ibbox = "+l.getIbbox()+": "+ph+" - "+Tm.getTranslateY()+"-"+Ts+"+"+r.getY());
									l.bbox.setBounds(l.getIbbox());
									//l.bbox.setSize(bw,bh);
									l.baseline = ascent;
									//l.setValid(true);   // efficiency hack (careful!)
									totalW = fw;
								}
								lastleaf.setValid(true);   // efficiency hack (careful!)
//System.out.println(lastleaf.getName()+" "+phelps.text.Formats.pretty(((Fixed)lastleaf).getIbbox()));

								// advance point according to width of text hunk vis-a-vis text parameters (Tc, ...)
								tmpat.setToTranslation(fw + (sTc*sub.length() - kern), 0.0); Tm.preConcatenate(tmpat);  // correct total kerning after rounded to nearest integer between characters
								lastX = Tm.getTranslateX();
								fconcat = !frot && !fType3 && /*Character.isLetter(*/sub.charAt(sub.length()-1) < 128/*)*/;   //true;--weird metrics on quotes and may substitute fonts.     // possible for next time (fixed later if fspace1)
//if (Tc!=0.0) System.out.println(sTc+" "+sub+": "+(sTc*sub.length())+" - "+kern);
//lastsub = sub;
							}

							// gobble space at that point
							if (fspace1) {
								Tm.translate(Tw, 0.0);  // word separator too
								fspace1=false;
								fconcat=false;  // fix earlier assumption

							} /*else -- multiple spaces*/
							if (e<smax && txt.charAt(e)==' ') {
								int spacecnt = 0;
								do { spacecnt++; e++; } while (e<smax && txt.charAt(e)==' ');
//System.out.print(spacew+", Tw="+Tw+", Tc="+Tc+", scale="+Tm.getScaleX()+" @ "+txt.substring(s,e)+"/"+txt.substring(e+1)+" "+Tm.getTranslateX());
//System.out.print(spacew+", scale="+Tm.getScaleX()+" @ "+txt.substring(s,e)+"/"+txt.substring(e+1));
//System.out.print(" => "+Tm.getTranslateX());
								Tm.translate(spacecnt * (spacew + Tw + Tc), 0.0);     // don't forget Tc on spaces
//System.out.println(" => "+Tm.getTranslateX());
								fconcat = false;    // no concat across spaces
							}
//if ("S".equals(lastsub)) System.out.println("S fconcat="+fconcat);
						}
					}
//if (DEBUG) System.out.println("TJ show text |"+txt+"|/"+txt.length()+" @ ("+Tm.getTranslateX()+","+(ph - Tm.getTranslateY())+")");// => "+transpt);
				}


			} else if (c2c3=='d') {    // tx ty 'Td' - translate Tlm, set Tm to it
				getDoubles(ops,d,2);
				//Tlm.deltaTransform(d,0, d,0, 1); -- NO! concatenated, so picks up prevailing scale
				Tlm.translate(d[0], d[1]);  // often see y=0 "1.9462 0 Td"
				Tm.setTransform(Tlm);

			} else if (c2c3=='D') {    // tx ty 'TD' - same as 'Td' + set leading to ty
				getDoubles(ops,d,2);
				//Tlm.deltaTransform(d,0, d,0, 1); -- NO!
				TL = -d[1];    // unscaled text space units -- minus!
				Tlm.translate(d[0], d[1]);
				Tm.setTransform(Tlm);

			} else if (c2c3=='m') {    // a b c d e f 'Tm' - set (not concat) the text matrix Tm, and the text line matrix Tlm
				double m00=Tm.getScaleX(), m01=Tm.getShearX(), m10=Tm.getShearY(), m11=Tm.getScaleY();

				getDoubles(ops,d,6); tmpat.setTransform(d[0], d[1], d[2], d[3], d[4], d[5]);
				Tm.setTransform(ctm);
				Tm.concatenate(tmpat);  // Tm actually Trm
				Tlm.setTransform(Tm);

				// microbrowser.pdf by Ghostscript 5.01 does 1 0 0 1 x1 y1 Tm ...(txt) Tj ... 1 0 0 1 x2 y2 !
				// livenotes.pdf by PDFMaker 5.0 for Word does Tm..Tj..Tm where Tm's have same scale
				if (m00!=Tm.getScaleX() || m01!=Tm.getShearX() || m10!=Tm.getShearY() || m11!=Tm.getScaleY()) fnewfont=true;

			} else if (c2c3=='*') {    // -- 'T*' - move to the start of the next line.  This operator has the same effect as the code 0 Tl 'Td'
				Tlm.translate(0.0, -TL);    // minus!
				Tm.setTransform(Tlm);

			// set text state
			} else if (c2c3=='c') {    // number 'Tc' - set the character spacing
				getDoubles(ops,d,1);
				//d[1]=0.0; Tm.deltaTransform(d,0, d,0, 1); -- unscaled text space units, but adjusted when text is drawn
				Tc=d[0];

			} else if (c2c3=='w') {    // number 'Tw' - set the word spacing
				getDoubles(ops,d,1);
				//d[1]=0.0; Tm.deltaTransform(d,0, d,0, 1);
				Tw=d[0];

			} else if (c2c3=='z') {    // number 'Tz' - set horizontal scaling
				getDoubles(ops,d,1);
				Tz=d[0];
//if (Tz!=1.0) sampledata("Tz "+Tz);

			} else if (c2c3=='L') {    // number 'TL' - text leading
				getDoubles(ops,d,1);
				//d[1]=0.0; Tm.deltaTransform(d,0, d,0, 1);
				TL=d[0];

			} else if (c2c3=='f') {    // font size 'Tf' - set current font
				assert fontres!=null: page;
				gs.fontdict = (Map)pdfr.getObject(fontres.get(ops[0]));  assert gs.fontdict!=null: ops[0]+" not in "+fontres;
				gs.pointsize = ((Number)ops[1]).doubleValue();
				fnewfont = true;

			} else if (c2c3=='r') {    // number 'Tr' - text rendering mode
				newTr = ((Number)ops[0]).intValue();   // check for validity?

			} else if (c2c3=='s') {    // number 'Ts' - text rise
				getDoubles(ops,d,1); d[1]=0.0; Tm.deltaTransform(d,0, d,0, 1);   // taken in V-space
				Ts = Math.abs(d[0]);  // FIX: Math.abs(d[1]=0.0? d[0]: d[0]==0.0? d[1]: Math.sqrt(d[0]*d[0] + d[1]*d[1])) -- coordinate with 'Tj'
//if (Ts!=0.0) sampledata(Ts+" Ts");
			}
			break;

			case 'v':   // v
			if (c2c3==' ') {   // x2 y2 x3 y3 'v' - append a cubic Bezier curve to the current path: current point to x3 y3, with current point and x2 y2 as control points
				getDoubles(ops,d,4); ctm.transform(d,0, d,0, 2);
				path.curveTo((float)curx,(float)cury, (float)d[0],(float)d[1], (float)(curx=d[2]),(float)(cury=d[3])); pathlen+=100;
			}
			break;

			// PDF Ref: "after the last path construction operator and before the path-painting operator that terminates a path object."
			// But Acrobat allows cheating path construction operators after W!
			// If something besides Ghostscript 5.10 does this (Ghostscript now at 7.04), then worry about it.
			case 'W':   // W, W*
			if (c2c3=='*' || c2c3==' ') {    // 'W'/'W*' - clipping nonzero/even-odd winding rule
				//assert fvalidpath: (c2c3=='*'? "W*": "W"); -- seen in matchingshapes.pdf
//System.out.println("empty clip");
				if (fvalidpath) {
					//if (clipp.size()==0) clipp.remove(); -- BUT 'q', 'W', 'Q', add to initial clip.  possible scenario: set clip, q, set clip (at which point first clip has no children), Q, add to first clip
					Rectangle bounds;
					//if (newclip contained in prevailing clip) do nothing
					if (pathrect!=null) {   // simpler, and more common -- faster?
						bounds = new Rectangle(pathrect.x, pathrect.y, pathrect.width+1, pathrect.height+1);    // + 1 for inclusive right and bottom edges
						// winding rule moot -- both give same result

						Shape oldshape = clipp.getClip();
						if (!(oldshape instanceof Rectangle) || !pathrect.contains((Rectangle)oldshape))    // larger rect doesn't enlarge clip
							clipp = new FixedIClip(c2c3=='*'? "W*": "W", null, clipp, new Rectangle(0,0, bounds.width, bounds.height), bounds);
						//else System.out.println("can't enlarge W "+bounds);  //-- e.g., 1677.pdf

						// no need to clear pathrect, even if in.peek=='n', since have to copy and transform anyhow

					} else if (pathline!=null) {    // (seen in jdj200108)
						// ignore?
						//System.out.println("clip to line, width="+gs.linewidth);
						bounds = null;

					} else {
						GeneralPath wpath;
						if (in.peek()=='n') { wpath=path; path=new GeneralPath();/*FixedIClip doesn't copy shape*/ } else { wpath = (GeneralPath)path.clone(); }  // don't need to clone if following token is 'n', which it almost always is
//if (peek=='n') System.out.println("no clone W");
						wpath.closePath();
						wpath.setWindingRule(c2c3=='*'? GeneralPath.WIND_EVEN_ODD: GeneralPath.WIND_NON_ZERO);

						//bounds = bounds.intersection(clipr);  // implicit in painting anyhow
						bounds = wpath.getBounds();

						tmpat.setToTranslation(-bounds.x, -bounds.y);     // clip (0,0) relative to bbox
						wpath.transform(tmpat);
//System.out.println((c2c3=='*'? "W*": "W")+" "+wpath+" in "+bounds);

						clipp = new FixedIClip(c2c3=='*'? "W*": "W", null, clipp, wpath, bounds);
					}
//System.out.println("clip: W"+(char)c2c3+": "+bounds);
				} //else System.out.println("W w/o path ");
			}
			break;

			case 'w':   // w
			if (c2c3==' ') {   // number 'w' - line width
				d[0]=((Number)ops[0]).doubleValue(); d[1]=0.0; ctm.deltaTransform(d,0, d,0, 1);     // can be rotated or sheared
				gs.linewidth = (float)Math.abs(d[1]==0.0? d[0]: d[0]==0.0? d[1]: Math.sqrt(d[0]*d[0] + d[1]*d[1]));  // in case rotated
				fnewline = true;
			}
			break;

			case 'y':   // y
			if (c2c3==' ') {   // x1 y1 x3 y3 'y' - append a cubic Bezier curve to the current path: current point to the point x3 y3, using x1 y1 and x3 y3 as control points
				getDoubles(ops,d,4); ctm.transform(d,0, d,0, 2);
				path.curveTo((float)d[0],(float)d[1], (float)d[2],(float)d[3], (float)(curx=d[2]),(float)(cury=d[3])); pathlen+=100;
			}
			break;

			case '%':   // %
				// if want to save content of comment, have to disentangle c2c3
				while ((c=in.read())!=-1 && c!='\r' && c!='\n') {/**/}    // comment - ignore for now, LATER make comment node
				//in.unread(c);
				//if (c=='\r' && (c=in.read())!='\n') in.unread(c); -- zap all whitespace
				PDFReader.eatSpace(in);
			break;

			//case -1:    // end of page => handled in loop test

			default:    // doesn't catch bad commands that start with same letter as valid command
				assert false: (char)c+" / "+c;
				throw new ParseException("invalid command: "+(char)c+"...");
				//break;  // corrupt input: bail out to close up spans => no need
			}

			// CLEAN UP for next command
			opsi = 0;   // clear stack


			// "painted" PATH, make another (do before span attachment, which may rely on shape created here just now)
			if (fstroke || ffill) {
				// collect all attributes just before shape, as with font attributes and text
//System.out.println("gs.linewidth = "+gs.linewidth);
				if (fnewline && ((gs.linewidth = (gs.linewidth < 1f? 1f: gs.linewidth))!=linewidth
					|| gs.linecap!=linecap || gs.linejoin!=linejoin     // if checking all these attributes performance drag, set flag that triggers more complete check
					|| gs.miterlimit!=miterlimit/*[].equals() same as ==*/ || gs.dashphase!=dashphase || !Arrays.equals(gs.dasharray, dasharray))
				) {
					fnewline = false;
					if (strokespan!=null) { strokespan.close(lastleaf); spancnt++; }

					linewidth=gs.linewidth; linecap=gs.linecap; linejoin=gs.linejoin; miterlimit=gs.miterlimit; dasharray=gs.dasharray; dashphase=gs.dashphase;
//System.out.println("line diff @ "+cmdcnt+": "+linewidth+", cap="+linecap+", join="+linejoin+", miter="+miterlimit+", "+dasharray+", "+dashphase);
					if (dasharray != null) {    // Neither Java nor Acrobat like dash array elements of 0f, so filter out
						int dai=0; for (int i=0,imax=dasharray.length; i<imax; i++) if (dasharray[i] > 0f) dasharray[dai++] = dasharray[i];
						if (dai < dasharray.length) { float[] newda=new float[dai]; System.arraycopy(dasharray,0, newda,0, dai); dasharray=newda; }
					}
					BasicStroke bs = new BasicStroke(linewidth, linecap, linejoin, miterlimit, dasharray, dashphase);

					if (Context.DEFAULT_STROKE.equals(bs)) { strokespan=null; vspancnt++; }
					else {
					//if (/*linewidth!=1.0*/(linewidth - 1.0) > 0.25) {   // in pixels
//System.out.print(/*"non-DEFAULT_STROKE: "+*/linewidth+" "+linecap+" "+linejoin+" "+miterlimit+" "+dashphase+" [ "); if (dasharray!=null) for (int i=0,imax=dasharray.length; i<imax; i++) System.out.print(dasharray[i]+" "); System.out.println("]");
						strokespan = (StrokeSpan)Behavior.getInstance("width"/*+linewidth*/+linejoin, "multivalent.std.span.StrokeSpan", null, scratchLayer);
						strokespan.setStroke(bs);
//System.out.println("line width = "+linewidth);
						// should only set what changed from before... but only one active span at a time so can't combine effects
						//strokespan.linewidth = linewidth;
						//strokespan.linecap = linecap; strokespan.linejoin = linejoin; strokespan.miterlimit = miterlimit;
						//strokespan.dasharray = dasharray; strokespan.dashphase = dashphase;
						strokespan.open(lastleaf);
					}
				}

// => method
				// bug: can fill and stroke path, so don't clear path until 'n' or 'Q'
				//assert fvalidpath: (fstroke? "stroke": "")+" "+(ffill? "fill": "")+" "+(char)c;     // I guess this is ok, just ignore?
				if (!fshowshape) {
					pathrect=null; pathline=null; path.reset();
					//System.out.print("S");

				} else if (fvalidpath) {

					// try to make longer paths by appending to previous.  have to have same fill/stroke uninterrupted by text or color or other changes
					Shape shape; Rectangle bounds; String name;
					//Shape shape=null; Rectangle bounds=null; String name=null;
					if (pathrect!=null) {  assert pathlen==1: pathlen;
//System.out.print("rect "+pathrect);
						bounds=pathrect;
						shape = new Rectangle(0,0, pathrect.width,pathrect.height);
						name = "rect";

						pathrect=null;
						assert pathline==null: pathline;
						//path.reset();   // just in case had close on rect => check before closepath, because no initial SEG_MOVETO

					} else if (pathline!=null) {  // line -- pretty rare, actually
						double x1=pathline.getX1(),y1=pathline.getY1(), x2=pathline.getX2(),y2=pathline.getY2(), xmin, ymin, w2d, h2d;
						if (x1<=x2) { xmin=x1; w2d=x2-x1; } else { xmin=x2; w2d=x1-x2; }
						if (y1<=y2) { ymin=y1; h2d=y2-y1; } else { ymin=y2; h2d=y1-y2; }

						/*if (!fexact  && w2d*linewidth<PIXEL_INVISIBLE && w2d<PIXEL_INVISIBLE /* && w2d>0.0 && h2d>0.0* /) { System.out.print("V"); }
						else {*/
							bounds = new Rectangle((int)Math.round(xmin), (int)Math.round(ymin), (w2d>1.0? (int)Math.ceil(w2d): 1), (h2d>1.0? (int)Math.ceil(h2d): 1));
//System.out.print("line "); //+x1+","+y1+".."+x2+","+y2+" => "+(x1-xmin)+","+(y1-ymin)+".."+(x2-xmin)+","+(y2-ymin));
							shape = new Line2D.Double(x1-xmin,y1-ymin, x2-xmin,y2-ymin);    // also used for points, but Point2D can't be drawn (it does not implement Shape)
							name = (DEBUG? "line"+(pathcnt): "line");
						//}

						pathline=null; path.reset();

					} else {    // GeneralPath
						Rectangle2D r2d = path.getBounds2D();   // new floats to zero path
						double w2d=r2d.getWidth(), h2d=r2d.getHeight();     //assert (w2d>0.0 && h2d>0.0) || gs.linewidth>0.0;   // matchingshapes.pdf has 0x0 w/width=3
//if (r2d.getWidth()==0 || r2d.getHeight()==0) System.out.println(r.width+"x"+r.height+" w/line width="+gs.linewidth);
//if (lcnt_ < 10) System.out.println("create path bounds = "+/*l.getIbbox()+" vs "+*/r);
						//if (r2d.width!=0 || r2d.height!=0 || gs.linewidth>0.0) {
						/*if (!fexact  && w2d*linewidth<PIXEL_INVISIBLE && w2d<PIXEL_INVISIBLE  && w2d>0.0 && h2d>0.0) { path.reset(); System.out.print("V"); }
						else {*/
							bounds = new Rectangle((int)Math.round(r2d.getX()), (int)Math.round(r2d.getY()), (w2d>1.0? (int)Math.ceil(w2d): 1), (h2d>1.0? (int)Math.ceil(h2d): 1));

							tmpat.setToTranslation(-r2d.getX(), -r2d.getY());    // path (0,0) relative to bbox
							path.transform(tmpat);
							shape = path;   //path.createTransformedShape(tmpat); -- don't need to copy
//if (r2d.getX()<0.0 || r2d.getY()<0.0) System.out.println("negative path "+phelps.text.Formats.pretty(r2d)); // OK
							name = (DEBUG? "path"+pathcnt: "path");

							path = new GeneralPath();
						//}
					}
						//} else path.reset();

//if (ffill && Color.WHITE.equals(fcolor) && shape instanceof Rectangle) System.out.println("danger");
/*					// stroke after stroke w/o attribute change => make longer path
					if (lastS && c=='S' && c2c3==' ' && lastleaf.sizeSticky()==0 && lastleaf instanceof FixedLeafShape) {
						FixedLeafShape l = (FixedLeafShape)lastleaf;
						Shape s = l.getShape();
if (firstS) System.out.print(s+" / "+l.getIbbox()+"  +  "+shape+" / "+bounds);
						GeneralPath lastpath;
						if (s instanceof GeneralPath) lastpath=(GeneralPath)s;
						else { lastpath=new GeneralPath(); lastpath.append(s, false); l.setShape(lastpath); }

						lastpath.append((pathrect!=null? (Shape)pathrect: pathline!=null? (Shape)pathline: (Shape)path), false);
						l.getIbbox().add(bounds); l.getBbox().add(bounds);
if (firstS) { System.out.println("  =  "+lastpath+" / "+l.getIbbox()); firstS=false; }
System.out.print("+");
*/
					//} else {
					if (shape!=null) {
						FixedLeafShape l = new FixedLeafShape(name,null, clipp, shape, fstroke, ffill); lastleaf=l; leafcnt++;
						l.getIbbox().setBounds(bounds); l.getBbox().setBounds(bounds); l.setValid(true);   // prevent double format
						pathlens[pathcnt]=pathlen; pathlen=0; if (pathcnt+1<pathlens.length) pathcnt++;
					}

				} else {
//sampledata("additional "+(fstroke? "stroke": "fill")+" on "+lastleaf);
					// maybe stroking and filling: if last leaf was LeafShape, set other stroke/fill flag
					//System.out.println("fill/stroke invalid path: "+(char)c);
					if (lastleaf instanceof FixedLeafShape) {
						FixedLeafShape l = (FixedLeafShape)lastleaf;
						if (fstroke) l.setStroke(true); else l.setFill(true);
					}
				}
				fvalidpath = false;
				fstroke = ffill = false;
				//lastS = (c=='S');   // last fill/stroke op was 'S'
			}


			// Color state transitions, which apply to text and splines.
			// problem with pageroot.getLastLeaf():  textp fooled if BT..ET w/o text to anchor and just for font, pageroot fooled by form which adds last child, clipp... -- should match close(), above and at end
			// textp fooled if BT..ET w/o text to anchor, pageroot fooled by form which adds last child, clipp... -- should match close(), above and at end
			if (color!=newcolor/*after every command so be fast*/ && !color.equals(newcolor)) {    // pdfTeX-0.13d generates redundant color changes: 1 1 1 1 k => 1 1 1 1 k
				//boolean fok = true;     // can come out of q..Q to different color, then back to q..Q without drawing anything, leaving a useless span... which we can reuse
				if (sspan!=null) { if (sspan.close(lastleaf)) spancnt++; else sspan.destroy()/*moveq(null)*/; }    //System.out.println("stroke span "+color);
//if (sspan!=null && sspan.getStart().equals(sspan.getEnd())) System.out.println("0-len stroke: "+sspan.getName()/*(sspan.stroke.getRed()/255.0)+" "+(sspan.stroke.getGreen()/255.0)+ " "+(sspan.stroke.getBlue()/255.0)*/);

				color = newcolor; assert color!=null: color;

				if (Color.BLACK.equals(color)) { sspan=null; vspancnt++; /*System.out.print("S");*/ }     // don't make span if same as stylesheet foreground setting
				else {
					sspan = (SpanPDF)Behavior.getInstance((DEBUG? "stroke "+Integer.toHexString(color.getRGB()): "stroke"), "multivalent.std.adaptor.pdf.SpanPDF", null, scratchLayer);
					sspan.stroke = color;
					sspan.open(lastleaf);
				}
			}
			if (fcolor!=newfcolor && !fcolor.equals(newfcolor)) {
				if (fillspan!=null) { if (fillspan.close(lastleaf)) spancnt++; else fillspan.destroy()/*moveq(null)*/; }    //System.out.println("fill "+fcolor+" @ "+(char)c);

				fcolor = newfcolor; assert fcolor!=null: fcolor;

				if (Color.BLACK.equals(fcolor)) { fillspan=null; vspancnt++; /*System.out.print("F");*/ }
				else {
					fillspan = (SpanPDF)Behavior.getInstance((DEBUG? "fill "+Integer.toHexString(fcolor.getRGB()): "fill"), "multivalent.std.adaptor.pdf.SpanPDF", null, scratchLayer);
					fillspan.fill = fcolor;
					fillspan.open(lastleaf);
				}
			}
		}
	}

/*	if (PERF && pathcnt>0) {
		System.out.print(pathcnt+" paths:  ");
		pathlens[pathcnt++]=Integer.MAX_VALUE;/*sentinal* / Arrays.sort(pathlens, 0, pathcnt);
		for (int i=0, j=0, lasti=pathlens[i]; i<pathcnt; i++) if (pathlens[i]!=lasti) { System.out.print(lasti+"x"+j+"  "); j=1; lasti=pathlens[i]; } else j++;
		System.out.println();
	}*/


	// CLEAN UP at end of page / form
	// A. text only
//System.out.println("closeAll on "+pdf);
	spancnt += Span.closeAll(clipp);
//Span.dumpPending();

/*	if (fontspan!=null /*&& !fnewfont/*exists some unspanned text -- could have 'cm' with no more text* /) { fontspan.close(lastleaf); spancnt++; }  // text only
	if (Trspan!=null) { Trspan.close(lastleaf); spancnt++; }
	if (strokespan!=null) { strokespan.close(lastleaf); spancnt++; }    // splines only
	if (sspan!=null) { sspan.close(lastleaf); spancnt++; }  // all objects
	if (fillspan!=null) { fillspan.close(lastleaf); spancnt++; }
	+ possible unclosed marked sections
*/
// maybe one big SpanPDF with everything, and check against virgin SpanPDF to see if should keep

	if (Multivalent.MONITOR && ocrimgs/*or doc*/!=null/*Type 3 use*/) {
		/*if (cmdcnt>0)*/ System.out.println(cmdcnt+" cmds, "+leafcnt+" leaves, "+spancnt+" spans ("+vspancnt+" saved), "+concatcnt+" concats, "+pathcnt+" paths, time="+(System.currentTimeMillis()-start));    // && # of leaves and spans
		//for (int i=INTS_MIN; i<=INTS_MAX; i++) if (ihist[i-INTS_MIN]>5) System.out.print(i+"/"+ihist[i-INTS_MIN]+" ");  System.out.println();
		//System.out.println("dcnt = "+dhist+", saved = "+dnot);
	}

	return cmdcnt;
  }


  private Leaf cmdDo(String xname, Map xores, Map resources,  AffineTransform ctm, Color newfcolor, FixedIClip clipp,  double[] d, List ocrimgs) throws IOException,ParseException {
	Leaf l = null;

	PDFReader pdfr = pdfr_;
	IRef iref = (IRef)xores.get(xname);
	Map xobj = (Map)pdfr.getObject(iref); assert xobj!=null: xname+" in "+xores+" -> "+iref;
	String subtype = (String)pdfr.getObject(xobj.get("Subtype"));    // probably always a literal, but getObject cheap
//System.out.println("XObject: "+xname+": "+xobj);

	if ("Image".equals(subtype)) {
//System.out.print(xname+" => "+iref);
		if ((getHints() & HINT_NO_IMAGE) == 0) {
			BufferedImage img = pdfr.getImage(iref/*NOT xobj*/, ctm, newfcolor);
			if (img!=null) {
				l = appendImage(xname, clipp, img, ctm);

				String imgtype = Images.getFilter(xobj, pdfr/*this*/);
				if (("CCITTFaxDecode".equals(imgtype) || "JBIG2Decode".equals(imgtype)) && ocrimgs!=null/*during Type 3*/ /*&& .width>1 && height>1*/) ocrimgs.add(l);
				//System.out.println(l.getIbbox()+" vs "+cropbox_);
			} else sampledata("null image "+iref+": "+xname+" / "+xobj.get("Name")+", subtype="+subtype);   // null if JBIG2 or i/o or JPEG flavor not supported by Java
		}

	} else if ("Form".equals(subtype)) {    // not like HTML form, rather more like subroutine
//System.out.println("Form XObject, bbox="+xobj.get("BBox"));
		// q, concat /Matrix with CTM, clip according to /BBox, paint content stream, Q
		// check that "FormType" is 1
		AffineTransform formAT = new AffineTransform(ctm);
		Object[] oa = (Object[])pdfr.getObject(xobj.get("Matrix"));
		if (oa!=null) { assert oa.length==6;
			getDoubles(oa,d,6); formAT.concatenate(new AffineTransform(d[0], d[1], d[2], d[3], d[4], d[5]));
//if (!tmpat.isIdentity()) System.out.println(ctm+" => "+formAT);
			//if (!tmpat.isIdentity()) sampledata("Form with non-Identity /Matrix "+xname+"  "+tmpat);    // concat to formAT
		}
		// clip to BBox
		FixedIClip formClip = clipp;
		Object o = pdfr.getObject(xobj.get("BBox"));
		if (o!=null) { assert o.getClass()==CLASS_ARRAY;
			Rectangle r = PDFReader.array2Rectangle((Object[])o, formAT, false);
//System.out.println("bbox = "+r);
			formClip = new FixedIClip(xname/*"formclip"*/, null, clipp, new Rectangle(0,0, r.width, r.height), r);
		}
		if (xobj.get("Resources")==null) xobj.put("Resources", resources);  // if no resources, inherit Page's

		CompositeInputStream formin = pdfr.getInputStream(iref);
		/*int fcmdcnt =*/ buildStream(xobj, /*pageroot* /clipp*/formClip, formAT, formin, ocrimgs);     // assumes Form independent except for CTM, which isn't necessarily so
		formin.close();
		//if (fcmdcnt==0) System.out.println("empty Form: "+iref);  -- EPodd has a lot of these => can have commands but still no additions to tree
//if (formClip.size()==7) dumpIbbox(formClip, 0);

		//if (formClip.size()==0 && formClip!=clipp) formClip.remove();
		//else l = clipp.getLastLeaf();    // forms self-contained, but marked content across form
		//if (lastform!=null) lastleaf=lastform;
		l = formClip.getLastLeaf();
		if (l==null && formClip!=clipp) formClip.remove();
//System.out.println("last leaf = "+l+" (==null? "+(l==null)+")");

	} else if ("PS".equals(subtype)) {  // "Note: Since PDF 1.3 encompasses all of the Adobe imaging model features of the PostScript language, there is no longer any reason to use PostScript XObjects.  This feature is likely to be removed from PDF in a future version."
		//System.err.println("contains embedded PostScript, which is obsolete in PDF -- redistill"); -- only shown in printed version anyhow

/*	} else if ("Group".equals(subtype)) { /Type Group /S /Transparency
		sampledata("Group Form "+xname);
*/
	} else { assert false: subtype; }

	return l;
  }


  private void cmdgs(Map gsdict, Map fontres, AffineTransform ctm, double[] d,  GraphicsState gs) throws IOException {
	PDFReader pdfr = pdfr_;
	Object o = pdfr.getObject(gsdict.get("Type"));  assert o==null || "ExtGState".equals(o);

	if ((o=pdfr.getObject(gsdict.get("Font")))!=null) { // array - [font-dict-indirect-ref size] (same as Tf command)
		Object[] oa = (Object[])o;
		gs.fontdict = (Map)pdfr.getObject(fontres.get(oa[0]));  assert gs.fontdict!=null: oa[0]+" not in "+fontres;
		gs.pointsize = ((Number)oa[1]).doubleValue();
	}
	if ((o=pdfr.getObject(gsdict.get("LW")))!=null) {    // number - line width
		d[0]=((Number)o).doubleValue(); d[1]=0.0; ctm.deltaTransform(d,0, d,0, 1);
		gs.linewidth = (float)Math.abs(d[1]==0.0? d[0]: d[0]==0.0? d[1]: Math.sqrt(d[0]*d[0] + d[1]*d[1]));
	}
	if ((o=pdfr.getObject(gsdict.get("LC")))!=null) gs.linecap = ((Number)o).intValue();   // integer - line cap style
	if ((o=pdfr.getObject(gsdict.get("LJ")))!=null) gs.linejoin = ((Number)o).intValue();  // integer - line join style
	if ((o=pdfr.getObject(gsdict.get("ML")))!=null) gs.miterlimit = ((Number)o).floatValue();   // number - miter limit

	if ((o=pdfr.getObject(gsdict.get("D")))!=null) {    // array - line dash pattern
		Object[] oa0 = (Object[])o, oa = (Object[])oa0;
		if (oa==OBJECT_NULL || oa.length==0) gs.dasharray = null; else getFloats(oa, gs.dasharray=new float[oa.length], oa.length);
		gs.dashphase = ((Number)oa0[1]).floatValue();
	}

	if ((o=pdfr.getObject(gsdict.get("RI")))!=null) gs.renderingintent = (String)o;   // name - rendering intent

	/* overprinting not really applicable to screen
	if ((o=pdfr.getObject(gsdict.get("OP")))!=null) {}   // boolean - overprinting everywhere
	if ((o=pdfr.getObject(gsdict.get("op")))!=null) {}   // boolean - overprint on non-stroke
	if ((o=pdfr.getObject(gsdict.get("OPM")))!=null) {}  // integer - overprint mode	*/

	/* not applicable -- RGB=>CMYK so print only
	if ((o=pdfr.getObject(gsdict.get("BG2")))!=null ||   // function or name - same as BG except can be "Default" (BG2 takes precedence)
		(o=pdfr.getObject(gsdict.get("BG")))!=null) {}   // function - black-generation function
	if ((o=pdfr.getObject(gsdict.get("UCR2")))!=null ||  // function or name - same as UCR except can be "Default" (UCR2 takes precedence)
		(o=pdfr.getObject(gsdict.get("UCR")))!=null) {}  // function - undercolor-removal function	*/

	if ((o=pdfr.getObject(gsdict.get("TR2")))!=null ||   // function or name - same as TR + "Default" (TR2 takes precedence)
		(o=pdfr.getObject(gsdict.get("TR")))!=null) {    // function, array - transfer function
	}
	if ((o=pdfr.getObject(gsdict.get("HT")))!=null) {    // dictionary - halftone dictionary or stream
	}
	if ((o=pdfr.getObject(gsdict.get("FL")))!=null) gs.flatness = ((Number)o).intValue();   // number - flatness tolerance
	if ((o=pdfr.getObject(gsdict.get("SM")))!=null) gs.smoothness = ((Number)o).doubleValue();     // number - smoothness tolerance

	if ((o=pdfr.getObject(gsdict.get("SA")))!=null) {    // boolean - apply auto stroke adjustment
		//RenderingHints.KEY_STROKE_xxx ?
	}
	if ((o=pdfr.getObject(gsdict.get("BM")))!=null) {    // name or array - blend mode
	}
	if ((o=pdfr.getObject(gsdict.get("SMask")))!=null) {   // dictionary or name - soft mask
	}
	if ((o=pdfr.getObject(gsdict.get("CA")))!=null) {    // number - stroking alpha constant
		gs.alphastroke = ((Number)o).floatValue();
if (gs.alphastroke != 1.0) PDF.sampledata("transparency (CA - stroking alpha)");
	}
	if ((o=pdfr.getObject(gsdict.get("ca")))!=null) {    // number - nonstroking alpha constant
		gs.alphanonstroke = ((Number)o).floatValue();
if (gs.alphanonstroke != 1.0) PDF.sampledata("transparency (ca - nonstroking alpha)");
	}
	if ((o=pdfr.getObject(gsdict.get("AIS")))!=null) {   // boolean - alpha source flag
	}
	if ((o=pdfr.getObject(gsdict.get("TK")))!=null) {    // boolean - text knockout
	}
  }


  private Leaf appendImage(String name, INode parent, BufferedImage img, AffineTransform ctm) {
	FixedLeafImage l = new FixedLeafImage(name,null, parent, img); //lastleaf=l; leafcnt++;
//System.out.println("img "+img.getWidth()+"x"+img.getHeight()+" @ "+img.getMinX()+","+img.getMinY());
	double majorx = Math.abs(ctm.getScaleX()) > Math.abs(ctm.getShearX())? ctm.getScaleX(): ctm.getShearX(),    // don't be fooled by a little shearing
		   majory = Math.abs(ctm.getScaleY()) > Math.abs(ctm.getShearY())? ctm.getScaleY(): ctm.getShearY();
	double left = ctm.getTranslateX() - (majorx<0.0/*ctm.getScaleX()<0.0 ^ ctm.getShearX()<0.0*/? img.getWidth(): 0.0),
		top = ctm.getTranslateY() - (majory<0.0? img.getHeight(): 0.0);
/*	srcpt.setLocation(1.0, 1.0); ctm.deltaTransform(srcpt, transpt);    // valid method also
System.out.println(srcpt+" => "+transpt);
	double left = ctm.getTranslateX() - (transpt.getX()>=0.0? 0.0: -transpt.getX()),
		top = ctm.getTranslateY() - (transpt.getY()<0.0? -transpt.getY(): 0.0);
System.out.println(ctm+": "+img.getWidth()+"x"+img.getHeight()+" => @"+left+","+top);*/
	l.getIbbox().setBounds((int)left, (int)top, img.getWidth(), img.getHeight());
	// could also l.bbox.setBounds(...); l.setValid(true);
//System.out.println("adding image "+name+" "+" @ "+l.getIbbox()+" "+ctm);

	return l;
  }



  /** Check that all leaves are valid and bbox.equals(ibbox). */
  private boolean checkTree(String id, INode pageroot) {
	for (Leaf l = pageroot.getFirstLeaf(), endl = (l!=null? pageroot.getLastLeaf().getNextLeaf(): null); l!=endl; l=l.getNextLeaf()) {
		assert l.isValid() || !(l instanceof FixedLeafAsciiKern): id+": "+l+" "+l.getClass().getName()+" "+l.getBbox();    // not images
		//assert l.bbox!=f.getIbbox(): l;
		//assert l.bbox.equals(f.getIbbox()): l.bbox+" vs "+f.getIbbox();
	}

	return true;    // OK
  }



  // => Anno class?
  private void createAnnots(Map pagedict, INode root) throws IOException {
	Object[] annots = (Object[])pdfr_.getObject(pagedict.get("Annots"));

//System.out.println(annots.length+" annots @ "+System.currentTimeMillis());
	Browser br = getBrowser();
	if (annots!=null && br!=null) for (int i=0,imax=annots.length; i<imax; i++) {
//System.out.print(" "+i); System.out.flush();
		br.event/*q--before close RAF*/(new SemanticEvent(br, Anno.MSG_CREATE, pdfr_.getObject(annots[i]), this, root));
	}
//System.out.println("done "+System.currentTimeMillis());
  }



  // PROTOCOLS

  /**
	Parses individual page indicated in {@link Document#ATTR_PAGE} of <var>parent</var>'s containing {@link Document}
	and returns formatted document tree rooted at <var>parent</var> as <a href='#doctree'>described</a> above.
	@return root of PDF subtree under <var>parent</var>
  */
  public Object parse(INode parent) throws IOException, ParseException {
	Document doc = parent.getDocument();
	if (pdfr_ == null) init(doc);
	if (fail_ != null) { new LeafAscii("Error opening PDF: "+fail_,null, parent); return parent; }
//System.out.println("layer = "+getLayer());

	//StyleSheet ss = new StyleSheet();   //PDFStyleSheet();
	//ss.setCascade(doc);
	//doc.setStyleSheet(ss);
	StyleSheet ss = doc.getStyleSheet();
	// we don't pick up PDF.css because not a CSS Style Sheet
	CLGeneral cl = (CLGeneral)ss.get("pdf");
	if (cl==null) { cl=new CLGeneral(); ss.put("pdf", cl); }
	cl.setStroke(Color.BLACK);  // augment CSS to be able to set this in stylesheet?
	cl.setBackground(Color.WHITE);    // refresh from last document -- FIX
	cl.setForeground(Color.BLACK);    // refresh from last document -- FIX


	// at start of each page, check that underling file still valid and has not changed
	PDFReader pdfr = pdfr_;
	pdfr.refresh();   // may have regenerated


	if (!isAuthorized()) {
		// have to ask user before continuing -- LATER
		// put up password dialog
		//requestPassword();

		return new LeafAscii("can handle encrypted PDFs only with null password for now",null, doc);
	}


	// first time only
	if (doc.getAttr(Document.ATTR_PAGECOUNT)==null) {
		// for Multipage protocol
		doc.putAttr(Document.ATTR_PAGECOUNT, Integer.toString(pdfr.getPageCnt()));

		Map info = pdfr.getInfo();   //(Map)pdfr.getObject(trailer.get("Info"));
		if (info!=null) {
//if (DEBUG) System.out.println("creator = "+info.get("Creator")+" / producer = "+info.get("Producer"));
			Object o=null;
			for (int i=0,imax=METADATA.length; i<imax; i++) if ((o=pdfr.getObject(info.get(METADATA[i])))!=null) doc.putAttr(METADATA[i], o.toString());
		}
	}


	//new LeafAscii("PDF "+doc.getAttr(Document.ATTR_URI)+", page "+doc.getAttr(Document.ATTR_PAGE), null, parent);
	if (doc.getAttr(Document.ATTR_PAGE)==null) return new LeafAscii("Loading...",null, parent);


	//Map page = getPage(Integers.parseInt(doc.getAttr(Document.ATTR_PAGE),1));

	// maybe scale up to ppi, so text not so small
	//Toolkit tk = Toolkit.getDefaultToolkit();
	//int ppi = tk.getScreenResolution();
	//int ppi = 72;   // !

	// rectangle, expressed in default user space units, defining the maximum imageable area
	INode pdf = new INode("pdf",null, parent);
	FixedI mediabox = new FixedI("MediaBox",null, pdf);     // need to move regions around, which INode doesn't do


	// temporary, until general Zoom behavior
	Browser br = getBrowser();
	Object z = br!=null? br.callSemanticEvent("getZoom", null): null;
	double zoom = (z instanceof Number? ((Number)z).doubleValue(): 100.0) / 100.0;

	buildPage(Integers.parseInt(doc.getAttr(Document.ATTR_PAGE, "1"), 1), mediabox, AffineTransform.getScaleInstance(zoom, zoom));
	//mediabox.addObserver(this);
	pdf.addObserver(this);


	pdfr.close();
	return pdf;
  }



//+ Protocols: restore, format, semanticevent,

/*  public void buildBefore(Document doc) {
	if (pdfr_==null) init(doc);
	super.buildBefore(doc);
  }*/

  /**
	If URI ref is to named destination, set intial page to that.

	<p>(<code>...#page=nnn</code> handled by {@link multivalent.std.ui.Multipage}.
	The Acrobat plug-in supports a highlight file referred to like so <code>http://www.adobe.com/a.pdf#xml=http://www.adobe.com/a.txt</code>;
	but that's awkward and nobody uses it, so it's not supported.)
  */
  private void init(Document doc) {
/*  public void restore(ESISNode n, Map attr, Layer layer) {
	super.restore(n, attr, layer);*/
//try { System.out.println("init on "+getFile()); } catch (IOException ioe) { System.out.println("IOE: "+ioe); }

	//Document doc = getDocument();
	//URI uri = docURI();
	String ref = docURI.getFragment();
//System.out.println("PDF initial page = "+doc.getAttr(Document.ATTR_PAGE));
	//File cachefile = getGlobal().getCache().mapTo(uri, null, Cache.COMPUTE);
//try { System.out.println("PDF getFile = "+getFile()+" for new PDFReader"); } catch (IOException ignore) {}
	try {
		pdfr_ = new PDFReader(getFile());
	} catch (Exception fail) {
fail.printStackTrace(); System.out.println(fail);

	fail_ = fail.getLocalizedMessage(); return;
	 }
//System.out.println("pdfr_ = "+pdfr_);

	if (ref!=null && doc.getAttr(Document.ATTR_PAGE)==null) try {    // set initial page before multipage and before forward/backward
		PDFReader pdfr = pdfr_;

		Object dest = Action.resolveNamedDest(new StringBuffer(ref), pdfr);
		if (dest==null) Action.resolveNamedDest(ref, pdfr);  // try old style

//System.out.println(ref+" PDF=> "+dest);
		if (dest!=null) {
			if (dest.getClass()==CLASS_DICTIONARY) dest = pdfr_.getObject(((Map)dest).get("D"));  // extract from << /D [...] >>
//System.out.println(" /D=> "+dest+", "+dest.getClass().getName()+" "+(dest.getClass()==PDF.CLASS_ARRAY));
			if (dest.getClass()==CLASS_ARRAY) {   // [page ... /XYZ left top zoom, /Fit, /FitH top, /FitV left, /FitR left bottom right top, /FitB, /FitBH top, /FitBV left
				Object page = pdfr_.getObject(((Object[])dest)[0]);
//System.out.println(" [0]=> "+page); // +" => "+(page!=null? String.valueOf(pdfr.page2num((Map)page)): "XXX"));
				if (/*page!=null &&*/ page.getClass()==CLASS_DICTIONARY) doc.putAttr(Document.ATTR_PAGE, String.valueOf(pdfr.page2num((Map)page)));
//System.out.println("initial page = "+pdfr.page2num((Map)page));
			}
		}
	} catch (/*IO, Parse*/Exception ignore) { /*System.err.println(ignore); ignore.printStackTrace();*/ }

	GoFast = Booleans.parseBoolean(getPreference("pdfGoFast", "true"), true);
  }


  /** Enlarge content root to MediaBox. */
  public boolean formatAfter(Node node) {
//System.out.println(node.bbox+" => "+cropbox_);
	//node.bbox.setBounds(cropbox_);
	//bbox.width = Math.max(bbox.width, cropbox_.width);
	//bbox.height = Math.max(bbox.height, cropbox_.height);
	node.bbox.setBounds(0,0, Math.max(node.bbox.x+node.bbox.width, cropbox_.x+cropbox_.width), Math.max(node.bbox.y+node.bbox.height, cropbox_.y+cropbox_.height));
	return super.formatAfter(node);
  }


  /** "Dump PDF to temp dir" in Debug menu. */
  public boolean semanticEventBefore(SemanticEvent se, String msg) {
	if (super.semanticEventBefore(se,msg)) return true;
	else if (multivalent.gui.VMenu.MSG_CREATE_VIEW==msg) {
		VCheckbox cb = (VCheckbox)createUI("checkbox", "Accelerate PDF (less accurate)", "event "+MSG_GO_FAST, (INode)se.getOut(), VMenu.CATEGORY_MEDIUM, false);
		cb.setState(GoFast);

	} else if (multivalent.devel.Debug.MSG_CREATE_DEBUG==msg) {
		VCheckbox cb = (VCheckbox)createUI("checkbox", "Dump PDF to temp dir", "event "+MSG_DUMP, (INode)se.getOut(), VMenu.CATEGORY_MEDIUM, false);
		cb.setState(Dump_);
	}
	return false;
  }


  /** Implements {@link #MSG_DUMP}, {@link #MSG_USER_PASSWORD}, {@link #MSG_OWNER_PASSWORD}. */
  public boolean semanticEventAfter(SemanticEvent se, String msg) {
	Object arg = se.getArg();
	if (MSG_DUMP==msg) {
		Dump_ = Booleans.parseBoolean(arg, !Dump_);

	} else if (MSG_USER_PASSWORD==msg) {    // as from dialog
		/*Encrypt e = pdfr_.getEncrypt();
		if (arg instanceof String && !e.isAuthorized()) {
			if (!e.authUser((String)arg)) requestPassword();
		}*/

	} else if (MSG_OWNER_PASSWORD==msg) {
		/*Encrypt e = pdfr_.getEncrypt();
		if (arg instanceof String && !e.isAuthorized()) {
			if (!e.authOwner((String)arg)) requestPassword();
		}*/

	} else if (MSG_GO_FAST==msg) GoFast = !GoFast; // => hints

	else if (Document.MSG_CLOSE==msg && pdfr_!=null) {
//System.out.println("close PDF / PDFReader");
		try { pdfr_.close(); } catch (IOException ignore) {/**/}
		pdfr_ = null;
	}

	return super.semanticEventAfter(se,msg);
  }


  /** If document is encrypted with non-null password, throw up dialog requesting user to enter it. */
  void requestPassword() {/**/
  }

  static String lastmsg = null;
  static void sampledata(String msg) {
	if (msg != lastmsg) { System.err.println("SAMPLE DATA: "+msg); lastmsg = msg; }
  }
  static void unsupported(String msg) {
	if (msg != lastmsg) { System.err.println("UNSUPPORTED: "+msg); lastmsg = msg; }
  }

  /** Debugging.
  void dumpIbbox(Fixed n, int level) {
	System.out.print("                                   ".substring(0, level*3));
	System.out.println(((Node)n).getName()+" "+phelps.text.Formats.pretty(n.getIbbox()));
	if (n instanceof INode) {
		INode p = (INode)n;
		for (int i=0,imax=p.size(); i<imax; i++) dumpIbbox((Fixed)p.childAt(i), level+1);
	}
  } */
}
