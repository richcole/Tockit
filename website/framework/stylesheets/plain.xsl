<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">

<xsl:template match="webpage">
	<html>
		<title>
			<xsl:value-of select="@title"/>
		</title>
		<body>
			<xsl:apply-templates/>
		</body>
	</html>
</xsl:template>

<xsl:template match="website">
	<table>
		<tr>
			<xsl:apply-templates/>
		</tr>
	</table>
</xsl:template>

<xsl:template match="website/homepage">
	<td>
		<a>
			<xsl:attribute name="href">
				<xsl:value-of select="../@baseURL"/>/<xsl:value-of select="@tname"/>
			</xsl:attribute>
			<xsl:value-of select="@name"/>
		</a>
	</td>
</xsl:template>

<xsl:template match="website/section">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="website/section/entry">
	<td>
		<a>
			<xsl:attribute name="href">
				<xsl:value-of select="../../@baseURL"/>/<xsl:value-of select="../@dir"/>/<xsl:value-of select="@tname"/>
			</xsl:attribute>
			<xsl:value-of select="."/>
		</a>
	</td>
</xsl:template>

<xsl:template match="html">
	<xsl:copy-of select="*"/>
</xsl:template>
</xsl:stylesheet>
