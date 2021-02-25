# Idea Silverstripe Plugin

## ⚠️ Work in progress ⚠️

This plugin adds Silverstripe template support to PHPStorm and other JetBrains IDEs.

### Installation

Until the plugin is ready for a stable release and is available on the JetBrains plugin marketplace, it must be
installed from disk:

- Disable the existing Silverstripe plugin if you have it installed
- Download the latest release from [the releases page](https://github.com/kinglozzer/idea-silverstripe-plugin/releases)
- Open the plugins menu in your IDE
- Open the options menu (`⋮` in PHPStorm)
- Choose “Install Plugin from Disk” and select the `.jar` file you downloaded

### Features

- Syntax highlighting for variables and block (if/else/loop/with) statements
- Error highlighting for malformed/incorrect block statements
- Auto-suggest/auto-completion of includes
- Click-to-navigate to include files in `<% include %>` statements

### Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for tips on getting the plugin to build.

## Credits

I must send a huge thank you to [Marcus Dalgren](https://github.com/MarcusDalgren) for his work on the
[original Silverstripe plugin](https://github.com/raket/idea-silverstripe). It has served Silverstripe developers
perfectly for a number of years before it finally succumbed to the effects of many platform updates. It served as the
inspiration for almost all the features in this version, and offered an excellent starting point for the JFlex lexer.

I also have to thank JetBrains for their open-source [IntelliJ plugins](https://github.com/JetBrains/intellij-plugins),
which have been an invaluable source of information on how to implement certain features.
