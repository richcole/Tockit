package multivalent.std.adaptor;

import java.io.*;
import java.net.URI;
//import java.net.MalformedURLException;
import java.util.List;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import multivalent.*;
//import multivalent.std.adaptor.ArchiveMediaAdaptor;
import phelps.text.Formats;



/**
	Media adaptor for RPM files.	For now just makes a directory listing.
	RPM file format taken from "Maximum RPM: Taking the Red Hat Package Manager to the Limit" by Edward C. Bailey,	February 17, 1997.
	cpio file format taken from GNU cpio's <tt>copyin.c</tt>.


	<p>To do
	extraction
	standard display format
		<pre>
		-rwxr-xr-x 121/1111	  5773 1995-09-04 12:42 conote_client.pl
		-rw-r--r-- 121/1111		61 1995-09-04 11:37 Annotation/Debug.pl
		-rwxr-xr-x 121/1111	  6606 1995-09-03 22:09 Annotation/authentication.pl
		</pre>

	@version $Revision$ $Date$
*/
public class RPM extends ArchiveMediaAdaptor {
  static final boolean DEBUG = false;

  public static final int // tags
	RPMTAG_NAME=1000, RPMTAG_VERSION=1001, RPMTAG_RELEASE=1002, RPMTAG_SERIAL=1003, RPMTAG_SUMMARY=1004, RPMTAG_DESCRIPTION=1005,
	RPMTAG_BUILDTIME=1006, RPMTAG_BUILDHOST=1007, RPMTAG_INSTALLTIME=1008, RPMTAG_SIZE=1009,
	RPMTAG_DISTRIBUTION=1010, RPMTAG_VENDOR=1011, RPMTAG_GIF=1012, RPMTAG_XPM=1013, RPMTAG_COPYRIGHT=1014, RPMTAG_PACKAGER=1015,
	RPMTAG_GROUP=1016, RPMTAG_CHANGELOG=1017, RPMTAG_SOURCE=1018, RPMTAG_PATCH=1019, RPMTAG_URL=1020,
	RPMTAG_OS=1021, RPMTAG_ARCH=1022, RPMTAG_PREIN=1023, RPMTAG_POSTIN=1024, RPMTAG_PREUN=1025,
	RPMTAG_POSTUN=1026, RPMTAG_FILENAMES=1027, RPMTAG_FILESIZES=1028, RPMTAG_FILESTATES=1029,
	RPMTAG_FILEMODES=1030, RPMTAG_FILEUIDS=1031, RPMTAG_FILEGIDS=1032, RPMTAG_FILERDEVS=1033, RPMTAG_FILEMTIMES=1034, RPMTAG_FILEMD5S=1035, RPMTAG_FILELINKTOS=1036, RPMTAG_FILEFLAGS=1037, RPMTAG_ROOT=1038, RPMTAG_FILEUSERNAME=1039,
	RPMTAG_FILEGROUPNAME=1040, RPMTAG_EXCLUDE=1041/*not used*/, RPMTAG_EXCLUSIVE=1042/*not used*/, RPMTAG_ICON=1043, RPMTAG_SOURCERPM=1044, RPMTAG_FILEVERIFYFLAGS=1045, RPMTAG_ARCHIVESIZE=1046, RPMTAG_PROVIDES=1047, RPMTAG_REQUIREFLAGS=1048, RPMTAG_REQUIRENAME=1049,
	RPMTAG_REQUIREVERSION=1050, RPMTAG_NOSOURCE=1051, RPMTAG_NOPATCH=1052, RPMTAG_CONFLICTFLAGS=1053, RPMTAG_CONFLICTNAME=1054, RPMTAG_CONFLICTVERSION=1055, RPMTAG_DEFAULTPREFIX=1056, RPMTAG_BUILDROOT=1057, RPMTAG_INSTALLPREFIX=1058, RPMTAG_EXCLUDEARCH=1059,
	RPMTAG_EXCLUDEOS=1060, RPMTAG_EXCLUSIVEARCH=1061, RPMTAG_EXCLUSIVEOS=1062, RPMTAG_AUTOREQPROV=1063 /* used internally by build */, RPMTAG_RPMVERSION=1064, RPMTAG_TRIGGERSCRIPTS=1065, RPMTAG_TRIGGERNAME=1066, RPMTAG_TRIGGERVERSION=1067, RPMTAG_TRIGGERFLAGS=1068, RPMTAG_TRIGGERINDEX=1069,
	/*no 1070..1078*/RPMTAG_VERIFYSCRIPT=1079;
  public static final int //
	SIGTAG_SIZE=1000, SIGTAG_MD5=1001, SIGTAG_PGP=1002;
  public static final int // types
	NULL=0, CHAR=1, INT8=2, INT16=3, INT32=4, INT64=5, STRING=6, BIN=7, STRING_ARRAY=8;
  public static final int // values for c_mode, OR'd together
	C_IRUSR=000400, C_IWUSR=000200, C_IXUSR=000100, C_IRGRP=000040, C_IWGRP=000020, C_IXGRP=000010, C_IROTH=000004, C_IWOTH=000002, C_IXOTH=000001,
	C_ISUID=004000, C_ISGID=002000,
	C_ISVTX=001000, C_ISBLK=060000, C_ISCHR=020000, C_ISDIR=040000, C_ISFIFO=010000, C_ISSOCK=0140000, C_ISLNK=0120000, C_ISCTG=0110000, C_ISREG=0100000;

