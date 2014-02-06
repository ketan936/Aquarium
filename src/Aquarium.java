import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import java.util.Vector;

import javax.swing.ImageIcon;

public class Aquarium extends Frame implements Runnable 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Image aquariumImage, memoryImage;
    Image[] fishImages = new Image[2];
    Thread thread;
    MediaTracker tracker;
    Graphics memoryGraphics;
    int numberFish = 20;
    int sleepTime = 10;
    Vector<Fish> fishes = new Vector<Fish>();
    boolean runOK = true;
  
    Aquarium() 
    {
        setTitle("The Aquarium");

        tracker = new MediaTracker(this);

        fishImages[0] = new ImageIcon(this.getClass().getResource("fish1.gif")).getImage();
        tracker.addImage(fishImages[0], 0);

        fishImages[1] =new ImageIcon(this.getClass().getResource("fish2.gif")).getImage();
        tracker.addImage(fishImages[1], 0);

        aquariumImage =new ImageIcon(this.getClass().getResource("bubbles.gif")).getImage();
        tracker.addImage(aquariumImage, 0);

        try {
            tracker.waitForID(0);
        }catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    
        setSize(aquariumImage.getWidth(this), 
            aquariumImage.getHeight(this));

        setResizable(true);
        setVisible(true);

        memoryImage = createImage(getSize().width, getSize().height);
        memoryGraphics = memoryImage.getGraphics();

        thread = new Thread(this);
        thread.start();
  
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(
                WindowEvent windowEvent){
                    runOK = false;
                    System.exit(0);
                }
            }
        );
    }

    public static void main(String[] args)
    {
        new Aquarium();
    }

    public void run() 
    {
        Rectangle edges = new Rectangle(0 + getInsets().left, 0 
            + getInsets().top, getSize().width - (getInsets().left
            + getInsets().right), getSize().height - (getInsets().top 
            + getInsets().bottom));

        for (int loopIndex = 0; loopIndex < numberFish; loopIndex++){
    
            fishes.add(new Fish(fishImages[0], 
                fishImages[1], edges, this));
            try {
                Thread.sleep(20);
            }
            catch (Exception exp) {
                System.out.println(exp.getMessage());
            }
        }

        Fish fish;

        while (runOK) {
    
            for (int loopIndex = 0; loopIndex < numberFish; 
                loopIndex++){
                fish = (Fish)fishes.elementAt(loopIndex);
                fish.swim();
            }

            try {
                Thread.sleep(sleepTime);
            }
            catch (Exception exp) {
                System.out.println(exp.getMessage());
            }
            repaint();
        }
    }
  
    public void update(Graphics g) 
    {
        memoryGraphics.drawImage(aquariumImage, 0, 0, this);

        for (int loopIndex = 0; loopIndex < numberFish; loopIndex++){
            ((Fish)fishes.elementAt(loopIndex)).drawFishImage
                (memoryGraphics);
        }

        g.drawImage(memoryImage, 0, 0, this);
    }
}

class Fish 
{
    Component tank;
    Image image1;
    Image image2;
    Point location;
    Point velocity;
    Rectangle edges;
    Random random; 

    public Fish(Image image1, Image image2, Rectangle edges, 
        Component tank)
    {
        random = new Random(System.currentTimeMillis());
        this.tank = tank;
        this.image1 = image1;
        this.image2 = image2;
        this.edges = edges;
        this.location = new Point(100 + (Math.abs(random.nextInt()) 
            % 300), 100 + (Math.abs(100 + random.nextInt()) % 100));

        this.velocity = new Point(random.nextInt() % 8, 
            random.nextInt() % 8);
    }

    public void swim() 
    {
        if(random.nextInt() % 7 <= 1){

            velocity.x += random.nextInt() % 4; 

            velocity.x = Math.min(velocity.x, 8);
            velocity.x = Math.max(velocity.x, -8);

            velocity.y += random.nextInt() % 4; 

            velocity.y = Math.min(velocity.y, 8);
            velocity.y = Math.max(velocity.y, -8);
        }
    
        location.x += velocity.x;
        location.y += velocity.y;

        if (location.x < edges.x) {
            location.x = edges.x;
            velocity.x = -velocity.x;
        }

        if ((location.x + image1.getWidth(tank))
            > (edges.x + edges.width)){
            location.x = edges.x + edges.width - 
                image1.getWidth(tank);
            velocity.x = -velocity.x;
        }
    
        if (location.y < edges.y){
            location.y = edges.y;
            velocity.y = -velocity.y;
        }

        if ((location.y + image1.getHeight(tank))
            > (edges.y + edges.height)){
            location.y = edges.y + edges.height - 
                image1.getHeight(tank);
            velocity.y = -velocity.y;
        }
    }
  
    public void drawFishImage(Graphics g)
    {
        if(velocity.x < 0) {
            g.drawImage(image1, location.x, 
                location.y, tank);
        }
        else {
            g.drawImage(image2, location.x, 
                location.y, tank);
        }
    }
}
