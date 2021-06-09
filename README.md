# Dungeon Viewer
 This is a program i made for my DM quite some time ago. Struggling with fog of war over discord, i decided that it be a fun project to see if i can make something in Java to do this for them, and this is the result:
 
## Loading images
When the program is started for the first time, it will create a input folder. Here, the program will look for either PNG or JPG files, named from 0 to both positive and negative numbers in increments. These become the floors of the dungeon.

Note: numbers have to be in order. Skipping a number means the images after wont be loaded!
 
## Viewport

This is the heart of Dungeon Viewer, and will be entirely blank when the program just starts. It is meant that this is streamed over discord to your players, and that they don't see any of the other windows.

clicking your left mouse button allows you to draw upon the screen with whatever colour has been selected.
right clicking removes ALL drawings made with the last placed colour.
the middle mouse button allows the screen to be shifted around.
The scroll wheel allows the image to be zoomed in or out.

### Viewport Controls
There is a separate window for the viewport controls. Here you will find the zoom controls of the viewport, as well as the ability to change the colour of the marker and the floor selector for the Viewport.

## Shadow Port

This window controls the fog of war upon each floor, which is selectable by the selector. The Brush selector allows different shapes to be drawn to the image, and the entire for layer can be disabled with the checkbox.

Left clicking will remove for from the fog layer, and right clicking will add it back in.

Once ready, the Commit button will allow the changes to be saved in memory.
Clicking Rollback will revert the fog layer back to the last state it was in.

Note: if the Shadow Port commits changes to the same image as the Viewport, then the viewport is automatically updated!

## Counter Port

The last menu is the Counter Port, which can display icons for allies, enemies, and neutral units, and is able to keep track of their health. Each icon can have their health and name displayed on the Viewport, and can be clicked to be dragged around.

The Is Dead checkbox will display a greyscale version of the icon to show that that unit has died.
The Auto Dead checkbox will, when checked, automatically set the dead state if the unit goes below zero health.

Dungeon Viewer will also make a Counter folder, in which PNG and JPG images can be placed to give the players the option of more unique icons.