package protocol;

import client.DataTable;
import client.IRoutingProtocol;
import client.LinkLayer;
import client.Packet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DetectEdgeCostChangesRoutingProtocol implements IRoutingProtocol {
    private LinkLayer linkLayer;
    public static int NUMBER_OF_NODES = 6;
    private int[] linkCosts = new int[NUMBER_OF_NODES];
    private int[] TTL = new int[NUMBER_OF_NODES];
    private static int maxTTL = 100;

    // You can use this data structure to store your forwarding table with extra information.
    private HashMap<Integer, Route> myForwardingTable = new HashMap<>();

    @Override
    public void init(LinkLayer linkLayer) {
        this.linkLayer = linkLayer;

        System.out.println("I am " + linkLayer.getOwnAddress());
        initForwardingTable();
        sendForwardTable();
    }

    public void initForwardingTable() {
        Arrays.fill(linkCosts, -1);

        for (int i = 1; i <= NUMBER_OF_NODES; i++) {
            linkCosts[i - 1] = (isMe(i) ? 0 : linkLayer.getLinkCost(i));
            if(linkCosts[i - 1] >= 0) {
                setForwardingTableEntry(new Route(i,linkCosts[i - 1],i));
            }
        }
        System.out.println(myForwardingTable);
    }

    public void sendForwardTable(){
        DataTable dt = new DataTable(NUMBER_OF_NODES);
        Integer[] costs = new Integer[NUMBER_OF_NODES];
        Arrays.fill(costs, -1);
        for(int i = 1; i <= NUMBER_OF_NODES; i++) {
            if(isMe(i)) {
                costs[i - 1] = 0;
            } else if (myForwardingTable.containsKey(i)) {
                costs[i - 1] = myForwardingTable.get(i).cost;
            }
        }
        dt.addRow(costs);
        System.out.println("Send :"  + dt.getRowString(0));
        Packet pkt = new Packet(linkLayer.getOwnAddress(), 0, dt);
        this.linkLayer.transmit(pkt);
    }

    public boolean isMe(int id) {
        return linkLayer.getOwnAddress() == id;
    }

    // Returns if the new information uses the same nextHop as the current best Route.
    public boolean infoIsFromSameNeighbour(Route newRoute) {
        return (newRoute.nextHop == myForwardingTable.get(newRoute.destination).nextHop);
    }

    private boolean updateForwardingTable(DataTable dt, int neighbour) {
        boolean change = false;

        int destination = 1;
        for(int cost: dt.getRow(0)) {
            if(updateForwardTableEntry(new Route(destination, cost, neighbour))){
                System.out.println("Added/updated/removed route to node " + destination + " eg. " + myForwardingTable.get(destination));
                System.out.println(myForwardingTable);
                change = true;
            }
            destination++;
        }

        return change;
    }

    public boolean updateForwardTableEntry(Route newRoute) {
        int destination = newRoute.destination;

        //Do not update when costs are -1 (eg. infinite), unless previous cost was retrieved from same neighbour.
        //Or unless we have the destination as neighbour and the direct route is better
        if(newRoute.cost < 0) {
            //Check if the destination is a neighbour, then use direct route.
            if(isNeighbour(destination)) {
                setForwardingTableEntry(new Route(destination, linkLayer.getLinkCost(destination), destination));
                return true;
            }

            //Check if current route info is from the same neighbour/nextHop.
            if(myForwardingTable.containsKey(destination) && infoIsFromSameNeighbour(newRoute)) {
                //Force update/drop
                System.out.println("Dropped " + myForwardingTable.get(destination));
                myForwardingTable.remove(destination);
                return true;
            }

            return false;
        }

        //Ingnore info that uses a me as a nexthop ('cause then I know better)
        if(isMe(newRoute.nextHop)) {
            return false;
        }

        int neighbour = newRoute.nextHop;
        Route currentRoute = myForwardingTable.get(destination);
        newRoute.cost += linkLayer.getLinkCost(neighbour); //Take cost of link into account!

        //Always update if previous info comes from the same node ('cause that's more up to date)
        //And ofcourse the info must be new.
        if(currentRoute != null && infoIsFromSameNeighbour(newRoute) && (newRoute.cost != currentRoute.cost)) {
            setForwardingTableEntry(newRoute);
            return true;
        }

        //Otherwise, update if needed
        if (currentRoute == null || newRoute.cost < currentRoute.cost) {
            setForwardingTableEntry(newRoute);
            return true;
        }

        return false;
    }

    public void setForwardingTableEntry(Route newRoute) {
        if(newRoute.cost == -1) {
            myForwardingTable.remove(newRoute.destination);
        } else {
            myForwardingTable.put(newRoute.destination, newRoute);
            resetTTL(newRoute.destination);
        }
    }

    public void forceUpdate(int destination, int newLinkCost){

        int oldLinkCost = linkCosts[destination-1];
        if(oldLinkCost == - 1) {
            setForwardingTableEntry(new Route(destination, newLinkCost, destination));
        } else if(newLinkCost == -1) {
            myForwardingTable.remove(destination);
            if(isNeighbour(destination)) {
                setForwardingTableEntry(new Route(destination, linkLayer.getLinkCost(destination), destination));
            }
        } else {
            Route currentRoute = myForwardingTable.get(destination);

            if(currentRoute != null) {
                int newCost = myForwardingTable.get(destination).cost - oldLinkCost + newLinkCost;
                setForwardingTableEntry(new Route(destination, newCost, currentRoute.nextHop));
            } else { //Add new route, if its the neighbour
                if(isNeighbour(destination)) {
                    setForwardingTableEntry(new Route(destination, newLinkCost, destination));
                }
            }
        }
    }

    public boolean isNeighbour(int id) {
        return linkLayer.getLinkCost(id) > 0;
    }

    public boolean updateLinkCosts() {
        boolean changed = false;
        for (int i = 1; i <= NUMBER_OF_NODES; i++) {
            int newLinkCost = linkLayer.getLinkCost(i);
            int oldLinkCost = linkCosts[i-1];
            if(!isMe(i) && (newLinkCost != oldLinkCost)) {
                //Force update any nodes that are reached via the link (me,i)
                for(int j = 1; j <= NUMBER_OF_NODES; j++) {
                    if(myForwardingTable.containsKey(j) && myForwardingTable.get(j).nextHop == i) {
                        forceUpdate(j,newLinkCost);
                    }
                }

                System.out.println(myForwardingTable);


                linkCosts[i - 1] = newLinkCost;
                changed = true;
                System.out.println(String.format("Edge cost (%d,%d) changed to %d",linkLayer.getOwnAddress(),i,linkCosts[i-1]));
            }
        }
        return changed;
    }

    public boolean updateTTL() {
        boolean changed = false;
        for(int i = 0; i < TTL.length; i++) {
            if(!isMe(i + 1)) {
                TTL[i] -= 1;
            }

            if(myForwardingTable.containsKey(i + 1) && TTL[i] < 0) {
                System.out.println("Reached TTL for destination " + (i+1));
                myForwardingTable.remove(i + 1);
                changed = true;
            }
        }
        return changed;
    }

    public void resetTTL(int id) {
        TTL[id - 1] = maxTTL;
    }

    @Override
    public void tick(Packet[] packets) {
        boolean update = false;

        System.out.println("\ntick");

        //Update TTL
        update = (update || updateTTL());

        //Update link costs if necessary
        update = (update || updateLinkCosts());

        //Handle input and
        update = (update || handleInput(packets));

        //Send forwardtable if anything changed
        if (update) {
            sendForwardTable();
        } else {
            System.out.println("No update");
        }
    }

    private boolean handleInput(Packet[] packets) {
        System.out.println("received " + packets.length + " packets");
        boolean change = false;

        // first process the incoming packets; loop over them:
        for (Packet packet : packets) {
            int neighbour = packet.getSourceAddress();
            DataTable dt = packet.getDataTable();

            System.out.printf("received packet from %d with %d rows and %d columns of data%n", neighbour, dt.getNRows(), dt.getNColumns());
            System.out.println(dt.getRowString(0));

            if (updateForwardingTable(dt, neighbour)) {
                System.out.println("Updating forwarding table");
                change = true;
            }

        }
        return change;
    }

    public HashMap<Integer, Integer> getForwardingTable() {
        // This code transforms your forwarding table which may contain extra information
        // to a simple one with only a next hop (value) for each destination (key).
        // The result of this method is send to the server to validate and score your protocol.

        // <Destination, NextHop>
        HashMap<Integer, Integer> ft = new HashMap<>();

        for (Map.Entry<Integer, Route> entry : myForwardingTable.entrySet()) {
            if(!isMe(entry.getKey())) {
                ft.put(entry.getKey(), entry.getValue().nextHop);
            }
        }

        return ft;
    }
}
