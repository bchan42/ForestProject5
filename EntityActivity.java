import java.util.List;

import processing.core.PImage;

public abstract class EntityActivity extends EntityAnimation {
    private final double actionPeriod;

    public EntityActivity(String id, Point position, List<PImage> images, double animationPeriod,  double actionPeriod)
    {
        super(id, position, images, animationPeriod);
        this.actionPeriod = actionPeriod;
    }

    public double getActionPeriod() { return this.actionPeriod;}

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), actionPeriod);
        scheduler.scheduleEvent(this, Functions.createAnimationAction(this, 0), getAnimationPeriod());
    }

    protected abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

}
