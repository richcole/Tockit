<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" indent="yes" doctype-system="http://www.w3.org/TR/html4/loose.dtd"/>
    <xsl:template match="html">
        <html>
            <head>
                <!-- copy the head to get title and meta data -->
                <xsl:copy-of select="head/node()"/>
                <style type="text/css">
                    <xsl:comment>
body {color: #990000; font-family: helvetica,arial,sans-serif; margin: 0 0 0 0;background-color:white;}
h1 {color: #0000cc; font-family: helvetica,arial,sans-serif; font-size: large; font-variant: normal; font-weight: bolder}
div.footer {font-family: helvetica,arial,sans-serif; font-size: xx-small; vertical-align:middle; text-align:center; }
strong {color: #0000CC; font-weight:normal;}
dt {color: #0000cc; font-style: italic}
img.nav { display:block; }
table.layout{ padding:0; }
a {color: #0000CC;}
			 	</xsl:comment>
                </style>
                <!-- preload all needed xweb images -->
                <script language="JavaScript" type="text/javascript">
                    <xsl:comment>Begin
                        <!-- the section buttons -->
                        <xsl:for-each select="//img[contains(@xwebtype,'activeSection')]">
                            <xsl:value-of select="@name"/> = new Image( <xsl:value-of select="@width"/>,
                                                                        <xsl:value-of select="@height"/>);
                            <xsl:value-of select="@name"/>.src = "<xsl:value-of select="@src"/>";
                        </xsl:for-each>
                        <!-- the page buttons for the currently active section -->
                        <xsl:for-each select="//section[@active='true']">
                            <xsl:for-each select=".//img[@xwebtype='active']">
                                <xsl:value-of select="@name"/> = new Image( <xsl:value-of select="@width"/>,
                                                                            <xsl:value-of select="@height"/>);
                                <xsl:value-of select="@name"/>.src = "<xsl:value-of select="@src"/>";
                            </xsl:for-each>
                        </xsl:for-each>

                    // End</xsl:comment>
                </script>
            </head>
            <body>
                <table cellpadding="0" cellspacing="0" border="0" style="height:100%; width:100%">
                    <tr>
                        <td>
                            <a href="{//homepage[@id='homepage']/@src}">
                                <img class="nav" src="{//file[@id='banner1']/@src}" width="146" height="84" alt="Back to start page" border="0"/>
                            </a>
                        </td>
                        <td>
                            <!-- we nest another table to get no problems with the main body -->
                            <table cellpadding="0" cellspacing="0" border="0">
                                <tr>
                                    <td>
                                        <a href="{//homepage[@id='homepage']/@src}">
                                            <img class="nav" src="{//file[@id='banner2']/@src}" width="258" height="84" alt="Back to start page" border="0"/>
                                        </a>
                                    </td>
                                    <xsl:for-each select="//section">
                                        <td>
                                            <a href="{@src}">
                                                <xsl:choose>
                                                    <xsl:when test="@active='true' ">
                                                        <xsl:for-each select="img[@xwebtype='activeSection']">
                                                            <!-- should select exactly one -->
                                                            <img class="nav" src="{@src}" name="{@name}" border="0" alt="{concat(@alt, ' (active section)')}" width="{@width}" height="{@height}"/>
                                                        </xsl:for-each>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <xsl:attribute name="onMouseOver"><xsl:text>document.</xsl:text><xsl:value-of select="img[@xwebtype='section']/@name"/><xsl:text>.src='</xsl:text><xsl:value-of select="img[@xwebtype='activeSection']/@src"/><xsl:text>';</xsl:text></xsl:attribute>
                                                        <xsl:attribute name="onMouseOut"><xsl:text>document.</xsl:text><xsl:value-of select="img[@xwebtype='section']/@name"/><xsl:text>.src='</xsl:text><xsl:value-of select="img[@xwebtype='section']/@src"/><xsl:text>';</xsl:text></xsl:attribute>
                                                        <xsl:for-each select="img[@xwebtype='section']">
                                                            <!-- should select exactly one -->
                                                            <img class="nav" src="{@src}" name="{@name}" border="0" alt="{concat(@alt, ' (section)')}" width="{@width}" height="{@height}"/>
                                                        </xsl:for-each>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </a>
                                        </td>
                                    </xsl:for-each>
                                    <td align="left">
                                        <img class="nav" src="{//file[@id='banner3']/@src}" width="37" height="84" alt="Banner" border="0"/>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr valign="top" style="height:100%;">
                        <td width="130">
                            <table cellpadding="0" cellspacing="0" border="0">
                                <xsl:for-each select="//section[@active='true']">
                                    <xsl:apply-templates mode="nav"/>
                                </xsl:for-each>
                                <tr style="width:130px; height:100px;">
            <td align="center">
                                <img class="nav" src="{//file[@id='navfill']/@src}" border="0" alt="-" width="130" height="{30*(10-count(//section[@active='true']/entry)-count(//section[@active='true']/homepage))}"/>
            </td>
                                </tr>
                                <tr style="width:130px; height:30px;">
            <td align="center">
                                <img class="nav" src="{//file[@id='navend']/@src}" border="0" alt="-" width="130" height="30"/>
            </td>
                                </tr>
                            </table>
                        </td>
                        <td>
                            <xsl:copy-of select="body/node()"/>
                            <div align="center">
                                <img src="{//file[@id='bar']/@src}" alt="----------------------------------------------" width="500" height="30"/>
                            </div>
                            <div class="footer" align="center">
        This project is hosted on <a href="http://sourceforge.net"><img src="http://sourceforge.net/sflogo.php?group_id=21448" border="0" width="88" height="31" alt="SourceForge Logo" align="absmiddle"/></a> --visit the <a href="http://sourceforge.net/projects/tockit">project page</a>
                            </div>
                        </td>
                    </tr>
                </table>
            </body>
        </html>
    </xsl:template>
    <xsl:template match="homepage|entry" mode="nav">
        <tr>
            <td align="center">
                <a>
                    <xsl:attribute name="href"><xsl:value-of select="@src"/></xsl:attribute>
                    <xsl:choose>
                        <xsl:when test="@active">
                            <xsl:for-each select="img[@xwebtype='activeButton']">
                                <!-- should select exactly one -->
                                <img class="nav" src="{@src}" name="{@name}" border="0" alt="{concat(@alt, ' (active)')}" width="{@width}" height="{@height}"/>
                            </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="onMouseOver"><xsl:text>document.</xsl:text><xsl:value-of select="img[@xwebtype='button']/@name"/><xsl:text>.src='</xsl:text><xsl:value-of select="img[@xwebtype='activeButton']/@src"/><xsl:text>';</xsl:text></xsl:attribute>
                            <xsl:attribute name="onMouseOut"><xsl:text>document.</xsl:text><xsl:value-of select="img[@xwebtype='button']/@name"/><xsl:text>.src='</xsl:text><xsl:value-of select="img[@xwebtype='button']/@src"/><xsl:text>';</xsl:text></xsl:attribute>
                            <xsl:for-each select="img[@xwebtype='button']">
                                <!-- should select exactly one -->
                                <img class="nav" src="{@src}" name="{@name}" border="0" alt="{@alt}" width="{@width}" height="{@height}"/>
                            </xsl:for-each>
                        </xsl:otherwise>
                    </xsl:choose>
                </a>
            </td>
        </tr>
    </xsl:template>
</xsl:stylesheet>
