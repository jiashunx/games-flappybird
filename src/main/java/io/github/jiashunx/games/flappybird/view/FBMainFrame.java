package io.github.jiashunx.games.flappybird.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.LinkedList;

import javax.swing.JFrame;

import org.dom4j.Element;

import io.github.jiashunx.games.flappybird.controller.FBListener;
import io.github.jiashunx.games.flappybird.model.Bird;
import io.github.jiashunx.games.flappybird.model.FBImgIcon;
import io.github.jiashunx.games.flappybird.model.Pipe;
import io.github.jiashunx.games.flappybird.model.Score;
import io.github.jiashunx.games.flappybird.xml.InXMLAnalysis;
import io.github.jiashunx.games.flappybird.xml.XMLRoot;

public class FBMainFrame extends JFrame implements InXMLAnalysis {

    private static final long serialVersionUID = 1L;

    private String     frameTitle;             // 主窗体标题
    
    private int frameWidth, frameHeight;   // 主窗体宽度高度
    
    private FBImgIcon[] bgImages = new FBImgIcon[3];       // 背景图片对象
    
    private FBImgIcon[] landImages = new FBImgIcon[3];     // 土地图片对象
    
    private FBImgIcon titleImage, playImage, rankImage;    // 标题，开始，排名图片对象
    
    private FBImgIcon readyImage, tabImage;     // 准备界面准备、点击图片对象
    
    private FBImgIcon overImage; // 结束界面图片对象
    
    private Bird bird = new Bird(this, null); // 游戏中的
    
    private LinkedList<Pipe> pipes = new LinkedList<Pipe>(); //游戏中的管道对链表
    
    /**
     * 游戏状态：
     * 未准备状态 ------to 准备状态 to-------结束状态 to---|
     *                       |                          |
     *                       |                          |
     *                       |--------to 开始状态 -------|
     */
    private boolean gamePrepared = false; // 游戏是否准备就绪，为false为尚未准备就绪，为true表示就绪
    private boolean gameStart = false;    // 游戏是否开始，为false为尚未开始，为true则表示已经开始
    private boolean gameOver = false;     // 游戏是否结束，为false为尚未结束，为true则表示已经结束
    
    private Score score = new Score();     // 积分对象
    
    public FBMainFrame() {
        initMainFrame();
    }
    
    /**
     * 开始游戏
     */
    public void startGame() {
        repaint();
    }
    
    @Override
    public void paint(Graphics g) {
        //创建一个跟窗体一样的内存图片
        BufferedImage buffer = new BufferedImage(this.getWidth(), this.getHeight()
                                                        , BufferedImage.TYPE_INT_RGB);
        //得到内存图片的画布对象
        Graphics g2 = buffer.getGraphics();
        /**
                  * 向内存中画图
         */
        //绘制背景、土地
        for (FBImgIcon icon : bgImages) {
            icon.drawImage(g2);
        }
        if (!gamePrepared) {       // 游戏尚未就绪时绘制标题开始排名部分
            titleImage.drawImage(g2);
            playImage.drawImage(g2);
            rankImage.drawImage(g2);
        } else if (!gameStart) {   // 游戏就绪，等待用户在tab区域点击即可开始
            readyImage.drawImage(g2);
            tabImage.drawImage(g2);
            score.drawImage(g2);
        } else {                   // 游戏开始或结束
            for (Pipe pipe : pipes) {
                pipe.drawImage(g2);
            }
            score.drawImage(g2);
        }
        //绘制小鸟
        bird.drawImage(g2);
        if (gameOver) {
            overImage.drawImage(g2);
            playImage.drawImage(g2);
            rankImage.drawImage(g2);
        }
        for (FBImgIcon icon : landImages) { // 先绘制管道再绘制土地
            icon.drawImage(g2);
        }
        g.drawImage(buffer, 0, 0, null);
    }
    
