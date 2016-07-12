import javax.imageio.ImageIO;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by shuorenwang on 2016-03-01.
 */
public class GameLoop extends Applet implements Runnable, KeyListener {

    public int x, y;  //x,y-coordinate
    public int pre_x, pre_y;     //x,y-coordinate before every move
    public static final int WIDTH = 854;
    public static final int HEIGHT = 480;
    public static final int GROUND_LEVEL = 400;
    public static final int CLOUD_WIDTH = WIDTH / 6;
    public static final int CLOUD_HEIGHT = HEIGHT / 3;
    public static final int LIZARD_WIDTH = WIDTH / 10;
    public static final int LIZARD_HEIGHT = HEIGHT / 5;

    // The object we will use to write with instead of the standard screen graphics
    public Graphics bufferGraphics;
    // The image that will contain everything that has been drawn on bufferGraphics
    public Image offscreen;

    public boolean jump, down, left, right, force_left, force_right, force_jump;

    public BufferedImage background, cloud[], wr[], wl[], lizard;
    public int clouds_data[][];  // x-cord,y-cord,width,height

    //animation counter        ?????????????
    public int counter;
    public int counter_left;

//        public double jump_step = 4;

    public int score;


    //previous direction; true=right; false=left
    public boolean pre, obstacle, reach_obstacle_level;
    int obstacle_level;


