package multivalent.std.adaptor.pdf;

import java.awt.Color;
import java.awt.Transparency;
import java.awt.image.*;
import java.awt.color.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;

import phelps.imageio.plugins.Fax;
import phelps.awt.color.ColorSpaceCMYK;



/**
	Interpret PDF image types, from objects or inline (content stream):
	DCT (JPEG), CCITT FAX (Group 3, Group 3 2D, Group 4), raw samples (bit depth 1,2,4,8), JBIG2.
	Does no cacheing; always creates new image.

	@see javax.imageio.*
	@see phelps.imageio.plugins.Fax

	@version $Revision$ $Date$
*/
public class Images {
  static final boolean DEBUG = true && PDF.DEBUG;


  private Images() {}

  /**
	Constructs new BufferedImage from dictionary attributes and data in stream.
	@param imgdict   image XObject, or Map with {@link PDFReader#STREAM_DATA} key set for inline images
  */
  public static BufferedImage createImage(Map imgdict, InputStream in, Color fillcolor, PDFReader pdfr) throws IOException {
	assert imgdict!=null && in!=null && "Image".equals(imgdict.get("Subtype")) || imgdict.get("Subtype") == null/*inline*/;

	BufferedImage img;  // AffineImageOp requires BufferedImage, not just java.awt.Image
	int w = ((Number)pdfr.getObject(imgdict.get("Width"))).intValue(), h = ((Number)pdfr.getObject(imgdict.get("Height"))).intValue();

//long start = System.currentTimeMillis();
	String filter = getFilter(imgdict, pdfr);
//System.out.println(imgdict.get("Name")+", filter="+filter+" "+w+"x"+h);
	if ("DCTDecode".equals(filter)) img = createJPEG(imgdict, in);
	else if ("JPXDecode".equals(filter)) img = createJPEG2000(imgdict, in);
	else if ("CCITTFaxDecode".equals(filter)) img = createFAX(imgdict, in, fillcolor, pdfr);
	else if ("JBIG2Decode".equals(filter) /*|| "no abbreviation".equals(filter)*/) img = createJBIG2(imgdict, in);
	else { /*assert filter==null: filter;--still FlateDecode*/ img = createRaw(imgdict, w, h, in, fillcolor, pdfr); }  // raw samples, including most inline images
//Object cs = pdfr.getObject(imgdict.get("ColorSpace"));
//System.out.println(filter+","+imgdict.get("BitsPerComponent")+", "+(cs.getClass()==PDFReader.CLASS_ARRAY? " / "+((Object[])cs)[0]: cs));
	if (img==null) return null;     // IOException, JBIG2, or problem with samples
//long end = System.currentTimeMillis();
//System.out.println("time = "+(end-start));
	assert w==img.getWidth(): "width="+img.getWidth()+" vs param "+w; assert h==img.getHeight(): "height="+img.getHeight()+" vs param "+h;  // possible that parameters are wrong

	return img;
  }


