<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">

<xsl:output method="text"/>
 
<xsl:template match="knowledgeBase">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="cg">
	<xsl:text>strict graph G {
</xsl:text>
		<xsl:apply-templates/>
	<xsl:text>}</xsl:text>
</xsl:template>

<xsl:template match="concept">
	<xsl:text>	</xsl:text>
	<xsl:value-of select="generate-id()"/>
	<xsl:text> [label=&quot;</xsl:text>
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
	<xsl:text>&quot; shape=&quot;box&quot;];
</xsl:text>
</xsl:template>

<xsl:template match="relation">
	<xsl:text>	</xsl:text>
	<xsl:value-of select="generate-id()"/>
	<xsl:text> [label=&quot;</xsl:text>
	<xsl:value-of select="@name"/>
	<xsl:text>&quot; shape=&quot;ellipse&quot;];
</xsl:text>
	<xsl:apply-templates/>
	<xsl:apply-templates mode="edges"/>
</xsl:template>

<xsl:template match="ref">
	<!-- nothing in normal mode -->
</xsl:template>

<xsl:template match="context">
	<xsl:text>	subgraph </xsl:text>
	<xsl:value-of select="generate-id()"/>
	<xsl:text> {
	label=&quot;</xsl:text>
	<xsl:value-of select="@type"/>
	<xsl:text>&quot;;
</xsl:text>
	<xsl:apply-templates/>
	<xsl:text>}
</xsl:text>
</xsl:template>

<xsl:template match="concept" mode="edges">
	<xsl:text>	</xsl:text>
	<xsl:for-each select="..">
		<xsl:value-of select="generate-id()"/>
	</xsl:for-each>	
	<xsl:text> -- </xsl:text>
	<xsl:value-of select="generate-id()"/>
	<xsl:text> [label=&quot;</xsl:text>
	<xsl:value-of select="position()"/>
	<xsl:text>&quot;];
</xsl:text>
</xsl:template>

<xsl:template match="ref" mode="edges">
	<xsl:text>	</xsl:text>
	<xsl:for-each select="..">
		<xsl:value-of select="generate-id()"/>
	</xsl:for-each>	
	<xsl:text> -- </xsl:text>
	<xsl:for-each select="//concept[@label = ./@label]">
		<xsl:value-of select="generate-id()"/>
	</xsl:for-each>	
	<xsl:text> [label=&quot;</xsl:text>
	<xsl:value-of select="position()"/>
	<xsl:text>&quot;];
</xsl:text>
</xsl:template>

</xsl:stylesheet>