    //EFFECTS: load Images
    public void loadImages() {
        try {
            //read background picture
            background = ImageIO.read(new File("/Users/shuorenwang/Documents/CPSC210/IntelliJ/Practice/Game/pic/background.png"));

            //read cloud pictures
               cloud[0] = ImageIO.read(new File("/Users/shuorenwang/Documents/CPSC210/IntelliJ/Practice/Game/pic/cloud-1.png"));
               cloud[1] = ImageIO.read(new File("/Users/shuorenwang/Documents/CPSC210/IntelliJ/Practice/Game/pic/cloud-2.png"));
               cloud[2] = ImageIO.read(new File("/Users/shuorenwang/Documents/CPSC210/IntelliJ/Practice/Game/pic/cloud-3.png"));

            //read lizard walking to the right pictures
            for (int i = 0; i < wr.length; i++) {
                wr[i] = ImageIO.read(new File("/Users/shuorenwang/Documents/CPSC210/IntelliJ/Practice/Game/pic/frame_" + i + ".gif"));
            }

            //read lizard walking to the left pictures
            for (int i = 0; i < wl.length; i++) {
                wl[i] = ImageIO.read(new File("/Users/shuorenwang/Documents/CPSC210/IntelliJ/Practice/Game/pic/frame_" + i + "_left.gif"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCloudsData() {
        clouds_data = new int[cloud.length][4];

        for (int i = 0; i < cloud.length; i++) {
            //set clouds y coordinate
            clouds_data[i][1] = 224;                         //HEIGHT - CLOUD_HEIGHT;
            //set clouds width and height
            clouds_data[i][2] = CLOUD_WIDTH;
            clouds_data[i][3] = CLOUD_HEIGHT;
        }
        //set cloud x-coord
        clouds_data[0][0] = WIDTH / 2;
        clouds_data[1][0] = 100;
        clouds_data[2][0] = WIDTH * 4 / 5;

    }


    //EFFECTS: if a direction is blocked , set the direction to false
    // clouds_data[i][0]:x-coordinate
    // clouds_data[i][1]:y-coordinate
    // clouds_data[i][2]:cloud width
    // clouds_data[i][3]:cloud height
    // return false if obsticle on L/R, return true otherwise
    public void boundaryCheck() {

        //100=clouds_data[i][0];HEIGHT-CLOUD_HEIGHT =clouds_data[i][1];HEIGHT=clouds_data[i][1]+clouds_data[i][3]
        for (int i = 0; i < cloud.length; i++) {

            int cloud_top=clouds_data[i][1];
            int cloud_buttom=clouds_data[i][1]+clouds_data[i][3];
            int cloud_left=clouds_data[i][0];
            int cloud_right=clouds_data[i][0]+clouds_data[i][2];

            int lizard_left=x;
            int lizard_right=x+LIZARD_WIDTH;
            int lizard_top=y;
            int lizard_bottom=y+LIZARD_HEIGHT;


            //move left
            if(pre_x>x &&        //move left
                    ((lizard_bottom>cloud_top && lizard_bottom<cloud_buttom)  ||   // bottom in cloud
                            (lizard_top>cloud_top && lizard_top<cloud_buttom)) && //top in cloud
                    (lizard_left<=cloud_right && lizard_left>=cloud_left)) {
                x = pre_x;
                return;
            }

            //move right
            if(pre_x<x &&        //move right
                    ((lizard_bottom>cloud_top && lizard_bottom<cloud_buttom)  ||   // bottom in cloud
                            (lizard_top>cloud_top && lizard_top<cloud_buttom)) && //top in cloud
                    (lizard_right<=cloud_right && lizard_right>=cloud_left)) {
                x = pre_x;
                return;
            }
            //move bottom
            if(pre_y<y &&
                    ((lizard_right<cloud_right && lizard_right>cloud_left)||
                            (lizard_left<cloud_right && lizard_left>cloud_left)) &&
                    (lizard_bottom>=cloud_top && lizard_bottom<=cloud_buttom) ){
                y=pre_y;
            }


            //move top
            if(pre_y>y &&
                    ((lizard_right<cloud_right && lizard_right>cloud_left)||
                            (lizard_left<cloud_right && lizard_left>cloud_left)) &&
                    (lizard_top>=cloud_top && lizard_top<=cloud_buttom)) {
                y = pre_y;

            }
        }
    }


    //so far only check for there is obstacle on the ground
    public void groundLevelCheck(){

        if(y>GROUND_LEVEL) {
            y = GROUND_LEVEL;
            return;
        }


        for (int i = 0; i < cloud.length; i++) {

            int cloud_top = clouds_data[i][1];
            int cloud_buttom = clouds_data[i][1] + clouds_data[i][3];
            int cloud_left = clouds_data[i][0];
            int cloud_right = clouds_data[i][0] + clouds_data[i][2];

            int lizard_left = x;
            int lizard_right = x + LIZARD_WIDTH;
            int lizard_top = y;
            int lizard_bottom = y + LIZARD_HEIGHT;

            //move bottom
            if ((lizard_right <= cloud_right && lizard_right >= cloud_left) ||
                            (lizard_left <= cloud_right && lizard_left >= cloud_left)) {
               y=cloud_top-LIZARD_HEIGHT;
            }
        }

    }


    @Override
    public void run() {
        cloud = new BufferedImage[3];
        clouds_data = new int[3][4];
        wr = new BufferedImage[12];
        wl = new BufferedImage[12];
        x = 0;
        y = GROUND_LEVEL;
        pre_x=x;
        pre_y=y;


        loadImages();
        setCloudsData();

        counter = 0;                 //??????????????
        counter_left = 0;            //??????????????
        lizard = wr[0];

        while (true) {
            pre_x=x;
            pre_y=y;
  //          boundaryCheck();
            // if lizard is not on the ground-level, jumps down
//            if (obstacle) {
//                if (y < obstacle_level && !jump) {
//                    if (y + 2 > obstacle_level) {
//                        y = obstacle_level;
//                        System.out.println("obstacle level " + y);
//                    } else {
//                        y += 2;
//                        System.out.println("obstacle level down " + y);
//                    }
//
//                }
//            } else

            if (y <= GROUND_LEVEL && !jump  ) {
                pre_y=y;
                y += 8;
                groundLevelCheck();
              //  System.out.println(y);
            }

            // lizard walking to the right animation
            if (right || (jump && pre) || (down && pre)) {
                if (counter < wr.length) {
                    lizard = wr[counter];
                    counter++;
                } else {
                    counter = 0;
                }
            }
            // lizard walking to the left animation
            if (left == true || (jump && !pre) || (down && !pre)) {
                if (counter_left < wl.length) {
                    lizard = wl[counter_left];
                    counter_left++;
                } else {
                    counter_left = 0;
                }
            }


            // lizard walking by keyboard arrows
            if (left) {
                pre_x=x;

                if (left) {
                    x -= 2;
                    pre = false;
                }
                boundaryCheck();
            }
            if (right) {
                pre_x=x;

                if (right) {
                    x += 2;
                    pre = true;
                }
                boundaryCheck();
            //   System.out.println(x);
            }
            if (jump) {
//                jump_step += 0.05;
//                y = y + (int) ((Math.sin(jump_step) + Math.cos(jump_step)) * 8); //y-coordinate : 0 in the top-left corner, when increase y, move down
//
//                if (jump_step > 6.8) {
//                    jump_step = 4;
//                }
                pre_y=y;
                y -= 3;
                boundaryCheck();
            }


            // making sure lizard do not jump below ground-level
            if (y > GROUND_LEVEL) {
                y = GROUND_LEVEL;
              //  boundaryCheck();
            }

            repaint();
            try {
                Thread.sleep(20);    //go to sleep in 10 milliseconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    //         KeyCode=37 =>Left Key  (clockwise)
    //         KeyCode=38 =>Top Key
    //         KeyCode=39 =>Right Key
    //         KeyCode=40 =>Down Key

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case 37:
                left = true;
                break;
            case 38:
                jump = true;
                break;
            case 39:
                right = true;
                break;
            case 40:
                down = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case 37:
                left = false;
                break;
            case 38:
                jump = false;
                break;
            case 39:
                right = false;
                break;
            case 40:
                down = false;
                break;
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {
    }

}