  /**
	Scale and rotate according to affine transform, adjusting image origin from PDF lower-left to Java upper-left.
	@see #createImage(Map, InputStream, Color, PDFReader)
  */
  public static BufferedImage createScaledImage(Map imgdict, InputStream in, AffineTransform ctm, Color fillcolor, PDFReader pdfr) throws IOException {
	assert imgdict!=null && in!=null && ctm!=null && "Image".equals(imgdict.get("Subtype")) || imgdict.get("Subtype") == null/*inline*/;

	BufferedImage img = null;
try {
	img = createImage(imgdict, in, fillcolor, pdfr);
} catch (Exception e) {
	e.printStackTrace();
	System.out.println(e);
}
	if (img==null) return null;     // JBIG2, IOException

	int w = ((Number)pdfr.getObject(imgdict.get("Width"))).intValue(), h = ((Number)pdfr.getObject(imgdict.get("Height"))).intValue();


	// scaling affine transform
	// "transformation from image space to user space could be described by the matrix [1/w 0 0 -1/h 0 1]."
	AffineTransform iat;
	if (ctm.getScaleX() != 0.0 && false) {
		Point2D srcpt = new Point2D.Double(w,h), transpt = new Point2D.Double();
		srcpt.setLocation(1.0, 1.0); ctm.deltaTransform(srcpt, transpt);
		System.out.println(srcpt+" => "+transpt+" in "+w+"X"+h+" "+ctm);

		double xscale=ctm.getScaleX(), yscale=ctm.getScaleY();
		iat = new AffineTransform(xscale/w, ctm.getShearY(),
			ctm.getShearX(), -/*invert*/(yscale/h + (yscale<0.0? -1.0: 1.0)/h),    // "+1.0/h" is fuzz so don't get choppy image from bad PDF distillation
			transpt.getX()>=0.0? 0.0: -transpt.getX(), transpt.getY()>=0.0? 0.0: -transpt.getY());

	} else if (ctm.getScaleX() != 0.0) {   // Math.abs(ctm.getScaleX()) > Math.abs(ctm.getShearX())
		double xscale=ctm.getScaleX(), yscale=ctm.getScaleY();
		//if (Math.abs(xscale*w)<1.0) xscale=(xscale<0.0? -1.0: 1.0); if (Math.abs(yscale*h)<1.0) yscale=(yscale<0.0? -1.0: 1.0);     // tiny images at least 1x1 => always add one pixel, so 0+1=1 min
//System.out.println("scaled "+(xscale*w)+"x"+(yscale*h));
		//double xscale=ctm.getScaleX()/w, yscale = ctm.getScaleY()/h;//(h>2? ctm.getScaleY()/h: 1.0);//ctm.getScaleY()<0? -1.0: 1.0); -- what, flip 1 pixel?
//if (ctm.getShearX()!=0.0) System.out.println("scale + shear "+ctm);
		iat = new AffineTransform(xscale/w, ctm.getShearY(), ctm.getShearX(), -/*invert*/(yscale/h + (yscale<0.0? -1.0: 1.0)/h), 0.0, 0.0);   // "+1.0/h" is fuzz so don't get choppy image from bad PDF distillation
		iat = new AffineTransform(xscale/w, 0.0, 0.0, -/*invert*/(yscale + (yscale<0.0? -1.0: 1.0))/h, 0.0, 0.0);   // Java bug with scale+shear on images of more than small size, so ignore shear
		//double xround = 1.0/w /* 0.5*/, yround = -1.0/h /* 0.5*/;     // copy images
		//iat = new AffineTransform(xscale/w + xround, ctm.getShearX(), ctm.getShearY(), /*-invert*/yscale/h + yround, 0.0, 0.0);
		if (iat.getScaleX()<0.0) iat.translate(-w,0);   // it happens
		if (iat.getScaleY()<0.0) iat.translate(0,-h);   // in transformed space
//System.out.println("yscale="+yscale+" / height="+h+" => "+iat.getScaleY());

	} else {    // 90 or -90 degree rotation
//System.out.println(ctm);
		double xshear=ctm.getShearX(), yshear=ctm.getShearY();
		iat = new AffineTransform(0.0, yshear/w, -xshear/h, 0.0, 0.0,0.0);
		if (iat.getShearX()<0.0) iat.translate(0,-h);
		if (iat.getShearY()<0.0) iat.translate(-w,0);
//System.out.println("rotated image: "+w+"x"+h+"  "+ctm+" => "+iat);   //+", pt="+pt);
	}
//System.out.println(w+"x"+h+", "+ctm+" => "+iat);



	// final image
	//Image imgout = img;
//System.out.println(iat);
	//String filter = getFilter(imgdict, pdfr);
	// almost always have to transform, if only to invert
	//boolean isFAX = "CCITTFaxDecode".equals(getFilter(imgdict, pdfr));
/*
	if (isFAX && iat.getScaleY()<0.0) iat = new AffineTransform(iat.getScaleX(), iat.getShearY(), iat.getShearX(), -iat.getScaleY(), 0.0, 0.0);

	int aftype = iat.getType();
	if (AffineTransform.TYPE_IDENTITY==aftype || AffineTransform.TYPE_TRANSLATION==aftype) {    // -- never happens
		// nothing

	} else if (iat.getScaleY()>0.0 && (AffineTransform.TYPE_GENERAL_SCALE==aftype || AffineTransform.TYPE_UNIFORM_SCALE==aftype)) {
		// Image.getScaledInstance not faster with SMOOTH, FAST stinks, AA slow and bad, DEFAULT fast but bad.  And doesn't handle PDF's upside down images (can flip FAX).
		// faster to handle as drawImage()? (hardware?) => magnify lens not that much faster
		int neww=(int)Math.round(w * iat.getScaleX()), newh=(int)Math.round(h * iat.getScaleY());
		imgout = img.getScaledInstance(neww, newh, Image.SCALE_DEFAULT);
System.out.println("scale only: "+iat.getScaleX()+" x "+iat.getScaleY()+", img="+img);

	} else*/

	try {
		if ("CCITTFaxDecode".equals(getFilter(imgdict, pdfr))) {    // custom scaling for FAX
			img = Fax.scale(img, iat);
		} else {
			AffineTransformOp aop = new AffineTransformOp(iat, AffineTransformOp.TYPE_BILINEAR);    //.TYPE_NEAREST_NEIGHBOR faster but drops text within image
			img = aop.filter(img, null);
		}

	} catch (/*java.awt.image.{RasterFormat,ImagingOp}*/Exception ioe) {
		ioe.printStackTrace();
		//System.out.println(img); System.out.println(ctm+" => "+iat); System.out.println(img.getColorModel());
		System.out.println(ioe);
		System.err.println(imgdict.get("Name")+" "+getFilter(imgdict, pdfr)+" "+w+"X"+h+", w/"+ctm+" => "+iat);//+" => "+pt);
		//img=null; -- return untransformed?
	}

	//assert img!=null: imgdict;  // could be too small to show
	return img;
  }


