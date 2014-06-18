package uspceu.logservice;

public class Point {

    private long timestamp;
    private double pointX;
    private double pointY;
    private double pointZ;

    public Point (long timestamp, double pointX, double pointY, double pointZ) {
        this.timestamp = timestamp;

        this.pointX = pointX;
        this.pointY = pointY;
        this.pointZ = pointZ;

    }

    public double getPointX() {
        return this.pointX;
    }
    public double getPointY() {
        return this.pointY;
    }
    public double getPointZ() {
        return this.pointZ;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

}
