<#-- An example of renaming the file/directory using the convenience macro defined in includes -->
<#import "/@includes/custom_macros.ftl" as custom/>
<@custom.replaceDirectoryWithOrganization/>
package ${organization}

case class Bar()