  /** Process inline image into Node. */
  public static BufferedImage createScaledInline(/*imgdict created here,*/ CompositeInputStream in, Map csres, AffineTransform ctm, Color fillcolor, PDFReader pdfr) throws IOException {
	Map iidict = PDFReader.readInlineImage(in);

	InputStream iin = pdfr.getInputStream(iidict);
	Object csobj=iidict.get("ColorSpace"); if (csres!=null && csres.get(csobj)!=null) iidict.put("ColorSpace", csres.get(csobj));   // key not literal
	BufferedImage img = Images.createScaledImage(iidict, iin, ctm, fillcolor, pdfr);
	iin.close();

	assert img!=null: "bad INLINE IMG "+iidict; //+", len="+bout.size();    // no JBIG2 in inline
	return img;
  }



  /**
	Return image part of filter, which may be in a cascade, or <code>null</code> if none.
	Expands abbreviations ("DCT" => "DCTDecode", "CCF" => "CCITTFaxDecode").
	For example, from <code>[ASCII85Decode CCF]</code>, returns <code>CCITTFaxDecode</code>.
  */
  public static String getFilter(Map imgdict, PDFReader pdfr) throws IOException {
	//Object attr = pdfr.getObject(ref.getClass()==CLASS_DICTIONARY? ((Map)ref).get("Filter"): ref);
	Object attr = imgdict.get("Filter"); if (pdfr!=null) attr = pdfr.getObject(attr);
	String f;
	if (attr==null) f=null;  // raw samples, uncompressed -- found in inline images
	else if (attr.getClass()==PDFReader.CLASS_NAME) f = (String)attr;
	else { assert attr.getClass()==PDFReader.CLASS_ARRAY;      // usually image filter wrapped in ASCII.  no JPG wrapping FAX I hope!
		Object[] oa=(Object[])attr;
		Object o = oa.length>0? oa[oa.length-1]: null; if (pdfr!=null) o = pdfr.getObject(o);
		f = (String)o; // image filter must be last
	}

	if ("DCT".equals(f)) f="DCTDecode"; else if ("CCF".equals(f)) f="CCITTFaxDecode";  // canonicalize

	return "DCTDecode".equals(f) || "CCITTFaxDecode".equals(f) || "JBIG2Decode".equals(f) || "JPXDecode".equals(f)? f: null;
  }

  /**
	Returns image's /DecodeParms, or <code>null</code> if none (or {@link PDFReader#OBJECT_NULL}).
	If /DecodeParms is an array, the one corresponding to the image is always the last array element.
  */
  public static Map getDecodeParms(Map imgdict, PDFReader pdfr) throws IOException {
	Object o = pdfr.getObject(imgdict.get("DecodeParms"));
	Map dp = (Map)(o==null? null: o.getClass()==PDFReader.CLASS_DICTIONARY? o: /*o.getClass()==PDFReader.CLASS_ARRAY?*/ pdfr.getObject(((Object[])o)[((Object[])o).length-1/*better be last*/]));

	return (dp!=PDFReader.OBJECT_NULL? dp: null);
  }


  /** Hand off to ImageIO. */
  static BufferedImage createJPEG(Map imgdict, InputStream in) throws IOException {
	assert imgdict!=null && in!=null;

	if (in.markSupported()) in.mark(1024);

	BufferedImage img;
	//img = ImageIO.read(in); -- easiest, but already know it's JPEG
	ImageReader iir = (ImageReader)ImageIO.getImageReadersByFormatName("JPEG").next();
	ImageIO.setUseCache(false);
	ImageInputStream iin = ImageIO.createImageInputStream(in);
	iir.setInput(iin, true);   //new MemoryCacheImageInputStream(in));
	try {
		img = iir.read(0);
/*	} catch (IIOException e) {  // 0819sr0210.pdf
		try {
			// if iir.canReadRaster() is true for JPEG
			Raster r = iir.readRaster(0, null);
			ColorModel cm =
			img = new BufferedImage(cm, (WritableRaster)r, true, null);
		} catch (IOException) {
			img = null; System.out.println("Couldn't read JPEG, not even raster: "+e);
		}*/
	} catch (IOException e) { img = null; System.err.println("Couldn't read JPEG: "+e); }
	iir.dispose();
	iin.close();

	in.reset();

/*	if (img==null) {    // try JFIF from JAI, which may or may not be installed => doesn't help
System.out.println("trying JFIF");
		iir = (ImageReader)ImageIO.getImageReadersByFormatName("jfif").next();
		ImageIO.setUseCache(false);
		iin = ImageIO.createImageInputStream(in);
		iir.setInput(iin, true);   //new MemoryCacheImageInputStream(in));
		try { img = iir.read(0); } catch (IOException e) { img = null; System.err.println("Couldn't read JFIF: "+e); }
		iir.dispose();
		iin.close();
	}*/

	return img;
  }



