import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: Mohammed
 * Date: 09/03/15
 * Time: 5:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class Main extends JComponent implements ActionListener,MouseListener,MouseMotionListener,MouseWheelListener,KeyListener {

    double scale = 1.0;
    int initialNodeSize=20;
    int nodeSize = initialNodeSize;
    int spacing = 20;
    HashMap<String, ArrayList<String>> adjDict = new HashMap<String, ArrayList<String>>();
    ArrayList<Node> nodes = new ArrayList<Node>();
    int draggingNodeIndex=-1;
    Point startDrag;
    int[] windowSize = new int[]{800,600};

    public enum MovementType{
        ORBITAL(),
        LINEAR();
    }


    public Main(){
        Scanner a = null;
        try {
            a = new Scanner(new File(System.getProperty("user.dir")+"/src/smaller map.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        String currentIndex = "";
        String currentLine;
        while (a.hasNext()){
            currentLine=a.nextLine();
            if (currentLine.contains(" ")){
                currentIndex=currentLine.split(" ")[0];
                adjDict.put(currentIndex, new ArrayList<String>());
            }else{
                adjDict.get(currentIndex).add(currentLine);
            }
        }


        JFrame window = new JFrame("Graph");
        window.setSize(windowSize[0],windowSize[1]);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.add(this);
        window.addMouseListener(this);
        window.addMouseMotionListener(this);
        window.addMouseWheelListener(this);
        window.addKeyListener(this);
        window.setVisible(true);
        setupNodes();
        Timer t = new Timer(30,this);
        t.start();
    }

    public void setupNodes(){
        nodes.clear();
        int x,y;
        ArrayList<String> alreadyAdded = new ArrayList<String>();
        Random gen = new Random();
        for(String nodeString:adjDict.keySet()){
            if(alreadyAdded.contains(nodeString)){continue;} //Creating node for the root node
            x=gen.nextInt(getWidth()-nodeSize*2)+nodeSize;
            y=gen.nextInt(getHeight()-nodeSize*2)+nodeSize;
            Color randColor = new Color(gen.nextInt(225)+30,gen.nextInt(225)+30,gen.nextInt(225)+30);
            nodes.add(new Node(nodeString.replace("/wiki/",""),nodeString,x,y, nodeSize /2,randColor,MovementType.ORBITAL));
            alreadyAdded.add(nodeString);

            for (String nodeName:adjDict.get(nodeString)){ //Creating child nodes
                if(alreadyAdded.contains(nodeName)){continue;}
                x=gen.nextInt(getWidth()-nodeSize*2)+nodeSize;
                y=gen.nextInt(getHeight()-nodeSize*2)+nodeSize;
                randColor = new Color(gen.nextInt(225)+30,gen.nextInt(225)+30,gen.nextInt(225)+30);
                nodes.add(new Node(nodeName.replace("/wiki/",""),nodeName,x,y, nodeSize /2,randColor,MovementType.ORBITAL));
                alreadyAdded.add(nodeName);
            }
        }

        Node largestNode=nodes.get(0);
        for(Node node:nodes){
            node.createConnections(adjDict,nodes);
            largestNode=node.getSize()>largestNode.getSize()?node:largestNode;
        }
        Random r = new Random();
        for(Node node:nodes){
            if (node.getSize()==largestNode.getSize()){
                node.setX(getWidth()/2);
                node.setY(getHeight()/2);
                continue;
            }else{
                node.setX(r.nextInt(getWidth()));
                node.setY(r.nextInt(getHeight()));
                node.setTargetCoords((int)largestNode.getX(),(int)largestNode.getY());
            }
        }

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (getWidth()!=windowSize[0] || getHeight()!=windowSize[1] ){
            windowSize[0]=getWidth();
            windowSize[1]=getHeight();
            setupNodes();
        }
        for(Node node:nodes){
            node.update();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.darkGray);
        g.fillRect(0,0,getWidth(),getHeight());
        for(Node node:nodes){
            node.drawEdges((Graphics2D)g);
        }
        for(Node node:nodes){
            node.drawNode((Graphics2D) g);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        startDrag=e.getPoint();
        for (int i =0; i<nodes.size();i++){
            if (nodes.get(i).collidesWithPoint(startDrag)){
                draggingNodeIndex=i;

                break;
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point endPoint = e.getPoint();
        int deltaX = (int)(endPoint.getX()-startDrag.getX());
        int deltaY = (int)(endPoint.getY()-startDrag.getY());
        if (draggingNodeIndex==-1){ //If no node is being dragged
            for(Node n:nodes) {
                n.setX(n.getX() + deltaX);
                n.setY(n.getY() + deltaY);
            }
        }else{

            nodes.get(draggingNodeIndex).setX(nodes.get(draggingNodeIndex).getX() + deltaX);
            nodes.get(draggingNodeIndex).setY(nodes.get(draggingNodeIndex).getY() + deltaY);
        }
        startDrag=e.getPoint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        scale+=e.getWheelRotation()/(e.getWheelRotation()<0?70.0:100.0);        //REFINE THIS BUT PRETTY MUCH WORKING
        nodeSize=(int)(initialNodeSize*scale);
        if(nodeSize<=2){
            nodeSize=3;
            scale=(double)(nodeSize)/initialNodeSize;
        }

        for(Node n:nodes) {
            int[] delta = distanceFromMouseToNode(e.getPoint(),n);
            double distance = Math.sqrt(Math.pow(delta[0],2)+Math.pow(delta[1],2));
            double angle = Math.atan2(delta[1],delta[0]);
            distance+=(double)(e.getWheelRotation())/(e.getWheelRotation()<0?70.0:100.0)/scale*distance;
            n.setX(e.getPoint().x+(Math.cos(angle)*distance));
            n.setY(e.getPoint().y+(Math.sin(angle)*distance));
            n.setSize(nodeSize);
        }



    }

    private int[] distanceFromMouseToNode(Point mousePos,Node node){
        return new int[]{(int) (node.getX()-mousePos.getX()), (int) (node.getY()-mousePos.getY())};
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        boolean anythingClicked = false;
        for(Node node:nodes){
            if (node.collidesWithPoint(e.getPoint())){
                node.setSelected(true);
                //node.setSelectLocked(true);
                anythingClicked=true;
            }else {
                //node.setSelectLocked(false);
                node.setSelected(false);
            }
        }
        if (!anythingClicked){ //If no node was clicked
            for(Node node:nodes){
                node.setSelected(true);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        draggingNodeIndex=-1;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

        if(e.getKeyChar()==' '){
            for(Node n:nodes){
                for(AnimatedEdge edge:n.getAnimatedEdges()){
                    edge.skip();
                }
            }
        }else if(e.getKeyChar()=='r'){
            Random r = new Random();
            for(Node n:nodes){
                n.setTargetCoords(r.nextInt(getWidth()),r.nextInt(getHeight()));
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
