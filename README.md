# sbt-fmpp-resolver
An [Apache FreeMarker](http://freemarker.org/) template resolver for the [sbt](http://www.scala-sbt.org/) [new command](http://www.scala-sbt.org/0.13/docs/sbt-new-and-Templates.html).

[FMPP](http://fmpp.sourceforge.net/) is used as the template processing engine.

## Usage
To allow a specific project to use the `FmppTemplateResolver`, add the following lines to its `project/plugins.sbt` file:
```
resolvers ++= Seq(
  Resolver.bintrayRepo("jeffreyolchovy", "maven"),
  Resolver.url(
    "bintray-jeffreyolchovy-sbt-plugins",
    url("http://dl.bintray.com/jeffreyolchovy/sbt-plugins")
  )(Resolver.ivyStylePatterns)
)

addSbtPlugin("com.github.jeffreyolchovy" % "sbt-fmpp-template" % "0.1.0rc0")
```

To globally allow sbt to evaluate templates using this resolver, add those same lines to `~/.sbt/0.13/plugins/build.sbt`.

NOTE: The addition of the above resolvers is a temporary measure. They can be lifted once the plugin and its supporting library have been published to wider distribution channels.

Once the plugin has been installed for a given project (or globally), you can leverage it using `sbt new` or via the `fmpp` input task.

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

### Examples
Example sbt projects that utilize the `FmppTemplateResolver` exist in the [plugin/src/sbt-test/sbt-fmpp-template](plugin/src/sbt-test/sbt-fmpp-template) directory.

The examples include projects that:
- Evaluate templates that exist on the [local file system](plugin/src/sbt-test/sbt-fmpp-template/local/test)
- Evaluate templates from a [remote git repository](plugin/src/sbt-test/sbt-fmpp-template/remote/test)
- Evaluate templates from a [remote git repository, referencing a given branch by name](plugin/src/sbt-test/sbt-fmpp-template/remote/test)
- Evaluate templates from a [remote git repository, referencing a given tag](plugin/src/sbt-test/sbt-fmpp-template/remote/test)
- Evaluate templates that need to create [dynamic directory paths and file names](plugin/src/sbt-test/sbt-fmpp-template/dynamic-files-and-dirs/templates/main/scala/${organization}/Bar.scala)
- Evaluate templates that utilize [custom user-defined macros](plugin/src/sbt-test/sbt-fmpp-template/macros/includes/custom_macros.ftl)

## Motivation
sbt supports the idea of pluggable template resolvers, however, only one implementation ([Giter8](http://www.foundweekends.org/giter8/)) is provided out of the box. While I have made extensive use of Giter8 for project templates and project archetypes in the past, Giter8 is somewhat lacking in terms of raw features and extensibility. This is not necessarily a bad thing. The simplicity and limited range of Giter8 has actually been quite welcoming for the majority of my use cases, but when more power is required, there's not a whole lot you can do...

I considered forking and extending Giter8 with additional functionality, but since there already exist myriad templating solutions, it would make more sense, in my opinion, to leverage a more robust templating engine and implement any of Giter8's integral features in the new resolver or in the chosen templating solution itself.

After surveying the landscape of existing templating engines, I landed on Apache FreeMarker. FreeMarker has much more power than I anticipated -- or even desired -- and its array of features can, at times, be rather dizzying. The thing that's worth noting, however, is how simple integration has been. The FMPP front-end has a well documented feature set and Java API.

sbt's mechanism for adding template resolvers has also proven to be rather simple. The [docs](http://www.scala-sbt.org/0.13/docs/sbt-new-and-Templates.html#Template+Resolver) explain the process rather well.

I've spent most of my time wrestling with configuring the plugin to work against both sbt 0.13 and 1.0. There is a bit of noise and hackery in the build.sbt that I can't seem to simplify, and I hope some attention is placed on cross-sbt-version-plugin-development before we get an official 1.0 release.

Notably, some issues that I ran into when developing the plugin against sbt 0.13 and 1.0 were:
- https://github.com/sbt/sbt/issues/3392
- https://github.com/sbt/sbt/issues/3393

See the build.sbt and the linked issues for more information on the workarounds.

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

## Credits
- The real heavy lifting is done by the underlying templating engine and templating "front-end" (FreeMarker and FMPP)
- For git repository integration, I've reused whatever functionality I could from Giter8

Licensed under the Apache 2.0 license

Copyright Jeffrey Olchovy, 2017
