package phelps.text;

import java.awt.geom.Rectangle2D;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.ParseException;



/**
	Pretty printing.

	<ul>
	<li>pretty printing: {@link #relativeDate(long,long)} / {@link #relativeDate(long) (long)},
		{@link #prettySize(long)}, {@link #pretty(Rectangle2D)}
	</ul>

	@see phelps.net.URIs#relativeURL(URL, URL)

	@version $Revision$ $Date$
*/
public class Formats {
  static final long SECOND=1, MINUTE=60*SECOND, HOUR=60*MINUTE, DAY=24*HOUR, WEEK=7*DAY;
  /*public*/ static final String[] int2dayOfWeek = { "XXX", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
  /*public*/ static final String[] int2day = { "XXX", "Sun", "Mon", "Tues", "Wed", "Thu", "Fri", "Sat" };
  /*public*/ static final String[] int2month = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
  /*public*/ static final String[] int2mon = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
  /*public*/ static final String[] ampm = { "am", "pm" };


  static DateFormat dateFormat = null;     // create on demand
  static Calendar cal_ = null;

  private Formats() {/**/}


  /**
	Parse <var>sdate</var> and pass on to {@link #relativeDate(long)}.
	<var>sdate</var> can either be a number, in the standard Java milliseconds since 1970,
	or a human-readable date that was produced by {@link java.util.Date#toString()}.
  */
  public static String relativeDate(String sdate) throws ParseException {
	long date=-1;

	try {
		long idate = Long.parseLong(sdate);
		return relativeDate(idate);
	} catch (NumberFormatException nfe) {/**/}

	if (dateFormat==null) dateFormat = DateFormat.getDateTimeInstance();   // should use static copy
	dateFormat.setLenient(true);
	try {
		date = dateFormat.parse(sdate).getTime();
	} catch (ParseException pe) {
		System.out.println("old date parsing: "+sdate); //-- always seems to be old
		//date = new Date(sdate);	// try old Date class -- I know it's deprecated
	}

	if (date == -1) throw new ParseException("can't parse  date as either number or humand readable: "+sdate, -1);

	return relativeDate(date);
  }

  /** Display <var>date</var> {@link #relativeDate(long,long) relative} to now. */
  public static String relativeDate(long date) { return relativeDate(date, System.currentTimeMillis()); }

  /**
	Display <var>date</var>, in the standard Java milliseconds since 1970, relative to <var>relativeTo</var>,
	using relations like "yesterday" and "3 hours ago".
  */
  public static String relativeDate(long date, long relativeTo) { //return relativeDate(new Date(date), new Date(relativeTo)); }
	long diffsec=(relativeTo-date)/1000; boolean future=(diffsec<0);
	String ago = " ago";
	if (future) { diffsec=-diffsec; ago=""; }	// => should leave "+/-" and "...ago" to caller

	if (diffsec<2*MINUTE) return (future?"+":"")+diffsec+" seconds"+ago;
	else if (diffsec<2*HOUR) return (future?"+":"")+(diffsec/MINUTE)+" minutes"+ago;
	else if (diffsec<24*HOUR) return (future?"+":"")+(diffsec/HOUR)+" hours"+ago;

	// should take output format from a preference pattern
	if (cal_==null) cal_=Calendar.getInstance();
	Calendar cal = cal_;
	cal.setTimeInMillis(date);
	int hour=cal.get(Calendar.HOUR_OF_DAY), min=cal.get(Calendar.MINUTE), month=cal.get(Calendar.MONTH);
	if (diffsec<2*DAY) return hour+":"+(min<10?"0":"")+min+(future? " tomorrow":" yesterday");
	else if (diffsec<WEEK) return hour+":"+(min<10?"0":"")+min+" "+int2dayOfWeek[cal.get(Calendar.DAY_OF_WEEK)];
	else if (diffsec<45*WEEK) return cal.get(Calendar.DAY_OF_MONTH)+" "+int2month[month];
	return cal.get(Calendar.DAY_OF_MONTH)+" "+int2month[month]+" "+cal.get(Calendar.YEAR);
  }



  static final String[] KSUFFIX = { "bytes", "KB", "MB", "GB", "TB", "EB" };
  /** Given a byte count, returns a string in more human-readable form; e.g., 13*1024*1024 => "13MB". */
  public static String prettySize(long bytes) {
	if (bytes==0) return "0 bytes"; else if (bytes==1) return "1 byte"; else if (bytes==-1) return "-1 byte";	 // special cases

	long sign = (bytes>=0? 1: -1);
	bytes = Math.abs(bytes);
	long K = 1024, div=0, rem=0;
	int sfxi = 0;
	while (bytes > K && sfxi+1<KSUFFIX.length) {
		div=bytes/K; rem=bytes - div*K;
		bytes = div;
		sfxi++;
	}
	return Long.toString(sign*bytes)+(rem>0?".":"")+(bytes<=10 && rem>0? Long.toString((rem*10)/K): "")+" "+KSUFFIX[sfxi];
  }

  // parseSize(String) ...


  /** Compact output for a Rectangle: <var>width</var>x<var>height</var> @ (<var>x</var>,<var>y</var>). */
  public static String pretty(Rectangle2D r) { return ""+r.getWidth()+"x"+r.getHeight()+"@("+r.getX()+","+r.getY()+")"; }


  /***************************************************
   *
   * sprintf -- just use Proskanzer's Fmt?
   * => superceded in Java 1.1's java.text.Format?
   *
   * To do:
   * later support XPG4 position specifiers
   * have to package up int's, float's, et cetera as Objects
   * later error handling
   *
   ***************************************************/
/*
  public static String sprintf(String spec) {
	return sprintf(spec, null, null, null, null, null);
  }
  public static String sprintf(String spec, Object arg1) {
	return sprintf(spec, arg1, null, null, null, null);
  }
  public static String sprintf(String spec, Object arg1, Object arg2) {
	return sprintf(spec, arg1, arg2, null, null, null);
  }
  public static String sprintf(String spec, Object arg1, Object arg2, Object arg3) {
	return sprintf(spec, arg1, arg2, arg3, null, null);
  }
  public static String sprintf(String spec, Object arg1, Object arg2, Object arg3, Object arg4) {
	return sprintf(spec, arg1, arg2, arg3, arg4, null);
  }
  public static String sprintf(String spec, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
	List args = new ArrayList(5);
	// grr, can't cascade
	args.add(arg1); args.add(arg2); args.add(arg3); args.add(arg4); args.add(arg5);
	return sprintf(spec, args);
  }

  public static String sprintf(String spec, List args) {
	// first convert escape characters
	spec = raw2javaString(spec);

	StringBuffer sb = new StringBuffer(spec.length()*2);

	char ch;
	int argnum=0, argcnt=args.size();
	Object arg=null;
	String sarg;
	char cvttype;

	String flags = "-+ 0#";
	boolean fminus, fplus, falt;
	char fillchar=' ';
	int minwidth, precision;

	String val;
	for (int i=0; i<spec.length(); i++) {
	  ch = spec.charAt(i);
	  if (ch!='%') {
		sb.append(ch);
	  } else {
		i++;
		// get argnum (later XPG3)

		// optional flags:
		// -=left adjustment, +=always print sign, ' '=space-filled, 0=zero-filled, #=alternative format
		fminus = fplus = falt = false; fillchar=' ';
		while (flags.indexOf(ch = spec.charAt(i++))!=-1) {
		  switch (ch) {
		  case '-': fminus=true; break;
		  case '+': fplus=true; break;
		  case ' ': fillchar=' '; break;
		  case '0': fillchar='0'; break;
		  case '#': falt=true; break;
		  default:
			// unrecognized flag
		  }
		}
		i--;

		// minimum field width
		minwidth = 0;
		if (spec.charAt(i)=='*') {
		} else {
		  while (Character.isDigit(ch=spec.charAt(i++))) {
			minwidth = (minwidth*10) + Character.digit(ch,10);
		  }
		  i--;
		}

		// period
		// precision: max chars from string or number of digits after decimal point or min digits for integer
		precision = Integer.MAX_VALUE;
		if (spec.charAt(i)=='.') {
		  if (spec.charAt(i)=='*') {
		  } else {
			while (Character.isDigit(ch=spec.charAt(i++))) {
			  minwidth = (minwidth*10) + Character.digit(ch,10);
			}
			i--;
		  }
		}

		// finally conversion character
		cvttype = spec.charAt(i);

		if (cvttype!='%') {
		  arg = args.get(argnum);
		  //System.out.println("cvttype="+cvttype+", arg="+arg);
		  argnum++;
		}

		sarg=null;
		switch (cvttype) {
		  // could just call toString() on large range, but cast so can check to see if object type is what is expected
		case 'd': case 'i':	sarg = ((Integer)arg).toString(); break;
		case 'o':
		  sarg = "0"+Integer.toOctalString(((Integer)arg).intValue());
		  break;
		case 'x':
		  sarg = "0x"+Integer.toHexString(((Integer)arg).intValue());
		  if (falt && !sarg.equals("0")) sarg="0x"+sarg;
		  break;
		case 'X':
		  sarg = Integer.toHexString(((Integer)arg).intValue());
		  if (falt && !sarg.equals("0")) sarg="0X"+sarg;
		  break;
		case 'u':	// unsigned decimal
		case 'c':	sarg = ((Character)arg).toString(); break;
		case 's':	sarg = (String)arg; break;
		case 'f':
		case 'e':
		case 'E':
		case 'g':
		case 'G':
		  sarg = ((Double)arg).toString();
		  break;
		case 'n':	sarg = String.valueOf(sb.length()); break;
		case '%':	sarg = "%"; break;

		case 'p':	// pointer
		  System.err.println("Java doesn't have pointers!");
		  // fall through
		default:
		  System.err.println("unknown conversion character `"+ch+"' in "+spec);
		  System.exit(1);
		}

		// format sarg according to flags, field widths
		int len = sarg.length();
		if (len<minwidth && !fminus) {
		  for (int j=0; j<(minwidth-len); j++) sb.append(fillchar);
		}

		sb.append(sarg);

		if (len<minwidth && fminus) {
		  for (int j=0; j<(minwidth-len); j++) sb.append(fillchar);
		}

	  }
	}

	//System.out.println("sprintf: "+spec+" => "+sb.substring(0));
	return sb.substring(0);
  }
*/
}
