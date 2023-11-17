/*
2
Dallas|Chicago|T
Houston|Dallas|C*/

public class FlightPlan {
    private String origin;
    private String destination;
    private String sortType;

    public FlightPlan(String origin, String destination, String sortType) {
        setOrigin(origin);
        setDestination(destination);
        setSortType(sortType);
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

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        if (!isValidSortType(sortType)) {
            throw new IllegalArgumentException("Invalid sortT");
        }
        this.sortType = sortType;
    }

    private boolean isValidSortType(String sortType) {
        return "C".equals(sortType) || "T".equals(sortType);
    }
}
