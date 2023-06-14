import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public class DudeNotFull extends Dude {


    public DudeNotFull(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int resourceLimit, int resourceCount){
        super(id, position, images, animationPeriod, actionPeriod, resourceLimit, resourceCount);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(getPosition(), new ArrayList<>(Arrays.asList(Tree.class, Sapling.class)));

        if (target.isEmpty() || !moveTo(world, target.get(), scheduler) || !transformDude(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), getActionPeriod());
        }
    }

    public boolean transformDude(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (getResourceCount() >= getResourceLimit()) {
            DudeFull dude = Functions.createDudeFull(getId(), getPosition(), getActionPeriod(), getAnimationPeriod(), getResourceLimit(), getImages());

            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(dude);
            dude.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    @Override
    protected void moveToAction(WorldModel world, Entity target, EventScheduler scheduler) {
        setResourceCount(getResourceCount() + 1);
        ((Transform)target).setHealth(((Transform)target).getHealth() - 1);
    }
}
