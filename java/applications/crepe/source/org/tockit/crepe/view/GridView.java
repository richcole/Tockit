package org.tockit.crepe.view;

import java.awt.geom.Point2D;
import java.util.Vector;

public class GridView {
    private double height;
    private double width;
    private Vector gridPoints;
    private int[] gridPointsStatus;
    int partitionX;
    int partitionY;
    int gridSize;
    double defaultWidth;
    double defaultHeight;
    int midGridPosition;

    public GridView(double width, double height) {
        setWidth(width);
        setHeight(height);
        defaultWidth = 130;
        defaultHeight = 40;
        gridPoints = new Vector();
        gridPointsStatus = new int[150];
        partitionX = (int) Math.floor(width / defaultWidth);
        partitionY = (int) Math.floor(height / defaultHeight);
        gridSize = partitionX * partitionY;
        midGridPosition = (int) Math.floor(gridSize / 2) - 1;
        assignGridPoints();
    }

    private void setHeight(double height) {
        this.height = height;
    }

    private void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public int getMidGridPosition() {
        return midGridPosition;
    }

    public int getPartitionX() {
        return partitionX;
    }

    public int getPartitionY() {
        return partitionY;
    }

    public int getGridPointStatus(int position) {
        return gridPointsStatus[position];
    }

    public void updateGridPointStatus(int position, int value) {
        gridPointsStatus[position] = value;
    }

    public void assignGridPoints() {

        double posX = - (defaultWidth / 2);
        double posY = - (defaultHeight / 2);
        int i = 0;

        for (int j = 1; j <= partitionY; j++) {
            posY += defaultHeight;
            for (int k = 1; k <= partitionX; k++) {
                posX += defaultWidth;
                gridPoints.add(new Point2D.Double(posX, posY));
                gridPointsStatus[i] = 1;
                i++;
            }
            posX = - (defaultWidth / 2);
        }
    }

    public void resetGrid() {
        for (int j = 0; j < gridSize; j++) {
            gridPointsStatus[j] = 1;
        }
    }

    public Point2D.Double getNextAvailableGridPoint(int typeOfObject) {

        if (typeOfObject == 1) {
            if (getGridPointStatus(midGridPosition) != 0) {
                updateGridPointStatus(midGridPosition, 0);
                return (Point2D.Double) gridPoints.get(midGridPosition);
            }

            for (int p = 4; midGridPosition - partitionX * p >= 0; p += 4) {
                if (getGridPointStatus(midGridPosition - partitionX * p)
                    == 1) {
                    return (Point2D.Double) gridPoints.get(
                        midGridPosition - partitionX * p);
                }
            }

            for (int p = 4;
                midGridPosition + partitionX * p <= gridSize;
                p += 4) {
                if (getGridPointStatus(midGridPosition + partitionX * p)
                    == 1) {
                    return (Point2D.Double) gridPoints.get(
                        midGridPosition + partitionX * p);
                }
            }
            return null;
        }

        if (typeOfObject == 2) {
            int XGridViewPosition =
                (int) Math.IEEEremainder(midGridPosition, partitionX) + 1;

            int topGridViewPosition;
            int bottomGridViewPosition;
            int rightGridViewPosition;
            int leftGridViewPosition;

            //Check top position
            if (midGridPosition - partitionX * 2 >= 0) {
                topGridViewPosition = midGridPosition - partitionX * 2;
            } else {
                topGridViewPosition = -1; //not valid
            }

            //Check right position
            if (XGridViewPosition <= 4) {
                rightGridViewPosition = midGridPosition + 2;
            } else
                rightGridViewPosition = -1; //not valid

            //Check bottom position
            if (midGridPosition + partitionX * 2 <= gridSize) {
                bottomGridViewPosition = midGridPosition + partitionX * 2;
            } else {
                bottomGridViewPosition = -1; //not valid
            }

            //Check left position
            if (XGridViewPosition >= 3) {
                leftGridViewPosition = midGridPosition - 2;
            } else
                leftGridViewPosition = -1; //not valid

            if (topGridViewPosition != -1
                && getGridPointStatus(topGridViewPosition) == 1) {
                updateGridPointStatus(topGridViewPosition, 0);
                return (Point2D.Double) gridPoints.get(topGridViewPosition);
            }

            if (rightGridViewPosition != -1
                && getGridPointStatus(rightGridViewPosition) == 1) {
                updateGridPointStatus(rightGridViewPosition, 0);
                return (Point2D.Double) gridPoints.get(rightGridViewPosition);
            }

            if (bottomGridViewPosition != -1
                && getGridPointStatus(bottomGridViewPosition) == 1) {
                updateGridPointStatus(bottomGridViewPosition, 0);
                return (Point2D.Double) gridPoints.get(bottomGridViewPosition);
            }

            if (leftGridViewPosition != -1
                && getGridPointStatus(leftGridViewPosition) == 1) {
                updateGridPointStatus(leftGridViewPosition, 0);
                return (Point2D.Double) gridPoints.get(leftGridViewPosition);
            }

        }
        return null;
    }
}