  static final String CPIO_END = "TRAILER!!!";	// A header for a filename "TRAILER!!!" indicates the end of the archive.
  static final int CPIO_MAGIC=070701;	// SVR4 (POSIX extended is 070707)

  static class Index {
	int tag;
	int type;
	int offset;
	int count;
	Index(int g, int y, int o, int c) { tag=g; type=y; offset=o; count=c; }
  }


  GZIPInputStream isz_ = null;


  //int read2() throws IOException { return (is.read()<<8) + is.read(); }
  int read4() throws IOException { InputStream is=getInputStream(); return (((((is.read()<<8) | is.read())<<8) | is.read())<<8) | is.read(); }

  int readoct6() throws IOException {
	int l=0; for (int i=0; i<6; i++) l = (l<<3) + (isz_.read() - '0');
	return l;
  }
  //int readhex8() throws IOException { return (int)readhex(8); }
  /* cpio: All the fields in the header are ISO 646 (approximately ASCII) strings of octal numbers, left padded, not NUL terminated. */
  int readhex8() throws IOException {	// not valid if cnt>=sizeof(long)-1 / 3
	int l=0;
	for (int i=0; i<8; i++) {
		int v = isz_.read();
		if (v>='0' && v<='9') v-='0'; else v-=('a'-10);
		l = (l<<4) + v;
	}
	return l;
  }


  //public void buildBefore(Document doc) {throw new ParseException("Use the class java.util.ZipFile to parse", -1);}
  public Object parse(INode parent) throws Exception {
	Document doc = parent.getDocument();
	URI uri = doc.getURI();
	//br.eventq(TableSort.MSG_ASCENDING, doc.findDFS("File")); -- keep archive order
	return parseHelper(toHTML(uri), "HTML", getLayer(), parent);
  }


