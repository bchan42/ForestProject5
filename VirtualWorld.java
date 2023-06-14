import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import processing.core.*;

public final class VirtualWorld extends PApplet {
    private static String[] ARGS;

    private static final int VIEW_WIDTH = 640;
    private static final int VIEW_HEIGHT = 480;
    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 32;

    private static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    private static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;

    private static final String IMAGE_LIST_FILE_NAME = "imagelist";
    private static final String DEFAULT_IMAGE_NAME = "background_default";
    private static final int DEFAULT_IMAGE_COLOR = 0x808080;

    private static final String FAST_FLAG = "-fast";
    private static final String FASTER_FLAG = "-faster";
    private static final String FASTEST_FLAG = "-fastest";
    private static final double FAST_SCALE = 0.5;
    private static final double FASTER_SCALE = 0.25;
    private static final double FASTEST_SCALE = 0.10;

    private String loadFile = "world.sav";
    private long startTimeMillis = 0;
    private double timeScale = 1.0;

    private ImageStore imageStore;
    private WorldModel world;
    private WorldView view;
    private EventScheduler scheduler;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        parseCommandLine(ARGS);
        loadImages(IMAGE_LIST_FILE_NAME);
        loadWorld(loadFile, this.imageStore);

        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH, TILE_HEIGHT);
        this.scheduler = new EventScheduler();
        this.startTimeMillis = System.currentTimeMillis();
        this.scheduleActions(world, scheduler, imageStore);
    }

    public void draw() {
        double appTime = (System.currentTimeMillis() - startTimeMillis) * 0.001;
        double frameTime = (appTime - scheduler.getCurrentTime())/timeScale;
        this.update(frameTime);
        view.drawViewport();
    }

    public void update(double frameTime){
        scheduler.updateOnTime(frameTime);
    }

    // Just for debugging and for P5
    // Be sure to refactor this method as appropriate
    public void mousePressed() { // MAKE HELPER FUNCTION!!
        Point pressed = mouseToPoint();
        System.out.println("CLICK! " + pressed.x + ", " + pressed.y);

        // visualChange(pressed); // works
//        redraw();

        // transformToZombie(pressed); // works but zombies don't move

        spawnZombie(pressed); // works but zombie don't move


        Optional<Entity> entityOptional = world.getOccupant(pressed);
        if (entityOptional.isPresent()) {
            Entity entity = entityOptional.get();
        }
    }

    // changes background tiles
    public void visualChange(Point pressed) {
        Background bubble = new Background("bubble", imageStore.getImageList("swampC")); // create bubble image
        Background swampE = new Background("swampE", imageStore.getImageList("swampE")); // create edge image
        Background swampTL = new Background("swampTL", imageStore.getImageList("swampTL")); // create top left image
        Background swampBL = new Background("swampBL", imageStore.getImageList("swampBL")); // create bottom left image
        Background swampTR = new Background("swampTR", imageStore.getImageList("swampTR")); // create top right image
        Background swampBR = new Background("swampBR", imageStore.getImageList("swampBR")); // create bottom right image

        for (int y = -1; y < 2; y++) {
            for (int x = -1; x < 2; x++) {
                Point around = new Point (pressed.x + x, pressed.y + y);
                if (x == 0 && y == 0)
                    world.setBackgroundCell(pressed, bubble);
                else if (x == -1 && y== -1)
                    world.setBackgroundCell(around, swampTL);
                else if (x == -1 && y == 1)
                    world.setBackgroundCell(around, swampBL);
                else if (x == 1 && y == -1)
                    world.setBackgroundCell(around, swampTR);
                else if (x == 1 && y == 1)
                    world.setBackgroundCell(around, swampBR);
                else {
                    world.setBackgroundCell(around, swampE);
                }
            }
        }
    }

    // transform
    public void transformToZombie(Point pressed) {
        for (int y = -2; y < 3; y++) {
            for (int x = -2; x < 3; x++) {
                Point areaP = new Point(pressed.x + x, pressed.y + y);
                Optional<Entity> occupant = world.getOccupant(areaP);

                if (occupant.isPresent() && occupant.get() instanceof Dude) {
                    Point zombieSpawn = (world.getOccupant(areaP).get()).getPosition();

                    Zombie zombie = Functions.createZombie("zombie", zombieSpawn, 1, 1, imageStore.getImageList("zombie"));
                    world.removeEntity(scheduler, world.getOccupant(areaP).get()); // scheduler??

                    world.addEntity(zombie);
                    zombie.scheduleActions(scheduler, world, imageStore);
                }
            }
        }
    }

    // spawns new entity
    public void spawnZombie(Point pressed) {

        EntityActivity zombie = Functions.createZombie("zombie", pressed, Functions.ZOMBIE_ANIMATION_PERIOD, Functions.ZOMBIE_ACTION_PERIOD, imageStore.getImageList("zombie"));

        world.addEntity(zombie);
        zombie.scheduleActions(scheduler, world, imageStore);
    }


    public void scheduleActions(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        for (Entity entity : world.getEntities()) {
            if (entity instanceof EntityAnimation)
                ((EntityAnimation)entity).scheduleActions(scheduler, world, imageStore);
        }
    }

    public Point mouseToPoint() { // add x int and y int?
        return view.getViewport().viewportToWorld(mouseX / TILE_WIDTH, mouseY / TILE_HEIGHT);

    }

    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP -> dy -= 1;
                case DOWN -> dy += 1;
                case LEFT -> dx -= 1;
                case RIGHT -> dx += 1;
            }
            view.shiftView(dx, dy);
        }
    }

    public static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME, imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }

    public static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        Arrays.fill(img.pixels, color);
        img.updatePixels();
        return img;
    }

    public void loadImages(String filename) {
        this.imageStore = new ImageStore(createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
        try {
            Scanner in = new Scanner(new File(filename));
            imageStore.loadImages(in,this);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public void loadWorld(String file, ImageStore imageStore) {
        this.world = new WorldModel();
        try {
            Scanner in = new Scanner(new File(file));
            Functions.load(world, in, imageStore, createDefaultBackground(imageStore));
        } catch (FileNotFoundException e) {
            Scanner in = new Scanner(file);
            Functions.load(world, in, imageStore, createDefaultBackground(imageStore));
        }
    }



    public void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG -> timeScale = Math.min(FAST_SCALE, timeScale);
                case FASTER_FLAG -> timeScale = Math.min(FASTER_SCALE, timeScale);
                case FASTEST_FLAG -> timeScale = Math.min(FASTEST_SCALE, timeScale);
                default -> loadFile = arg;
            }
        }
    }

    public static void main(String[] args) {
        VirtualWorld.ARGS = args;
        PApplet.main(VirtualWorld.class);
    }

    public static List<String> headlessMain(String[] args, double lifetime){
        VirtualWorld.ARGS = args;

        VirtualWorld virtualWorld = new VirtualWorld();
        virtualWorld.setup();
        virtualWorld.update(lifetime);

        return virtualWorld.world.log();
    }
}
