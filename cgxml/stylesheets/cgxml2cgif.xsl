<?xml version="1.0" encoding="UTF-8"?>
<!-- this stylesheets converts cgxml into CGIF. The XML has to use the graph element has outermost 
     element, modules are not yet supported -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">

<xsl:output method="text"/>

<xsl:template match="graph|descriptor">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="concept">
	<xsl:text> [</xsl:text>
	<xsl:value-of select="@type"/>
	<xsl:if test="@id">
		<xsl:text> *</xsl:text><xsl:value-of select="@id"/>
	</xsl:if>
	<xsl:if test="designator/* | descriptor/* | quantifier/universal | qunatifier/defined">
		<xsl:text>:</xsl:text>
	</xsl:if>
	<xsl:if test="quantifier/universal">
		<xsl:text>@every</xsl:text>
	</xsl:if>
	<xsl:if test="quantifier/defined">
		<xsl:text>@</xsl:text>
		<xsl:value-of select="quantifer/defined"/>
	</xsl:if>
	<xsl:if test="designator/locator">
		<xsl:value-of select="designator/locator"/>
	</xsl:if>
	<xsl:if test="designator/literal">
		<xsl:text>'</xsl:text>
		<xsl:value-of select="designator/literal"/>
		<xsl:text>'</xsl:text>
	</xsl:if>
	<xsl:if test="descriptor/*">
		<xsl:apply-templates/>
	</xsl:if>
	<xsl:text>]</xsl:text>
</xsl:template>

<xsl:template match="relation">
	<xsl:text>( </xsl:text>
	<xsl:value-of select="@type"/>
	<xsl:apply-templates/>
	<xsl:text> )</xsl:text>
</xsl:template>

<xsl:template match="conceptRef">
	<xsl:text> ?</xsl:text>
	<xsl:value-of select="."/>
</xsl:template>

</xsl:stylesheet>