  public String toHTML(URI uri) throws IOException {
	//Browser br = getBrowser();
	Cache cache = getGlobal().getCache();
	//String urifile = uri.getPath();
	File f = cache.mapTo(uri, null, Cache.COMPUTE);

	long now = System.currentTimeMillis();

	// make an HTML table -- make HTML table nodes public
	StringBuffer sb = new StringBuffer((int)f.length());

	sb.append("<html>\n<head>");
	//sb.append("\t<title>").append("Contents of RPM file ").append(urifile).append("</title>\n");
	sb.append("\t<base href='").append(uri).append("/'>\n");	// .zip as if directory!
	sb.append("</head>\n");
	sb.append("<body>\n");


	// 1. LEAD -- use to verify RPM only; otherwise obsolete
	/*
	struct rpmlead {
		unsigned char magic[4]; = edab eedb
		unsigned char major, minor;
		short type;
		short archnum;
		char name[66];
		short osnum;
		short signature_type;
		char reserved[16];
	};
	*/
	InputStream is=getInputStream();
	if (is.read()!=0xed || is.read()!=0xab || is.read()!=0xee || is.read()!=0xdb) return "Not a valid RPM file -- bad magic number.";
	for (int i=4,imax=4+2+2+2+66+2+2+16; i<imax; i++) is.read();	  // is.read(length) is unreliable


	// 2. SIGNATURE -- len, MD5, PGP => skip
	if (is.read()!=0x8e || is.read()!=0xad || is.read()!=0xe8) return "Not a valid RPM file -- in signature's \"header structure header\".";
	/*int sigversion =*/ is.read();
	read4();	// reserved
	int sigicnt = read4();	   // count of index items
	int sigslen = read4();	   // size of store

//System.out.println("sig indexcnt="+sigicnt+", store len="+sigslen);
	for (int i=0,imax=(16*sigicnt)+sigslen + 4/*padding to boundary?*/; i<imax; i++) is.read();    // skip for now



	// 3. HEADER
	// "header struture header"
	if (is.read()!=0x8e || is.read()!=0xad || is.read()!=0xe8) return "Not a valid RPM file -- in \"header structure header\".";
	/*int version =*/ is.read();
	read4();	// reserved
	int icnt = read4();	// count of index items
	int slen = read4();	// size of store

	// Index, each entry 16 bytes long
	Index[] index = new Index[icnt];
	for (int i=0; i<icnt; i++) {
		int g=read4();
		int y=read4();
		int o=read4();
		int c=read4();
		index[i] = new Index(g,y,o,c);
	}

	// Store
	byte[] store = new byte[slen];
	is.read(store);

	// 4. ARCHIVE (cpio archive in SVR4 format with a CRC checksum, compressed with GNU zip)
	// collect header fields => should go in getCatalog, which should take an InputStream
	/*
	A cpio archive consists of a sequence of files.
	Each file has a 76 byte header,
	a variable length, NUL terminated filename,
	and variable length file data.
	*/
//System.out.print("GZIP MAGIC "); for (int i=0; i<2; i++) System.out.print(" "+Integer.toHexString(is.read())); System.out.println();
	isz_ = new GZIPInputStream(is);

	StringBuffer fsb = new StringBuffer(200);
	List files = new ArrayList(200);
	while (true) {
//for (int i=0,imax=104; i<imax; i++) System.out.print(Integer.toHexString(isz_.read())+" ");
/*
		int magic = readoct6();
System.out.println("magic # = "+Integer.toOctalString(magic)+" vs "+Integer.toOctalString(CPIO_MAGIC));
		if (magic != CPIO_MAGIC) {
			for (int i=0; i<20; i++) System.out.print(" "+Integer.toHexString(isz_.read()));
			System.exit(0);
			break;
		}*/
		// for now scan for magic (070701)
		int skip = 0;
		for ( ; true; skip++) {
			if ((isz_.read())!='0') continue;
			if ((isz_.read())!='7') continue;
			if ((isz_.read())!='0') continue;
			if ((isz_.read())!='7') continue;
			if ((isz_.read())!='0') continue;
			if ((isz_.read())!='1') continue;
			break;
		}
		System.out.println("skip = "+skip);

		int mtime=readhex8(), filesize=readhex8(),
			namesize=readhex8();
System.out.println("namesize="+namesize+", filesize="+filesize); //if (5>4)break;
		fsb.setLength(0); for (int i=0,imax=namesize-1; i<imax; i++) fsb.append((char)isz_.read());
		isz_.read(); // trailing null
		String filename = fsb.substring(0);
System.out.println("read "+filename);
		if (filename.equals(CPIO_END)) break;

		for (int i=0; i<filesize; i++) isz_.read(); // is.skip() doesn't always skip

		files.add(new ArchiveFileEntry(filename, 0, null, filesize, filesize, -1/*offset*/, mtime, mtime));

		// skip bytes to 4-byte boundary
		int elen = 6 + 8*13 + namesize + filesize, mod = elen % 4;
		System.out.println("elen="+elen+", /4="+((elen/4)*4)+", mod4="+(elen%4));
		//if (mod>0)
		for (int i=0,imax=4-mod; i<imax; i++) isz_.read();
	}
	isz_.close();	// ?


	// write catalog HTML (separated from collection because collection should get in getCatalog() so can use for extraction too)
	int filecnt=files.size();
	sb.append(filecnt).append(" file").append(filecnt>0?"s":"").append(", ");
	int sizei=sb.length(); long sizec=0;

	//<h3>Contents of zip file ").append(urifile).append("</h3>\n"); => apparent in URI entry
	//zhsb.append("<p>").append(dirlist.length).append(" file"); if (dirlist.length!=1) hsb.append('s');
	sb.append("\n<table width='90%'>\n");

	// headers.  click to sort
	sb.append("<tr><span Behavior='ScriptSpan' script='event tableSort <node>'	title='Sort table'>");
	sb.append("<th align='left'>File / <b>Directory<th align='right'>Size<th align='right'>Last Modified</b></span>\n");

	for (int i=0,imax=files.size(); i<imax; i++) {
		ArchiveFileEntry afe = (ArchiveFileEntry)files.get(i);
		String name=afe.filename;

		sb.append("<tr><td>");
		boolean dir = name.endsWith("/");
		if (dir) sb.append("<b>").append(name).append("</b>"); else sb.append(name);
		sb.append("\n");

		long size = afe.length;
		sb.append("<td align='right'>").append(size);
		sizec += size;
//System.out.println("size = "+size);
//		sb.append("<td><span Behavior='ElideSpan'>").append(lastmod).append("</span> ").append(Formats.relativeDate(lastmod, now));

		long lastmod = afe.lastmod;
		lastmod *= 1000;	// sec=>ms
//System.out.println("lastmod = "+lastmod+", rel="+Formats.relativeDate(lastmod, now)+", rel*1000="+Formats.relativeDate(lastmod*1000, now));
		sb.append("<td align='right'><span Behavior='ElideSpan'>").append(lastmod).append("</span> ").append(Formats.relativeDate(lastmod, now));
	}

	sb.insert(sizei, Formats.prettySize(sizec));
	sb.append("</table>\n</body></html>\n");

	return sb.toString();
  }


