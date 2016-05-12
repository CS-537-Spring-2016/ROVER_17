package swarmBots.Rover17Utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;

public class GraphRepresentation {
    private ArrayList<Node> nodes;
    private ArrayList<Node> sciences;
    private ArrayList<Edge> edges;

    public GraphRepresentation() {
        nodes = new ArrayList();
        edges = new ArrayList();
        sciences = new ArrayList();
    }

    public ArrayList<Node> getNodes(){
        return nodes;
    }

    public void setNodes(ArrayList<Node> nodes){
        this.nodes = nodes;
    }

    public ArrayList<Node> getSciences(){
        return sciences;
    }

    public boolean addSciences(Node x){
        if(sciences.contains(x)){
            return false;
        }
        else {
            sciences.add(x);
            return true;
        }
    }


    public boolean adjacent(Node x, Node y) {
        for (Edge edge: edges) {
            if (edge.getFrom().equals(x) && edge.getTo().equals(y)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Node> neighbors(Node x) {
        ArrayList<Node> result = new ArrayList();

        if (!nodes.contains(x)) {
            return result;
        }

        for (Edge edge: edges) {
            if (edge.getFrom().equals(x)) {
                result.add(edge.getTo());
            }
        }

        return result;
    }

    public boolean addNode(Node x) {
        if (nodes.contains(x)) {
            return false;
        }

        nodes.add(x);
        return true;
    }

    public boolean removeNode(Node x) {
        if (!nodes.contains(x)) {
            return false;
        }

        Iterator<Edge> iterator = edges.iterator();
        while (iterator.hasNext()) {
            Edge edge = iterator.next();
            if (edge.getTo().equals(x) || edge.getFrom().equals(x)) {
                iterator.remove();
            }
        }

        return nodes.remove(x);
    }

    public boolean addEdge(Edge x) {
        if (!nodes.contains(x.getFrom())) {
            nodes.add(x.getFrom());
        }
        if (!nodes.contains(x.getTo())) {
            nodes.add(x.getTo());
        }

        if (edges.contains(x)) {
            return false;
        }

        edges.add(x);

        return true;
    }


    public boolean removeEdge(Edge x) {
        return edges.remove(x);
    }


    public int distance(Node from, Node to) {
        for (Edge edge: edges) {
            if (edge.getFrom().equals(from) && edge.getTo().equals(to)) {
                return edge.getValue();
            }
        }

        return 0;
    }

    @Override
    public String toString() {
        String nodestring = "";
        for (Node n : nodes){
            nodestring += n.toString() + ", ";
        }
        return nodestring;
    }

    //see if edges are added correctly
    public String edgesToString(){
        String edgeString = "";
        for (Edge e : edges){
            edgeString += e.toString() +"\n";
        }
        return edgeString;
    }
}