  /** Hand off to ImageIO. */
  static BufferedImage createJPEG2000(Map imgdict, InputStream in) throws IOException {
	assert /*imgdict!=null &&*/ in!=null;

	if (in.markSupported()) in.mark(1024);

	BufferedImage img;
	//img = ImageIO.read(in); -- easiest, but already know it's JPEG
	ImageReader iir = (ImageReader)ImageIO.getImageReadersByFormatName("JPEG2000").next();
	ImageIO.setUseCache(false);
	ImageInputStream iin = ImageIO.createImageInputStream(in);
	iir.setInput(iin, true);   //new MemoryCacheImageInputStream(in));
	try {
		img = iir.read(0);
	} catch (IOException e) { img = null; System.err.println("Couldn't read JPEG2000: "+e); }
	iir.dispose();
	iin.close();

	in.reset();

	return img;
  }



  /** Decode parameters from PDF dictionary and pass on to {@link phelps.imageio.plugins.Fax}. */
  static BufferedImage createFAX(Map imgdict, InputStream in, Color fillcolor, PDFReader pdfr) throws IOException {
	assert imgdict!=null && in!=null/* && pdfr!=null -- testing*/;

//System.out.println(imgdict.get("Name")+", K="+K+", cols="+cols+", rows="+rows+", swap="+swapbw);
	Map dp = getDecodeParms(imgdict, pdfr); if (dp==null) new HashMap(3);   // no /DP very rare, at least have /K -1

	Object o;
	int K = ((o=pdfr.getObject(dp.get("K"))) instanceof Number? ((Number)o).intValue(): 0);   // <0 = pure 2D Group 4 (only one used in practice by PDF), ==0 = 1D Group 3, >0 = mixed 1D/2D Group 3
	if (K>0) { PDF.sampledata("Group 3 mixed");	 }
	int cols = ((o=pdfr.getObject(dp.get("Columns"))) instanceof Number? ((Number)o).intValue(): 1728);  assert cols>=1;     // supposed to round up to nearest byte boundary (width % 8 == 0)
	int height = ((Number)pdfr.getObject(imgdict.get("Height"))).intValue();     // Estimate rows by height, not cols*1.5 as can get "FAX strips" of 2500 width but only 107 height, so hugely overestimate and provoke flood of garbage collections!
	int rows = ((o=pdfr.getObject(dp.get("Rows"))) instanceof Number? ((Number)o).intValue(): -height/*just go until hit EOF -- estimate for performance*/);  //assert rows>=1;   //if (rows<=0) rows=Integer.MAX_VALUE;

	boolean EndOfLine = ((o=pdfr.getObject(dp.get("EndOfLine"))) instanceof Boolean? ((Boolean)o).booleanValue(): false);
	boolean EndOfBlock = ((o=pdfr.getObject(dp.get("EndOfBlock"))) instanceof Boolean? ((Boolean)o).booleanValue(): true);
	boolean EncodedByteAlign = ((o=pdfr.getObject(dp.get("EncodedByteAlign"))) instanceof Boolean? ((Boolean)o).booleanValue(): false);
//if (EncodedByteAlign) PDF.sampledata("FAX /EncodedByteAlign");
	boolean BlackIs1 = ((o=pdfr.getObject(dp.get("BlackIs1"))) instanceof Boolean? ((Boolean)o).booleanValue(): false);
	int DamagedRowsBeforeError = ((o=pdfr.getObject(dp.get("DamagedRowsBeforeError"))) instanceof Number? ((Number)o).intValue(): 0);

	//o=pdfr.getObject(imgdict.get("Decode")); => handled in scale()'s color map
	boolean swapbw = (java.util.Arrays.equals(PDFReader.A10, (Object[])pdfr.getObject(imgdict.get("Decode"))));

	byte white = (byte)(BlackIs1^swapbw? 1: 0);     // /Decode [1 0] different than BlackIs1.  INVERTED for performance, set to 0 as Java clears page to 0 and most of page is white, so save rewriting.


	BufferedImage img = Fax.decode(K, cols, rows, EndOfLine, EndOfBlock, EncodedByteAlign, white, DamagedRowsBeforeError, in);


	// if image masks replace color map
	boolean mask = ((o=pdfr.getObject(imgdict.get("ImageMask"))) instanceof Boolean? ((Boolean)o).booleanValue(): false);
	if (mask && !Color.BLACK.equals(fillcolor)) {   // special case: if fillcolor is black treat as ordinary non-ImageMask
//System.out.println("image mask "+fillcolor);// imgdict);
		//if (DEBUG) fillcolor = Color.BLUE;    // mozilla-ch02.pdf paints lizard with black mask on white mask, which this merges as blue on blue
		ColorModel cm = new IndexColorModel(1, 2, new int[] { 0, fillcolor.getRGB() }, 0, true, 0/*trans*/, DataBuffer.TYPE_BYTE);
		img = new BufferedImage(cm, img.getRaster(), false, new java.util.Hashtable());
	}

	return img;
  }



