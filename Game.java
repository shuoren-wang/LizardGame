import java.awt.Graphics;
import java.applet.Applet;
/**
 * Created by shuorenwang on 2016-03-01.
 */
public class Game extends GameLoop{

    public void init(){
        setSize(WIDTH,HEIGHT);
        Thread th=new Thread(this);
        th.start();

        //Create an offscreen image to draw on
        offscreen=createImage(WIDTH,HEIGHT);
        //everything drawn by bufferGraphics will be
        // written on the offscreen image
        bufferGraphics=offscreen.getGraphics();

        addKeyListener(this);

    }

    public void paint(Graphics g){
        //clear bufferGraphic itself,Wipe off everything drawn before
        bufferGraphics.clearRect(0,0,WIDTH,HEIGHT);

        //Draw background
        bufferGraphics.drawImage(background,0,0,WIDTH,HEIGHT,this);

        //draw lizard walking animation to the offscreen to offscreen
        bufferGraphics.drawImage(lizard,x,y,LIZARD_WIDTH,LIZARD_HEIGHT,this);

        //Draw foreground clouds--obstacles
        for (int i=0; i<cloud.length;i++){
          //  bufferGraphics.drawImage(cloud[i],clouds_data[i][0],clouds_data[i][1],clouds_data[i][2],clouds_data[i][3],this);
            bufferGraphics.fillRect(clouds_data[i][0],clouds_data[i][1],clouds_data[i][2],clouds_data[i][3]);
        }

        //Draw score
        bufferGraphics.drawString("SCORE: "+Integer.toString(score),5,15);

        //draw the offscreen image to the screen like a normal image
        g.drawImage(offscreen,0,0,this);
    }

    // part of double-buffering
    // update() is called automatically when repaint() is called
    //EFFECTS: cause the applet not to first wipe off previous drawing
    //         but to immediately repaint.(the wiping off causes flickering)
    public void update(Graphics g){
        paint(g);
    }

}
