import processing.core.PImage;

import java.util.List;

public abstract class Movement extends EntityActivity {
    public Movement(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod) {
        super(id, position, images, animationPeriod, actionPeriod);
    }


    // moveToDude, moveToFairy
    boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacent(getPosition(), target.getPosition())) {
            moveToAction(world, target, scheduler); // use abstract helper
            return true;
        } else {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }



    protected abstract void moveToAction(WorldModel world, Entity target, EventScheduler scheduler);


    // nextPositionDude, nextPositionFairy
//    public Point nextPosition(WorldModel world, Point destPos){
//        int horiz = Integer.signum(destPos.x - getPosition().x);
//        Point newPos = new Point(getPosition().x + horiz, getPosition().y);
//
//        if (horiz == 0 || world.isOccupied(newPos) && !nextPositionKind(world, newPos)) { // use abstract helper
//            int vert = Integer.signum(destPos.y - getPosition().y);
//            newPos = new Point(getPosition().x, getPosition().y + vert);
//
//            if (vert == 0 || world.isOccupied(newPos) && !nextPositionKind(world, newPos)) { // use abstract helper
//                newPos = getPosition();
//            }
//        }
//
//        return newPos;
//    }

    // program runs using SingleStepPathingStrategy
    public Point nextPosition(WorldModel world, Point destPos) {
        PathingStrategy strat = new SingleStepPathingStrategy();

        // exclude start / end, and be in ascending order
        List<Point> path = strat.computePath(getPosition(), destPos,
                pt -> world.withinBounds(pt) && (!world.isOccupied(pt)
                        || world.getOccupancyCell(pt).getClass().equals(nextPositionKind())),
                Point::adjacent, PathingStrategy.CARDINAL_NEIGHBORS);

        // return first point from path
        // handle case where path is empty - stay where we are
        if (path.size() != 0)
            return path.get(0);
        return getPosition();

    }

    protected abstract Class nextPositionKind();

}
