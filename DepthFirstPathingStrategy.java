import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;

import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import java.util.stream.Collectors;
import java.util.stream.Stream;


class DepthFirstPathingStrategy
        implements PathingStrategy
{


    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        List<Point> path = new LinkedList<>();
        boolean found = dfs(start, end, canPassThrough, withinReach, potentialNeighbors, new HashSet<>(), path);

        if (found)
            path.remove(0);
        return path;
    }

    private static boolean dfs(Point cur, Point end,
                               Predicate<Point> canPassThrough,
                               BiPredicate<Point, Point> withinReach,
                               Function<Point, Stream<Point>> potentialNeighbors,
                               Set<Point> searched,
                               List<Point> path)
    {
        boolean found = false;
        if (!canPassThrough.test(cur) || !searched.add(cur))
            found = false;
        else if (withinReach.test(cur, end))
            found = true;
        else{

            List<Point> neighbors = potentialNeighbors.apply(cur).collect(Collectors.toList());
            for (Point p: neighbors)
                found = found || dfs(p, end, canPassThrough, withinReach, potentialNeighbors, searched, path);
        }
        if (found)
            path.add(0, cur);

        return found;
    }

}
