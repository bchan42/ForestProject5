import processing.core.PImage;
import java.util.List;

public abstract class Dude extends Movement {
    private final int resourceLimit;
    private int resourceCount;
    public Dude(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int resourceLimit, int resourceCount) {
        super(id, position, images, animationPeriod, actionPeriod);
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
    }

    // getters
    public int getResourceCount(){ return this.resourceCount; }
    public void setResourceCount(int r) {
        resourceCount = r;
    }
    public int getResourceLimit(){ return this.resourceLimit; }

    public abstract boolean transformDude(WorldModel world, EventScheduler scheduler, ImageStore imageStore);

//    @Override
//    protected boolean nextPositionKind(WorldModel world, Point newPos) {
//        return world.getOccupancyCell(newPos) instanceof Stump;
//    }

    @Override
    protected Class<Stump> nextPositionKind() {
        return Stump.class;
    }

}
