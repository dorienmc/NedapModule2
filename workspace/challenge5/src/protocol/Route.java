package protocol;

/**
 * Simple object which describes a route entry in the forwarding table.
 * Can be extended to include additional data.
 */
public class Route {
    public int nextHop;
    public int cost;
    public int destination;

    public Route(int destination, int cost, int nextHop){
        this.nextHop = nextHop;
        this.cost = cost;
        this.destination = destination;
    }

    public Route(int myId){
        this(myId,0, myId);
    }

    @Override
    public String toString() {
        return String.format("Route (d:%d,c:%d,n:%d)", destination,cost,nextHop);
    }
}
