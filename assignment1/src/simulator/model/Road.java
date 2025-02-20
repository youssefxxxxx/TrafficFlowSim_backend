package simulator.model;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;


//we declare abstarct because we have two subclasses of this City Road and inter-city 
public abstract class Road extends SimulatedObject {
    private Junction srcJunc; //  The junction where the road starts.
    private Junction destJunc; // The junction where the road ends.
    private int length; // Length of the road
    private int maxSpeed; // Maximum speed allowed
    private int speedLimit; // Current speed limit
    private int contLimit; // Contamination alarm limit
    private Weather weather; // Weather conditions
    private int totalCO2; // Total accumulated contamination
    private List<Vehicle> vehicles; // List of vehicles on the road

    // Constructor
    Road(String id, Junction srcJunc, Junction destJunc, int maxSpeed, int contLimit, int length, Weather weather) {
        super(id);
        if (maxSpeed <= 0)
            throw new IllegalArgumentException("Max speed must be positive.");
        if (contLimit < 0)
            throw new IllegalArgumentException("Contamination limit must be non-negative.");
        if (length <= 0)
            throw new IllegalArgumentException("Length must be positive.");
        if (srcJunc == null || destJunc == null || weather == null)
            throw new IllegalArgumentException("Source junction, destination junction, and weather cannot be null.");

        this.srcJunc = srcJunc;
        this.destJunc = destJunc;
        this.maxSpeed = maxSpeed;
        this.speedLimit = maxSpeed; // Initial speed limit equals max speed
        this.contLimit = contLimit;
        this.length = length;
        this.weather = weather;
        this.totalCO2 = 0;
        this.vehicles = new ArrayList<>();

        // Add this road to the source and destination junctions
        srcJunc.addOutgoingRoad(this);
        destJunc.addIncomingRoad(this);
    }

    // Add a vehicle to the road
    void enter(Vehicle v) {
        if (v.getLocation() != 0)
            throw new IllegalArgumentException("Vehicle location must be 0 to enter the road.");
        if (v.getSpeed() != 0)
            throw new IllegalArgumentException("Vehicle speed must be 0 to enter the road.");
        vehicles.add(v);
    }

    // Remove a vehicle from the road
    void exit(Vehicle v) {
        vehicles.remove(v);
    }

    // Set weather conditions
    void setWeather(Weather w) {
        if (w == null)
            throw new IllegalArgumentException("Weather cannot be null.");
        this.weather = w;
    }

    // Add contamination to the road
    void addContamination(int c) {
        if (c < 0)
            throw new IllegalArgumentException("Contamination must be non-negative.");
        totalCO2 += c;
    }

    // Abstract methods to be implemented by subclasses
    abstract void reduceTotalContamination();
    abstract void updateSpeedLimit();
    abstract int calculateVehicleSpeed(Vehicle v);

    // Advance the state of the road
    void advance(int time) {
        // Reduce total contamination
        reduceTotalContamination();

        // Update speed limit
        updateSpeedLimit();

        // Update vehicle speeds and advance them
        for (Vehicle v : vehicles) {
            v.setSpeed(calculateVehicleSpeed(v));
            v.advance(time);
        }

        // Sort vehicles by location (descending order)
        vehicles.sort((v1, v2) -> Integer.compare(v2.getLocation(), v1.getLocation()));
    }

    // Generate a JSON report of the road's state
    public JSONObject report() {
        JSONObject json = new JSONObject();
        json.put("id", getId());
        json.put("speedlimit", speedLimit);
        json.put("weather", weather.toString());
        json.put("co2", totalCO2);

        // Add vehicle IDs in order
        List<String> vehicleIds = new ArrayList<>();
        for (Vehicle v : vehicles) {
            vehicleIds.add(v.getId());
        }
        json.put("vehicles", vehicleIds);

        return json;
    }

    // Getters
    public int getLength() { return length; }
    public Junction getDest() { return destJunc; }
    public Junction getSrc() { return srcJunc; }
    public Weather getWeather() { return weather; }
    public int getContLimit() { return contLimit; }
    public int getMaxSpeed() { return maxSpeed; }
    public int getTotalCO2() { return totalCO2; }
    public int getSpeedLimit() { return speedLimit; }
    public List<Vehicle> getVehicles() { return Collections.unmodifiableList(vehicles); }

	public Junction getSrcJunc() {
		return srcJunc;
	}

	public void setSrcJunc(Junction srcJunc) {
		this.srcJunc = srcJunc;
	}

	public Junction getDestJunc() {
		return destJunc;
	}

	public void setDestJunc(Junction destJunc) {
		this.destJunc = destJunc;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setMaxSpeed(int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public void setSpeedLimit(int speedLimit) {
		this.speedLimit = speedLimit;
	}

	public void setContLimit(int contLimit) {
		this.contLimit = contLimit;
	}

	public void setTotalCO2(int totalCO2) {
		this.totalCO2 = totalCO2;
	}

	public void setVehicles(List<Vehicle> vehicles) {
		this.vehicles = vehicles;
	}
    
}