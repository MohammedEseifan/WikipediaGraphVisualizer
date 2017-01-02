# Purpose
Initally started as a way to beat the '[Wikipedia Game](http://thewikigame.com/ "In-case you're unfamiliar")' by creating a Python script that would crawl through Wikipedia and make a local copy of all the connections between pages. Then the user could quickly search for the shorest path between any two pages. I soon discovered that this file would be several GBs in size so I narrowed it down to all webpages within 2 degrees of seperation.

# Evolution
Once I had the data in a text file I wanted to do something with it. Searching it was cool but it was starting to get boring. I wanted to come up with a way to see it visually. Enter Java. It had all the libraries I needed and I found it easier to work with objects and classes in Java. I created a program that would take a list of nodes and display them in an animated graph. The animations could still use some refinement but the core functionailty is there. The user can even zoom and drag nodes around. Pretty cool stuff, only problem is the massive amount of data return from the Python script is really hard to display in an elegant way so I was forced to reduce the data to less than 100 nodes.

