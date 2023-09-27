# ImageTagManager

<!---
[![start with why](https://img.shields.io/badge/start%20with-why%3F-brightgreen.svg?style=flat)](http://www.ted.com/talks/simon_sinek_how_great_leaders_inspire_action)
--->
[![GitHub release](https://img.shields.io/github/release/elbosso/ImageTagManager/all.svg?maxAge=1)](https://GitHub.com/elbosso/ImageTagManager/releases/)
[![GitHub tag](https://img.shields.io/github/tag/elbosso/ImageTagManager.svg)](https://GitHub.com/elbosso/ImageTagManager/tags/)
[![GitHub license](https://img.shields.io/github/license/elbosso/ImageTagManager.svg)](https://github.com/elbosso/ImageTagManager/blob/master/LICENSE)
[![GitHub issues](https://img.shields.io/github/issues/elbosso/ImageTagManager.svg)](https://GitHub.com/elbosso/ImageTagManager/issues/)
[![GitHub issues-closed](https://img.shields.io/github/issues-closed/elbosso/ImageTagManager.svg)](https://GitHub.com/elbosso/ImageTagManager/issues?q=is%3Aissue+is%3Aclosed)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/elbosso/ImageTagManager/issues)
[![GitHub contributors](https://img.shields.io/github/contributors/elbosso/ImageTagManager.svg)](https://GitHub.com/elbosso/ImageTagManager/graphs/contributors/)
[![Github All Releases](https://img.shields.io/github/downloads/elbosso/ImageTagManager/total.svg)](https://github.com/elbosso/ImageTagManager)
[![Website elbosso.github.io](https://img.shields.io/website-up-down-green-red/https/elbosso.github.io.svg)](https://elbosso.github.io/)

## Overview

This project offers a visual tag manager for images - one can use it to add arbitrary tags to images. The tags are saved inside one hidden file for each directory. 
Tags can then be searched using ordinary command line tools as for example `grep` or any other tools able to find text (regular expressions) inside text files.
The application can be built by issuing

```
mvn compile package
```

and then starting the resulting monolithic jar file by issuing

```
$JAVA_HOME/bin/java -jar target/tagmanager-<version>-jar-with-dependencies.jar
```

## Usage

The application has been developed mainly with one main goal - to be as non-intrusive as possible. Therefore,
all important functionality - all functionality that has to do with the adding of tags - is usable using
only the keyboard.

### Concepts

Tags are always three-part-strings, the individual parts being separated with dots. One example for such a 
tag would be stationwagon.color.blue. Tags always follow a simple pattern: class.property.value. The first part 
describes a class - in our example a station wagon. The second part names a property that class might have -
in our example its color. and the third part names a possible value for that property - in our example it
is the color blue.

### The layout and components of the main window

The window is divided into 4 parts: to the left we have the GUI for adding/removing tags. This is split up into two 
components where onyly one of them is possible at any one time. 

One of those components is the *TagPanel*:
it groups together buttons for tags known to the application using the classes of the tags as keys
for the grouping. Each button has a different color - the reason for that becomes apparent when
we explain the component in the lower part of the main window. Furthermore, each button has a checkbox.
This checkbox is checked if the tag represented by the button is set on the image currently  worked on.

Tags can be set/unset by simply clicking on the corresponding button.

The second component on the left is the List of favourites: Each time a tag is added by whatever means
to an image, this action is countes for that tag. In the favourites list, the tags are sorted according to that
number - the ones used more often can be found on the top of the list. The first 12 of them can be added
to the image currently worked on by simply pressing the corresponding function key.
This component offers a toolbar to sort the favourites by several criteria:

* ![image](images/sort_frequency_down2_32.png)  
  This action sorts by usage frequency - the most used will be at the bottom of the list.
* ![image](images/sort_frequency_up_32.png)  
  This action sorts by usage frequency - the most used will be at the top of the list.
* ![image](images/sort_time_up_32.png)  
  This action sorts by time of usage - the most recently used will be at the top of the list.
* ![image](images/sort_time_down2_32.png)  
  This action sorts by time of usage - the most recently used will be at the bottom of the list.
* ![image](images/sort_alpha_up_32.png)  
  This action sorts by name.
* ![image](images/sort_alpha_down2_32.png)  
  This action sorts by name (descending).


The third component on the left contains a command palette with some actions:

* ![image](images/baseline_delete_black_36dp.png)  
  This action clears all tags from the application. This does not mean that the trags from the 
  currently selected image are cleared but the component for choosing tags to add is cleared and
  basically the user can start building a new ontology!
* ![image](images/baseline_add_box_black_36dp.png)  
  This action opens a file selection dialog. The user can select a file containing tags as described
  further down - holding an ontology. The tags contained within are  expanded and added to the
  component for choosing tags
* ![image](images/baseline_cloud_upload_black_36dp.png)  
  This action saves all tags currently in the component for choosing tags as ontology to a file.
  It opens a file choosing dialog where the user can choose the file to write the ontology to.
* ![image](images/baseline_select_all_black_36dp.png)  
  This action selects the first image in the current directory having no tags. Automatically
  set tags are ignored in the search: If an image has only tags set automatically - for
  example tags derived from the metadata of the image - then this image is selected

The next component is located on the top of the main window. It is a panel that only becomes visible when 
at least one tag is selected for the image currently worked on. It displays all tags currently set for that image.
It does this by holding a button for each of the tags. Tags can be removed by clicking on the corresponding
button in this panel.

The central area of the main window is reserved for the image selection/viewing component. It consists of two components:
To the left, there is a list containing miniature representations of the folder currently worked in.
This list can be scrolled using the mouse wheel. Some of the icon representations here can have a small
icon representing a folder on them. This designates a sub folder - if the user double-clicks on such an
Icon, the application changes to that folder and displays all Images inside it. If the user double-clicks on 
an ordinary icon, the corresponding image is loaded into the viewer component and its associated tags 
are read and the gui initialized accordingly (tag buttons checked,...). The list has icons above
it that - when clicked on - execute various actions:

* ![image](images/baseline_navigate_before_black_36dp.png)  
  This action selects the image before the currently seelcted one
* ![image](images/baseline_navigate_next_black_36dp.png)  
  This action selects the image after the currently seelcted one
* ![image](images/baseline_arrow_upward_black_36dp.png)  
  This action changes the current directory to its parent directory.
* ![image](images/document_tagged_32.png)  
  This action copies all tags added to the currently selected image to all images in the current directory.

The viewer has a toolbar above it with some actions for example to zoom the image freely or to lock the zoom factor 
in a way that the whole image is visible or the whole height/width of the image is visible.

* ![image](images/baseline_add_a_photo_black_36dp.png)  
  This action saves a copy of the currently selected image. It opens a file save dialog to let the user select the file the image is written to
* ![image](images/baseline_open_with_black_36dp.png)  
  This action scales the image to fit in its entirety into the viewport.
* ![image](images/constrain_width_32.png)  
  This action scales the image to fit vorizontally into the viewport.
* ![image](images/constrain_height_32.png)  
  This action scales the image to fit vertically into the viewport.
* ![image](images/zoom_32.png)  
  This action scales the image according to the manually choosen zoom factor. This can be set with 
  the three following actions.
* ![image](images/baseline_zoom_in_black_36dp.png)  
  This action increases the zoom factor.
* ![image](images/baseline_crop_original_black_36dp.png)  
  This action resets the zoom factor - the image is displayed in its original size.
* ![image](images/baseline_zoom_out_black_36dp.png)  
  This action decreases the zoom factor.
* ![image](src/main/resources/gfx/compass_36.png)  
  This action opens a small window showing a copy of the currently selected window and a tiny rectangle
  representing the currently visible part inside the viewer. This rectangle can be moved
  using the mouse to pan the viewport and make navigation in large zoom factor configurations easier.

The last component is at the bottom of the main window. It is a text entry field where the user can enter
arbitrary text. If she finishes the text entry by pressing return, the text in the text field at that point
is added as tag to the image currently worked on. If this is a new tag, a corresponding button is added
to the TagPanel at the left of the main window. This text field has some comfort functions: It offers 
auto completion for already present tags as the user types along. Possible completions are displayed in a list
the user can navigate using the keys CURSOR UP/DOWN and PgUP and PgDn. Pressing ENTER uses the selected
entry and the user can continue typing.

Furthermore, the user can type ALT BACK_SPACE to delete all text until the last dot. Two more key strokes
are defined to select the next/previous image to work on - those are ALT ENTER and ALT SHIFT ENTER.
At last, one more keystroke is defined to go up one folder - that key stroke is ALT CURSOR_UP. 

This text field always has the focus. It is not necessary to click it to be able to type in it.  

### The ontology

As described above - tags are descriptions of the content of images. Tags are always consisting of three parts:
The first part describes some class or concept - for example `tractor`. The second part then describes
a property of this concept - for example `engine`. And the third part describes the flavour
of this property - for example `diesel`.

The user can import such ontologies. To do so, the application understands a special notation 
to make the files more compact: if some classes have some property in common, the user does not
have to specify this property over and over on numerous lines but can make use of an 
abbreviation. Instead of writing 

```ini
stationwagon.color.blue
truck.color.blue
```

she can just put

```ini
stationwagon|truck.color.blue
```

