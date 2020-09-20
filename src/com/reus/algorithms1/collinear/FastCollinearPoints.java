package com.reus.algorithms1.collinear;

import java.util.Arrays;

public class FastCollinearPoints {
    private static final int MIN_REPEATED_SLOTS = 3;
    private final Point[] startSegmentPoints;
    private final Point[] endSegmentPoints;
    private int counter = 0;
    private LineSegment[] lineSegments;

    public FastCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException();
        }
        Point[] clonedPoints = Arrays.copyOf(points, points.length);
        checkPoints(clonedPoints);
        lineSegments = new LineSegment[clonedPoints.length * clonedPoints.length];
        startSegmentPoints = new Point[lineSegments.length];
        endSegmentPoints = new Point[lineSegments.length];
        Arrays.sort(clonedPoints, Point::compareTo);
        if (clonedPoints.length < MIN_REPEATED_SLOTS) {
            return;
        }
        for (int i = 0; i < clonedPoints.length; i++) {
            Point current = clonedPoints[i];
            Point[] sortedBySlopeToCurrent = new Point[clonedPoints.length - 1];
            System.arraycopy(clonedPoints, 0, sortedBySlopeToCurrent, 0, i);
            System.arraycopy(clonedPoints, i + 1, sortedBySlopeToCurrent, i, clonedPoints.length - 1 - i);
            Arrays.sort(sortedBySlopeToCurrent, current.slopeOrder());
            int repeatedSlopes = 1;
            double curSlope = sortedBySlopeToCurrent[0].slopeTo(current);
            for (int j = 1; j < sortedBySlopeToCurrent.length; j++) {
                if (sortedBySlopeToCurrent[j].slopeTo(current) == curSlope) {
                    repeatedSlopes++;
                    if (j == sortedBySlopeToCurrent.length - 1 && repeatedSlopes >= MIN_REPEATED_SLOTS) {
                        Point[] foundSegment = new Point[repeatedSlopes + 1];
                        foundSegment[0] = current;
                        System.arraycopy(sortedBySlopeToCurrent, sortedBySlopeToCurrent.length - repeatedSlopes, foundSegment, 1, repeatedSlopes);
                        addLineSegment(foundSegment);
                    }
                } else {
                    if (repeatedSlopes >= MIN_REPEATED_SLOTS) {
                        Point[] foundSegment = new Point[repeatedSlopes + 1];
                        foundSegment[0] = current;
                        System.arraycopy(sortedBySlopeToCurrent, j - repeatedSlopes, foundSegment, 1, repeatedSlopes);
                        addLineSegment(foundSegment);
                    }
                    curSlope = sortedBySlopeToCurrent[j].slopeTo(current);
                    repeatedSlopes = 1;
                }
            }
        }
    }

    private void checkPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException();
        }
        if (points.length == 0) {
            return;
        }
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) {
                throw new IllegalArgumentException();
            }
        }
        Arrays.sort(points);
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null
                    || (i != points.length - 1 && points[i + 1] == null)
                    || (i != points.length - 1 && points[i].compareTo(points[i + 1]) == 0)) {
                throw new IllegalArgumentException();
            }
        }
    }

    private void addLineSegment(Point[] foundSegment) {
        Arrays.sort(foundSegment, Point::compareTo);
        LineSegment newLineSegment = new LineSegment(foundSegment[0], foundSegment[foundSegment.length - 1]);
        for (int i = 0; i < lineSegments.length; i++) {
            Point existingStart = startSegmentPoints[i];
            Point existingEnd = endSegmentPoints[i];
            if (lineSegments[i] == null && existingStart == null && existingEnd == null) {
                lineSegments[i] = newLineSegment;
                startSegmentPoints[i] = foundSegment[0];
                endSegmentPoints[i] = foundSegment[foundSegment.length - 1];
                counter++;
                return;
            } else if (startSegmentPoints[i] != null && endSegmentPoints[i] != null
                    && foundSegment[0].compareTo(startSegmentPoints[i]) == 0
                    && endSegmentPoints[i].compareTo(foundSegment[foundSegment.length - 1]) == 0) {
                return;
            }
        }
    }

    public int numberOfSegments() {
        return counter;
    }

    public LineSegment[] segments() {
        return Arrays.copyOfRange(lineSegments, 0, counter);
    }

}
