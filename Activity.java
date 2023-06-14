/**
 * An action that can be taken by an entity
 */
public final class Activity implements Action {
    private final Entity entity;
    private final WorldModel world;
    private final ImageStore imageStore;

    public Activity(Entity entity, WorldModel world, ImageStore imageStore) {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
    }

    public void executeAction(EventScheduler scheduler) {
        ((EntityActivity) entity).executeActivity(world, imageStore, scheduler);
    }
}
