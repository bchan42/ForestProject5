import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public class DudeFull extends Dude {


    public DudeFull(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int resourceLimit, int resourceCount){
        super(id, position, images, animationPeriod, actionPeriod, resourceLimit, resourceCount);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = world.findNearest(getPosition(), new ArrayList<>(List.of(House.class)));

        if (fullTarget.isPresent() && moveTo(world, fullTarget.get(), scheduler)) {
            transformDude(world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), getActionPeriod());
        }
    }

    public boolean transformDude(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        DudeNotFull dude = Functions.createDudeNotFull(getId(), getPosition(), getActionPeriod(), getAnimationPeriod(), getResourceLimit(), getImages());

        world.removeEntity(scheduler, this);

        world.addEntity(dude);
        dude.scheduleActions(scheduler, world, imageStore);

        return true;
    }

    @Override
    protected void moveToAction(WorldModel world, Entity target, EventScheduler scheduler) {
        }
}
