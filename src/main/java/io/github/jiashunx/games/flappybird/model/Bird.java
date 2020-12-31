package io.github.jiashunx.games.flappybird.model;

import java.awt.Graphics;
import java.util.Calendar;
import java.util.Random;

import javax.swing.ImageIcon;

import org.dom4j.Element;

import io.github.jiashunx.games.flappybird.view.FBMainFrame;
import io.github.jiashunx.games.flappybird.xml.InXMLAnalysis;
import io.github.jiashunx.games.flappybird.xml.XMLRoot;

/**
 * 跳跃的小鸟鸟类对象
 */
public class Bird extends FBImgIcon implements InXMLAnalysis, Runnable {

    private static final long serialVersionUID = 1L;

    private ImageIcon[] imgIcons = new ImageIcon[3];
    
    private int iconIndex = 0;
    
    private enum Colorr {
        RED, BLUE, YELLOW
    }
    
    private Colorr nowColorr = Colorr.RED;
    
    private int birdShift = 3;    // 小鸟在开始界面的速度
    
    private boolean live = true; // 为false时终止线程
    
    public Bird(FBMainFrame frame, String url) {
        super(frame, url);
        confirmColor();
        xmlAnalysis(XMLRoot.getConfigRootElement());
    }

    /*public Bird(FBMainFrame frame, URL url) {
        super(frame, url);
        confirmColor();
        xmlAnalysis(XMLRoot.getConfigRootElement());
    }*/

    @Override
    public void drawImage(Graphics g) {
        g.drawImage(imgIcons[iconIndex].getImage(), x, y, null);
    }
    
    @Override
    public void run() {
        while (live) {
            try { Thread.sleep(100);} catch (InterruptedException e) { e.printStackTrace();}
            if (iconIndex == 2) {
                iconIndex = 0;
            } else {
                iconIndex++;
            }
        }
    }
    
    @Override
    public void xmlAnalysis(Element root) {
        Element birdNode = root.element("FlappyBird").element("model").element("Bird");
        width = Integer.valueOf(birdNode.element("png_bird_width").getText());
        height = Integer.valueOf(birdNode.element("png_bird_height").getText());
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        switch (nowColorr) {
        case RED:
            imgIcons[0] = new ImageIcon(loader.getResource(birdNode.element("png_bird20_url").getText()));
            imgIcons[1] = new ImageIcon(loader.getResource(birdNode.element("png_bird21_url").getText()));
            imgIcons[2] = new ImageIcon(loader.getResource(birdNode.element("png_bird22_url").getText()));
            break;
        case BLUE:
            imgIcons[0] = new ImageIcon(loader.getResource(birdNode.element("png_bird10_url").getText()));
            imgIcons[1] = new ImageIcon(loader.getResource(birdNode.element("png_bird11_url").getText()));
            imgIcons[2] = new ImageIcon(loader.getResource(birdNode.element("png_bird12_url").getText()));
            break;
        case YELLOW:
            imgIcons[0] = new ImageIcon(loader.getResource(birdNode.element("png_bird00_url").getText()));
            imgIcons[1] = new ImageIcon(loader.getResource(birdNode.element("png_bird01_url").getText()));
            imgIcons[2] = new ImageIcon(loader.getResource(birdNode.element("png_bird02_url").getText()));
            break;
        default:
            break;
        }
    }
    
    /**
     * 确定小鸟颜色
     */
    private void confirmColor() {
        Random random = new Random(Calendar.getInstance().getTimeInMillis());
        int a = random.nextInt(3); //0， 1， 2
        switch (a) {
        case 0:
            nowColorr = Colorr.YELLOW;
            break;
        case 1:
            nowColorr = Colorr.BLUE;
            break;
        case 2:
            nowColorr = Colorr.RED;
            break;
        default:
            break;
        }
    }
    
    public int getBirdShift() {
        return birdShift;
    }
    
}
