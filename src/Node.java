import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Mohammed
 * Date: 09/03/15
 * Time: 1:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class Node {
    private double x,y;
    private int size = 0;
    private String displayName,nodeValue;
    private Color color;
    private boolean selected=true;
    private boolean selectLocked = false;
    private ArrayList<AnimatedEdge> animatedEdges = new ArrayList();
    private int movementSpeed =3;
    private double orbitSpeed =0.2;
    private double orbitRadius;
    private double currentOrbitAngle=0;
    private int orbitMultiplier;
    private int[] targetCoords = new int[]{-50,-50};
    public Main.MovementType movementType;



    public Node(String displayName,String nodeValue,int x,int y,int size,Color color, Main.MovementType movementType){
        this.x = x;
        this.y = y;
        this.size = size;
        this.color= color;
        this.movementType= movementType;
        this.displayName=displayName;
        this.nodeValue=nodeValue;
        if (movementType== Main.MovementType.ORBITAL){
            Random r = new Random();
            this.orbitMultiplier=r.nextBoolean()?1:-1;
        }
    }

    public void createConnections(HashMap<String,ArrayList<String>> adjDict,ArrayList<Node> nodeList){
        if(animatedEdges.size()>0){return;}
        if (!adjDict.containsKey(nodeValue)){return;}
        for(String adjacentNode:adjDict.get(nodeValue)){
            Node endingNode = findNodewithValue(adjacentNode,nodeList);
            if (endingNode!= null){
                animatedEdges.add(new AnimatedEdge(this,endingNode,this.color,endingNode.color,5));

            }
        }
        size*=1+(double)animatedEdges.size()/nodeList.size();
    }

    public void addEdge(Node startingNode, Node endingNode, Color startColor, Color endColor){
        for(AnimatedEdge edge:animatedEdges){
            if (edge.getStartingNode().equals(startingNode) && edge.getEndingNode().equals(endingNode)){
                return;
            }
        }
        animatedEdges.add(new AnimatedEdge(startingNode,endingNode,startColor,endColor,5,true));

    }

    public void update(){

        for(AnimatedEdge edge:animatedEdges){
            edge.update();
        }

        if (targetCoords[0]+targetCoords[1]==-100){return;}
        switch (movementType){

            case ORBITAL:
                x = targetCoords[0]+Math.cos(Math.toRadians(currentOrbitAngle))*orbitRadius;
                y = targetCoords[1]+Math.sin(Math.toRadians(currentOrbitAngle))*orbitRadius;
                currentOrbitAngle+= orbitSpeed*orbitMultiplier;
                break;
            case LINEAR:

                int deltaX= (int) (targetCoords[0]-x);
                int deltaY= (int) (targetCoords[1]-y);

                if(Math.sqrt(Math.pow(deltaX,2)+Math.pow(deltaY,2))<=movementSpeed){
                    targetCoords[0]= (int) x;
                    targetCoords[1]= (int) y;
                }else{
                    double angle =  Math.atan2(deltaY, deltaX);
                    x+= movementSpeed*Math.cos(angle);
                    y+= movementSpeed*Math.sin(angle);
                }
                break;
        }


    }

    public void drawEdges(Graphics2D g){
        for(AnimatedEdge edge:animatedEdges){
            edge.draw(g);
        }
    }

    public void drawNode(Graphics2D g){
        Color temp = new Color(color.getRed(),color.getGreen(),color.getBlue(),selected?255:50);
        g.setColor(temp);
        g.fillOval((int)x - size, (int)y - size, size * 2, size * 2);
        g.setColor(new Color(255,255,255,selected?255:50));
        int stringWidth = g.getFontMetrics().stringWidth(displayName);
        g.drawString(displayName,(int)this.x-stringWidth/2,(int)this.y-size);
        if (selected) {
            g.drawString(String.valueOf(animatedEdges.size()), (int) this.x - g.getFontMetrics().stringWidth(String.valueOf(animatedEdges.size())) / 2, (int) this.y + g.getFontMetrics().getHeight() / 2);
        }
    }

    private Node findNodewithValue(String value,ArrayList<Node> nodeList){

        for (Node currentNode:nodeList){
            if (currentNode.getNodeValue().equals(value)){
                return currentNode;
            }
        }
       return null;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getNodeValue() {
        return nodeValue;
    }

    public void setTargetCoords(int x,int y){
        targetCoords[0]=x;
        targetCoords[1]=y;
        if (movementType== Main.MovementType.ORBITAL) {
            this.orbitRadius = Math.sqrt(Math.pow(targetCoords[0] - this.x, 2) + Math.pow(targetCoords[1] - this.y, 2));
            this.currentOrbitAngle =  Math.toDegrees(Math.atan2(this.y-y,this.x-x));
        }
    }

    public void setX(double x) {
        for (AnimatedEdge edge:animatedEdges){
            edge.offsetEdge(x-this.x,0);
        }
        if (targetCoords[0]!=-50){targetCoords[0]+=x-this.x;}
        this.x = x;
    }

    public void setY(double y) {
        for (AnimatedEdge edge:animatedEdges){
            edge.offsetEdge(0,y-this.y);
        }
        if (targetCoords[1]!=-50){targetCoords[1]+=y-this.y;}
        this.y = y;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean collidesWithPoint(Point p){
        double distance = Math.sqrt(Math.pow(p.getX()-this.x,2)+Math.pow(p.getY()-30-this.y,2));
        return distance<=size*1.5;
    }

    public void setSelected(boolean selected){
        if(selectLocked){return;}

        for(AnimatedEdge edge:animatedEdges){
            edge.setSelected(selected);
            edge.getEndingNode().setHalfSelect(selected);
            edge.getEndingNode().setSelectLocked(true);

        }


    }

    public void setSelectLocked(boolean selectLocked) {
        this.selectLocked = selectLocked;
    }

    public void setHalfSelect(boolean selected){
        this.selected=selected;
    }

    public boolean equals(Node n){
        return this.size==n.getSize() && this.x==n.getX() && this.y==n.getY();
    }

    public ArrayList<AnimatedEdge> getAnimatedEdges() {
        return animatedEdges;
    }
}
