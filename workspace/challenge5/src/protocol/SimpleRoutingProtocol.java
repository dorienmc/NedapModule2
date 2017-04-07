package protocol;

import client.DataTable;
import client.IRoutingProtocol;
import client.LinkLayer;
import client.Packet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SimpleRoutingProtocol implements IRoutingProtocol {
    private LinkLayer linkLayer;
    public static int NUMBER_OF_NODES = 6;

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
        for (int i = 1; i <= NUMBER_OF_NODES; i++) {
            if(i == linkLayer.getOwnAddress()) {
                myForwardingTable.put(i,new Route(linkLayer.getOwnAddress()));
            } else {
                int cost = linkLayer.getLinkCost(i);
                if(cost >= 0) {
                    myForwardingTable.put(i,new Route(i,cost,i));
                }

            }
            System.out.println(linkLayer.getLinkCost(i));
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
        System.out.println(dt.getRowString(0));
        Packet pkt = new Packet(linkLayer.getOwnAddress(), 0, dt);
        this.linkLayer.transmit(pkt);
    }

    public boolean isMe(int id) {
        return linkLayer.getOwnAddress() == id;
    }

    private boolean updateForwardingTable(DataTable dt, int neighbour) {
        boolean change = false;

        int destination = 1;
        for(int cost: dt.getRow(0)) {
            if(updateForwardTableEntry(new Route(destination, cost, neighbour))){
                System.out.println("Added/updated route to " + destination + " eg. " + myForwardingTable.get(destination));
                System.out.println(myForwardingTable);
                change = true;
            }
            destination++;
        }

        return change;
    }

    public boolean updateForwardTableEntry(Route newRoute) {
        //Do not update when costs are -1 (eg. infinite)
        if(newRoute.cost < 0) {
            return false;
        }

        //Update if needed
        int neighbour = newRoute.nextHop;
        Route currentRoute = myForwardingTable.get(newRoute.destination);

        if (currentRoute == null || newRoute.cost + linkLayer.getLinkCost(neighbour) < currentRoute.cost) {
            newRoute.cost += linkLayer.getLinkCost(neighbour); //add cost of link!
            myForwardingTable.put(newRoute.destination, newRoute);
            return true;
        }
        return false;
    }

    @Override
    public void tick(Packet[] packets) {
        if (handleInput(packets)) {
            sendForwardTable();
        } else {
            System.out.println("No update");
        }
    }

    private boolean handleInput(Packet[] packets) {
        System.out.println("tick; received " + packets.length + " packets");
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
            ft.put(entry.getKey(), entry.getValue().nextHop);
        }

        return ft;
    }
}
