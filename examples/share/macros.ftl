<#macro replaceDirectoryWith newDirectoryName>
  <@pp.renameOutputFile name="../${newDirectoryName}/${pp.outputFileName}"/>
</#macro>

<#macro replaceFileWith newFileName>
  <@pp.renameOutputFile name="${newFileName}"/>
</#macro>

<#function packaged input>
  <#return input?replace('.', '/')/>
</#function>

<#function snake input>
  <#return input?replace('( +|\\.)', '_', 'r')/>
</#function>
