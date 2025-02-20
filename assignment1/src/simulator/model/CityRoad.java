package simulator.model;

public class CityRoad extends Road {

    // Constructor
    public CityRoad(String id, Junction srcJunc, Junction destJunc, int maxSpeed, int contLimit, int length, Weather weather) {
        super(id, srcJunc, destJunc, maxSpeed, contLimit, length, weather);
    }

    // Reduce total contamination based on weather
    @Override
    void reduceTotalContamination() {
        int x = 2;
        if (getWeather() == Weather.WINDY || getWeather() == Weather.STORM) {
            x = 10;
        }
        int currentCO2 = getTotalCO2();
        int newCO2 = Math.max(0, currentCO2 - x);
        addContamination(newCO2 - currentCO2); // Update totalCO2 using addContamination
    }

    // Speed limit is always the maximum speed
    @Override
    void updateSpeedLimit() {
        setSpeedLimit(getMaxSpeed()); // Use setter method to update speedLimit
    }

    // Calculate vehicle speed based on contamination class
    @Override
    int calculateVehicleSpeed(Vehicle v) {
        return ((11 - v.getContClass()) * getSpeedLimit()) / 11; // Use getter method to access speedLimit
    }
}