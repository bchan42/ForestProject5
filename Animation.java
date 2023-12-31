/**
 * An action that can be taken by an entity
 */
public final class Animation implements Action{
    private final Entity entity;
    private final int repeatCount;

    public Animation(Entity entity, int repeatCount) {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler) {
        entity.nextImage();

        if (repeatCount != 1) {
            scheduler.scheduleEvent(entity, Functions.createAnimationAction(this.entity, Math.max(this.repeatCount - 1, 0)), ((EntityAnimation) this.entity).getAnimationPeriod());

        }
    }
}
