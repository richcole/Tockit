This plugin supports the following formats by default:
- Encapsulated Postscript (EPS)
- Extended Meta Files (EMF/WMF)
- Shockwave Flash (SWF)
- Portable Pixel Map (PPM)
- Portable Document Format (PDF)
- Scalable Vector Graphics (SVG)

For the latter two better options exist (iText, Batik).
If you want to disable FreeHEP's support for these formats,
delete the corresponding lines from "plugin.txt".

Known issues are:
- Unicode is not supported properly
- PDF: page size does not get adjusted to diagram size
- SVG: files could be smaller by using less groups and
       more complex objects