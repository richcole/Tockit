package docsearcher;

import org.apache.poi.hpsf.*;
import org.apache.poi.poifs.eventfilesystem.*;
import org.apache.poi.util.HexDump;

class MyPOIFSReaderListener
    implements POIFSReaderListener
{
    String title;
    String author;
    String keyWords = "";

    public void processPOIFSReaderEvent(POIFSReaderEvent e)
    {
        PropertySet ps = null;
        SummaryInformation si = null;
        try
        {
            //si = (SummaryInformation)PropertySetFactory.create(e.getStream());
            ps = PropertySetFactory.create(e.getStream());
            si = (SummaryInformation)ps;
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Property set stream \"" + e.getPath()
                                       + e.getName() + "\": " + ex);
        }

        title = si.getTitle();
        if (title != null)
        {
            //System.out.println("Title: \"" + title + "\"");
        }
        else
        {
            //System.out.println("Document has no title.");
            title = "";
        }

        // author
        author = si.getLastAuthor();
        if (author != null)
        {
            // System.out.println("Author: \"" + author + "\"");
        }
        else
        {
            //System.out.println("Document has no author .");
            author = "";
        }

        // keywords
        keyWords = si.getKeywords();
        if (keyWords != null)
        {
            // System.out.println("keyWords : \"" + keyWords + "\"");
        }
        else
        {
            //System.out.println("Document has no keyWords .");
            keyWords = "";
        }

        // now iterate over the properties
//	if (ps !=null) {
//		List sections = ps.getSections();
//		Iterator it=sections.iterator();
//		
//		/* Print the number of sections: */
//		final long sectionCount = ps.getSectionCount();
//		System.out.println("   No. of sections: " + sectionCount);
//		
//		if (!sections.isEmpty()) {
//			Section sec;
//			int secNo=0;
//			int numProps=0;
//			String secStr="";
//			org.apache.poi.hpsf.Property[] properties;
//			org.apache.poi.hpsf.Property p;
//			long type;
//			Object value;
//			String s;	
//			while (it.hasNext()) {
//				sec = (Section)it.next();
//				s = hex(sec.getFormatID().getBytes());
//				s = s.substring(0, s.length() - 1);
//				properties = sec.getProperties();
//				numProps=properties.length;
//				System.out.println("---section #"+secNo+" has "+numProps+" props---");
//				for (int i=0;i<numProps;i++) {
//					p = properties[i];
//					type = p.getType();
//					value = p.getValue();
//					//System.out.println("["+type+"] <"+s+"> "+value);
//					if (type==31) System.out.println(value);
//					// if type==30 its likely text
//					} // end for props
//				secNo++;
//				} // end for hasnext
//			} // end for not empty
//		} // end for not null
    }

    public String hex(byte[] value)
    {
        //HexDump hd=new HexDump();
        return HexDump.toHex(value);
    }
}
