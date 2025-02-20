package simulator.model;

public class InterCityRoad extends Road {
    // Constructor
    public InterCityRoad(String id, Junction srcJunc, Junction destJunc, int maxSpeed, int contLimit, int length, Weather weather) {
        super(id, srcJunc, destJunc, maxSpeed, contLimit, length, weather);
    }

    // Reduce total contamination based on weather
    @Override
    void reduceTotalContamination() {
        int x = 0;
        switch (getWeather()) {
            case SUNNY: x = 2; break;
            case CLOUDY: x = 3; break;
            case RAINY: x = 10; break;
            case WINDY: x = 15; break;
            case STORM: x = 20; break;
        }
        int currentCO2 = getTotalCO2();
        int newCO2 = ((100 - x) * currentCO2) / 100;
        addContamination(newCO2 - currentCO2); // Update totalCO2 using addContamination
    }

    // Update speed limit based on contamination
    @Override
    void updateSpeedLimit() {
        if (getTotalCO2() > getContLimit()) {
            setSpeedLimit(getMaxSpeed() / 2); // Use setter method to update speedLimit
        } else {
            setSpeedLimit(getMaxSpeed()); // Use setter method to update speedLimit
        }
    }

    // Calculate vehicle speed based on speed limit and weather
    @Override
    int calculateVehicleSpeed(Vehicle v) {
        if (getWeather() == Weather.STORM) {
            return (getSpeedLimit() * 8) / 10; // Use getter method to access speedLimit
        }
        return getSpeedLimit(); // Use getter method to access speedLimit
    }
}