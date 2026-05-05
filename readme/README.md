# My Coloring Book Application by Color-Coding Coders
A JavaFX-based color-by-number application that allows the user to sign the book with their name.

## Functionality
On the cover page in the application, the user is prompted to sign the book with their name. Upon clicking `Open the Book!`, the user is presented with brief instructions for using the application. Once the user clicks `Start Drawing!`, a selection screen allows them to choose from one of four coloring pages. The selected coloring page determines the number layout and color palette presented to the user, and the user can click a color from the palette and begin coloring the image by clicking the corresponding number on the page.

## Special Files (Included in Respository)
**Drivers**: `sqlite-jdbc-3.53.0.0.jar`  
**Assets**: `animals.png`, `flowers.png`, `houses.png`, `nature.png`   
**Databases**: `coloring_book.db`, `coloringbook.db`  

## How to Clone & Run
**Requirements**: Java JDK 17+, JavaFX libraries and JGrasp.  
**1.** Clone the repository: ```https://github.com/MaryJonez/Color-Coding-Coders.git```  
**2.** Open JGrasp and select the project folder in the file explorer.   
**3.** Set up JavaFX: Navigate to `Settings` -> `Compiler Settings` -> `Workspace`. In the JavaFX tab, add the path for your JavaFX library folder.   
**4.** Set up SQLite driver: Navigate to `Settings` -> `CLASSPATH` -> `Workspace`, add the path for the `sqlite-jdbc-3.53.0.0.jar` file found in the repository.   
**5.** Open `ColorByNumberApp.java`, click compile and then click run.
