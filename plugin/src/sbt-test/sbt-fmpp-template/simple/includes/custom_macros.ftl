<#macro copyright date>
  Copyright (C) ${date} ${mail}. All rights reserved.
</#macro>

<#assign mail="user@example.com">

<#macro replaceDirectoryWithOrganization>
  <@pp.renameOutputFile name="../${organization?replace('.', '/')}/${pp.outputFileName}"/>
</#macro>
