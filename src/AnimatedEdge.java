import java.awt.*;


public class AnimatedEdge {
    private Node startingNode, endingNode;
	private double[] startingPoint,endingPoint;
	private Color startColor, endColor;
    double[] line=new double[4];
    private int speed;
    private Pulse pulse=null;
    private boolean doneMoving = false;
    private boolean selected=true;
    private boolean reciprocalEdge=false;
    private boolean createdreciprocalEdge = false;


    public AnimatedEdge(Node startingNode, Node endingNode, Color startColor, Color endColor, int speed){
        this.startingNode=startingNode;
        this.endingNode=endingNode;
        this.startingPoint=new double[]{startingNode.getX(),startingNode.getY()};
        this.endingPoint= new double[]{endingNode.getX(),endingNode.getY()};
        this.startColor=startColor;
        this.endColor=endColor;
        this.speed=speed;
        line[0]= (int) startingPoint[0];
        line[1]= (int) startingPoint[1];
        line[2]= (int) startingPoint[0];
        line[3]= (int) startingPoint[1];
    }

    public AnimatedEdge(Node startingNode, Node endingNode, Color startColor, Color endColor, int speed, boolean reciprocalEdge){
        this.startingNode=startingNode;
        this.endingNode=endingNode;
        this.startingPoint=new double[]{startingNode.getX(),startingNode.getY()};
        this.endingPoint= new double[]{endingNode.getX(),endingNode.getY()};
        this.startColor=startColor;
        this.endColor=endColor;
        this.speed=speed;
        this.reciprocalEdge=reciprocalEdge;
        this.doneMoving=true;
        line[0]= (int) startingPoint[0];
        line[1]= (int) startingPoint[1];
        line[2]= (int) startingPoint[0];
        line[3]= (int) startingPoint[1];
    }

    public void update(){
        this.startingPoint=new double[]{startingNode.getX(),startingNode.getY()};
        line[0]=startingPoint[0];
        line[1]=startingPoint[1];
        this.endingPoint= new double[]{endingNode.getX(),endingNode.getY()};
        if ( pulse!=null && !pulse.isDead()){
            pulse.grow();
        }
        if (doneMoving){return;}
        //Moving Line
        double deltaX=endingPoint[0]-line[2];
        double deltaY=endingPoint[1]-line[3];
        if(Math.sqrt(Math.pow(deltaX,2)+Math.pow(deltaY,2))<=speed){
            doneMoving=true;
            if(!reciprocalEdge&&!createdreciprocalEdge){endingNode.addEdge(endingNode,startingNode,endColor,startColor);createdreciprocalEdge=true;}
            if (pulse==null){pulse=new Pulse((int)endingPoint[0],(int)endingPoint[1],endingNode.getSize()*3,3,endColor,endColor);}

        }else{
            double angle =  Math.atan2(deltaY, deltaX);
            line[2]+= speed*Math.cos(angle);
            line[3]+= speed*Math.sin(angle);
        }

    }

    public void offsetEdge(double x, double y){
        startingPoint[0]+=x;
        startingPoint[1]+=y;

        endingPoint[0]+=x;
        endingPoint[1]+=y;
    }

    public void draw(Graphics2D g2){
        if (pulse!=null){pulse.draw(g2);}
        g2.setPaint(new GradientPaint((int)startingPoint[0],(int)startingPoint[1],new Color(startColor.getRed(),startColor.getGreen(),startColor.getBlue(),selected?255:5),(int)endingPoint[0],(int)endingPoint[1],new Color(endColor.getRed(),endColor.getGreen(),endColor.getBlue(),selected?255:5)));
        if(doneMoving){
            g2.drawLine((int)startingPoint[0],(int)startingPoint[1],(int)endingPoint[0],(int)endingPoint[1]);
        }else{
            g2.drawLine((int)line[0],(int)line[1],(int)line[2],(int)line[3]);
        }


    }

    public Node getStartingNode() {
        return startingNode;
    }

    public Node getEndingNode() {
        return endingNode;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void skip(){
        doneMoving=true;
    }

    public boolean equals(AnimatedEdge line2){

        return startingPoint.equals(line2.startingPoint) && endingPoint.equals(line2.endingPoint);

    }
}
