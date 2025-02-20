package simulator.model;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;

public class Junction extends SimulatedObject {
	//Understand map (HachMap)
	//Map<String, Integer> map = new HashMap<>();
	//map.put("apple", 10);  // Key: "apple", Value: 10
	//int apples = map.get("apple");  // Retrieves 10
    private List<Road> incomingRoads;
    private Map<Junction, Road> outgoingRoads;
    private List<List<Vehicle>> queues;
    private Map<Road, List<Vehicle>> roadQueueMap;
    private int greenLightIndex;
    private int lastSwitchingTime;
    private LightSwitchingStrategy lsStrategy;
    private DequeuingStrategy dqStrategy;
    private int x, y;

    Junction(String id, LightSwitchingStrategy lsStrategy, DequeuingStrategy dqStrategy, int xCoor, int yCoor) {
        super(id);
        if (lsStrategy == null || dqStrategy == null)
            throw new IllegalArgumentException("Strategies cannot be null.");
        if (xCoor < 0 || yCoor < 0)
            throw new IllegalArgumentException("Coordinates must be non-negative.");

        this.incomingRoads = new ArrayList<>();
        this.outgoingRoads = new HashMap<>();
        this.queues = new ArrayList<>();
        this.roadQueueMap = new HashMap<>();
        this.greenLightIndex = -1;
        this.lastSwitchingTime = 0;
        this.lsStrategy = lsStrategy;
        this.dqStrategy = dqStrategy;
        this.x = xCoor;
        this.y = yCoor;
    }

    void addIncomingRoad(Road r) {
        if (r.getDest() != this)
            throw new IllegalArgumentException("Road destination is not this junction.");
        incomingRoads.add(r);
        List<Vehicle> q = new LinkedList<>();
        queues.add(q);
        roadQueueMap.put(r, q);
    }

     void addOutgoingRoad(Road r) {
        if (r.getSrc() != this)
            throw new IllegalArgumentException("Road source is not this junction.");
        Junction dest = r.getDest();
        if (outgoingRoads.containsKey(dest))
            throw new IllegalArgumentException("Outgoing road to junction " + dest.getId() + " already exists.");
        outgoingRoads.put(dest, r);
    }

    void enter(Vehicle v) {
        Road r = v.getRoad();
        List<Vehicle> q = roadQueueMap.get(r);
        if (q == null)
            throw new IllegalArgumentException("Vehicle's road is not an incoming road to this junction.");
        q.add(v);
    }

    Road roadTo(Junction j) {
        return outgoingRoads.get(j);
    }

    void advance(int time) {
        // Dequeue vehicles from the current green light queue
        if (greenLightIndex != -1) {
            List<Vehicle> queue = queues.get(greenLightIndex);
            List<Vehicle> toMove = dqStrategy.dequeue(queue);
            for (Vehicle v : toMove) {
                v.moveToNextRoad();
            }
            queue.removeAll(toMove);
        }

        // Update green light using the light switching strategy
        int newGreen = lsStrategy.chooseNextGreen(incomingRoads, queues, greenLightIndex, lastSwitchingTime, time);
        if (newGreen != greenLightIndex) {
            greenLightIndex = newGreen;
            lastSwitchingTime = time;
        }
    }

    public JSONObject report() {
        JSONObject json = new JSONObject();
        json.put("id", getId());
        json.put("green", (greenLightIndex == -1) ? JSONObject.NULL : incomingRoads.get(greenLightIndex).getId());

        JSONArray queuesJson = new JSONArray();
        for (int i = 0; i < incomingRoads.size(); i++) {
            Road r = incomingRoads.get(i);
            List<Vehicle> q = queues.get(i);
            JSONObject queueJson = new JSONObject();
            queueJson.put("road", r.getId());
            JSONArray vehiclesJson = new JSONArray();
            for (Vehicle v : q) {
                vehiclesJson.put(v.getId());
            }
            queueJson.put("vehicles", vehiclesJson);
            queuesJson.put(queueJson);
        }
        json.put("queues", queuesJson);

        return json;
    }

    // Getters for testing or internal use
    public List<Road> getIncomingRoads() { return Collections.unmodifiableList(incomingRoads); }
    public Map<Junction, Road> getOutgoingRoads() { return Collections.unmodifiableMap(outgoingRoads); }
    public int getGreenLightIndex() { return greenLightIndex; }
    public int getLastSwitchingTime() { return lastSwitchingTime; }
    public int getX() { return x; }
    public int getY() { return y; }
}