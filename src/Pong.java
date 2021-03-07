/*

<!-- This file implements a simple form of literate programming. By
embedding HTML tags in the Java comments, we produce a single
file which can be compiled by the Java compiler and also can display
a formatted discussion of the program in a web browser.  Most systems
require different filename extensions to accomplish these two task.
Under Unix I use a symbolic link to maintain a single copy of the
file with two names. -->

<center>
<H3>Pong game</h3>

written by mike slattery - jan 2003<br>
literate programming version, mcs - sep 2003
</center>
<p>
<hr></hr>
This program is intended to demonstrate the use of
a KeyListener to obtain keyboard input from a player in a game.
This applet will implement a simple pong game.  The game
will be for two players, each will have a paddle on one
side of the screen and two keys to control the up and down
movement of the paddle.  We'll also display a ball which
will bounce off the top and bottom of the screen and off paddles
and a score for each side which will be updated whenever
the ball gets past a paddle.
<p>

We need to import the <code>applet </code> package and <code>awt </code>
to draw and <code>awt.event </code>
to work with listeners.
<pre>
*/
import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Pong extends Applet implements Runnable {
/*
</pre>
Our Pong class will be the main applet and will also
use the Runnable interface to set up a separate animation
Thread.  The Thread will be stored in the variable anim,
<pre>
*/
   Thread anim;
/*
</pre>
and will be created in start() and signalled to end in
stop().
<pre>
*/
   public void start()
   {
      anim = new Thread(this);
      anim.start();
   }
         
   public void stop()
   {
      anim = null;
   }
/*
</pre>
The run() method provides a basic animation loop.
<pre>
*/
   public void run ()
   {
      while(anim != null)
      {
         updatePaddles();
         updateBall();
      
         repaint();
   
         try
         {
            Thread.sleep(30);
         } catch (InterruptedException e)
            {System.err.println("Sleep catch:"+e);}
      }
   }
/*
</pre>
<hr></hr>
The paddles will be controlled by user input.  The KeyListener
will update four variables
<pre>
*/
   boolean rup = false;
   boolean rdown = false;
   boolean lup = false;
   boolean ldown = false;
/*
</pre>
to indicate which control keys are currently pressed.  The right
player's input is reflected in <code>rup </code> and <code>rdown </code>
and similarly for the left player.
<p>
We extend the KeyAdapter class so that only the keyPressed() and
keyReleased() methods need be implemented.  Each updates the appropriate
boolean variable (thus one sees that the left paddle is controlled
by the A and Z keys and the right by semicolon and period).
<pre>
*/
   private class keyBd extends KeyAdapter
   {
      public void keyPressed (KeyEvent e)
      {
         int code = e.getKeyCode();
         switch (code)
         {
            case KeyEvent.VK_SEMICOLON: rup = true; break;
            case KeyEvent.VK_PERIOD: rdown = true; break;
            case KeyEvent.VK_A: lup = true; break;
            case KeyEvent.VK_Z: ldown = true; break;
         }
      }

      public void keyReleased (KeyEvent e)
      {
         int code = e.getKeyCode();
         switch (code)
         {
            case KeyEvent.VK_SEMICOLON: rup = false; break;
            case KeyEvent.VK_PERIOD: rdown = false; break;
            case KeyEvent.VK_A: lup = false; break;
            case KeyEvent.VK_Z: ldown = false; break;
         }
      }
   }
/*
</pre>
In order to ensure that we receive keyboard
events (for the Listener to react to), we need a minimal mouse handler
to get keyboard focus.
<pre>
*/
   private class mseL extends MouseAdapter
   {
      public void mouseClicked(MouseEvent e)
      {
         requestFocus();
      }
   }
/*
</pre>
<hr></hr>
Next we need to look at how the paddles and ball are updated for
each new frame.
<p>
The current position of the paddles is recorded in the variables
<pre>
*/
   int rpx, rpy;
   int lpx, lpy;
/*
</pre>
In this program the x-coordinates of the paddles don't change during
the game.  The job of the updatePaddles() method is to adjust the paddle
y-coordinates based on the user input boolean variables.  In each case we
only change the position if that will not move the paddle out of the
play area.  The constants
<pre>
*/
   final int PLAY_TOP = 20;
   final int PLAY_BOTTOM = 280;
/*
</pre>
define the limits of paddle and ball movement.  When checking
if a paddle can move down, we compare the y-coordinate + 30,
to get the bottom edge of the paddle.
<pre>
*/
   void updatePaddles()
   {
      if (rup)
      {
         if (rpy > PLAY_TOP + 4)
            rpy -= 4;
      }
      if (rdown)
      {
         if (rpy + 30 < PLAY_BOTTOM - 4)
            rpy += 4;
      }
      if (lup)
      {
         if (lpy > PLAY_TOP + 4)
            lpy -= 4;
      }
      if (ldown)
      {
         if (lpy + 30 < PLAY_BOTTOM - 4)
            lpy += 4;
      }
   }
/*
</pre>
The ball has variables to hold its current position and also
the x and y components of its current velocity.
<pre>
*/
   int bx, by;
   int bdx, bdy;   
/*
</pre>
The updateBall() method will also be responsible to update
the score if the ball gets past a paddle.  The current
right and left scores will be kept in
<pre>
*/
int rpts = 0, lpts = 0;
/*
</pre>
In order to move the ball in each frame, we add the current
velocity to the current position.
<pre>
*/
   void updateBall()
   {
      bx += bdx; by += bdy;
/*
</pre>
If the ball slips past a paddle on either side, the appropriate
score is incremented, the ball is repositioned for a new serve
(with some random variation of the y-coordinate), and the
ball's velocity is set for the serve.
<pre>
*/
      if (bx < 20)
      {
         rpts++;
         bx = 430; by = (int)(70+20*Math.random());
         bdx = -7; bdy = 4;
      }
      if (bx > Pong.FrameWidth-20)
      {
         lpts++;
         bx = 70; by = (int)(70+20*Math.random());
         bdx = 7; bdy = 4;
      }
/*
</pre>
Next we check to see if the ball has hit the top
or bottom.  If so, we simulate a bounce by reversing
the sign of the y-component of the ball's velocity.
Thus, if the ball was going up, it switches to down,
if it was going down, it switches to up.  We adjust the
y-coordinate by the radius of the ball in our comparisons.
<pre>
*/
      if (by - 10 < PLAY_TOP)
         bdy = -bdy;
      if (by + 10 > PLAY_BOTTOM)
      bdy = -bdy;
/*
</pre>
The last step of the ball update is checking to see if it has
hit a paddle.  We need to check x and y coordinates of the ball
and paddles to see if they overlap.  If so, we switch the left/right
motion of the ball by changing the sign of the x-component of the
ball's velocity.
<pre>
*/
      if ((Math.abs(rpx-bx) < 14) && (by > rpy-10) && (by < rpy+40))
         bdx = -bdx;
      if ((Math.abs(lpx-bx) < 14) && (by > lpy-10) && (by < lpy+40))
         bdx = -bdx;

   }
/*
</pre>
<hr></hr>
The paint() method is very straight-forward.  We define constants
for the applet size.
<pre>
*/   
   public static final int FrameWidth = 500;
   public static final int FrameHeight = 350;
/*
</pre>
We can control the look of the score by setting our own
Font (rather than just using the default).  We will initialize
this variable in the init() method.
<pre>
*/   
   Font myFont;
/*
</pre>
We clear the screen,
<pre>
*/
   public void paint (Graphics g)
   {
      g.clearRect(0, 0, FrameWidth, FrameHeight);
/*
</pre>
draw the ball,
<pre>
*/
      g.setColor(Color.red);
      g.fillOval(bx-10, by-10, 20, 20);
/*
</pre>
draw the paddles,
<pre>
*/
      g.setColor(Color.blue);
      g.fillRect(rpx-4, rpy, 8, 30);
      g.fillRect(lpx-4, lpy, 8, 30);
/*
</pre>
and display the scores (using our own Font).
<pre>
*/
      g.setColor(Color.black);
      g.setFont(myFont);
      g.drawString(""+lpts,80,310);
      g.drawString(""+rpts,340,310);
   }
/*
</pre>
In order to provide smooth animation, we use the standard
code for double buffering.
The image is drawn on an offscreen buffer and then copied to
the display screen all at once.
<pre>
*/
   Image buffer;
   Graphics bufgr;

   public void update (Graphics g)
   {
      if (buffer == null)
      {
         buffer = createImage (FrameWidth, FrameHeight);
         bufgr = buffer.getGraphics();
      }
      paint(bufgr);
      g.drawImage (buffer, 0, 0, null);
   }
/*
</pre>
The init() method simply does the basic setup: initializing position and velocity
for the ball and
position for the paddles, defining a Font to display the score, and adding
the listeners.
<pre>
*/
   public void init()
   {
      bx = 70; by = 70;
      bdx = 7; bdy = 3;
      rpx = 450; rpy = 50;
      lpx = 50; lpy = 50;
      myFont = new Font("Courier", Font.BOLD, 24);
      addKeyListener (new keyBd());
      addMouseListener (new mseL());
   }
}
/*
</pre>
*/