  protected String[] getPatterns() { String[] pat = { ".rpm/" }; return pat; }

  public List getCatalog(File archive) throws IOException {
	List files = new ArrayList(500);
/*
	ZipFile zf = new ZipFile(archive);
	for (Enumeration e=zf.entries(); e.hasMoreElements(); ) {
		ZipEntry ze = (ZipEntry)e.nextElement();
		String zfname = ze.getName();
		files.add(new ArchiveFileEntry(ze.getName(), -1, null, ze.getSize(), ze.getCompressedSize(), -1, ze.getTime(), ze.getTime()));
	}*/
	return files;
  }


//	public InputStream getInputStream(File archive, String filename) throws IOException {
  public File extractFile(File archive, String filename, File outdir) throws IOException {
	File newfile = null;
/*
	ZipFile zf = new ZipFile(archive);
//	Cache cache = getGlobal().getCache();
//	URI zipuri = zf.toURI();
	for (Enumeration e=zf.entries(); e.hasMoreElements(); ) {
		ZipEntry ze = (ZipEntry)e.nextElement();
		String zfname = ze.getName();
//System.out.println("*** comparing "+filename+" with "+ze.getName());
		if (filename==ALLFILES || filename.equals(zfname)) {
			int inx=zfname.lastIndexOf('/'); if (inx!=-1) zfname=zfname.substring(inx+1);
			newfile = new File(outdir, zfname);
			//newfile.getParentFile().mkdirs();
if (DEBUG) System.out.println("*** extract");
			InputStream in = new BufferedInputStream(zf.getInputStream(ze));	// buffered make any difference in this case?
if (DEBUG) System.out.println("*** in = "+in);
if (DEBUG) System.out.println("*** out = "+newfile);
			OutputStream out = new BufferedOutputStream(new FileOutputStream(newfile));
if (DEBUG) System.out.println("*** out = "+out);
			byte[] buf = new byte[8*1024];
			for (int len; (len=in.read(buf))!=-1; ) {out.write(buf, 0,len);
if (DEBUG) System.out.println("*** "+len);}
			out.close(); in.close();
			if (filename!=ALLFILES) break;
		}
	}
	zf.close();*/
	return newfile;
  }
}
