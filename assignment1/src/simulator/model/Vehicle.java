package simulator.model;

import org.json.JSONObject;
import java.util.Collections;
import java.util.List;

public class Vehicle extends SimulatedObject {
    private List<Junction> itinerary;
    private int maxSpeed;
    private int currentSpeed;
    private VehicleStatus status;
    private Road road;
    private int location;
    private int contClass;
    private int totalCO2;
    private int totalDistance;
    private int currentJunctionIdx; // Tracks the last visited junction

    // Constructor
    public Vehicle(String id, int maxSpeed, int contClass, List<Junction> itinerary) {
        super(id);
        if (maxSpeed <= 0)
            throw new IllegalArgumentException("Max speed must be positive.");
        if (contClass < 0 || contClass > 10)
            throw new IllegalArgumentException("Contamination class must be between 0 and 10.");
        if (itinerary == null || itinerary.size() < 2)
            throw new IllegalArgumentException("Itinerary must have at least 2 junctions.");

        this.maxSpeed = maxSpeed;
        this.currentSpeed = 0; // Vehicles start with 0 speed
        this.contClass = contClass;
        this.itinerary = Collections.unmodifiableList(itinerary); // Read-only list
        this.status = VehicleStatus.PENDING;
        this.road = null;
        this.location = 0;
        this.totalCO2 = 0;
        this.totalDistance = 0;
        this.currentJunctionIdx = 0; // Start at first junction
    }

    // Sets the current speed
    public void setSpeed(int speed) {
        if (speed < 0)
            throw new IllegalArgumentException("Speed cannot be negative.");
        if (status == VehicleStatus.TRAVELING) {
            this.currentSpeed = Math.min(speed, maxSpeed); // Ensure speed does not exceed maxSpeed
        }
    }

    // Sets the contamination class
    public void setContClass(int contClass) {
        if (contClass < 0 || contClass > 10)
            throw new IllegalArgumentException("Contamination class must be between 0 and 10.");
        this.contClass = contClass;
    }

    // Advances the vehicle over time
    @Override
    public void advance(int time) {
        if (status != VehicleStatus.TRAVELING) return; // Do nothing if not traveling

        int prevLocation = location;
        location = Math.min(location + currentSpeed, road.getLength()); // Move the vehicle
        int traveledDistance = location - prevLocation;

        totalDistance += traveledDistance;
        int contamination = traveledDistance * contClass;
        totalCO2 += contamination;
        road.addContamination(contamination); // Add CO2 to the road

        if (location == road.getLength()) { // Reached end of road
            status = VehicleStatus.WAITING;
            currentSpeed = 0; // Set speed to 0 when entering the junction
            itinerary.get(currentJunctionIdx + 1).enter(this); // Add vehicle to junction's queue
        }
    }

    // Moves the vehicle to the next road in the itinerary
    public void moveToNextRoad() {
        if (status != VehicleStatus.PENDING && status != VehicleStatus.WAITING)
            throw new IllegalStateException("Vehicle must be PENDING or WAITING to move to the next road.");

        if (status == VehicleStatus.PENDING) {
            // Get the outgoing road from the first junction to the second junction
            road = itinerary.get(0).roadTo(itinerary.get(1));
        } else {
            // Exit the current road
            road.exit(this);
            currentJunctionIdx++;

            // Check if the vehicle has reached the last junction
            if (currentJunctionIdx == itinerary.size() - 1) {
                status = VehicleStatus.ARRIVED;
                road = null;
                location = 0;
                return;
            }

            // Get the outgoing road from the current junction to the next junction
            road = itinerary.get(currentJunctionIdx).roadTo(itinerary.get(currentJunctionIdx + 1));
        }

        // Enter the new road
        location = 0;
        road.enter(this);
        status = VehicleStatus.TRAVELING;
    }

    // Returns the vehicle's status as a JSON object
    @Override
    public JSONObject report() {
        JSONObject json = new JSONObject();
        json.put("id", getId());
        json.put("speed", currentSpeed);
        json.put("distance", totalDistance);
        json.put("co2", totalCO2);
        json.put("class", contClass);
        json.put("status", status.toString());

        if (status == VehicleStatus.TRAVELING || status == VehicleStatus.WAITING) {
            json.put("road", road.getId());
            json.put("location", location);
        }

        return json;
    }

    // Getters
    public int getLocation() { return location; }
    public int getSpeed() { return currentSpeed; }
    public int getMaxSpeed() { return maxSpeed; }
    public int getContClass() { return contClass; }
    public VehicleStatus getStatus() { return status; }
    public int getTotalCO2() { return totalCO2; }
    public List<Junction> getItinerary() { return itinerary; }
    public Road getRoad() { return road; }
}
