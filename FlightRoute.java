/*Flight 1: Dallas, Houston (Time)
Path 1: Dallas -> Houston. Time: 51 Cost: 101.00
Path 2: Dallas -> Austin -> Houston. Time: 86 Cost: 193.00

Flight 2: Chicago, Dallas (Cost)
Path 1: Chicago -> Austin -> Dallas. Time: 237 Cost: 242.00
Path 2: Chicago -> Austin -> Houston -> Dallas. Time: 282 Cost: 340.00 */

import java.util.ArrayList;
import java.util.List;

public class FlightRoute {
    private double totalCost;
    private double totalTime;
    private List<String> cities;
    private List<FlightRoute> alternativeRoutes;

    public FlightRoute() {
        totalCost = 0;
        totalTime = 0;
        cities = new ArrayList<>();
        alternativeRoutes = new ArrayList<>();
    }

    public void addCity(String cityName) {
        cities.add(cityName);
    }

    public void setTotalCost(double totalCost) {
        if (totalCost >= 0) {
            this.totalCost = totalCost;
        } else {
            System.err.println("Invalid Tcost");
        }
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalTime(double totalTime) {
        if (totalTime >= 0) {
            this.totalTime = totalTime;
        } else {
            System.err.println("Invalid Ttime");
        }
    }

    public double getTotalTime() {
        return totalTime;
    }

    public List<String> getCities() {
        return cities;
    }

    public void removeCity(int index) {
        if (index >= 0 && index < cities.size()) {
            cities.remove(index);
        }
    }

    public void copyFrom(FlightRoute newRoute) {
        this.totalCost = newRoute.totalCost;
        this.totalTime = newRoute.totalTime;
        this.cities.clear();
        this.cities.addAll(newRoute.cities);
    }

    public FlightRoute copy() {
        FlightRoute newRoute = new FlightRoute();
        newRoute.setTotalCost(this.getTotalCost());
        newRoute.setTotalTime(this.getTotalTime());
        newRoute.getCities().addAll(this.getCities());
        return newRoute;
    }

    public List<FlightRoute> getAlternativeRoutes() {
        return alternativeRoutes;
    }

    public void addAlternativeRoute(FlightRoute route) {
        alternativeRoutes.add(route);
    }
}
