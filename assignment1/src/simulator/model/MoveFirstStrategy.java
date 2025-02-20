package simulator.model;

import java.util.List;

public class MoveFirstStrategy implements DequeuingStrategy {
    @Override
    public List<Vehicle> dequeue(List<Vehicle> q) {
        return List.of(q.get(0)); // Return the first vehicle in the queue
    }
}