<!-- Authors: Rob Fisher   -->
<!-- Authors: Charles Shaw -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" indent="yes" encoding="iso-8859-1" />
	<xsl:strip-space elements="*"/>
	<xsl:template match="TestScript">
		<html>
			<style type="text/css">
				<xsl:comment>
					.scriptInformationBoxTop {
						background-color: #999999;
						height: 6px;
					}
					.scriptInformationBox {
						margin:5px;
						padding:5px;
						font-size: small;
						font-family: "Courier New", Courier;
						background:whitesmoke;
						line-height:110%;
					}
					.scriptInitTitle {
						cursor: pointer;
						margin: 5px;
						padding:1px;
						font-size: small;
						font-family: "Courier New", Courier;
						background:whitesmoke;
						line-height:110%;
						text-align: left;
						width: 100%;
					}
					.scriptInitBox {
						margin:5px;
						padding:1px;
						font-size: small;
						font-family: "Courier New", Courier;
						background:whitesmoke;
						border: solid 1px silver;
						line-height:110%;
						text-align: center;
						width:100%
					}.locationTitle {
						cursor: pointer;
						margin:0px;
						padding:0px;
						font-size: small;
						font-family: "Courier New", Courier;
						background:offwhite;
						border: none;
						line-height:110%;
						text-align: left;
						width:100%
					}
					.location {
						margin:0px;
						padding:0px;
						font-size: small;
						font-family: "Courier New", Courier;
						background:offwhite;
						border: none;
						line-height:110%;
						text-align: left;
						width:100%
					}
					.scriptInitBody {
						padding:10px;
						font-size: small;
						font-family: "Courier New", Courier;
						background:whitesmoke;
						line-height:110%;
						width:100%
					}
					.testCaseBox {
						margin:5px;
						padding:5px;
						font-size: small;
						font-family: "Courier New", Courier;
						background:whitesmoke;
						border: solid 1px silver;
						line-height:110%;
						text-align: center;
						width:100%
					}
					.testCaseTitle {
						cursor: pointer;
						padding:5px;
						font-size: small;
						font-family: "Courier New", Courier;
						background:whitesmoke;
						line-height:110%;
						text-align: left;
						width: 100%;
					}
					.testCaseTitleName {
						font-size: small;
						font-family: "Courier New", Courier;
						background:whitesmoke;
						line-height:110%;
						left: 10px;
					}
					.testCaseTitleResults {
						padding:5px;
						font-size: small;
						font-family: "Courier New", Courier;
						background:whitesmoke;
						line-height:110%;
						right: 10px;
					}
					.testCaseBody {
						padding:10px;
						font-size: small;
						font-family: "Courier New", Courier;
						background:whitesmoke;
						line-height:110%;
						width:100%
					}
					.hdrserv {
						FONT-WEIGHT: bold; FONT-SIZE: 13px; BACKGROUND: #003366; COLOR: #ffffff; TEXT-DECORATION: none
					}
					.passed {
						font-size: small;
						font-family: "Courier New", Courier;
					}
					.failed {
						font-size: small;
						font-family: "Courier New", Courier;
						font-weight:bold;
						color:#FF0000;
					}
				</xsl:comment>
			</style>
			<script language="JavaScript1.2" type="text/javascript">
				function toggleDisplay(id) {
					var element = document.getElementById(id).style	
					element.display = (element.display == 'none')?'block':'none';
				}
				function toggleAllLocationElements() {
 				 for (var i = 0; !(i >= document.all.locationElement.length); i++) {
					  document.all.locationElement[i].style.display == (document.all.locationElement[i].style.display == 'none')?'block':'none';  
				  }
				}
			</script>
			<body>
				<div class="scriptInformationBoxTop"><xsl:comment>This comment is here to fix a IE height display bug</xsl:comment></div>
				<div class="scriptInformationBox">
							
				<TABLE width="95%">					
					<TR>
						<TD COLSPAN="1" style="width: 200px;"><b>DISPLAY FORMAT:</b></TD><TD COLSPAN="5" style="text-align: left;">Flat</TD>
					</TR>					
					<TR>
						<TD COLSPAN="1" style="width: 200px;"><b>TEST SCRIPT:</b> </TD><TD COLSPAN="5" style="text-align: left;"> <xsl:value-of select="Config/ScriptName"/></TD>
					</TR>
					<TR>
						<TD COLSPAN="1" style="width: 200px;"><b>TEST ENVIRONMENT:</b> </TD><TD COLSPAN="5" style="text-align: left;"> <xsl:value-of select="Config/Environment"/></TD>
					</TR>
					<TR>
						<TD COLSPAN="1" style="width: 200px;"><b>REVISION:</b> </TD><TD COLSPAN="5" style="text-align: left;"> <xsl:value-of select="Config/ScriptVersion/@revision"/>
						<xsl:variable name="modifiedFlag" select="Config/ScriptVersion/@modifiedFlag"/>
						<xsl:if test="string($modifiedFlag) and not($modifiedFlag = '-')"> - <SPAN style="color: red;"><xsl:value-of select="$modifiedFlag" /></SPAN></xsl:if>						</TD>
					</TR>	
					<TR>
						<TD COLSPAN="1" style="width: 200px;"><b>LAST AUTHOR:</b> </TD><TD COLSPAN="5" style="text-align: left;"> <xsl:value-of select="Config/ScriptVersion/@lastAuthor"/></TD>
					</TR>
					<TR> 
						<TD COLSPAN="1" style="width: 200px;"><b>LAST MODIFIED:</b> </TD><TD COLSPAN="5" style="text-align: left;"> <xsl:value-of select="Config/ScriptVersion/@lastModified"/></TD> 
					</TR>		
					<xsl:variable name="elapsedTime" select="ScriptResult/ElapsedTime"/>
					<xsl:variable name="hours" select="floor($elapsedTime div (1000 * 60 * 60))"/>
					<xsl:variable name="minutes" select="floor(($elapsedTime - ($hours * 1000 * 60 * 60)) div (1000 * 60))"/>
					<xsl:variable name="seconds" select="floor(($elapsedTime - ($hours * 1000 * 60 * 60) - ($minutes * 1000 * 60)) div (1000))"/>
					<xsl:variable name="millis" select="floor($elapsedTime - ($hours * 1000 * 60 * 60) - ($minutes * 1000 * 60) - ($seconds * 1000))"/>
					<TR>
						<TD COLSPAN="1" style="width: 200px;"><b>SCRIPT RUNTIME:</b> </TD><TD COLSPAN="5" style="text-align: left;"><xsl:value-of select="$hours"/>h <xsl:value-of select="$minutes"/>m <xsl:value-of select="$seconds"/>s <xsl:value-of select="$millis"/>ms</TD>
					</TR>									
				</TABLE>
				<TABLE width="50%">
					<TR>
						<TD COLSPAN="1" style="width: 200px;">
							<b>TEST POINTS:</b>
						</TD>
						<TD>
							<b>Total: </b>
							<xsl:value-of select="count(*//TestPoint)"/>
						</TD>
						<TD>
							<b>PASS: </b>
							<xsl:value-of select="count(*//TestPoint[Result='PASSED'])"/>
						</TD>
						<TD>
						<xsl:variable name="fails" select="count(*//TestPoint[Result = 'FAILED'])"/>
						<xsl:choose>
							<xsl:when test="$fails &gt; 0">
								<SPAN style="color: red;">
									<b>FAIL: </b>
								</SPAN>
								<SPAN style="color: red;">
									<xsl:value-of select="count(*//TestPoint[Result='FAILED'])"/>										
								</SPAN>
							</xsl:when>
							<xsl:otherwise>
								<b>FAIL: </b>
								<xsl:value-of select="count(*//TestPoint[Result='FAILED'])"/>
							</xsl:otherwise>
						</xsl:choose>
						</TD>
						
						<xsl:variable name="execResults" select="*//ExecutionResult"/>
						<xsl:if test="$execResults='ABORTED'">
							<TD style="font-size: large; text-align: right; width: 10%;">
							<SPAN style="color: red;">
								ABORTED
							</SPAN>
							</TD>
						</xsl:if>
					</TR>				
				</TABLE>
				</div>
				<xsl:apply-templates select="TestCase/OteScriptInfo/VersionInformation"/>
				<xsl:apply-templates/>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="TestScript/Config"/> <!-- Already handled in "TestScript" template -->
	<xsl:template match="Tracability"/> <!-- Already handled in "TestScript/TestCase" template -->
	<xsl:template match="Trace/ObjectName"/> <!-- Already handled in "Trace" template -->
	<xsl:template match="Trace/MethodName"/> <!-- Already handled in "Trace" template -->
	<xsl:template match="Trace/MethodArguments"/> <!-- Already handled in "Trace" template -->
	<xsl:template match="Trace/MethodArguments/Argument"/> <!-- Already handled in "Trace" template -->
	<xsl:template match="Trace/MethodArguments/Argument/Type"/> <!-- Already handled in "Trace" template -->
	<xsl:template match="Trace/MethodArguments/Argument/Value"/> <!-- Already handled in "Trace" template -->
	<xsl:template match="Trace/AdditionalInfo"/> <!-- Already handled in "Trace" template -->
	<xsl:template match="CheckGroup/Result"/> <!-- Already handled in "CheckGroup" template -->
	<xsl:template match="CheckPoint/TestPointName"/> <!-- Already handled in "CheckPoint" template -->
	<xsl:template match="CheckGroup/GroupName"/> <!-- Already handled in "CheckGroup" template -->

	<!-- These are logs outside of test cases -->
	<xsl:template match="TestScript/Debug"/>
	<xsl:template match="TestScript/Messaging"/>
	<xsl:template match="TestScript/Trace"/>
	<xsl:template match="TestScript/Support"/>
	<xsl:template match="TestScript/ScriptResult/ElapsedTime"/>
	<xsl:template match="TestScript/Debug/Location|TestScript/Messaging/Location|TestScript/Support/Location"/>
	<xsl:template match="UutLoggingInfo"/> 
		
	<xsl:template match="Info">
   	<xsl:variable name="infoData" select="Info" />

   	<xsl:variable name="title" select="@title" />
   		<TR>
				<TD colspan="2">
					<TABLE width="100%">
						<TR>
							<TD style="FONT-SIZE: 13px; width: 40%;">
							<b>
							<xsl:choose>
							<xsl:when test="string-length($title) > 0">
								<xsl:value-of select="@title"/>:
							</xsl:when>
							<xsl:otherwise>
								Value:
							</xsl:otherwise>
							</xsl:choose>	
							</b>
							</TD>
							<TD colspan="1" style="FONT-SIZE: 13px;">
								<xsl:value-of select="."/>
							</TD>
						</TR>	
					</TABLE>
					<xsl:apply-templates select="Location"/>	
				</TD>	
			</TR>
   	</xsl:template>
	
		<xsl:template match="Attention">
	   		<TR bgColor="#ffffff">
				<TD colspan="2">
					<TABLE width="100%" style="border-left: 1px solid #999999;">
						<TR>
							<TD colspan="1" style="FONT-SIZE: 13px;">
								<b>Message: </b><xsl:value-of select="Message"/>
							</TD>
						</TR>	
						<TR>
							<TD colspan="1" style="FONT-SIZE: 13px;">
								<xsl:apply-templates select="Location"/>	
							</TD>
						</TR>						
					</TABLE>
				</TD>	
			</TR>
	</xsl:template>
	
	<xsl:template match="ExecutionStatus|TestCase/ExecutionStatus">
		<xsl:variable name="execStatus" select="ExecutionResult"/>
		<xsl:if test="$execStatus='ABORTED'">
			<table style="font-size:13px; width: 95%;" bgColor="lightgrey" border="1">
				<td align="center" width="10%">
					<img src="http://urlToDesiredGif/under_construction.gif" alt="Aborted"/>
				</td>
				<td style="vertical-align: top; text-align: left; width:90%;">
					<b><i>
						<xsl:value-of select="ExecutionDetails"/>
					</i></b>
				</td>
			</table>
		</xsl:if>
	</xsl:template>
	
	<!-- These are logs within test cases or script init -->
	<xsl:template match="TestCase/Debug|Trace/Debug|TestCase/Messaging|Trace/Messaging|TestCase/Support|Trace/Support|ScriptInit/Messaging|ScriptInit/Support">
		<TR onmouseover="javascript:style.background='#F5F5DC'" onmouseout="javascript:style.background='#FFFFFF'" bgColor="#ffffff">
			<TD colspan="2">
				<TABLE width="100%">
					<TR>
						<TD style="FONT-SIZE: 13px; width: 100px;">
							<b><xsl:value-of select="local-name(.)"/>:</b>
						</TD>
						<TD colspan="1" style="FONT-SIZE: 13px;">
							<xsl:value-of select="Message"/>
						</TD>
					</TR>	
				</TABLE>
				<xsl:apply-templates select="Location"/>	
			</TD>	
		</TR>
	</xsl:template>
		
	<xsl:template match="TestCase/Trace|Trace/Trace|ScriptInit/Trace">
		<TR onmouseover="javascript:style.background='#F5F5DC'" onmouseout="javascript:style.background='#FFFFFF'" bgColor="#ffffff">
			<TD colspan="2">
				<TABLE width="100%" style="border-left: 1px solid #999999;">
					<TR>
						<TD style="FONT-SIZE: 13px; width: 5%;">
							<b>Trace:</b>
						</TD>
						<TD style="FONT-SIZE: 13px; FONT-WEIGHT: bold; width: 95%;">
														<!-- Only Include the ObjectName in the outfile if it was given. -->
							<xsl:variable name="objname" select="ObjectName"/>
							<xsl:choose>
								<xsl:when test="not(string($objname))">
									<xsl:value-of select="Location/Time"/> - <xsl:value-of select="MethodName"/>
									(<xsl:for-each select="MethodArguments/Argument">								

										<xsl:variable name="data" select="Value"/>																			
										<xsl:choose>
											<xsl:when test="starts-with($data,'&lt;![CDATA[')">
													<xsl:variable name="startValue" select="substring-after($data, '&lt;![CDATA[')" />	
													<xsl:variable name="finalValue" select="substring-before($startValue,']]&gt;')" />
													(&lt;<xsl:value-of select="Type"/>&gt; <xsl:value-of select="$finalValue"/>)
											</xsl:when>
											<xsl:otherwise>
												<xsl:variable name="finalValue" select="$data" />
												(&lt;<xsl:value-of select="Type"/>&gt; <xsl:value-of select="$finalValue"/>)
											</xsl:otherwise>
										</xsl:choose> 										
										
										
										
									</xsl:for-each>)
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="Location/Time"/> - <xsl:value-of select="ObjectName"/>.<xsl:value-of select="MethodName"/>
									(<xsl:for-each select="MethodArguments/Argument">

										<xsl:variable name="data" select="Value"/>
										<xsl:choose>
											<xsl:when test="starts-with($data,'&lt;![CDATA[')">
													<xsl:variable name="startValue" select="substring-after($data, '&lt;![CDATA[')" />	
													<xsl:variable name="finalValue" select="substring-before($startValue,']]&gt;')" />
													(&lt;<xsl:value-of select="Type"/>&gt; <xsl:value-of select="$finalValue"/>)
											</xsl:when>
											<xsl:otherwise>
												<xsl:variable name="finalValue" select="$data" />
												(&lt;<xsl:value-of select="Type"/>&gt; <xsl:value-of select="$finalValue"/>)
											</xsl:otherwise>
										</xsl:choose> 	

										</xsl:for-each>)
								</xsl:otherwise>
							</xsl:choose>
						</TD>							
					</TR>
					<xsl:variable name="messageType" select="AdditionalInfo/Message/Type"/>
					<xsl:choose>
					<xsl:when test="not(string($messageType))"/>
					<xsl:otherwise>
					<TR>
						<TD style="FONT-SIZE: 13px; width: 5%;">
							<b>Type:</b>
						</TD>
						<TD style="FONT-SIZE: 13px; FONT-WEIGHT: bold; width: 95%;">
							<xsl:value-of select="$messageType"/>
						</TD>
					</TR>
					</xsl:otherwise>
					</xsl:choose>
					
					<!-- 
					<TR>
						<TD style="FONT-SIZE: 13px; width: 5%;">
							<b>Source:</b>
						</TD>
						<TD align="left" style="FONT-SIZE: 13px; width: 95%;">
                      <A HREF="Open-{Location/Source}-{Location/Line}">
							<xsl:value-of select="Location/Source"/> 
                     (Line: <xsl:value-of select="Location/Line"/>)
                     </A>
						</TD>
					</TR> -->
					<TR>
						<TD colspan="2">
							<TABLE width="100%">
								<TR>
									<TD>
										<xsl:apply-templates/>
									</TD>
								</TR>
							</TABLE>								
						</TD>
					</TR>
				</TABLE>
			</TD>	
		</TR>
	</xsl:template>
	<xsl:template match="Location">
   	<div class="location">

			<xsl:variable name="id" select="@id"/>
			<div class="locationTitle" onClick="toggleDisplay('Location{$id}')">
				<TABLE width="100%">					
					<TR>	<TR>	
						

							<TD style="FONT-SIZE: 13px; width: 0%;">
								<b>Source...</b>
							</TD>
						</TR>
						</TR></TABLE>
			</div>
			<div class="location" style="display:none;" id="Location{$id}"> 
				<TABLE width="100%">					
					<TR>
						<xsl:for-each select="Stacktrace">
							<TR>
								<TD align="left" style="FONT-SIZE: 13px; width: 95%;">
		                     <A HREF="Open-{@source}-{@line}">
										<xsl:value-of select="@source"/>(Line:<xsl:value-of select="@line"/>)
		                     </A>
								</TD>					
							</TR>
						</xsl:for-each>
					</TR>
				</TABLE>
			</div>
		</div>
	</xsl:template>
	
	<xsl:template match="TestCase/OteScriptInfo/VersionInformation">
	<div class="scriptInformationBox">
	<TABLE width="100%">		
			<TR>
					<TD style="vertical-align: top; ">						
						<b>Version Information:</b>
					</TD>			
					</TR>
					<TR>
						<TR>
								<TD align="left" style="FONT-SIZE: 13px">
			                    <b> Name</b>
								</TD>					
								<TD align="left" style="FONT-SIZE: 13px">
		                      <b>Version:</b>
								</TD>					
								<TD align="left" style="FONT-SIZE: 13px">
		                      <b>Version Unit:</b>
								</TD>	
								<TD align="left" style="FONT-SIZE: 13px">
		                      <b>Under Under Test:</b> 
								</TD>	
							</TR>
						<xsl:for-each select="Version">
							<TR>
								<TD align="left" style="FONT-SIZE: 13px">
			                    <xsl:value-of select="@name"/>
								</TD>					
								<TD align="left" style="FONT-SIZE: 13px">
		                      <xsl:value-of select="@version"/>
								</TD>					
								<TD align="left" style="FONT-SIZE: 13px">
		                      <xsl:value-of select="@versionUnit"/>
								</TD>	
								<TD align="left" style="FONT-SIZE: 13px">
		                      <xsl:value-of select="@underTest"/>
								</TD>	
							</TR>
						</xsl:for-each>
					</TR>
				</TABLE>
				</div>
	  </xsl:template>
	
	<!-- 
	<xsl:template match="Location|TestPoint/Location">
		<TABLE width="100%">					
			<TR>
				<TR>
					<TD style="FONT-SIZE: 13px; width: 50px;">
						<b>Trace:</b>
					</TD>
					<TD style="FONT-SIZE: 13px;">
						<b><xsl:value-of select="Time"/></b>
					</TD>
				</TR>
				<TR>
						<TD style="FONT-SIZE: 13px; width: 5%;">
							<b>Source:</b>
						</TD>
						<TD align="left" style="FONT-SIZE: 13px; width: 95%;">
                      <A HREF="Open-{Source}-{Line}">
							<xsl:value-of select="Source"/> 
                     (Line: <xsl:value-of select="Line"/>)
                     </A>
						</TD>					
				</TR>
			</TR>
		</TABLE>
	</xsl:template>
	 -->
	<xsl:template match="TestPoint">
		<xsl:variable name="result" select="Result"/>
		<xsl:choose>
			<xsl:when test="contains($result,'PASSED')">
				<TR onmouseover="javascript:style.background='#F5F5DC'" onmouseout="javascript:style.background='#FFFFFF'" bgColor="#ffffff">
					<TD colspan="3">
						<TABLE width="100%">
							<TR>
								<TD style="FONT-SIZE: 13px;">
									<font color="blue">Test Point # <xsl:value-of select="Number"/></font>
									<b>
										<font color="green"> PASSED </font>
									</b>
								</TD>
							</TR>
						</TABLE>
						<xsl:apply-templates select="Location"/>
						<xsl:apply-templates select="CheckPoint"/>
						<xsl:apply-templates select="CheckGroup"/>
						<xsl:apply-templates select="RetryGroup"/>
					</TD>
				</TR>
			</xsl:when>
			<xsl:otherwise>
				<TR onmouseover="javascript:style.background='#FFEEEE'" onmouseout="javascript:style.background='#FFFFFF'" bgColor="#ffffff">
					<TD colspan="3">
						<TABLE width="100%">
							<TR>
								<TD style="FONT-SIZE: 13px;">
									<font color="blue">Test Point # <xsl:value-of select="Number"/></font>
									<b>
										<font color="red"> FAILED </font>
									</b>
								</TD>
							</TR>													
						</TABLE>
						<xsl:apply-templates select="Location"/>
						<xsl:apply-templates select="CheckPoint"/>	
						<xsl:apply-templates select="CheckGroup"/>
						<xsl:apply-templates select="RetryGroup"/>
					</TD>
				</TR>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="TestPoint/CheckPoint">
		<TABLE cellpadding="4">					
			<TR style="font-weight: bold;">
				<TD style="FONT-SIZE: 13px;">NAME</TD>
				<TD style="FONT-SIZE: 13px;">EXPECTED</TD>
				<TD style="FONT-SIZE: 13px;">ACTUAL</TD>
				<TD style="FONT-SIZE: 13px;">ELAPSED TIME</TD>
			</TR>
			<xsl:variable name="cpResult" select="Result"/>
			<xsl:choose>
				<xsl:when test="contains($cpResult,'PASSED')">
					<TR style="background-color: #DEDEDE;">
						<TD style="FONT-SIZE: 13px;"><xsl:value-of select="TestPointName"/></TD>
						<TD style="FONT-SIZE: 13px;"><xsl:value-of select="Expected"/></TD>
						<TD style="FONT-SIZE: 13px;"><xsl:value-of select="Actual"/></TD>
						<TD style="FONT-SIZE: 13px;"><xsl:value-of select="ElapsedTime"/></TD>
					</TR>
			</xsl:when>
				<xsl:otherwise>
					<TR style="background-color: #FFA9A9;">
						<TD style="FONT-SIZE: 13px;"><xsl:value-of select="TestPointName"/></TD>
						<TD style="FONT-SIZE: 13px;"><xsl:value-of select="Expected"/></TD>
						<TD style="FONT-SIZE: 13px;"><xsl:value-of select="Actual"/></TD>
						<TD style="FONT-SIZE: 13px;"><xsl:value-of select="ElapsedTime"/></TD>
					</TR>
				</xsl:otherwise>
			</xsl:choose>
		</TABLE>
	</xsl:template>
	
	<xsl:template match="CheckGroup/CheckPoint">
		<xsl:variable name="cpResult" select="Result"/>
		<xsl:choose>
			<xsl:when test="contains($cpResult,'PASSED')">
					<TR style="background-color: #DEDEDE;">
						<TD style="FONT-SIZE: 13px;"><xsl:value-of select="TestPointName"/></TD>
						<TD style="FONT-SIZE: 13px;"><xsl:value-of select="Expected"/></TD>
						<TD style="FONT-SIZE: 13px;"><xsl:value-of select="Actual"/></TD>
						<TD style="FONT-SIZE: 13px; FONT-WEIGHT: bold;"><xsl:value-of select="Result"/></TD>
					</TR>
				</xsl:when>
				<xsl:otherwise>
					<TR style="background-color: #FFA9A9;">
						<TD style="FONT-SIZE: 13px;"><xsl:value-of select="TestPointName"/></TD>
						<TD style="FONT-SIZE: 13px;"><xsl:value-of select="Expected"/></TD>
						<TD style="FONT-SIZE: 13px;"><xsl:value-of select="Actual"/></TD>
						<TD style="FONT-SIZE: 13px; FONT-WEIGHT: bold;"><xsl:value-of select="Result"/></TD>
					</TR>
				</xsl:otherwise>
			</xsl:choose>
	</xsl:template>
	
	<xsl:template match="RetryGroup">
		<xsl:variable name="cpResult" select="Result"/>
			<xsl:choose>
			<xsl:when test="contains($cpResult, 'PASSED')">
				<TABLE cellpadding="3" style="border-left: 5px solid green; margin-left: 10px;">					
					<TR>
						<TD colspan="3"  style="FONT-SIZE: 13px; font-weight: bold; ">
							RetryGroup [<xsl:value-of select="@Mode"/>] - <xsl:value-of select="GroupName"/>
						</TD>
					</TR>
					<TR>
						<TD colspan="2">
							<TABLE style="margin-left: 10px;" width="100%">
								<TR>
									<TD>
										<xsl:apply-templates/>
									</TD>
								</TR>
							</TABLE>
						</TD>
					</TR>
				</TABLE>		

			</xsl:when>
				<xsl:otherwise>
					<TABLE cellpadding="3" style="border-left: 5px solid red; margin-left: 10px;">					
						<TR>
							<TD colspan="3"  style="FONT-SIZE: 13px; font-weight: bold; ">
								RetryGroup [<xsl:value-of select="@Mode"/>] - <xsl:value-of select="GroupName"/>
							</TD>
						</TR>	
											<TR>
						<TD colspan="2">
							<TABLE style="margin-left: 10px;" width="100%">
								<TR>
									<TD>
										<xsl:apply-templates/>
									</TD>
								</TR>
							</TABLE>
						</TD>
					</TR>					
					</TABLE>
				</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="CheckGroup">
		<xsl:variable name="cpResult" select="Result"/>
		<xsl:choose>
			<xsl:when test="contains($cpResult, 'PASSED')">
				<TABLE cellpadding="3" style="border-left: 5px solid green; margin-left: 10px;">					
					<TR>
						<TD colspan="3"  style="FONT-SIZE: 13px; font-weight: bold; ">CheckGroup [<xsl:value-of select="@Mode"/>] - <xsl:value-of select="GroupName"/></TD>
					</TR>
					<TR style="font-weight: bold;">
						<TD style="FONT-SIZE: 13px;">NAME</TD>
						<TD style="FONT-SIZE: 13px;">EXPECTED</TD>
						<TD style="FONT-SIZE: 13px;">ACTUAL</TD>
						<TD>RESULT</TD>
					</TR>
					<xsl:apply-templates/>
				</TABLE>		
			</xsl:when>
				<xsl:otherwise>
					<TABLE cellpadding="3" style="border-left: 5px solid red; margin-left: 10px;">					
						<TR>
							<TD colspan="3"  style="FONT-SIZE: 13px; font-weight: bold; ">CheckGroup [<xsl:value-of select="@Mode"/>] - <xsl:value-of select="GroupName"/></TD>
						</TR>
						<TR style="font-weight: bold;">
							<TD style="FONT-SIZE: 13px;">NAME</TD>
							<TD style="FONT-SIZE: 13px;">EXPECTED</TD>
							<TD style="FONT-SIZE: 13px;">ACTUAL</TD>
							<TD>RESULT</TD>
						</TR>
						<xsl:apply-templates/>
					</TABLE>
				</xsl:otherwise>
		</xsl:choose>
	</xsl:template>		
	
	<xsl:template match="CheckGroup/CheckGroup">
		<TR>
			<TD colspan="4">
				<xsl:variable name="cpResult" select="Result"/>
				<xsl:choose>
					<xsl:when test="contains($cpResult, 'PASSED')">
						<TABLE cellpadding="3" style="border-left: 5px solid green; margin-left: 10px;">					
							<TR>
								<TD colspan="3"  style="FONT-SIZE: 13px; font-weight: bold; ">CheckGroup [<xsl:value-of select="@Mode"/>] - <xsl:value-of select="GroupName"/></TD>
							</TR>
							<TR style="font-weight: bold;">
								<TD style="FONT-SIZE: 13px;">NAME</TD>
								<TD style="FONT-SIZE: 13px;">EXPECTED</TD>
								<TD style="FONT-SIZE: 13px;">ACTUAL</TD>
								<TD>RESULT</TD>
							</TR>
							<xsl:apply-templates/>
						</TABLE>		
					</xsl:when>
						<xsl:otherwise>
							<TABLE cellpadding="3" style="border-left: 5px solid red; margin-left: 10px;">					
								<TR>
									<TD colspan="3"  style="FONT-SIZE: 13px; font-weight: bold; ">CheckGroup [<xsl:value-of select="@Mode"/>] - <xsl:value-of select="GroupName"/></TD>
								</TR>
								<TR style="font-weight: bold;">
									<TD style="FONT-SIZE: 13px;">NAME</TD>
									<TD style="FONT-SIZE: 13px;">EXPECTED</TD>
									<TD style="FONT-SIZE: 13px;">ACTUAL</TD>
									<TD>RESULT</TD>
								</TR>
								<xsl:apply-templates/>
							</TABLE>
						</xsl:otherwise>
				</xsl:choose>
			</TD>
		</TR>
	</xsl:template>		
	<!--  
	<xsl:template match="Debug/Location|Messaging/Location|Support/Location|Trace/Location">
		<TABLE width="100%">					
			<TR>
				<TD style="FONT-SIZE: 13px; width: 100px;">
					<b>Source:</b>
				</TD>
				<TD style="FONT-SIZE: 13px;">
					<xsl:value-of select="Source"/>
				</TD>
			</TR>
			<TR>
				<TD style="FONT-SIZE: 13px; width: 100px;">
					<b>Line:</b>
				</TD>
				<TD style="FONT-SIZE: 13px;">
					<xsl:value-of select="Line"/>
				</TD>
			</TR>
			<TR>
				<TD style="FONT-SIZE: 13px; width: 100px;">
					<b>Time:</b>
				</TD>
				<TD style="FONT-SIZE: 13px;">
					<xsl:value-of select="Time"/>
				</TD>
			</TR>
		</TABLE>
	</xsl:template>
	-->
	<xsl:template match="ScriptInit">
		<br/>
		
		<!-- Division for the whole script init -->
		<div class="scriptInitBox">
					
			<!-- Division for header of single script init -->
			<xsl:variable name="number" select="Number"/>
			<div class="scriptInitTitle" onClick="toggleDisplay('testCase{$number}')">
				
				<TABLE style="font-size: 15px; width: 100%;" border="0">
					<TD style="vertical-align: top; width: 80%;">						
						Project Script Init 
					</TD>
					
					
				<td style="vertical-align: top; horizontal-align: left; width: 20%;">
					<table style="font-size: 15px; width: 100%; vertical-align: top; horizontal-align: left;" border="0">

						<TD style="text-align: left; width: 5%;">Pass: </TD>
						<TD style="text-align: right; width: 10%;"><xsl:value-of select="count(.//TestPoint[Result = 'PASSED'])"/><br/></TD>
						
						<xsl:variable name="fails" select="count(.//TestPoint[Result = 'FAILED'])"/>
						<xsl:choose>
							<xsl:when test="$fails &gt; 0">
								<TD style="text-align: left; width: 5%;"><SPAN style="color: red;"><b>Fail: </b></SPAN></TD>
								<TD style="text-align: right; width: 10%;"><SPAN style="color: red;"><b><xsl:value-of select="count(.//TestPoint[Result = 'FAILED'])"/></b><br/></SPAN></TD>
							</xsl:when>
							<xsl:otherwise>
								<TD style="text-align: left; width: 5%;">Fail: </TD>
								<TD style="text-align: right; width: 10%;"><xsl:value-of select="count(.//TestPoint[Result = 'Failed'])"/><br/></TD>					
							</xsl:otherwise>
						</xsl:choose>
						
 					 	<td style="text-align: left; width: 5%;">Result: </td> 
			
						<xsl:variable name="execResults" select="ExecutionStatus/ExecutionResult"/>
						
						<xsl:choose>
							<xsl:when test="$execResults='ABORTED'">
								<TD style="text-align: right; width: 5%;"><SPAN style="color: red;">ABORTED</SPAN></TD>
							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when test="$fails &gt; 0">
										<TD style="text-align: right; width: 5%;"><SPAN style="color: red;">FAILED</SPAN></TD>
									</xsl:when>
									<xsl:otherwise>
										<TD style="text-align: right; width: 5%;"><SPAN style="color: blue;">PASSED</SPAN></TD>				
									</xsl:otherwise>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose> 
					</table>
				</td>

				</TABLE>	
			
			
			
			
			</div>
			
			<!-- Division for data internal to script init -->
			<div class="scriptInitBody" style="display:none;" id='testCase{$number}'>
				<TABLE width="100%" style="background:whitesmoke;">
					<tr>
						<td colspan="3" class="hdrserv">
							Script Initialization
						</td>
					</tr>
					<xsl:apply-templates/>
				</TABLE>
			</div>
		</div>
		
	</xsl:template>
	
	<xsl:template match="TestCase/Name"/> 
	<xsl:template match="TestCase/Number"/> <!-- TestScript/TestCase template handles this -->
	<xsl:template match="TestScript/TestCase">
		<br/>
		
		<!-- Division for the whole test case -->
		<div class="testCaseBox">
					
			<!-- Division for header of single test case -->
			<xsl:variable name="number" select="Number"/>
			<xsl:variable name="nameID" select="Name"/>
			<div class="testCaseTitle" onClick="toggleDisplay('testCase{$number}')">
				<TABLE style="font-size: 15px; width: 100%;" border="0">
																
					<xsl:choose>
						<xsl:when test="($number = '0')">
							<td style="vertical-align: top; horizontal-align: left; width:80%;"><xsl:value-of select="/TestScript/Config/ScriptName"/> Init</td>
						</xsl:when>
						<xsl:otherwise>
							<td style="vertical-align: top; horizontal-align: left; width: 10%;">Test Case <xsl:value-of select="Number"/></td>
						</xsl:otherwise>
					</xsl:choose>

					
					<xsl:if test="not(($number = '0'))">
						<TD style="font-size: 13px; vertical-align: top; horizontal-align: left; width: 70%;">
							<b><u><xsl:value-of select="Name"/></u></b>
						</TD>
					</xsl:if>
					
					<!-- Determine Pass/Fail of test case-->
				<td style="vertical-align: top; horizontal-align: left; width: 20%;">
					<table style="font-size: 15px; width: 100%; vertical-align: top; horizontal-align: left;" border="0">

						<TD style="text-align: left; width: 5%;">Pass: </TD>
						<TD style="text-align: right; width: 10%;"><xsl:value-of select="count(.//TestPoint[Result = 'PASSED'])"/><br/></TD>
						
						<xsl:variable name="fails" select="count(.//TestPoint[Result = 'FAILED'])"/>
						<xsl:choose>
							<xsl:when test="$fails &gt; 0">
								<TD style="text-align: left; width: 5%;"><SPAN style="color: red;"><b>Fail: </b></SPAN></TD>
								<TD style="text-align: right; width: 10%;"><SPAN style="color: red;"><b><xsl:value-of select="count(.//TestPoint[Result = 'FAILED'])"/></b><br/></SPAN></TD>
							</xsl:when>
							<xsl:otherwise>
								<TD style="text-align: left; width: 5%;">Fail: </TD>
								<TD style="text-align: right; width: 10%;"><xsl:value-of select="count(.//TestPoint[Result = 'Failed'])"/><br/></TD>					
							</xsl:otherwise>
						</xsl:choose>
						
 					 	<td style="text-align: left; width: 5%;">Result: </td> 
			
						<xsl:variable name="execResults" select="ExecutionStatus/ExecutionResult"/>
						
						<xsl:choose>
							<xsl:when test="$execResults='ABORTED'">
								<TD style="text-align: right; width: 5%;"><SPAN style="color: red;">ABORTED</SPAN></TD>
							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when test="$fails &gt; 0">
										<TD style="text-align: right; width: 5%;"><SPAN style="color: red;">FAILED</SPAN></TD>
									</xsl:when>
									<xsl:otherwise>
										<TD style="text-align: right; width: 5%;"><SPAN style="color: blue;">PASSED</SPAN></TD>				
									</xsl:otherwise>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose> 
					</table>
				</td>
				
				</TABLE>	
			</div>
			
			<!-- Division for data internal to single test case -->
			<div class="testCaseBody" style="display:none;" id='testCase{$number}'>
				<TABLE width="100%">
					<tr>
						<td class="hdrserv">
							Tracability
						</td>
					</tr>
					<xsl:for-each select="Tracability/RequirementId">
						<TR onmouseover="javascript:style.background='#F5F5DC'" onmouseout="javascript:style.background='#FFFFFF'" bgColor="#ffffff">
							<TD>
								<xsl:apply-templates/>
							</TD>
						</TR>
					</xsl:for-each>
				</TABLE>
				<br/>
				<!-- Body of the running of the test case -->
				<TABLE width="100%" style="background:whitesmoke;">
					<tr>
						<td colspan="3" class="hdrserv">
							Execution Details
						</td>
					</tr>
					<xsl:apply-templates/>
				</TABLE>
			</div>
		</div>
	</xsl:template>
</xsl:stylesheet>