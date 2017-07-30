<#macro replaceDirectoryWithOrganization>
  <@pp.renameOutputFile name="../${organization?replace('.', '/')}/${pp.outputFileName}"/>
</#macro>
