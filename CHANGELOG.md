# Changelog

## [Unreleased]
### Added

### Changed

### Deprecated

### Removed

### Fixed
## [0.3.1]
### Fixed
- Issues with numbers in translation keys (#12)

## [0.3.0]
### Added
- Support for generic open & closed block statements (e.g. cacheinclude cache statements)

### Changed
- Significant internal refactor of parser logic
- Renamed a number of token types to better indicate what they represent
- Added a new token type for "nested" statements (inside closed blocks)
- Removed unused token type for $ThemeDir variable
- Simplified comment lexing

### Fixed
- Issue with background indexing of templates (#3)
## [0.2.3]
### Fixed
- Further issues with how $Variables without {} braces were handled

## [0.2.2]
### Fixed
- Regression in how $Variables without {} braces were handled

## [0.2.1]
### Fixed
- Issues with typing `$` or `>` in an empty template
- Includes with numbers in file name were broken
- Annotations for includes using double-backslash separators were broken

## [0.2.0]
### Added
- Support for renaming/moving include files (auto-update references)

### Fixed
- Broken click to navigate to include
- Stale references to includes when include statement is changed

## [0.1.0]
### Added
- Live templates for code completion/surrounding

### Fixed
- Issues with include statements/references triggering exceptions
## [0.0.2]

### Fixed
- Some include files wouldn’t be recognised as valid in SS4 projects

## [0.0.1]

- Initial release
