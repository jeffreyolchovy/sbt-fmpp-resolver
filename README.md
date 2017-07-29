# sbt-fmpp-resolver
An [Apache FreeMarker](http://freemarker.org/) template resolver for the [sbt](http://www.scala-sbt.org/) [new command](http://www.scala-sbt.org/0.13/docs/sbt-new-and-Templates.html).

[FMPP](http://fmpp.sourceforge.net/) is utilized as the template processing engine.

## Project Structure
- [plugin](#plugin)
- [plugin-buildinfo](#plugin-buildinfo)
- [resolver](#resolver)

### plugin
An sbt plugin that adds the [`FmppTemplateResolver`](resolver/src/main/scala/sbtfmppresolver/FmppTemplateResolver.scala) to sbt's `templateResolverInfos` setting.

### plugin-buildinfo
**NOT YET IMPLEMENTED**

An additional plugin that requires the [sbt-buildinfo](https://github.com/sbt/sbt-buildinfo) plugin, as it allows users to inject contextual project information into the templates at evaluation time.

A `TDD` renderer is provided for exporting `BuildInfoResult`s in a format digestable by FMPP.

### resolver
The template resolver implementation.

Currently, it programmatically invokes the FMPP `CommandLine` front-end via FMPP's Java API.

This will be replaced by a custom front-end which can resolve remote resources.

## Limitations (and workarounds)
- ~~Does not support dynamic file and directory names~~
  - Dynamic file and directory names can be obtained by utilizing `pp` directives *inside* templates
- Does not include built-ins for Java-esque style string conversions (e.g. to/from UpperCamelCase or lowerCamelCase)
  - These operations can be defined in custom macros and included as a library (**namespace**, in FreeMarker lingo)