  /** Not implemented. */
  static BufferedImage createJBIG2(Map imgdict, InputStream in) throws IOException {
	PDF.sampledata("JBIG2");
	return null;
  }



  /**
	Create image from raw samples, in various bit depths (8, 4, 2, 1), in a variety of color spaces, with various numbers of samples per component (4,3,1).
  */
  static BufferedImage createRaw(Map imgdict, int w, int h, InputStream in, Color fillcolor, PDFReader pdfr) throws IOException {
	assert imgdict!=null && w>0 && h>0 && in!=null && fillcolor!=null && pdfr!=null;

	Boolean mask = (Boolean)pdfr.getObject(pdfr.getObject(imgdict.get("ImageMask")));
	int bpc = (mask==Boolean.TRUE? 1: ((Number)pdfr.getObject(pdfr.getObject(imgdict.get("BitsPerComponent")))).intValue());
	Object[] decode = (Object[])pdfr.getObject(imgdict.get("Decode"));

	ColorModel cm = createRawColorModel(pdfr.getObject(imgdict.get("ColorSpace")), mask, bpc, fillcolor, pdfr);
//System.out.println("colorspace = "+imgdict.get("ColorSpace")+" => "+cm+" "+w+"x"+h);

	int spd = (cm instanceof IndexColorModel? 1 : cm.getNumComponents());   // 1 for gray or indexed (regardless of cm.getNumComponents()), 3 for rgb, 4 for CMYK
	byte[] rawdata = readRawData(in, w*h*spd /* bpc/8*/, decode);

/*
	if (bpc==8 && cm.getColorSpace() instanceof ICC_ColorSpace) {
		int[] hist = new int[256];
		for (int i=0,imax=rawdata.length; i<imax; i++) hist[rawdata[i]&0xff]++;
		for (int i=0,imax=hist.length; i<imax; i++) if (hist[i]>0) System.out.print(i+"="+hist[i]+" ");
	}*/

	// work around AffineTransformOp bug on CMYK
	if (bpc==8 && cm.getColorSpace() instanceof ColorSpaceCMYK) {
		rawdata = transcode4to3(rawdata, w, h, bpc);
		cm = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		spd = 3;
	}
if (rawdata.length < (w*h*spd * bpc + bpc-1)/8) { System.out.println("short data: "+rawdata.length+" < "+(w*h*spd * bpc + bpc-1)/8+": "+w+"x"+h+" * "+spd+" @ "+bpc+" bpp"); System.out.println(imgdict); return null;/*System.exit(1);*/ }


	WritableRaster r = createRawRaster(rawdata, w, h, bpc, spd);

	// create image with color model so survives affine transform
	BufferedImage img = null;
try {
	img = new BufferedImage(cm, r, false, new java.util.Hashtable());
} catch (Exception e) {
	//e.printStackTrace();
	System.err.println(e);

	System.out.println("color model = "+cm);
	SampleModel sm = r.getSampleModel();
	System.out.println("sample model = "+sm);
/*
		if (sm instanceof ComponentSampleModel) {
			if (sm.getNumBands() != getNumComponents()) {
				return false;
			}
			for (int i=0; i<nBits.length; i++) {
				if (sm.getSampleSize(i) < nBits[i]) {
					return false;
				}
			}
			return (raster.getTransferType() == transferType);
		}
*/
	// from IndexedColorModel.java
	//int size = r.getSampleModel().getSampleSize(0);
	//System.out.println(r.getTransferType()+" ==? "+cm.getTransferType());
	//System.out.println(r.getNumBands()+" ==? 1");
	//System.out.println((1 << size)+" >=? "+((IndexColorModel)cm).getMapSize());
	//if (DEBUG) System.exit(1);

	System.out.println("sample model instance of ComponentSampleModel "+(sm instanceof ComponentSampleModel));
	System.out.println("num bands = "+sm.getNumBands()+" ==? "+cm.getNumComponents()+" cm num comp");
	int[] nbits = cm.getComponentSize();
	for (int i=0; i<nbits.length; i++) System.out.println("  "+sm.getSampleSize(i)+" >=? "+nbits[i]);
	System.out.println(r.getTransferType()+" ==? "+sm.getTransferType());

}

	return img;
  }


