Lab 6

\[valid 2024-2025]

Graphical User Interfaces (Swing, JavaFX)

Consider the following game played on a drawing board by two players.

At the beginning of the game, a number of dots (small circles) are randomly generated at various positions.

Each player, on their turn, chooses a pair of dots and connects them with a line (a player with blue, the other with red).

The goal of a player is to add enough lines such that all the dots and the lines form a connected structure (any two dots are connected by a path formed by lines and other dots) and to minimize the score, i.e. the sum of the lengths of the lines. The length of a line is the euclidean distance between its dots.

In order to create a graphical user interface for the game, you may use either Swing or JavaFX.



The main specifications of the application are:



Compulsory (1p)



Create the following components:



The main frame of the application.

A configuration panel for introducing parameters, such as the number of dots, and a button for creating a new game. The panel must be placed at the top part of the frame. The panel must contain at least one label and one input component.

A canvas (drawing panel) for drawing the board. Draw the dots as small circles. This panel must be placed in the center part of the frame.

A control panel for managing the game. This panel will contain the buttons: Load, Save, Exit, etc. and it will be placed at the bottom part of the frame.

Homework (2p)



Implement the retained mode for drawing the game board.

Implement the logic of the game. You can add a line either by selecting two dots or by dragging a line from one dot to another.

Compare the score obtained by each player with the best score that could have been obtained.

Export the image of the game board into a PNG file.

Use object serialization in order to save and restore the current status of the game.

Bonus (2p)



Implement an algorithm that, considering the current status of the game, generates a number of connected structures (spanning trees) that could be created, descending by their score (cost), starting with the one with minimum cost.

Implement an AI for the game. The algorithm will choose, when it is its turn to move, one of the spanning trees generated above, depending on a difficulty level. If the difficulty level is the highest, it will select the first generated tree, if it is the lowest, the last generated tree.

When starting a new game, you should be able to choose for each player if it's human or AI, and select a difficulty level.

Resources



Slides

Creating a GUI With JFC/Swing

The Java Tutorials: 2D Graphics

Getting Started with JavaFX

JavaFX Scene Builder

Objectives



Get familiar with the basic elements of design involved in creating a GUI.

Understand the concepts of component, container, layout manager.

Get acquainted with various libraries for creating a GUI application, such as AWT, Swing, SWT, Java FX.

Write event listeners to handle events.

Understand how Swing components are painted.

Create custom components using Java2D that coexist with standard Swing components.

Understand the concept of graphic context.

Get familiar with Java2D basic geometric primitives, use colours, fonts, images

Get acquainted with JavaFX technology and understand the differences between Swing and JavaFX.

