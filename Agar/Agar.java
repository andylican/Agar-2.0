/**
 * Agar.java
 * Version 1
 * @author Andy Li
 * May 2020
 * Multithreaded program that spawns circles of varying sizes upon pressing space and
 * has them bouncing around randomly. Bigger circles slowly absorb smaller ones when they are in contact.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;

class Agar {
     JFrame frame;
    MyPanel panel;
  
    
    final int WINDOW_SIZE = 1080;
   
    final int FRAME_DURATION = 15;
    private ArrayList<Circle>circles = new ArrayList<Circle>();
    private final int CIRCLE_SIZE = 30;
    private final int STEP = 5;
    private int currentId = 0;
    private char currentLetter;
    private boolean inUse = false;
     public static void main(String[] args) {
        Agar demo = new Agar();
        demo.start();
    }
    
    private void start() {
        frame = new JFrame("Circles");
        panel = new MyPanel();
        
        frame.getContentPane().add(BorderLayout.CENTER, panel);
       
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(WINDOW_SIZE, WINDOW_SIZE);
        frame.addKeyListener(new MyKeyListener());
        animate();
    }
    
    private void animate() {
        
        //black animation
        while(true){
         
            //pause thread execution for the duration of one video frame
            try{
                Thread.sleep(FRAME_DURATION);
            } catch (Exception exc){}
            frame.repaint();
        }
    }
     class CircleThread implements Runnable { 
        public void run() { 
            boolean exists = true;
            int size = (int)(Math.random()*50+10);
            int x = (int)(Math.random()*(WINDOW_SIZE-2*size)+size);
            int y = (int)(Math.random()*(WINDOW_SIZE-2*size)+size);
            int xStep = (int)(Math.random()*10+1);
            int yStep = (int)(Math.random()*10+1);
         
            Circle circle = new Circle(x,y,size,xStep,yStep,currentLetter,currentId+"");
            circles.add(circle);
            while(circle.exists()){
                synchronized(circles) {
                    Thread t = Thread.currentThread();
                    String id = t.getName();
                    int index = 0;
                    int circleIndex = -1;
                    for(int i=0; i<circles.size(); i++) {
                   
                        if(circles.get(i).getId().equals(id)) {
                            circleIndex = index;
                        } 
                        index++;
                    }
                    
                    for(int i=0; i<circles.size(); i++) {
                        Circle c = circles.get(i);
                        if(!c.getId().equals(circle.getId())) {
                            if(circle.collide(c)) {
                                if(circle.getR() > c.getR()) {
                                    circle.gain();
                                    c.drain();
                                    if(!c.exists()) {
                                        circles.remove(i);
                                         if(circleIndex > i)circleIndex--;
                                         i--;
                                    }
                                }
                            }
                        }
                    }
                    
               }
                
                if(circle.getX()  <= 0) {
                    circle.setXStep(-circle.getXStep());
                } else if(circle.getX() >= WINDOW_SIZE) {
                    circle.setXStep(-circle.getXStep());
                } else if(circle.getY() <= 0) {
                     circle.setYStep(-circle.getYStep());
                } else if(circle.getY()  >= WINDOW_SIZE) {
                    circle.setYStep(-circle.getYStep());
                }
                
                circle.move();
            
                //pause thread execution for the duration of one video frame
                try{
                    Thread.sleep(FRAME_DURATION);
                } catch (Exception exc){}
                frame.repaint();
            }
        }
    }    
    
//------------------------------------------------------------------------------    
    class MyPanel extends JPanel {
        public void paintComponent(Graphics g) {
           
            for(int i=0; i<circles.size(); i++) {
                Circle c = circles.get(i);
                 g.setColor(Color.BLACK);
                g.fillOval(c.getX()-(int)(c.getR()),c.getY()-(int)(c.getR()),(int)(c.getR()*2),(int)(c.getR()*2));

            }
           
            
        }
    }
    private class MyKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent keyEvent) {
            char keyChar = keyEvent.getKeyChar();
            currentId++;
            
            Thread myThread = new Thread(new CircleThread(),currentId+"");
            myThread.start();
        }
        
        @Override
        public void keyPressed(KeyEvent keyEvent) {
            /*
            int keyCode = keyEvent.getKeyCode();
            if (keyCode == KeyEvent.VK_LEFT){
                System.out.println("move left");
            } else if (keyCode == KeyEvent.VK_RIGHT){
                System.out.println("move right");
            } else if (keyCode == KeyEvent.VK_ESCAPE){
                System.out.println("Quitting!"); 
                game.dispose(); //close the frame & quit
            }
            */
        }
        
        @Override
        public void keyReleased(KeyEvent keyEvent) {
            
        }
        
    }
}
