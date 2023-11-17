/*2
Dallas|Houston|T
Chicago|Dallas|C */

public class FlightData {
    private String origin;
    private String destination;
    private double cost;
    private double time;

    public FlightData() {
        this("", "", 0.0, 0.0); // Initialize cost and time to 0
    }

    public FlightData(String origin, String destination, double cost, double time) {
        setOrigin(origin);
        setDestination(destination);
        setCost(cost);
        setTime(time);
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Invalid cost");
        }
        this.cost = cost;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        if (time <= 0) {
            throw new IllegalArgumentException("Invalid time");
        }
        this.time = time;
    }

    public FlightData copy() {
        return new FlightData(this.origin, this.destination, this.cost, this.time);
    }
}
