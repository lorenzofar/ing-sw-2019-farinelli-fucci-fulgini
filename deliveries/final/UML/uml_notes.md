## Notes on UML class diagrams

In order to improve readability, we provide UML diagrams at three levels,
plus some additional diagrams:

+ **COMPLETE DIAGRAM** \
Contains all the classes and interfaces in the project, only *public members are
shown*. \
Due to the complexity of the scheme, we could not show associations
between classes, but we made some *clusters* of classes that work together.
These clusters have been framed with distinctive colors and have names
(model, controller, client, network, etc.).\
This diagram's purpose is to show the general layout of the packages, but
in order to see internal dependencies, the next diagrams best fit the
purpose. \
Due to the fact that we could not move the classes around with the IntelliJ
editor, the re-layout has been carried out in Photoshop. \
If this image looks grainy and pixelated, just give your image viewer some
time to load it properly.

+ **MACRO-PACKAGE OVERVIEW** \
This is provided for the packages *model, client* and *controller*.\
Contains all the classes in the package and its sub-packages, connected
with *generalization* and *association* dependencies.
This diagram is a lot better than the previous one, because it really highlights
the relations between classes. We also included interfaces which belong to other
packages, but are implemented by classes in the package.

+ **PACKAGE-SPECIFIC DIAGRAM** \
For each package and sub-package we provide a diagram with all the classes
and the packages inside it. Shown dependencies are *generalization*
and *association*. All members, including private ones, are shown.

+ **ADDITIONAL DIAGRAMS** \
We provide some additional diagrams which also show the «create» and «use»
dependencies:
 - `controller/match_action_powerups` shows the classes of the controller
 which actually handle the flow of the game, excluding weapon controllers
 - `controller/persistentiview_requests` shows the PersistentView and the
 classes it uses, which help in understanding how the controller classes
 interact with the remote client through this interface
