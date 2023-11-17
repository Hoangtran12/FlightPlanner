import java.io.IOException;

public class P3345 {
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("java P3345.java <FlightDataFile> <PathsToCalculateFile> <OutputFile>");
            System.exit(1);
        }

        String flightDataFile = args[0];
        String flightPlansFile = args[1];
        String outputFile = args[2];

        System.out.println("Processing your request...");

        FlightPlanner planner = new FlightPlanner();
        planner.readFlightData(flightDataFile);
        planner.processFlightPlans(flightPlansFile, outputFile);

        System.out.println("Thank you for waiting, your initerary is in " + outputFile + " file. Double click to view it.");
    }
}
