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
		<xsl:text>(</xsl:text>
			<xsl:apply-templates mode="concepts"/>
			<xsl:text>
	(and</xsl:text>
			<xsl:apply-templates mode="names"/>
			<xsl:apply-templates mode="relations"/>
		<xsl:text>))</xsl:text>
	</pre>
</xsl:template>

<xsl:template match="concept" mode="concepts">
	<xsl:choose>
		<xsl:when test="@quantifier = 'universal' ">forall </xsl:when>
		<xsl:otherwise>exists </xsl:otherwise>
	</xsl:choose>
	<xsl:text>(?</xsl:text>
	<xsl:choose>
		<xsl:when test="@label"><xsl:value-of select="@label"/></xsl:when>
		<xsl:otherwise><xsl:value-of select="generate-id()"/></xsl:otherwise>
	</xsl:choose>
	<xsl:text> </xsl:text>
	<xsl:value-of select="@type"/>
	<xsl:text>) </xsl:text>
</xsl:template>

<xsl:template match="relation" mode="concepts">
	<xsl:apply-templates mode="concepts"/>
</xsl:template>

<xsl:template match="context" mode="concepts">
	<!-- don't go into contexts -->
</xsl:template>

<xsl:template match="concept" mode="names">
	<xsl:if test="@name">
		<xsl:text> (Name ?</xsl:text>
		<xsl:choose>
			<xsl:when test="@label"><xsl:value-of select="@label"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="generate-id()"/></xsl:otherwise>
		</xsl:choose>
		<xsl:text> </xsl:text>
		<xsl:value-of select="@name"/>
		<xsl:text>)</xsl:text>
	</xsl:if>
</xsl:template>

<xsl:template match="relation" mode="names">
	<xsl:apply-templates mode="names"/>
</xsl:template>

<xsl:template match="context" mode="names">
	<!-- don't go into contexts -->
</xsl:template>

<xsl:template match="relation" mode="relations">
	<xsl:text> (</xsl:text>
	<xsl:value-of select="@name"/>
	<xsl:apply-templates mode="inRelation"/>
	<xsl:text>)</xsl:text>
</xsl:template>

<xsl:template match="concept" mode="inRelation">
	<xsl:text> ?</xsl:text>
	<xsl:choose>
		<xsl:when test="@label"><xsl:value-of select="@label"/></xsl:when>
		<xsl:otherwise><xsl:value-of select="generate-id()"/></xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="ref" mode="inRelation">
	<xsl:text> ?</xsl:text>
	<xsl:value-of select="@label"/>
</xsl:template>

<xsl:template match="context" mode="inRelation">
		<xsl:text>
		(</xsl:text>
			<xsl:apply-templates mode="concepts"/>
			<xsl:text>
	(and</xsl:text>
			<xsl:apply-templates mode="names"/>
			<xsl:apply-templates mode="relations"/>
		<xsl:text>))</xsl:text>
</xsl:template>

</xsl:stylesheet>
