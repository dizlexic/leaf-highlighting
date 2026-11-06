# Leaf Syntax Highlighting

An IntelliJ Platform plugin that provides syntax highlighting for [Leaf](https://github.com/vapor/leaf-kit) templates.

The plugin recognizes `.leaf` files and highlights Leaf tag names, `#` markers, expression variables, strings, numbers, operators, and punctuation while retaining the IDE's HTML syntax highlighting for template markup. It supports interpolations such as `#(user.name)` and tags with nested expressions and optional body separators, such as `#if(count(users) > 0): ... #endif`. It also offers completion for common built-in tags, folds multiline block tags, and reports unmatched or unclosed tags. Leaf syntax colors can be customized in the IDE's color settings.

## Build and run

Use Gradle with a JDK supported by IntelliJ IDEA 2026.2. The plugin compiles against the IntelliJ Platform Community distribution and declares its required platform and XML modules, rather than targeting a specific JetBrains IDE:

```shell
gradle test
gradle runIde
```

To check compatibility against the recommended IntelliJ-based IDE releases with Plugin Verifier:

```shell
gradle verifyPlugin
```

To build a distributable plugin ZIP:

```shell
gradle buildPlugin
```

## Example

```leaf
#if(title == "Welcome"):
    Hello, #(title)!
#else:
    Welcome!
#endif
```

Tag names are not limited to Leaf's built-in tags, so custom tags can be highlighted with the same syntax:

```leaf
The time is #now()
#hello("John")
#panel(title): #(title) #endpanel
```

## License

This project is licensed under the [Apache License 2.0](LICENSE). Apache-2.0 permits commercial use, redistribution, and resale, subject to its terms; it does not prevent others from selling copies.

## Support

If you find this plugin useful, [buy me a coffee](https://www.buymeacoffee.com/dizlexic).