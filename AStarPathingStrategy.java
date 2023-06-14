import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AStarPathingStrategy
        implements PathingStrategy
{

//    public static boolean withinBounds(Point p, boolean[][] grid)
//    {
//        return p.y >= 0 && p.y < grid.length &&
//                p.x >= 0 && p.x < grid[0].length;
//    }
//
//    public static void main(String[] args) {
//
//        boolean[][] grid = {
//                {true, true, false, true, true},
//                {true, true, false, true, true},
//                {true, true, false, true, false},
//                {false, true, false, true, false},
//                {true, true, true, true, true}
//        };
//
//        Point start = new Point(0, 0);
//        Point goal = new Point(4, 4);
//        PathingStrategy ps = new AStarPathingStrategy();
//        List<Point> path = ps.computePath(
//                start, goal,
//                p -> withinBounds(p, grid) && grid[p.y][p.x],
//                Point::adjacent,
//                PathingStrategy.CARDINAL_NEIGHBORS
//        );
//
//        System.out.println(path);
//    }


    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {

        // validity checks: out of bounds?
        // if (start.x >= 0 && end.x >= 0 && start.y >= 0 && end.y >= 0 && start.x <  && end.x >= 0 && start.y >= 0 && end.y >= 0)


        List<Point> path = new LinkedList<>();

        // initialize lists (HashMap, Priority Queue, Comparator)
        // initialize Priority Queue using Comparator (to check lowest f)
        Comparator<Node> compNeighbor = Comparator.comparing(Node::getF).thenComparing(Node::getH); // compares f value, break ties compare h
        PriorityQueue<Node> checkF = new PriorityQueue<>(100, compNeighbor);

        // initialize HashMap for Open List: adds potential future nodes
        HashMap<Point, Node> openList = new HashMap<>();

        // initialize HashMap for Closed List: visited nodes
        HashMap<Point, Node> closedList = new HashMap<>();


        // add start to openList & mark current node
        Node current = new Node(start, null, 0, calcH(start, end));
        openList.put(start, current);


        while (!openList.isEmpty()) {
            if (withinReach.test(current.position, end)) { // found end
                break;
            }

            // analyze adjacent nodes (stream)
            List<Point> neighbors = potentialNeighbors.apply(current.position)
                    .filter(canPassThrough)
                    .collect((Collectors.toList()));

            // for each valid adjacent nodes
            for (Point adj : neighbors) {

                // if node NOT in openList or closedList -> go to next node
                if (!openList.containsKey(adj) && !closedList.containsKey(adj)) {
                    Node adjN = new Node(adj, current, current.g + 1, calcH(adj, end));
                    openList.put(adj, adjN); // add to open list
                    checkF.add(adjN);
                }
                // if node already on openList
                else if (openList.containsKey(adj)) {
                    Node node = openList.get(adj);
                    if (current.g + 1 < node.g) { // replace with better g-value
                        node.g = current.g + 1;
                        node.prior = current;
                        checkF.remove(node);
                        checkF.add(node);
                    }
                }
                // if node already on closedList
                else if (closedList.containsKey(adj)) {
                    Node node = closedList.get(adj);
                    if (current.g + 1 < node.g) {
                        node.g = current.g + 1;
                        node.prior = current;
                        openList.put(adj, node);
                        closedList.remove(node);
                        checkF.add(node);
                    }
                }
            }

            // move node from open to closed list
            openList.remove(current.position);
            closedList.put(current.position, current);

            // choose node with smallest f (first in checkF)
            current = checkF.poll();

        }

        if (start.equals(end)) { // start & end are same point
            System.out.println("Start and End are the same point");
            return path;
        }

        if (current == null || !withinReach.test(current.position, end)) {
            System.out.println("No path exists");
            return path; // no path
        }

        // add prior nodes to path
        while (current.prior != null) {
            path.add(0, current.position);
            current = current.prior;
        }

        return path;
    }


    public static int calcH(Point adj, Point end) {
        return Math.abs(end.x - adj.x) + Math.abs(end.y - adj.y); // Manhattan Distance
    }


    // create Node class
    public static class Node {
        private Point position;
        private Node prior;
        private int g;
        private int h;
        private int f;

        public Node(Point position, Node prior, int g, int h) {
            this.position = position;
            this.prior = prior;
            this.g = g;
            this.h = h;
            this.f = g + h;
        }

        public int getF() {
            return f;
        }

        public int getH() {
            return h;
        }

        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Node n = (Node) o;
            return Objects.equals(position, n.position) &&
                    Objects.equals(prior, n.prior) &&
                    g == n.g &&
                    h == n.h &&
                    f == n.f;
        }

    }

}