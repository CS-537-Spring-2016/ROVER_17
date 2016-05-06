package swarmBots.Rover17Utilities;




import java.util.*;

public class AStar {
    private Map<Node, Coordinates> coordinate = new HashMap<>();
    private Map<Coordinates, Node> ipCoordinate = new HashMap<>();
    private GraphRepresentation gp = new GraphRepresentation();

    public LinkedList<Edge> search(GraphRepresentation graph, Node source, Node dist) {
        Map<Node, Node> parents = new HashMap<>();
        Map<Node, Integer> distances = new HashMap<>();
        Set<Node> visited = new HashSet<>();
        LinkedList<Edge> result = new LinkedList<>();
        Coordinates end = new Coordinates(0, 0);

        PriorityQueue<Node> queue = new PriorityQueue<>(1, new Comparator<Node>() {
            public int compare(Node x, Node y){
                double distX = distances.get(x) + heuristic(x, dist);
                double distY = distances.get(y) + heuristic(y, dist);
                if (distX > distY)
                    return 1;
                else if (distY > distX)
                    return -1;
                else {
                    if(coordinate.get(x).getY()<coordinate.get(y).getY())
                        return -1;
                    if (coordinate.get(x).getX() >coordinate.get(y).getX() && Math.abs(coordinate.get(x).getY()-coordinate.get(y).getY()) == 1)
                        return 1;
                    else
                        return -1;
                }

            }
        });

        distances.put(source, 0);
        parents.put(source, null);
        queue.add(source);

        while (!queue.isEmpty()) {
            Node temp = queue.poll();

            if (temp.equals(dist)) {
                break;
            }

            for(Node node:graph.neighbors(temp)) {
                if (!visited.contains(node)) {
                    parents.put(node, temp);
                    distances.put(node, distances.get(temp) + 1);
                    visited.add(node);
                    queue.add(node);
                }
                else {
                    int newDistance = distances.get(temp) + 1;
                    if (distances.get(node) > newDistance) {
                        distances.remove(node);
                        parents.remove(node);
                        distances.put(node, newDistance);
                        parents.put(node, temp);
                        queue.add(node);
                    }
                }

            }
        }

        Node currentNode = dist;

        while (!currentNode.equals(source)) {
            Node parent = parents.get(currentNode);

            if (parent != null) {
                result.add(new Edge(parent, currentNode, graph.distance(parent, currentNode)));
            }

            currentNode = parents.get(currentNode);
        }

        Collections.reverse(result);

        return result;
    }


    public double heuristic(Node current, Node end){
        Coordinates curIP = coordinate.get(current);
        Coordinates endIP = coordinate.get(end);
        double doubleX = Math.pow((double)(curIP.getX() - endIP.getX()),2);
        double doubleY = Math.pow((double)(curIP.getY() - endIP.getY()),2);
        return Math.sqrt(doubleX + doubleY);
    }
}
