<p align="center">
    <a href="https://kickstartfx.xpipe.io/" target="_blank" rel="noopener">
        <img src="https://kickstartfx.xpipe.io/images/demo1.png" alt="XPipe Banner" />
    </a>
</p>

<h1></h1>

## About

KickstartFX is a ready-to-use template for JavaFX applications. It can serve as a solid foundation for your own JavaFX application as everything is fully customizable and extendable.

KickstartFX is more than just a basic template that opens a simple window. It contains a lot of code to handle the challenges of applications in the real world to achieve the best possible desktop application experience across all operating systems. The code is based on [XPipe](https://github.com/xpipe-io/kickstartfx), a well-established JavaFX application and is the result of years of experience developing desktop application that is used by many thousands of users right now.

It features the following noteworthy features that you won't get with any other templates:
- A fully up-to-date build using the latest features of JDK25, Gradle 9, JavaFX 25, WiX 6, and much more
- Native executable and installer generation for all operating systems using native tools
- A fully modularized build, including fully modularized dependencies and the usage of jmods
- A ready-to-deploy GitHub actions pipeline to automatically build end release your application
- Close-to-native theming capabilities with AtlantaFX themes as the basis combined with many manual adjustments
- Advanced error handling and issue tracking with support for Sentry
- Integrated ability to automatically codesign the application on Windows and macOS
- Solid state management for caches, persistent data, and more
- Many customization options available to users in a comprehensible settings menu
- Update check capabilities for GitHub releases
- Built-in troubleshooting tools for developers and users, including debug mode, heap dump, and more
- Self-restart functionality to spawn new independent processes of your application
- Desktop and registry access support classes
- Plenty of checks to warn users about problems with their system configuration, environment, and compatibility
- Robust dependency management and font handling, your application will even run in WSL
- Hot-reload capabilities for all resources, including reapplying stylesheets
- Application instant management via inter-process communication
- System tray icon support
- Built-in support for Jackson and Lombok
- Built-in translations support with language changes applying instantly

# Downloads

You can run the latest release right now to see for yourself. These releases were generated using this template repository.

## Windows

Installers are the easiest way to get started and come with an optional automatic update functionality:

- [Windows .msi Installer (x86-64)](https://github.com/xpipe-io/kickstartfx/releases/latest/download/xpipe-installer-windows-x86_64.msi)
- [Windows .msi Installer (ARM 64)](https://github.com/xpipe-io/kickstartfx/releases/latest/download/xpipe-installer-windows-arm64.msi)

If you don't like installers, you can also use a portable version that is packaged as an archive:

- [Windows .zip Portable (x86-64)](https://github.com/xpipe-io/kickstartfx/releases/latest/download/xpipe-portable-windows-x86_64.zip)
- [Windows .zip Portable (ARM 64)](https://github.com/xpipe-io/kickstartfx/releases/latest/download/xpipe-portable-windows-arm64.zip)

## macOS

Installers are the easiest way to get started and come with an optional automatic update functionality:

- [MacOS .pkg Installer (x86-64)](https://github.com/xpipe-io/kickstartfx/releases/latest/download/xpipe-installer-macos-x86_64.pkg)
- [MacOS .pkg Installer (ARM 64)](https://github.com/xpipe-io/kickstartfx/releases/latest/download/xpipe-installer-macos-arm64.pkg)

If you don't like installers, you can also use a portable version that is packaged as an archive:

- [MacOS .dmg Portable (x86-64)](https://github.com/xpipe-io/kickstartfx/releases/latest/download/xpipe-portable-macos-x86_64.dmg)
- [MacOS .dmg Portable (ARM 64)](https://github.com/xpipe-io/kickstartfx/releases/latest/download/xpipe-portable-macos-arm64.dmg)

## Linux

### Debian-based distros

The following debian installers are available:

- [Linux .deb Installer (x86-64)](https://github.com/xpipe-io/kickstartfx/releases/latest/download/xpipe-installer-linux-x86_64.deb)
- [Linux .deb Installer (ARM 64)](https://github.com/xpipe-io/kickstartfx/releases/latest/download/xpipe-installer-linux-arm64.deb)

Note that you should use apt to install the package with `sudo apt install <file>` as other package managers, for example dpkg,
are not able to resolve and install any dependency packages.

### RHEL-based distros

The following rpm installers are available:

- [Linux .rpm Installer (x86-64)](https://github.com/xpipe-io/kickstartfx/releases/latest/download/xpipe-installer-linux-x86_64.rpm)
- [Linux .rpm Installer (ARM 64)](https://github.com/xpipe-io/kickstartfx/releases/latest/download/xpipe-installer-linux-arm64.rpm)

The same applies here, you should use a package manager that supports resolving and installing required dependencies if needed.

### Portable

In case you prefer to use an archive version that you can extract anywhere, you can use these:

- [Linux .tar.gz Portable (x86-64)](https://github.com/xpipe-io/kickstartfx/releases/latest/download/xpipe-portable-linux-x86_64.tar.gz)
- [Linux .tar.gz Portable (ARM 64)](https://github.com/xpipe-io/kickstartfx/releases/latest/download/xpipe-portable-linux-arm64.tar.gz)

Note that the portable version assumes that you have some basic packages for graphical systems already installed
as it is not a perfect standalone version. It should however run on most systems.