    @Override
    public void xmlAnalysis(Element root) {
        Element fbMainFrameNode = root.element("FlappyBird").element("view").element("FBMainFrame");
        frameTitle = fbMainFrameNode.element("frame_title").getText();
        frameWidth = Integer.valueOf(fbMainFrameNode.element("frame_width").getText());
        frameHeight = Integer.valueOf(fbMainFrameNode.element("frame_height").getText());
        int bgWidth = Integer.valueOf(fbMainFrameNode.element("png_bg_width").getText());
        int bgHeight = Integer.valueOf(fbMainFrameNode.element("png_bg_height").getText());
        int landWidth = Integer.valueOf(fbMainFrameNode.element("png_land_width").getText());
        int landHeight = Integer.valueOf(fbMainFrameNode.element("png_land_height").getText());
        int titleWidth = Integer.valueOf(fbMainFrameNode.element("png_title_width").getText());
        int titleHeight = Integer.valueOf(fbMainFrameNode.element("png_title_height").getText());
        int playWidth = Integer.valueOf(fbMainFrameNode.element("png_play_width").getText());
        int playHeight = Integer.valueOf(fbMainFrameNode.element("png_play_height").getText());
        int rankWidth = Integer.valueOf(fbMainFrameNode.element("png_rank_width").getText());
        int rankHeight = Integer.valueOf(fbMainFrameNode.element("png_rank_height").getText());
        int readyWidth = Integer.valueOf(fbMainFrameNode.element("png_ready_width").getText());
        int readyHeight = Integer.valueOf(fbMainFrameNode.element("png_ready_height").getText());
        int overWidth = Integer.valueOf(fbMainFrameNode.element("png_over_width").getText());
        int overHeight = Integer.valueOf(fbMainFrameNode.element("png_over_height").getText());
        int tabWidth = Integer.valueOf(fbMainFrameNode.element("png_tab_width").getText());
        int tabHeight = Integer.valueOf(fbMainFrameNode.element("png_tab_height").getText());
        /*String bgUrl = fbMainFrameNode.element("png_bg_url").getText();
        String landUrl = fbMainFrameNode.element("png_land_url").getText();
        String titleUrl = fbMainFrameNode.element("png_title_url").getText();
        String playUrl = fbMainFrameNode.element("png_play_url").getText();
        String rankUrl = fbMainFrameNode.element("png_rank_url").getText();
        String readyUrl = fbMainFrameNode.element("png_ready_url").getText();
        String overUrl = fbMainFrameNode.element("png_over_url").getText();
        String tabUrl = fbMainFrameNode.element("png_tab_url").getText();*/
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL bgUrl = loader.getResource(fbMainFrameNode.element("png_bg_url").getText());
        URL landUrl = loader.getResource(fbMainFrameNode.element("png_land_url").getText());
        URL titleUrl = loader.getResource(fbMainFrameNode.element("png_title_url").getText());
        URL playUrl = loader.getResource(fbMainFrameNode.element("png_play_url").getText());
        URL rankUrl = loader.getResource(fbMainFrameNode.element("png_rank_url").getText());
        URL readyUrl = loader.getResource(fbMainFrameNode.element("png_ready_url").getText());
        URL overUrl = loader.getResource(fbMainFrameNode.element("png_over_url").getText());
        URL tabUrl = loader.getResource(fbMainFrameNode.element("png_tab_url").getText());
        for (int i = 0; i < bgImages.length; i++) {
            bgImages[i] = new FBImgIcon(this, bgUrl, bgWidth * i, 0, bgWidth, bgHeight);
        }
        for (int i = 0; i < landImages.length; i++) {
            landImages[i] = new FBImgIcon(this, landUrl, landWidth * i, frameHeight - landHeight
                    , landWidth, landHeight);
        }
        int aa = frameHeight / 4;
        titleImage = new FBImgIcon(this, titleUrl, (frameWidth - titleWidth) / 2, aa - titleHeight / 2
                                                                    , titleWidth, titleHeight);
        playImage = new FBImgIcon(this, playUrl, frameWidth / 2 - playWidth - 1, aa * 2 - playHeight / 2
                                                                    , playWidth, playHeight);
        rankImage = new FBImgIcon(this, rankUrl, frameWidth / 2 + 1, aa * 2 - rankHeight / 2
                                                                    , rankWidth, rankHeight);
        readyImage = new FBImgIcon(this, readyUrl, (frameWidth - readyWidth) / 2, aa - readyHeight / 2
                                                                    , readyWidth, readyHeight);
        overImage = new FBImgIcon(this, overUrl, (frameWidth - overWidth) / 2, aa
                                                                    , overWidth, overHeight);
        tabImage = new FBImgIcon(this, tabUrl, (frameWidth - tabWidth) / 2, aa + readyHeight
                                                                    , tabWidth, tabHeight);
    }
    
    private void addListener() {
        FBListener listener = new FBListener(this);
        this.addMouseListener(listener);
        this.addMouseMotionListener(listener);
        new Thread(listener).start();
    }
    
    private void move() {
        bird.setX((frameWidth - bird.getWidth()) / 2);
        bird.setY(frameHeight / 4 + titleImage.getHeight());
        new Thread(bird).start();
    }

    /**
     * 初始化游戏主窗体
     */
    private void initMainFrame() {
        xmlAnalysis(XMLRoot.getConfigRootElement());
        move();
        addListener();
        this.setTitle(frameTitle);
        this.setSize(new Dimension(frameWidth, frameHeight));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
    }

    
    public boolean isGamePrepared() {
        return gamePrepared;
    }

    public void setGamePrepared(boolean gamePrepared) {
        this.gamePrepared = gamePrepared;
    }

    public boolean isGameStart() {
        return gameStart;
    }

    public void setGameStart(boolean gameStart) {
        this.gameStart = gameStart;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public FBImgIcon getPlayImage() {
        return playImage;
    }

    public FBImgIcon getRankImage() {
        return rankImage;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public FBImgIcon[] getBgImages() {
        return bgImages;
    }

    public FBImgIcon[] getLandImages() {
        return landImages;
    }

    public Bird getBird() {
        return bird;
    }

    public FBImgIcon getReadyImage() {
        return readyImage;
    }

    public FBImgIcon getOverImage() {
        return overImage;
    }

    public FBImgIcon getTabImage() {
        return tabImage;
    }

    public LinkedList<Pipe> getPipes() {
        return pipes;
    }

    public Score getScore() {
        return score;
    }
    
}
