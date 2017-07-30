# sbt-fmpp-resolver
An [Apache FreeMarker](http://freemarker.org/) template resolver for the [sbt](http://www.scala-sbt.org/) [new command](http://www.scala-sbt.org/0.13/docs/sbt-new-and-Templates.html).

[FMPP](http://fmpp.sourceforge.net/) is used as the template processing engine.

## Usage
To allow a specific project to use the `FmppTemplateResolver`, add the following lines to its `project/plugins.sbt` file:
```
addSbtPlugin("com.github.jeffreyolchovy" % "sbt-fmpp-template" % "0.1.0-SNAPSHOT")
```

To globally allow sbt to evaluate templates using this resolver, add those same lines to `~/.sbt/0.13/plugins/build.sbt`.

Once the plugin has been installed for a given project (or globally), you can leverage it using `sbt new`.

For example, given the following sbt project structure:
```
(root)
  |-/templates  # a directory containing a structured set of FreeMarker templates and/or static files
  |-/includes   # a directory containing FreeMarker files that can be used as libraries
  |-/project
  |-build.sbt
```

The following command will:
- Evaluate the templates found in the `templates` directory using the data given on the command line (`-D ...`)
- Allow templates to use any custom macros and instructions defined in the `includes` directory
- Emit the generated files to the `src` directory
```
sbt new -S templates -O src -D "name:MyProject, organization:com.example" --freemarker-links includes:includes
```

The file system structure of the `templates` directory will be preserved, however, file and directory names can be altered using FreeMarker macros. This allows for the interpolation and expansion of template file paths, if desired.

By default, empty directories will not be copied to the target destination. This behavior is configurable.

For more information refer to the [FreeMarker manual](http://freemarker.org/docs/index.html) and the [FMPP Command-line tool documentation](http://fmpp.sourceforge.net/commandline.html).

All of FMPP's command-line options are respected.

## Motivation
sbt supports the idea of pluggable template resolvers, however, only one implementation ([Giter8](http://www.foundweekends.org/giter8/)) is provided out of the box. While I have made extensive use of Giter8 for project templates and project archetypes in the past, Giter8 is somewhat lacking in terms of raw features and extensibility. This is not necessarily a bad thing. The simplicity and limited range of Giter8 has actually been quite welcoming for the majority of my use cases, but when more power is required, there's not a whole lot you can do...

I considered forking and extending Giter8 with additional functionality, but since there already exist myriad templating solutions, it would make more sense, in my opinion, to leverage a more robust templating engine and implement any of Giter8's integral features in the new resolver or in the chosen templating solution itself.

After surveying the landscape of existing templating engines, I landed on Apache FreeMarker. FreeMarker has much more power than I anticipated -- or even desired -- and its array of features can, at times, be rather dizzying. The thing that's worth noting, however, is how simple integration has been. The FMPP front-end has a well documented feature set and Java API.

sbt's mechanism for adding template resolvers has also proven to be rather simple. The [docs](http://www.scala-sbt.org/0.13/docs/sbt-new-and-Templates.html#Template+Resolver) explain the process rather well.

I've spent most of my time wrestling with configuring the plugin to work against both sbt 0.13 and 1.0. There is a bit of noise and hackery in the build.sbt that I can't seem to simplify, and I hope some attention is placed on cross-sbt-version-plugin-development before we get an official 1.0 release.

Notably, some issues that I ran into when developing the plugin against sbt 0.13 and 1.0 were:
- A plugin (Plugin A) that has a library dependency on another plugin (Plugin B) fails to resolve the plugin artifact (Plugin B's artifact) correctly when using `addSbtPlugin` in the build.sbt file
  - This issue is worked around by invoking `Defaults.sbtPluginExtra` with the current sbt and Scala versions
- A plugin that has a dependency on another plugin in your build.sbt fails to resolve the plugin artifact correctly when using `dependsOn`
  - This issue is also worked around by invoking `Defaults.sbtPluginExtra` with the current sbt, Scala, and project versions
- A plugin that has a dependency on a non-plugin subproject in your build.sbt fails to resolve the project artifact correctly when using `dependsOn` if `scalaVersion` is not explicitly/correctly set for the current version of sbt in the plugin project.
  - This issue is worked around by explicity setting the Scala version conditionally, using the current sbt version as input

See the build.sbt for more informations on the workarounds. These were non-obvious, and honestly, that's where I spent most of my time.

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