  private static ColorModel createRawColorModel(Object csref, Boolean mask, int bpc, Color fillcolor, PDFReader pdfr) throws IOException {
	//assert csref!=null; // => image mask
	Object csobj = pdfr.getObject(csref);
	//ColorSpace cs = (csobj!=null/*error, except for image masks*/? getColorSpace(csref): ColorSpace.getInstance(ColorSpace.CS_sRGB));
	ColorSpace cs = pdfr.getColorSpace(csref, null, null);  assert cs!=null || (mask!=null && mask.booleanValue());

	ColorModel cm;

	// 1: INDEX or GRAY
	if (mask!=null && mask.booleanValue()) {   // as abused in riggs.pdf
		assert bpc == 1;
//System.out.println("fillcolor = "+fillcolor+"/"+Integer.toHexString(fillcolor.getRGB())+", decode="+decode+", "+w+"x"+h);
		// "unmasked areas will be painted using the current nonstroking color"
		cm = new IndexColorModel(1, 2, new int[] { fillcolor.getRGB(), 0 }, 0, /*false--grr*/true, 1, DataBuffer.TYPE_BYTE);
		//cm = new IndexColorModel(1, 2, new int[] { Color.BLUE.getRGB(), Color.GREEN.getRGB() }, 0, false, -1, DataBuffer.TYPE_BYTE);

	} else if (csobj.getClass()==PDFReader.CLASS_ARRAY && ("Indexed".equals(((Object[])csobj)[0]) || "I".equals(((Object[])csobj)[0]))) { // indexed special case
		Object[] oa = (Object[])csobj;   // [/Indexed base hival samples]
		int hival = ((Number)pdfr.getObject(oa[2])).intValue();
		Object cmap = pdfr.getObject(oa[3]);  assert cmap.getClass()==PDFReader.CLASS_DICTIONARY || cmap.getClass()==PDFReader.CLASS_STRING;

		byte[] samp;
		if (cmap.getClass()==PDFReader.CLASS_DICTIONARY) {
			samp = pdfr.getInputStreamData(oa[3]/*NOT cmap-- could be encrypted*/, false, false);
//System.out.println("index data "+oa[3]+"/"+pdfr.getObject(oa[3])+" => size="+samp.length+" vs hival="+hival+"/"+((hival+1)*3));

		} else { assert cmap.getClass()==PDFReader.CLASS_STRING;
			StringBuffer sb = (StringBuffer)cmap;
			samp = new byte[sb.length()];
			for (int i=0,imax=sb.length(); i<imax; i++) samp[i] = (byte)(sb.charAt(i) & 0xff);
		}

		// Java's IndexColorModel requires base RGB space, but PredicateCalculus has ICCBased and woods.pdf has DeviceGray
		// So compute colors based on current color space and vis-a-vis decode array.
		Object base = pdfr.getObject(oa[1]);     // "array or name"
		if (null==base || "DeviceRGB".equals(base) || "RGB".equals(base)) {}  // ok as is
		else {  // "any device or CIE-based color space or (in PDF 1.3) a f or DeviceN space, but not a Pattern space or another Indexed space."
			ColorSpace bcs = pdfr.getColorSpace(base, null, null);
			int spd = bcs.getNumComponents(), sampcnt = samp.length / spd;
			byte[] brgb = new byte[sampcnt*3];  // RGB
			float[] fsamp = new float[spd];
//System.out.println("converting "+base+"/"+oa[1]+" to RGB, length "+samp.length+"=>"+brgb.length+", spd="+spd+", sampcnt="+sampcnt);
			for (int i=0, is=0, id=0; i<sampcnt; i++) {
				for (int j=0; j<spd; j++) fsamp[j] = (samp[is++]&0xff) / 256f;  // GIVE US UNSIGNED BYTES!
				float[] rgb = bcs.toRGB(fsamp);
//if (id<10*spd) System.out.println(fsamp[0]+" "+fsamp[1]+" "+fsamp[2]+" => "+rgb[0]+" "+rgb[1]+" "+rgb[2]);
				for (int j=0; j<3; j++) brgb[id++] = (byte)(rgb[j] * 256f);
			}
			samp = brgb;
		}

		// cache conversion...

		// Java bug: IndexColorModel.isCompatibleRaster() checks (1<<raster_bpc)>=map_size should be (1<<raster_bpc)<=map_size
		//assert b.length == hival+1: bpc+" vs "+(hival+1);
		// workaround Java bug: make equal (ColorModels not cached).  Tickled by pdfTeX-0.14d.  RESTORE.
//System.out.println("indexed size = "+(hival+1)+", bpc="+bpc+", samp.length="+samp.length);
		cm = new IndexColorModel(bpc, Math.min(1<<bpc, hival+1), samp, 0, false);
		// Index works with both Interleaved and Packed Rasters

	} else if (cs.getNumComponents()==1 && bpc<8) {   // grayscale (/Indexed handled above)
		// Java has to have IndexColorModel for Packed Rasters so translate grayscale
		if (bpc==1) cm = new IndexColorModel(1, 2, new int[] { 0x000000, 0xffffff }, 0, false, -1, DataBuffer.TYPE_BYTE);
		else if (bpc==2) cm = new IndexColorModel(2, 4, new int[] { 0x000000, 0x404040, 0xc0c0c0, 0xffffff }, 0, false, -1, DataBuffer.TYPE_BYTE);
		else /*assert bpc==4*/ cm = new IndexColorModel(4, 16, new int[] { 0x000000, 0x111111, 0x222222, 0x333333, 0x444444, 0x555555, 0x666666, 0x777777, 0x888888, 0x999999, 0xaaaaaa, 0xbbbbbb, 0xcccccc, 0xdddddd, 0xeeeeee, 0xffffff }, 0, false, -1, DataBuffer.TYPE_BYTE);


	// 3/4: RGB, CMYK
	} else if (bpc==8 || bpc==4) {    // 4-bit case split out in readRawData to separate bytes
		cm = new ComponentColorModel(cs, false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);   // "each sample in a separate data element", that is, interleaved raster

	} else {    // fabricate an IndexColorModel so can pack all samples into single data element, 2^(2*4) = 256, but usually 2^(2*3)=64
		//ColorSpace cs = (csobj!=null/*error, except for image masks*/? getColorSpace(csref): ColorSpace.getInstance(ColorSpace.CS_sRGB));
		int spd = cs.getNumComponents();
		assert (spd==3 || spd==4) && (bpc==2 || bpc==1): "bpc="+bpc;
//System.out.println("imgdict = "+imgdict+", colorspace="+csobj+", spd="+spd);

		// color, 3 or 4 components -- compute entries vis-a-vis base colorspace
		int po2 = (1<<bpc);
		byte[] b = new byte[(1<<(bpc*spd)) * spd];
//System.out.println("bpc="+bpc+", spd="+spd+", b.length="+b.length);
		float red=0f, green=0f, blue=0f, black=0f, inc=(float)(1f/po2);
		float[] comp = new float[4];    // enough for CMYK
		Color c;
		int i=0;
		for (int r=0; r<po2; r++, red+=inc, green=0f, blue=0f, black=0f) {
			comp[0]=red;
			for (int g=0; g<po2; g++, green+=inc, blue=0f, black=0f) {
				comp[1]=green;
				for (int bl=0; bl<po2; bl++, blue+=inc, black=0f) {
					comp[2]=blue;
					for (int k=0; k<po2; k++, black+=inc) {
						comp[3]=black;
						c = new Color(cs, comp, 0f);
						b[i++]=(byte)c.getRed(); b[i++]=(byte)c.getGreen(); b[i++]=(byte)c.getBlue();
						if (spd==3) break;
					}
				}
			}
		}
		assert i==b.length: i+" vs "+b.length;

		cm = new IndexColorModel(bpc, b.length/spd, b, 0, false);
	}

	return cm;
  }


