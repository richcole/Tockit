#!/bin/sh

javac -classpath .:libs/jdom.jar:libs/xerces.jar org/kvocentral/toscanaj/gui/MainPanel.java && java org/kvocentral/toscanaj/gui/MainPanel

