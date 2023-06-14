import java.util.*;

import processing.core.PImage;

public abstract class EntityAnimation extends Entity {
    private final double animationPeriod;

    public EntityAnimation(String id, Point position, List<PImage> images, double animationPeriod) {
        super(id, position, images);
        this.animationPeriod = animationPeriod;
    }

    public double getAnimationPeriod() {
        return animationPeriod;
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, Functions.createAnimationAction(this, 0), getAnimationPeriod());
    }
}