  private static byte[] readRawData(InputStream in, int estlength, Object[] decode) throws IOException {
// => pdfr.getStreamData(), except better estimate here
//int est = (w*spd*bpc + (bpc-1))/8 * h;
	byte[] buf = new byte[8 * 1024];
	ByteArrayOutputStream bout = new ByteArrayOutputStream(estlength);
	for (int hunk; (hunk = in.read(buf))!=-1; ) bout.write(buf, 0, hunk);
//System.out.println("data buf len = "+bout.size());
	bout.close();
	byte[] rawdata = bout.toByteArray();
//System.out.println(est+" vs "+rawdata.length+" actual @ "+bpc);

	// decode matrix
	if (decode!=null) {  // cheaper to tweak color map if indexed
//System.out.println("decode = "+o);
		// if IndexColorModel, twiddle map instead
		double[] da = new double[decode.length];
		for (int i=0,imax=decode.length; i<imax; i++) da[i] = ((Number)decode[i]).doubleValue();
		if (da.length==2 && da[0]==1.0 && da[1]==0.0) { // || (da[2]==1.0 && da[3]==0.0 && da[))) {
//System.out.println("invert");
			for (int i=0,imax=rawdata.length; i<imax; i++) rawdata[i] ^= 0xff;
		}// else PDF.unsupported("Decode matrix: "+da.length+" len, "+da);
	}

	// could invert image as read in: if (ctm.getScaleY() < 0) ...
//System.out.println("data size="+bout.size());

	return rawdata;
  }


  /**
	Work around Java AffineTransformOp bug on 4-component color spaces by transcoding data to RGB.
	Just handles 8-bit CMYK case for now.
  */
  private static byte[] transcode4to3(byte[] data, int w, int h, int bpc) {
	assert bpc==8: bpc;
	assert data.length == w*h*4: data.length+" vs "+(w*h*4);

	byte[] newdata = new byte[w * h * 3];
	for (int i=0,imax=data.length,j=0; i<imax; i+=4, j+=3) {
		int k = data[i+3]&0xff;
		newdata[j]   = (byte)(255 - Math.min(255, (data[i]&0xff)   + k));
		newdata[j+1] = (byte)(255 - Math.min(255, (data[i+1]&0xff) + k));
		newdata[j+2] = (byte)(255 - Math.min(255, (data[i+2]&0xff) + k));
	}

	return newdata;
  }


