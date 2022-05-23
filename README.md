# BitrixExcluder

![Build](https://github.com/ilimurzin/BitrixExcluder/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/19207-bitrixexcluder.svg)](https://plugins.jetbrains.com/plugin/19207-bitrixexcluder)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/19207-bitrixexcluder.svg)](https://plugins.jetbrains.com/plugin/19207-bitrixexcluder)

<!-- Plugin description -->
This plugin allows you to exclude Bitrix directories in two clicks.
Plugin will exclude Bitrix `cache`, `upload` and `install` directories.
Files in excluded directories are ignored by code completion, navigation and inspection.
So excluding them will speed up indexing and also remove unnecessary completions from `install` directories.
<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "BitrixExcluder"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/ilimurzin/BitrixExcluder/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
