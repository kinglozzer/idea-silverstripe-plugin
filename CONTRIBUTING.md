# Setting up a development environment

## Import the project
* Fork this project, clone it to your machine
* Launch IDEA and `File -> New -> Project from Existing Sources...` the `idea-silverstripe-plugin` directory as a Gradle project
* At this point you should be ready to go! Open the Gradle panel, select `Tasks -> intellij -> buildPlugin` to build the plugin and open an IDE instance to test your changes.

## Tips
* You might need to manually mark the following directories (Right Click -> Mark Directory as):
  * `src/main` - Unmark if this has already been marked
  * `src/main/gen` - Mark as Generated Sources Root
  * `src/main/java` - Mark as Sources Root
  * `src/main/resources` - Mark as Resources Root
* **Regenerating the lexer:** the heart of the plugin is the grammar defined in `src/main/java/com/kinglozzer/silverstripe/parser/Silverstripe.flex`.  If you modify this grammar, you need to right click the file and select “Run JFlex Generator” to regenerate the class (`com.kinglozzer.silverstripe.parser._Silverstripe`) which does the actual lexing. Make sure you have the Grammar-Kit and PsiViewer modules installed to help you.
