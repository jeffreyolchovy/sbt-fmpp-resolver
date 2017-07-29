<#-- The actual file name does not get interpretted, that is just being used for convention -->
<#-- The output file name changes due to the following directive -->
<@pp.renameOutputFile name="Hello${name}.md"/>
<#import "/@includes/custom_macros.ftl" as custom/>
<@custom.copyright date="2017"/>
