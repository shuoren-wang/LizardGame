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
    public static final int WIDTH = 854;
    public static final int HEIGHT = 480;
    public static final int GROUND_LEVEL = HEIGHT * 5 / 6;
    public static final int CLOUD_WIDTH = WIDTH / 10;
    public static final int CLOUD_HEIGHT = HEIGHT / 3;
    public static final int LIZARD_WIDTH = WIDTH / 10;
    public static final int LIZARD_HEIGHT = HEIGHT / 5;

    // The object we will use to write with instead of the standard screen graphics
    public Graphics bufferGraphics;
    // The image that will contain everything that has been drawn on bufferGraphics
    public Image offscreen;

    public boolean jump, down, left, right, force_left, force_right, force_jump;

    public BufferedImage background, cloud[], w[], wl[], lizard;
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
            //   cloud[0] = ImageIO.read(new File("/Users/shuorenwang/Documents/CPSC210/IntelliJ/Practice/Game/pic/cloud-1.png"));
            //   cloud[1] = ImageIO.read(new File("/Users/shuorenwang/Documents/CPSC210/IntelliJ/Practice/Game/pic/cloud-2.png"));
            //   cloud[2] = ImageIO.read(new File("/Users/shuorenwang/Documents/CPSC210/IntelliJ/Practice/Game/pic/cloud-3.png"));

            //read lizard walking to the right pictures
            for (int i = 0; i < w.length; i++) {
                w[i] = ImageIO.read(new File("/Users/shuorenwang/Documents/CPSC210/IntelliJ/Practice/Game/pic/frame_" + i + ".gif"));
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
            clouds_data[i][1] = HEIGHT - CLOUD_HEIGHT;
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
    // clouds_data[i][2]:cloud height
    public void notBlocked(int x, int y) {
        force_left = true;
        force_right = true;
        force_jump = true;
        obstacle = false;

//        for (int i = 0; i < cloud.length; i++) {
//            //Lizard next to an obstacle on the left
//            if (x+LIZARD_WIDTH > clouds_data[i][0] &&
//                    ((y>clouds_data[i][1] && y<clouds_data[i][1]+clouds_data[i][3])||
//                            (y+LIZARD_HEIGHT>clouds_data[i][1] && y+LIZARD_HEIGHT<clouds_data[i][1]+clouds_data[i][3])))
//                force_right=false;
//            //Lizard next to an obstacle on the right
//            if (x < clouds_data[i][0]+clouds_data[i][2] &&
//                    ((y>clouds_data[i][1] && y<clouds_data[i][1]+clouds_data[i][3])||
//                            (y+LIZARD_HEIGHT>clouds_data[i][1] && y+LIZARD_HEIGHT<clouds_data[i][1]+clouds_data[i][3])))
//                force_left=false;
//            //Lizard right below an obstacle
//            if (y < clouds_data[i][1]+clouds_data[i][3] &&
//                    ((x>clouds_data[i][0] && x<clouds_data[i][2])||
//                            (x+LIZARD_WIDTH>clouds_data[i][0] && x+LIZARD_WIDTH<clouds_data[i][2])))
//                force_jump=false;
//        }


        //100=clouds_data[i][0];HEIGHT-CLOUD_HEIGHT =clouds_data[i][1];HEIGHT=clouds_data[i][1]+clouds_data[i][3]
        for (int i = 0; i < cloud.length; i++) {
            //Lizard next to an obstacle on the left
            if ((x + LIZARD_WIDTH > clouds_data[i][0] && x + LIZARD_WIDTH < clouds_data[i][0] + CLOUD_WIDTH) &&
                    ((y > HEIGHT - CLOUD_HEIGHT && y < HEIGHT) ||
                            (y + LIZARD_HEIGHT > HEIGHT - CLOUD_HEIGHT && y + LIZARD_HEIGHT < HEIGHT)))
                force_right = false;
            //Lizard next to an obstacle on the right
            if ((x > clouds_data[i][0] && x < clouds_data[i][0] + CLOUD_WIDTH) &&
                    ((y > HEIGHT - CLOUD_HEIGHT && y < HEIGHT) ||
                            (y + LIZARD_HEIGHT > HEIGHT - CLOUD_HEIGHT && y + LIZARD_HEIGHT < HEIGHT)))
                force_left = false;
            if ((x > clouds_data[i][0] && x < clouds_data[i][0] + CLOUD_WIDTH) ||
                    (x + LIZARD_WIDTH > clouds_data[i][0] && x + LIZARD_WIDTH < clouds_data[i][0] + CLOUD_WIDTH)) {
                obstacle = true;
                obstacle_level = HEIGHT - CLOUD_HEIGHT - LIZARD_HEIGHT;
            }
        }

    }

    @Override
    public void run() {
        cloud = new BufferedImage[3];
        clouds_data = new int[3][4];
        w = new BufferedImage[12];
        wl = new BufferedImage[12];
        x = 0;
        y = GROUND_LEVEL;


        loadImages();
        setCloudsData();

        counter = 0;                 //??????????????
        counter_left = 0;            //??????????????
        lizard = w[0];

        while (true) {
            notBlocked(x, y);
            // if lizard is not on the ground-level, jumps down
            if (obstacle) {
                if (y < obstacle_level && !jump) {
                    if (y + 8 > obstacle_level) {
                        y = obstacle_level;
                        System.out.println("obstacle level " + y);
                    } else {
                        y += 8;
                        System.out.println("obstacle level down " + y);
                    }

                }
            } else if (y <= (GROUND_LEVEL) && !jump) {
                y += 8;
                System.out.println("down ground");
            }

            // lizard walking to the right animation
            if (right || (jump && pre) || (down && pre)) {
                if (counter < w.length) {
                    lizard = w[counter];
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
                notBlocked(x - 2, y);
                if (left) {
                    x -= 2;
                    pre = false;
                }
            }
            if (right) {
                notBlocked(x + 2, y);
                if (right) {
                    x += 2;
                    pre = true;
                }
            }
            if (jump) {
//                jump_step += 0.05;
//                y = y + (int) ((Math.sin(jump_step) + Math.cos(jump_step)) * 8); //y-coordinate : 0 in the top-left corner, when increase y, move down
//
//                if (jump_step > 6.8) {
//                    jump_step = 4;
//                }
                y -= 3;
            }


            // making sure lizard do not jump below ground-level
            if (y > GROUND_LEVEL) {
                y = GROUND_LEVEL;
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

    boolean validMoveCheck(){

        return false;
    }
}
