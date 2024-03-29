# Silverstripe Template Language Support

This plugin adds Silverstripe template support to PHPStorm and other JetBrains IDEs.

## Platform requirements

This plugin requires PHPStorm (or other IDEA-based IDE) to be running version 2022.3 or greater.

## Installation

It’s recommended to install the plugin via the JetBrains marketplace. Just search for “Silverstripe Template Language Support”, or use the following link:

[View on JetBrains marketplace](https://plugins.jetbrains.com/plugin/17014-silverstripe-template-language-support)

Alternatively, the plugin can be installed from disk:

- Download the latest release from [the releases page](https://github.com/kinglozzer/idea-silverstripe-plugin/releases)
- Open the plugins menu in your IDE
- Open the options menu (`⋮` in PHPStorm)
- Choose “Install Plugin from Disk” and select the `.jar` file you downloaded

## Features

- Syntax highlighting for variables and block (if/else/loop/with) statements
- Error highlighting for malformed/incorrect block statements
- Auto-suggest/auto-completion of includes
- Click-to-navigate to include files in `<% include %>` statements
- Live templates for auto-completing blocks/tags and surrounding statements with  if/else etc

### Live templates

The keyboard shortcuts referenced below are for MacOS - to check for your operating system, review the [live templates documentation](https://www.jetbrains.com/help/idea/using-live-templates.html).

\* These templates include placeholders for variables - simply type a variable, then press the `return` key to continue.

#### Completion:

The following live templates are available for completion - simply type the abbreviation and hit the `TAB` key:

- `if`*
- `elseif`*
- `else`
- `with`*
- `loop`*
- `cached`*
- `inc`* - auto-suggests template includes
- `reqcss`* - a `<% require themedCSS() %>` tag
- `reqjs`* - a `<% require themedJavascript() %>` tag
- `base` - a `<% base_tag %>` tag
- `_t`* - a translation tag

#### Surround:

The following live templates are available for surrounding existing code. Highlight a portion of your template, then press `⌥⌘J` to bring up the templates menu and select from the following:

- `if`* - surround with an if statement
- `with`* - surround with a "with" statement
- `loop`* - surround with a loop
- `cached`* - surround with a partial caching block

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for tips on getting the plugin to build.

## Credits

I must send a huge thank you to [Marcus Dalgren](https://github.com/MarcusDalgren) for his work on the
[original Silverstripe plugin](https://github.com/raket/idea-silverstripe). It has served Silverstripe developers
perfectly for a number of years before it finally succumbed to the effects of many platform updates. It served as the
inspiration for almost all the features in this version, and offered an excellent starting point for the JFlex lexer.

I also have to thank JetBrains for their open-source [IntelliJ plugins](https://github.com/JetBrains/intellij-plugins),
which have been an invaluable source of information on how to implement certain features.
