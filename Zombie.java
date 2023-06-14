import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Zombie extends Movement {

    public Zombie(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod) {
        super(id, position, images, animationPeriod, actionPeriod);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> dudeTarget = world.findNearest(getPosition(), new ArrayList<>(List.of(Dude.class))); // zombies target dudes

        if (dudeTarget.isPresent()) {
            Point tgtPos = dudeTarget.get().getPosition();

            if (moveTo(world, dudeTarget.get(), scheduler)) {

                EntityActivity zombie = Functions.createZombie(Functions.ZOMBIE_KEY + "_" + dudeTarget.get().getId(), tgtPos, Functions.ZOMBIE_ANIMATION_PERIOD, Functions.ZOMBIE_ACTION_PERIOD, imageStore.getImageList("zombie"));

                world.addEntity(zombie);
                zombie.scheduleActions(scheduler, world, imageStore);

            }
        }

        scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), getActionPeriod());
    }



    @Override
    protected void moveToAction(WorldModel world, Entity target, EventScheduler scheduler) {
        world.removeEntity(scheduler, target);
    }

//    @Override
//    protected boolean nextPositionKind(WorldModel world, Point newPos) {
//        return world.getOccupancyCell(newPos) instanceof House;
//    }

    @Override
    protected Class<House> nextPositionKind() {
        return House.class;
    }

}
