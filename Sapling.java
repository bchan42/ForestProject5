import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public class Sapling extends Transform {

    public Sapling(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int health, int healthLimit) {
        super(id, position, images, animationPeriod, actionPeriod, health, healthLimit);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        setHealth(getHealth() + 1);
        if (!transformPlant(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), getActionPeriod());
        }
    }


    public boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        // super.transformTree(world, scheduler, imageStore);
        if (getHealth() <= 0) {
            Entity stump = Functions.createStump(Functions.STUMP_KEY + "_" + getId(), getPosition(), imageStore.getImageList(Functions.STUMP_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(stump);

            return true;

        } else if (getClass().equals(Sapling.class) && getHealth() >= getHealthLimit()) {
            EntityActivity tree = Functions.createTree(Functions.TREE_KEY + "_" + getId(), getPosition(), getNumFromRange(TREE_ANIMATION_MAX, TREE_ANIMATION_MIN), getNumFromRange(TREE_ACTION_MAX, TREE_ACTION_MIN), getIntFromRange(TREE_HEALTH_MAX, TREE_HEALTH_MIN), imageStore.getImageList(Functions.TREE_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(tree);
            tree.scheduleActions(scheduler, world, imageStore);

            return true;
        }

            return false;
        }
}
