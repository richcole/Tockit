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
		<xsl:apply-templates mode="concepts"/>
		<xsl:apply-templates mode="names"/>
		<xsl:apply-templates mode="relations"/>
	</pre>
</xsl:template>

<xsl:template match="concept" mode="concepts">
	<xsl:choose>
		<xsl:when test="@quantifier = 'universal' ">
			<xsl:text>&quot;</xsl:text>
			<xsl:choose>
				<xsl:when test="@label"><xsl:value-of select="@label"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="generate-id()"/></xsl:otherwise>
			</xsl:choose>
			<xsl:text>&quot; represents every instance of type &quot;</xsl:text>
		</xsl:when>
		<xsl:otherwise>
			<xsl:text>There is one &quot;</xsl:text>
			<xsl:choose>
				<xsl:when test="@label"><xsl:value-of select="@label"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="generate-id()"/></xsl:otherwise>
			</xsl:choose>
			<xsl:text>&quot; which is of type &quot;</xsl:text>
		</xsl:otherwise>
	</xsl:choose>
	<xsl:value-of select="@type"/>
	<xsl:text>&quot;. </xsl:text>
</xsl:template>

<xsl:template match="relation" mode="concepts">
	<xsl:apply-templates mode="concepts"/>
</xsl:template>

<xsl:template match="context" mode="concepts">
	<!-- don't go into contexts -->
</xsl:template>

<xsl:template match="concept" mode="names">
	<xsl:if test="@name">
		<xsl:text>The name of &quot;</xsl:text>
		<xsl:choose>
			<xsl:when test="@label"><xsl:value-of select="@label"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="generate-id()"/></xsl:otherwise>
		</xsl:choose>
		<xsl:text>&quot; is &quot;</xsl:text>
		<xsl:value-of select="@name"/>
		<xsl:text>&quot;. </xsl:text>
	</xsl:if>
</xsl:template>

<xsl:template match="relation" mode="names">
	<xsl:apply-templates mode="names"/>
</xsl:template>

<xsl:template match="context" mode="names">
	<!-- don't go into contexts -->
</xsl:template>

<xsl:template match="relation" mode="relations">
	<xsl:text>The &quot;</xsl:text>
	<xsl:value-of select="@name"/>
	<xsl:text>&quot; of </xsl:text>
	<xsl:apply-templates mode="firstInRelation"/>
	<xsl:text> </xsl:text>
	<xsl:choose>
		<xsl:when test="count(child::*) = 2">
			<xsl:text>is </xsl:text>
			<xsl:apply-templates mode="lastInRelation"/>
			<xsl:text>. </xsl:text>
		</xsl:when>
		<xsl:otherwise>
			<xsl:text>are ...</xsl:text>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="concept" mode="firstInRelation">
	<xsl:if test="position() = 1">
		<xsl:text>&quot;</xsl:text>
		<xsl:choose>
			<xsl:when test="@label"><xsl:value-of select="@label"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="generate-id()"/></xsl:otherwise>
		</xsl:choose>
		<xsl:text>&quot;</xsl:text>
	</xsl:if>
</xsl:template>

<xsl:template match="ref" mode="firstInRelation">
	<xsl:if test="position() = 1">
		<xsl:text>&quot;</xsl:text>
		<xsl:value-of select="@label"/>
		<xsl:text>&quot;</xsl:text>
	</xsl:if>
</xsl:template>

<xsl:template match="context" mode="firstInRelation">
	<xsl:if test="position() = 1">
		<xsl:text>a context of type &quot;</xsl:text>
		<xsl:value-of select="@type"/>
		<xsl:text>&quot; describing the following situation. [Begin Context]</xsl:text>
		<xsl:apply-templates mode="concepts"/>
		<xsl:apply-templates mode="names"/>
		<xsl:apply-templates mode="relations"/>
		<xsl:text>[End Context]</xsl:text>
	</xsl:if>
</xsl:template>

<xsl:template match="concept" mode="lastInRelation">
	<xsl:if test="position() = last()">
		<xsl:text>&quot;</xsl:text>
		<xsl:choose>
			<xsl:when test="@label"><xsl:value-of select="@label"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="generate-id()"/></xsl:otherwise>
		</xsl:choose>
		<xsl:text>&quot;</xsl:text>
	</xsl:if>
</xsl:template>

<xsl:template match="ref" mode="lastInRelation">
	<xsl:if test="position() = last()">
		<xsl:text>&quot;</xsl:text>
		<xsl:value-of select="@label"/>
		<xsl:text>&quot;</xsl:text>
	</xsl:if>
</xsl:template>

<xsl:template match="context" mode="lastInRelation">
	<xsl:if test="position() = last()">
		<xsl:text>a context of type &quot;</xsl:text>
		<xsl:value-of select="@type"/>
		<xsl:text>&quot; describing the following situation. [Begin Context]</xsl:text>
		<xsl:apply-templates mode="concepts"/>
		<xsl:apply-templates mode="names"/>
		<xsl:apply-templates mode="relations"/>
		<xsl:text>[End Context]</xsl:text>
	</xsl:if>
</xsl:template>

</xsl:stylesheet>
