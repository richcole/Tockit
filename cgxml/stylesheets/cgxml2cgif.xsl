<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">

<xsl:template match="knowledgeBase">
	<html>
	<head>
		<title>Knowledge Base</title>
	</head>
	<body>
		<xsl:apply-templates/>
	</body>
	</html>
</xsl:template>

<xsl:template match="cg">
	<pre>
		<xsl:apply-templates/>
	</pre>
</xsl:template>

<xsl:template match="concept">
	<xsl:text> [</xsl:text>
	<xsl:value-of select="@type"/>
	<xsl:if test="@name or @label or @quantifier = 'universal' ">
		<xsl:text>:</xsl:text>
	</xsl:if>
	<xsl:if test="@name">
		<xsl:text> &apos;</xsl:text>
		<xsl:value-of select="@name"/>
		<xsl:text>&apos;</xsl:text>
	</xsl:if>
	<xsl:if test="@label">
		<xsl:text> *</xsl:text>
	</xsl:if>
	<xsl:value-of select="@label"/>
	<xsl:if test="@quantifier = 'universal' ">
		<xsl:text> @every</xsl:text>
	</xsl:if>
	<xsl:text>]</xsl:text>
</xsl:template>

<xsl:template match="relation">
	<xsl:text> (</xsl:text>
	<xsl:value-of select="@name"/>
	<xsl:apply-templates/>
	<xsl:text>)</xsl:text>
</xsl:template>

<xsl:template match="ref">
	<xsl:text> ?</xsl:text>
	<xsl:value-of select="@label"/>
</xsl:template>

<xsl:template match="context">
	<xsl:text> [</xsl:text>
	<xsl:value-of select="@type"/>
	<xsl:text>:</xsl:text>
	<xsl:apply-templates/>
	<xsl:text>]</xsl:text>
</xsl:template>

</xsl:stylesheet>
