<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">

<xsl:template match="webpage">
	<html>
		<title>
			<xsl:value-of select="@title"/>
		</title>
		<body bgcolor="white">
			<xsl:apply-templates/>
			<hr/>
			<table align="center" border="0">
			<tr valign="center">
				<td width="50%"/>
				<td nowrap="true">
				This project is hosted on 
				</td>
				<td>
				<a href="http://sourceforge.net">
					<img src="http://sourceforge.net/sflogo.php?group_id=21448&amp;type=1" width="88" height="31" border="0" alt="SourceForge Logo"/>
				</a>
				</td>
				<td nowrap="true">
				  -- visit the <a href="http://sourceforge.net/projects/tockit">project page</a>
				</td>
				<td width="50%"/>
			</tr>
			</table>
		</body>
	</html>
</xsl:template>

<xsl:template match="website">
	<div align="center">
		<p><font size="+8"><b>Tockit</b></font></p>
		<p><font size="+2">Framework for Conceptual Knowledge Processing</font></p>
	</div>
	<hr/>
	<table align="center" cellpadding="12">
		<tr>
			<xsl:apply-templates/>
		</tr>
	</table>
	<hr/>
</xsl:template>

<xsl:template match="website/homepage">
	<td>
		<font size="+1"><a>
			<xsl:attribute name="href">
				<xsl:value-of select="../@baseURL"/>/<xsl:value-of select="@tname"/>
			</xsl:attribute>
			<xsl:value-of select="@name"/>
		</a></font>
	</td>
</xsl:template>

<xsl:template match="website/section">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="website/section/entry">
	<td>
		<font size="+1"><a>
			<xsl:attribute name="href">
				<xsl:value-of select="../../@baseURL"/>/<xsl:value-of select="../@dir"/>/<xsl:value-of select="@tname"/>
			</xsl:attribute>
			<xsl:value-of select="."/>
		</a></font>
	</td>
</xsl:template>

<xsl:template match="html">
	<xsl:copy-of select="*"/>
</xsl:template>
</xsl:stylesheet>
