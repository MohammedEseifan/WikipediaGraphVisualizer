import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Mohammed
 * Date: 19/11/13
 * Time: 8:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class Pulse {
    public int x;
    public int y;
    public int maxSize;
    public Color startColor;
    public Color endColor;
    private int speed;
    private int currentSize = 1;

    private boolean dead = false;

    public Pulse(int startX, int startY, int size, int growSpeed, Color startColor, Color endColor){
        x= startX;
        y=startY;
        speed=growSpeed;
        maxSize=size;
        this.startColor = new Color(startColor.getRed(),startColor.getGreen(),startColor.getBlue(),0);
        this.endColor = endColor;
    }

    public void grow(){
        if (currentSize>=maxSize*2){
            dead=true;
            return;
        }
        currentSize+=speed;
    }

    public int getDrawX(){
        return x-currentSize/2;
    }

    public int getDrawY(){
        return y-currentSize/2;
    }

    public int getSize(){
        return currentSize;
    }

    public boolean isDead(){
        return dead;
    }

    public void draw(Graphics2D g2){
        if (dead){return;}
        float[] dist = {0.0f, 0.25f, 0.8f,1.0f};
        Color[] colors = {this.startColor, this.endColor, this.endColor,this.startColor};

        g2.setPaint(new RadialGradientPaint(this.x,this.y,this.maxSize,dist,colors, MultipleGradientPaint.CycleMethod.REFLECT));
        g2.drawOval(this.getDrawX(),this.getDrawY(),this.getSize(),this.getSize());
    }


}
