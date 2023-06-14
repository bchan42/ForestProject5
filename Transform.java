import processing.core.PImage;

import java.util.List;
import java.util.Random;

public abstract class Transform extends EntityActivity {

    private int health;
    private final int healthLimit;

    public static final double TREE_ANIMATION_MAX = 0.600;
    public static final double TREE_ANIMATION_MIN = 0.050;
    public static final double TREE_ACTION_MAX = 1.400;
    public static final double TREE_ACTION_MIN = 1.000;
    public static final int TREE_HEALTH_MAX = 3;
    public static final int TREE_HEALTH_MIN = 1;

    public Transform(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int health, int healthLimit) {
        super(id, position, images, animationPeriod, actionPeriod);
        this.health = health;
        this.healthLimit = healthLimit;
    }

    // getters!!
    public int getHealth() {
        return health;
    }
    public void setHealth(int h) {
        health = h;
    }
    public int getHealthLimit() {
        return healthLimit;
    }

    // transformPlant, transformSapling, transformTree
    public abstract boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore);

    public int getIntFromRange(int max, int min) {
        Random rand = new Random();
        return min + rand.nextInt(max-min);
    }

    public double getNumFromRange(double max, double min) {
        Random rand = new Random();
        return min + rand.nextDouble() * (max - min);
    }

//    public boolean transformTree(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
//        if (getHealth() <= 0) {
//            Entity stump = Functions.createStump(Functions.STUMP_KEY + "_" + getId(), getPosition(), imageStore.getImageList(Functions.STUMP_KEY));
//
//            world.removeEntity(scheduler, this);
//
//            world.addEntity(stump);
//
//            return true;
//        }
//        return false;
//    }

}
