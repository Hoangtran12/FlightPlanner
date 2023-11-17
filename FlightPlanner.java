import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class FlightPlanner {
    private Map<String, List<FlightData>> flightDataMap = new HashMap<>();
    private List<FlightPlan> flightPlans = new ArrayList<>();
    private DecimalFormat costFormatter = new DecimalFormat("0.00");

    public void readFlightData(String flightDataFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(flightDataFile));
        int numFlights = Integer.parseInt(br.readLine().trim());
        for (int i = 0; i < numFlights; i++) {
            String[] flightInfo = br.readLine().split("\\|");
            String origin = flightInfo[0];
            String destination = flightInfo[1];
            double cost = Double.parseDouble(flightInfo[2]);
            double time = Double.parseDouble(flightInfo[3]);

            FlightData flightData = new FlightData(origin, destination, cost, time);

            flightDataMap.computeIfAbsent(origin, k -> new ArrayList<>()).add(flightData); // Starting forward
            flightDataMap.computeIfAbsent(destination, k -> new ArrayList<>())             // Starting backward
                    .add(new FlightData(destination, origin, cost, time));
        }
        br.close();
    }

    private void calculateTotalCostAndTime(FlightRoute route, double costIncrement, double timeIncrement,
            String sortType) {
        if (sortType.equals("T")) {
            route.setTotalTime(route.getTotalTime() + timeIncrement);
            route.setTotalCost(route.getTotalCost() + costIncrement);
        } else if (sortType.equals("C")) {
            route.setTotalCost(route.getTotalCost() + costIncrement);
            route.setTotalTime(route.getTotalTime() + timeIncrement);
        }
    }

    private FlightData getReturnFlightData(String fromCity, String toCity) {     // Find a flight from fromCity to toCity
        List<FlightData> flightDataList = flightDataMap.get(fromCity);
        if (flightDataList != null) {
            for (FlightData flightData : flightDataList) {
                if (flightData.getDestination().equals(toCity)) {
                    return flightData;
                }
            }
        }
        return null;
    }

    public void processFlightPlans(String flightPlansFile, String outputFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(flightPlansFile));
        String line = br.readLine();

        if (line == null) {
            System.err.println("No data found in the flight plans file.");
            br.close();
            return;
        }

        int numPlans = Integer.parseInt(line.trim());

        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

        for (int i = 0; i < numPlans; i++) {
            line = br.readLine();

            if (line == null) {
                System.err.println("Insufficient data");
                break;
            }

            String[] planInfo = line.split("\\|");

            if (planInfo.length < 3) {
                System.err.println("Invalid data format");
                continue;
            }

            String origin = planInfo[0];
            String destination = planInfo[1];
            String sortType = planInfo[2];

            // Check if there is a return flight
            boolean isReturnFlight = origin.equals(destination);

            FlightPlan flightPlan = new FlightPlan(origin, destination, sortType);
            flightPlans.add(flightPlan);

            if (!isReturnFlight) {
                // Only find routes for non-return flights
                List<FlightRoute> routes = findShortestRoutes(flightPlan);
                writeFlightRoutes(bw, origin, destination, sortType, routes, i + 1);
            }
        }

        br.close();
        bw.close();
    }

    private List<FlightRoute> findShortestRoutes(FlightPlan flightPlan) {
        String origin = flightPlan.getOrigin();
        String destination = flightPlan.getDestination();
        String sortType = flightPlan.getSortType();

        PriorityQueue<FlightRoute> queue = new PriorityQueue<>(Comparator.comparingDouble(FlightRoute::getTotalCost));
        List<FlightRoute> routes = new ArrayList<>();
        FlightRoute initialRoute = new FlightRoute();
        initialRoute.addCity(origin);

        queue.offer(initialRoute);

        while (!queue.isEmpty()) {
            FlightRoute currentRoute = queue.poll();
            String currentCity = currentRoute.getCities().get(currentRoute.getCities().size() - 1);

            if (currentCity.equals(destination)) {
                // Destination reached
                routes.add(currentRoute);
            } else {
                List<FlightData> flightDataList = flightDataMap.get(currentCity);
                if (flightDataList != null) {
                    for (FlightData flightData : flightDataList) {
                        String nextCity = flightData.getDestination();
                        if (!currentRoute.getCities().contains(nextCity)) {
                            FlightRoute newRoute = currentRoute.copy();
                            newRoute.addCity(nextCity);

                            double costIncrement = flightData.getCost();
                            double timeIncrement = flightData.getTime();
                            calculateTotalCostAndTime(newRoute, costIncrement, timeIncrement, sortType);

                            queue.offer(newRoute);
                        }
                    }
                }
            }
        }

        Stack<FlightRoute> stack = new Stack<>();
        stack.push(initialRoute);

        while (!stack.isEmpty()) {
            FlightRoute currentRoute = stack.pop();
            String currentCity = currentRoute.getCities().get(currentRoute.getCities().size() - 1);

            if (currentCity.equals(destination)) {
                // Destination reached
                routes.add(currentRoute);
            } else {
                List<FlightData> flightDataList = flightDataMap.get(currentCity);
                if (flightDataList != null) {
                    for (FlightData flightData : flightDataList) {
                        String nextCity = flightData.getDestination();
                        if (!currentRoute.getCities().contains(nextCity)) {
                            FlightRoute newRoute = currentRoute.copy();
                            newRoute.addCity(nextCity);

                            double costIncrement = flightData.getCost();
                            double timeIncrement = flightData.getTime();
                            calculateTotalCostAndTime(newRoute, costIncrement, timeIncrement, sortType);

                            stack.push(newRoute);
                        }
                    }
                }
            }

            // Explore alternative routes
            for (int i = 1; i < currentRoute.getCities().size() - 1; i++) {
                FlightRoute alternativeRoute = currentRoute.copy();
                alternativeRoute.removeCity(i);
                String fromCity = alternativeRoute.getCities().get(i - 1);
                String toCity = alternativeRoute.getCities().get(i);

                // Get data for this route
                FlightData flightData = getReturnFlightData(fromCity, toCity);

                if (flightData != null) {
                    double costIncrement = flightData.getCost();
                    double timeIncrement = flightData.getTime();
                    calculateTotalCostAndTime(alternativeRoute, costIncrement, timeIncrement, sortType);

                    stack.push(alternativeRoute);
                    routes.add(alternativeRoute);

                }
            }
        }
        return routes;
    }

    private void writeFlightRoutes(BufferedWriter bw, String origin, String destination, String sortType,
            List<FlightRoute> routes, int flightNumber) throws IOException {
        String sortTypeLabel = (sortType.equals("T") ? "Time" : "Cost");

        bw.write("Flight " + flightNumber + ": " + origin + ", " + destination + " (" + sortTypeLabel + ")");
        bw.newLine();

        int pathNumber = 1;
        for (FlightRoute route : routes) {
            if (pathNumber <= 3) { // Change this if you want to produce more output.
                writeRouteInfo(bw, route, pathNumber);
            } else {
                break;
            }
            pathNumber++;
        }
    }

    private void writeRouteInfo(BufferedWriter bw, FlightRoute route, int pathNumber) throws IOException {
        String citiesPath = String.join(" -> ", route.getCities());
        int pathTime = (int) route.getTotalTime();
        String pathCost = costFormatter.format(route.getTotalCost());

        bw.write("Path " + pathNumber + ": " + citiesPath + ". Time: " + pathTime + " Cost: " + pathCost);
        bw.newLine();
    }
}
