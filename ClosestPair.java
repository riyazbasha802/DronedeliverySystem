import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

class Point {
    int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static double distance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }
}

class ClosestPair {

    public static double bruteForce(Point[] points) {
        double minDist = Double.MAX_VALUE;
        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                double dist = Point.distance(points[i], points[j]);
                if (dist < minDist) {
                    minDist = dist;
                }
            }
        }
        return minDist;
    }

    public static double divideAndConquer(Point[] points) {
        Point[] pointsSortedByX = points.clone();
        Arrays.sort(pointsSortedByX, Comparator.comparingInt(p -> p.x));

        Point[] pointsSortedByY = points.clone();
        Arrays.sort(pointsSortedByY, Comparator.comparingInt(p -> p.y));

        return closestPair(pointsSortedByX, pointsSortedByY);
    }

    private static double closestPair(Point[] pointsSortedByX, Point[] pointsSortedByY) {
        int n = pointsSortedByX.length;
        if (n <= 3) {
            return bruteForce(pointsSortedByX);
        }

        int mid = n / 2;
        Point midPoint = pointsSortedByX[mid];

        Point[] leftByX = Arrays.copyOfRange(pointsSortedByX, 0, mid);
        Point[] rightByX = Arrays.copyOfRange(pointsSortedByX, mid, n);

        List<Point> leftByY = new ArrayList<>();
        List<Point> rightByY = new ArrayList<>();

        for (Point p : pointsSortedByY) {
            if (p.x <= midPoint.x) {
                leftByY.add(p);
            } else {
                rightByY.add(p);
            }
        }

        double leftMinDist = closestPair(leftByX, leftByY.toArray(new Point[0]));
        double rightMinDist = closestPair(rightByX, rightByY.toArray(new Point[0]));

        double minDist = Math.min(leftMinDist, rightMinDist);

        List<Point> strip = new ArrayList<>();
        for (Point p : pointsSortedByY) {
            if (Math.abs(p.x - midPoint.x) < minDist) {
                strip.add(p);
            }
        }

        return Math.min(minDist, stripClosest(strip.toArray(new Point[0]), minDist));
    }

    private static double stripClosest(Point[] strip, double minDist) {
        double min = minDist;
        for (int i = 0; i < strip.length; i++) {
            for (int j = i + 1; j < strip.length && (strip[j].y - strip[i].y) < min; j++) {
                double dist = Point.distance(strip[i], strip[j]);
                if (dist < min) {
                    min = dist;
                }
            }
        }
        return min;
    }

    public static void main(String[] args) {
        Point[] points = generateRandomPoints(1000);

        long startTime = System.nanoTime();
        double minDistBruteForce = bruteForce(points);
        long endTime = System.nanoTime();
        long durationBruteForce = endTime - startTime;

        System.out.println("Brute Force Closest Pair Distance: " + minDistBruteForce);
        System.out.println("Brute Force Duration: " + durationBruteForce + " nanoseconds");

        startTime = System.nanoTime();
        double minDistDivideAndConquer = divideAndConquer(points);
        endTime = System.nanoTime();
        long durationDivideAndConquer = endTime - startTime;

        System.out.println("Divide and Conquer Closest Pair Distance: " + minDistDivideAndConquer);
        System.out.println("Divide and Conquer Duration: " + durationDivideAndConquer + " nanoseconds");
    }

    public static Point[] generateRandomPoints(int n) {
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = ThreadLocalRandom.current().nextInt(0, 1001);
            int y = ThreadLocalRandom.current().nextInt(0, 1001);
            points[i] = new Point(x, y);
        }
        return points;
    }
}

