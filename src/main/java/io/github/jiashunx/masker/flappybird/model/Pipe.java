package io.github.jiashunx.masker.flappybird.model;

import java.awt.Graphics;
import java.net.URL;

import javax.swing.ImageIcon;

import org.dom4j.Element;

import io.github.jiashunx.masker.flappybird.view.FBMainFrame;
import io.github.jiashunx.masker.flappybird.xml.InXMLAnalysis;
import io.github.jiashunx.masker.flappybird.xml.XMLRoot;

/**
 * 障碍对象(管道)
 */
public class Pipe extends FBImgIcon implements InXMLAnalysis {

    private static final long serialVersionUID = 1L;

    private ImageIcon upIcon, downIcon;        // 每对管道的上下图标
    
    private int verticalSpace = 100;      // 每对管道上下之间的距离
    
    private boolean passed = false;      // 记录管道是否被通过
    
    /**
     * 管道形状
     * -------------- 1(x2,y1)8(x4,y1)----------------
     *                |       |
     *  (1)部分       |       |
     *                |       |
     *       (x1,y2)3-2(x2,y2)--
     *  (2)部分     |          |
     *       (x1,y3)4__________5(x3,y3)
     *                  (5)部分
     *              
     *                  BG
     *                  
     *                  (6)部分
     *               __________
     *       (x1,y4)6          7(x3,y4)
     *  (3)部分     |          |
     *       (x1,y5)8-9(x2,y5)--
     *                |       |
     *  (4)部分       |       |
     *                |       |
     * --------------10(x2,y6)-----------------
     * 
     *                 Land
     * 
     * ----------------------------------------
     * 
     * 共计10个点需要记录
     * 纵坐标共有y1(1) y2(3,2) y3(4,5) y4(6,7) y5(8,9) y6(10) 共6个纵坐标值，y2与y3相差24像素
     * 横坐标有x1(3,4,6,8) x2(1,2,9,10) x3(5,7) 共3个横坐标值，x1与x2相差3像素值
     */
    private int x1, x2, x3, x4, y1, y2, y3, y4, y5, y6;
    
    /**
     *  每次判断碰撞检测时小鸟的位置
     *       (bLeftX,bUpY) ------- (bRightX,bUpY)
     *       |                                  |
     *       |              Bird                |
     *       |                                  |
     *       (bLeftX,bDownY) --- (bRightX,bDownY)
     * 
     */
    private int bLeftX, bRightX, bUpY, bDownY;
    
    public Pipe(FBMainFrame frame, String url) {
        super(frame, url);
        xmlAnalysis(XMLRoot.getConfigRootElement());
    }

    /*public Pipe(FBMainFrame frame, URL url) {
        super(frame, url);
        xmlAnalysis(XMLRoot.getConfigRootElement());
    }*/

    @Override
    public void drawImage(Graphics g) {
        g.drawImage(upIcon.getImage(), x, y, null);
        g.drawImage(downIcon.getImage(), x, y + height + verticalSpace, null);
        //重绘后需要重新计算各个点值
        x1 = x;
        x2 = x1 + 3;
        x3 = x1 + width;
        x4 = x3 - 3;
        y1 = 0;
        y3 = y + height;
        y2 = y3 - 24;
        y4 = y3 + verticalSpace;
        y5 = y4 + 24;
        y6 = frame.getLandImages()[0].getY();
    }
    
    /**
     * 判断管道是否和小鸟碰撞
     * @return false不碰撞，true碰撞
     */
    public boolean crash() {
        calculBird();
        if (bDownY < y2 && (bRightX < x2 || bLeftX > x4))
            return false;
        if (bDownY >= y2 && bDownY < y3 && (bRightX < x1 || bLeftX > x3))
            return false;
        if (bUpY >= y2 && bUpY <= y3 && (bRightX < x1 || bLeftX > x3))
            return false;
        if (bUpY > y3 && bDownY < y4)
            return false;
        if (bDownY >= y4 && bDownY < y5 && (bRightX < x1 || bLeftX > x3))
            return false;
        if (bUpY >= y4 && bUpY <= y5 && (bRightX < x1 || bLeftX > x3))
            return false;
        if (bUpY > y5 && (bRightX < x2 || bLeftX > x4))
            return false;
        
        
        //同上做法
//        if (bDownY < y2) {
//            if (bRightX < x2 || bLeftX > x4)
//                return false;
//        } else if (bDownY <= y3) { // y2<= <=y3
//            if (bRightX < x1 || bLeftX > x3)
//                return false;
//        } else if (bUpY <= y3) {
//            if (bRightX < x1 || bLeftX > x3)
//                return false;
//        } else if (bDownY < y4) {
//            return false;
//        } else if (bDownY <= y5) {
//            if (bRightX < x1 || bLeftX > x3)
//                return false;
//        } else if (bUpY < y5) {
//            if (bRightX < x1 || bLeftX > x3)
//                return false;
//        } else if (bRightX < x2 || bLeftX > x4) {
//            return false;
//        }
        
        return true;
    }
    
    /**
     * 判断管道是否执行和小鸟的碰撞检测
     * @return true执行碰撞检测，false不执行
     */
    public boolean doCrashDetection() {
        Bird bird = frame.getBird();
        if (!passed) {
            if (x < bird.getX() - width) {
                passed = true;
                Score score = frame.getScore();
                int a = score.getScore();
                score.setScore(a + 1);
            }
        }
        if (x < bird.getWidth() + bird.getX() + 1 && x > bird.getX() - width - 1) {
            return true;
        }
        return false;
    }
    
    @Override
    public void xmlAnalysis(Element root) {
        Element pipe = root.element("FlappyBird").element("model").element("Pipe");
        width = Integer.valueOf(pipe.element("png_pipe_width").getText());
        height = Integer.valueOf(pipe.element("png_pipe_height").getText());
        /*String upUrl = pipe.element("pipe_up_url").getText();
        String downUrl = pipe.element("pipe_down_url").getText();*/
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL upUrl = loader.getResource(pipe.element("pipe_up_url").getText());
        URL downUrl = loader.getResource(pipe.element("pipe_down_url").getText());
        upIcon = new ImageIcon(upUrl);
        downIcon = new ImageIcon(downUrl);
    }

    private void calculBird() {
        Bird bird = frame.getBird();
        bLeftX = bird.getX();
        bRightX = bird.getX() + bird.getWidth();
        bUpY = bird.getY();
        bDownY = bird.getY() + bird.getHeight();
    }
    
    public int getVerticalSpace() {
        return verticalSpace;
    }
    
}