  private static WritableRaster createRawRaster(byte[] rawdata, int w, int h, int bpc, int spd) {
	WritableRaster r;

	int[] offs=new int[spd]; for (int i=0,imax=offs.length; i<imax; i++) offs[i]=i;

//System.out.println("bpc="+bpc+", spd="+spd);
//if (bpc<8) System.out.println("w="+w+", h="+h+", bpc="+bpc+", spd="+spd+", data buf len = "+rawdata.length+" vs w*h*spd="+(w*h*spd*bpc/8+w*spd));
	// provided SampleModels either force all samples in same element (packed), or sample-per-element (interleaved)
	if (bpc==8 || (bpc==2 && spd==4)) {
		// if (spd==1) PackedRaster; else
//System.out.println("data len ="+rawdata.length+" vs  needed "+(w*h*spd)+"  ("+(rawdata.length/(w*spd))+" rows)");
		r = Raster.createInterleavedRaster(new DataBufferByte(rawdata, rawdata.length), w, h, w*spd,spd, offs, null);

	} else if (spd==1) { assert bpc==4 || bpc==2 || bpc==1;   // indexed or grayscale: samples fit in a byte
		r = Raster.createPackedRaster(new DataBufferByte(rawdata, rawdata.length), w,h, bpc, null);
//System.out.println("spd=1, bpc="+bpc+", bands="+r.getNumBands());
//System.out.print("spd==1: "); for (int i=0, imax=Math.min(10, rawdata.length); i<imax; i++) System.out.print(Integer.toHexString(rawdata[i])+" ");
//System.out.println("   @ "+ctm.getTranslateX()+","+ctm.getTranslateY()+", scale="+ctm.getScaleX()+"x"+ctm.getScaleY());
/*		if (!(cm instanceof IndexColorModel)) { // Java has to have IndexColorModel for this Raster class and got here from grayscale
			cm = (bpc==1? GRAY1: bpc==2? GRAY2: /*bpc==4* / GRAY4);
		}*/

	} else if (bpc==4 || (bpc==1 && spd==4)) {  // doubles size for RGB or CMYK samples, which is acceptable expansion
		byte[] newdata = new byte[w*spd * h];
		for (int y=0, newi=0, base=0, stride=(w*spd*bpc + 7)/8; y<h; y++, base=y*stride) {
			for (int x=0; x<w; x+=2) {
				byte b = rawdata[base++];
				newdata[newi++] = (byte)((b>>4)&0xf);
				if (x+1 < w) newdata[newi++] = (byte)((b&0xf));
				// decode array...
			}
		}
		r = Raster.createInterleavedRaster(new DataBufferByte(newdata, newdata.length), w,h, w*spd,spd, offs, null);
PDF.sampledata("4 bpc x 2 bytes: "+rawdata.length+" => "+newdata.length);

	} else if (bpc==2) {	// spd==3: samples smeared across data elements
		byte[] newdata = new byte[w*h];
		int valid=0, vbpc=0;
		for (int y=0, newi=0, base=0, stride=(w*spd*bpc + 7)/8; y<h; y++, base=y*stride) {
			for (int x=0; x<w; x++) {   // slow, but unusual
				if (valid<6) { vbpc = (vbpc<<8) | rawdata[base++]; valid+=8; }
				newdata[newi++] = (byte)((vbpc >> (valid-6)) & 0x3f);
				valid -= 6;
			}
		}
		//r = Raster.createPackedRaster(new DataBufferByte(newdata, newdata.length), w,h, w, new int[] { 0x30, 0x0c, 0x03 }, null);
		r = Raster.createInterleavedRaster(new DataBufferByte(newdata, newdata.length), w,h, w*spd,spd, new int[] { 0 }, null);
PDF.sampledata("2 bpc packed BYTE: "+rawdata.length+" => "+newdata.length);

	} else { assert bpc==1;
		byte[] newdata = new byte[w*h];
		int valid=0, vbpc=0;
		for (int y=0, newi=0, base=0, stride=(w*spd*bpc + 7)/8; y<h; y++, base=y*stride) {
			for (int x=0; x<w; x++) {   // slow, but unusual
				if (valid<3) { vbpc = (vbpc<<8) | rawdata[base++]; valid+=8; }
				newdata[newi++] = (byte)((vbpc >> (valid-3)) & 7);
				valid -= 3;
			}
		}
		//r = Raster.createPackedRaster(new DataBufferByte(newdata, newdata.length), w,h, w, new int[] { 4,2,1 }, null);
		r = Raster.createInterleavedRaster(new DataBufferByte(newdata, newdata.length), w,h, w*spd,spd, new int[] { 0 } , null);
PDF.sampledata("1 bit packed byte: "+rawdata.length+" => "+newdata.length);
	}

	return r;
  }
}
