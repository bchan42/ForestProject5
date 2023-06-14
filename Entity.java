import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public abstract class Entity {
    private final String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;

    public Entity(String id, Point position, List<PImage> images) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
    }

    // getters!!
    public String getId(){ return id; }
    public Point getPosition() {
        return position;
    }
    public void setPosition(Point p){
        position = p;
    }
    public List<PImage> getImages() {
        return images;
    }
    public void setImages(List<PImage> im) {
        images = im;
    }
    public int getImageIndex() {
        return imageIndex;
    }


    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    public String log(){
        return this.getId().isEmpty() ? null :
                String.format("%s %d %d %d", this.getId(), this.getPosition().x, this.getPosition().y, this.getImageIndex());
    }

    public PImage getCurrentImage() {
        return getImages().get(getImageIndex() % getImages().size());
    }

    public void nextImage() {
        this.imageIndex = this.imageIndex + 1;
    }
}
