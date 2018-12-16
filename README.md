##### *NB: This repository has been cloned from a GitLab server that was used for assignment delivery*

# Chess Connoisseurs

![example1](/docs/slides/example1.PNG)

![example2](/docs/slides/example2.PNG)

###### Images are from the [jar-executable](/executable/chess-application-3.0.jar)

Team members:
------
+ Keerthan Kumaranayagam
+ Malin Jakobsen
+ Olav Gjerde
+ Ole Kristian Solheim Gjerløw
+ Renate Nikolajeva
+ Rune Vatne
+ Simen Gad Hasvi
+ Henrik Borgli

    + Coach: Jonathan Prieto-Cubides
 
How to run:
----
+ Run the already compiled version in the 'executable' folder

```
$ cd executable
$ java -jar .\chess-application-3.0.jar
```


+ Or run "mvn package" in the 'application' folder
   - Then launch the compiled ...-shaded.jar file in the 'target' folder

```
$ cd application
$ mvn package
```

Regarding the post_develop branch:
---
+ The code in this branch is a slightly refactored version of the original (master) version.   
+ The documentation for the GUI is a bit slim in some parts.  
+ The "bubble-effect" behind the chessboard is not present in this version.
+ There is no sound in this version either.
+ Internet connectivity is disabled in the sourcecode
   - But the original release (with sound, effects and connectivity) remains in the 'executable' folder.  
   
###### Refactoring  

    The GUI code is still by no means pretty, but some of the main parts  
    have been separated into their own classes. I did this because I had  
    some spare time on my hands, and wanted the structure of the application  
    to at least hint at what we would have wanted for the game.  
    Following a MVC pattern or some established method would have been optimal.

Extra license information:
---
This project is inspired by [Amir Afghani's](https://github.com/amir650) [Black Widow-chess](https://github.com/amir650/BlackWidow-Chess), and therefore alters code available in his application.   
Works based on Black Widow-chess must use the same license, hence our license is the [GNU Lesser General Public License 2.1](https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html).  
##### Main changes to, and use of open source inspriation code

+ Our code uses JavaFX as its GUI framework instead of Swing
+ Our code uses coordinate(x,y) representation instead of tile numbering (0-64)
+ All tests were written by us
+ No code has been directly copied but written line by line to judge if it's  
  feature/function "x" was necessary, and after this expanded upon where needed.  

##### Artwork
+ Chess piece graphics were made by JohnPablok (2018)   
  Available on: [https://opengameart.org/content/chess-pieces-and-board-squares](https://opengameart.org/content/chess-pieces-and-board-squares)  
  License: [https://creativecommons.org/licenses/by-sa/3.0/](https://creativecommons.org/licenses/by-sa/3.0/)
  
+ GUI artwork from Free toolbar icons by Aha-soft
  Available on: [https://www.iconfinder.com/iconsets/16x16-free-toolbar-icons](https://www.iconfinder.com/iconsets/16x16-free-toolbar-icons)
  License: Free for commercial use (Include link to authors website)  
  For more information see: [https://docs.google.com/spreadsheets/u/1/d/1E8X2_xmJkkoeZwa1HPNG6jT3ytAZlcAgzTDRX0jDF-Q/pubhtml?gid=0&single=true](https://docs.google.com/spreadsheets/u/1/d/1E8X2_xmJkkoeZwa1HPNG6jT3ytAZlcAgzTDRX0jDF-Q/pubhtml?gid=0&single=true)
  
##### Music and Sounds
+ DropPieceNew.wav  
  Title: chess pieces, Author: simone_ds  
  Available at:[https://freesound.org/people/simone_ds/sounds/366065/](https://freesound.org/people/simone_ds/sounds/366065/)  
  Licensed under Creative Commons "Attribution 1.0" [https://creativecommons.org/publicdomain/zero/1.0/](https://creativecommons.org/publicdomain/zero/1.0/)

+ ButtonClick.wav  
  Title: Click1, Author: annabloom  
  Available at:[https://freesound.org/people/annabloom/sounds/219069/](https://freesound.org/people/annabloom/sounds/219069/)  
  Licensed under Creative Commons "Attribution 3.0" [https://creativecommons.org/licenses/by-nc/3.0/](https://creativecommons.org/licenses/by-nc/3.0/)

+ MenuMusic.wav  
  Title: Mario's way, Author: Gianni Caratelli  
  Available at:[https://freesound.org/people/xsgianni/sounds/388079/](https://freesound.org/people/xsgianni/sounds/388079/)  
  Licensed under Creative Commons "Attribution 3.0" [https://creativecommons.org/licenses/by-nc/3.0/](https://creativecommons.org/licenses/by-nc/3.0/)

+ GameOverSound.wav  
  Title: Tropical Musical sound 3, Author: 2011 Varazuvi™ (www.varazuvi.com)  
  Available at:[https://freesound.org/people/Soughtaftersounds/sounds/145464/](https://freesound.org/people/Soughtaftersounds/sounds/145464/)  
  Licensed under Creative Commons "Attribution 3.0" [https://creativecommons.org/licenses/by/3.0/](https://creativecommons.org/licenses/by/3.0/)

+ GameMusic.wav
  Title: Nodens (Field Song), Author: axtoncrolley  
  Available at:[https://freesound.org/people/axtoncrolley/sounds/172707/](https://freesound.org/people/axtoncrolley/sounds/172707/)  
  Licensed under Creative Commons "Attribution 1.0" [https://creativecommons.org/publicdomain/zero/1.0/](https://creativecommons.org/publicdomain/zero/1.0/)
