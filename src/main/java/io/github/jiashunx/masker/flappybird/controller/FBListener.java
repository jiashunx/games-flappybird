package io.github.jiashunx.masker.flappybird.controller;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Random;

import io.github.jiashunx.masker.flappybird.model.Bird;
import io.github.jiashunx.masker.flappybird.model.FBImgIcon;
import io.github.jiashunx.masker.flappybird.model.Pipe;
import io.github.jiashunx.masker.flappybird.view.FBMainFrame;

/**
 * 游戏窗体的监听器，控制器
 */
public class FBListener implements MouseListener, MouseMotionListener, Runnable {

    private FBMainFrame frame;
    
    private int x1, x2, x3, x4, y1, y2;
    
    private int x5, x6, y3, y4;
    
    private boolean live = true;
    
    private boolean work = true;
    
    private int bgShift = 2;   // 背景左移速度
    
    private int landShift = 2; // 土地左移速度
    
    private int pipeShift = 2; // 管道左移速度
    
    private int gapLength = 100;  // 管道对之间的距离
    
    private boolean drop = true; // 小鸟处于坠落状态，为false处于上升状态
    
    private long jumpTime = 0l;   // 小鸟上升时上升持续时间
    
    private Long lastJumpTime = null; // 记录小鸟上次开始跳跃的时间点
    
    private Long lastDropTime = null; // 记录小鸟上次开始下降的时间点
    
    private int lastDropY = 0;        // 记录小鸟上次开始下降时的纵坐标值
    
    private boolean jumpFinished = true;     // 记录小鸟的跳跃动作是否完成
    
    private static long jumpLastTime = 400l; //小鸟每次上升持续时间
    
    public FBListener(FBMainFrame frame) {
        this.frame = frame;
        confirmRange();
    }
    
    @Override
    public void run() {
        while (live) {
            frame.repaint();
            try { Thread.sleep(100);} catch (InterruptedException e) { e.printStackTrace();}
            if (work) {
                move();
            }
        }
    }
    
    /**
     * 控制游戏界面画面移动的方法
     */
    private void move() {
        Bird bird = frame.getBird();
        if (!frame.isGamePrepared()) { // 游戏未准备状态控制小鸟移动即可
            if (bird.getX() >= frame.getContentPane().getWidth()) {
                bird.setX(-bird.getWidth());
            } else {
                bird.setX(bird.getX() + bird.getBirdShift());
            }
            return;
        }
        if (!frame.isGameStart()) {     // 游戏处于准备完成状态仍然控制小鸟移动
            moveBgLand();
            return;
        }
        if (!frame.isGameOver()) {      // 游戏处于开始状态控制小鸟土地
            moveBgLand();
            if (drop) {
                birdDrop();
            } else {
                birdJump();
            }
            createPipe();
            crashDetection();
            return;
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (!frame.isGamePrepared()) {  // 游戏处于未准备状态
            boolean canPlay = isInPlayRange(x, y);
            boolean canRank = isInRankRange(x, y);
            if (canPlay) {
                gamePrepare();  // 未准备 -> 准备
            } else if (canRank) {
                gameRank();
            }
            return;
        }
        if (!frame.isGameStart()) {     // 游戏处于准备状态
            if (isInTabRange(x, y)) {
                gameStart();    // 准备 -> 开始
            }
            return;
        }
        if (!frame.isGameOver()) {      // 游戏处于开始状态
                                // 开始 -> 开始/结束
            bJump();
            return;
        }
                                        // 游戏处于结束状态
        boolean canPlay = isInPlayRange(x, y);
        boolean canRank = isInRankRange(x, y);
        if (canPlay) {         // 结束 -> 准备
            frame.setGameStart(false);
            frame.setGameOver(false);
            gamePrepare();
        } else if (canRank) {
            gameRank();
        }
    }
    
    /**
     * 碰撞检测，只有当管道x值 小于等于 小鸟x值+小鸟图片宽度时该管道执行碰撞检测
     */
    private void crashDetection() {
        LinkedList<Pipe> pipes = frame.getPipes();
        for (Pipe pipe : pipes) {
            if (pipe.doCrashDetection()) {
                if (pipe.crash()) {
                    frame.setGameOver(true);
                    break;
                }
            }
        }
    }
    
    /**
     * 改变小鸟状态为上升
     */
    private void bJump() {
        if (!drop) { // 如果小鸟本来的状态就是上升
            lastDropTime = new Long(Calendar.getInstance().getTimeInMillis());
        } else {
            drop = false;
        }
    }
    
    /**
     * 改变小鸟状态为下降
     */
    private void bDrop() {
        drop = true;
    }
    
    /**
     * 小鸟上升
     */
    private void birdJump() {
        Long nowTime = new Long(Calendar.getInstance().getTimeInMillis());
        long ti = 0l; //上升持续的时间
        if (null != lastJumpTime && !jumpFinished) { // 纵坐标发生改变
            ti = nowTime.longValue() - lastJumpTime.longValue();
            Bird bird = frame.getBird();
            int tii = ((int) ti) / 100;
            bird.setY(bird.getY() - (5 * tii - tii * tii / 2));
            insureBirdInRange();
        } else {
            jumpFinished = false;
            lastJumpTime = nowTime;
            jumpTime = jumpLastTime;
        }
        if (ti > jumpTime) {
            jumpFinished = true;
            bDrop();
            lastDropTime = new Long(Calendar.getInstance().getTimeInMillis());
            lastDropY = frame.getBird().getY();
        }
    }
    
    /**
     * 小鸟坠落
     */
    private void birdDrop() {
        Long nowTime = new Long(Calendar.getInstance().getTimeInMillis());
        if (null == lastDropTime) {
            lastDropTime = nowTime;
        }
        int ti = (int) (nowTime.longValue() - lastDropTime.longValue()) / 100;
        Bird bird = frame.getBird();
        bird.setY(lastDropY + ti * ti / 2);
        insureBirdInRange();
    }
    
    /**
     * 游戏由准备状态进入开始状态的方法
     */
    private void gameStart() {
        cursorHand(); // 用户鼠标指针变为手型
        initBird();
        
        LinkedList<Pipe> pipes = frame.getPipes();
        while (pipes.size() > 0) {
            pipes.remove(0);
        }
        frame.setGameStart(true);
        addPipe(pipes); //添加第一个管道对
        bJump(); // 开始时小鸟是上升一次
    }
    
    /**
     * 游戏由未准备状态进入准备状态的方法
     */
    private void gamePrepare() {
        initBird();
        frame.getScore().setScore(0); //分数重置
        frame.setGamePrepared(true); // 游戏准备就绪
    }

    private void initBird() {
        //改变小鸟位置
        Bird bird = frame.getBird();
        bird.setX(frame.getFrameWidth() / 4 - bird.getWidth() / 2);
        bird.setY(frame.getFrameHeight() / 2 - bird.getHeight());
    }
    
    /**
     * 查看游戏排名
     */
    private void gameRank() {
        System.out.println("gameRank");
    }
    
    /**
     * 创建管道
     * 管道队列中最后一组管道右侧距离窗体右侧距离在大于设定的管道间距离时创建管道对
     */
    private void createPipe() {
        LinkedList<Pipe> pipes = frame.getPipes();
        if (pipes.size() > 0) {
            Pipe last = pipes.get(pipes.size() - 1);
            if (frame.getWidth() - (last.getX() + last.getWidth()) >= gapLength) {
                addPipe(pipes);
            }
        }
    }
    
    /**
     * 向窗体添加管道对
     * @param pipes
     */
    private void addPipe(LinkedList<Pipe> pipes) {
        Pipe pipe = new Pipe(frame, null);
        pipe.setX(frame.getWidth());
        // 最大y值
        int y1 = 20;
        // 最小y值
        int y2 = -(2 * pipe.getHeight() + pipe.getVerticalSpace() - frame.getHeight()
                                                    + frame.getLandImages()[0].getHeight());
        Random random = new Random(Calendar.getInstance().getTimeInMillis());
        int value = random.nextInt(y1 - y2 + 1);
        while (value < y1) {
            value = random.nextInt(y1 - y2 + 1);
        }
        pipe.setY(value + y2);
        pipes.add(pipe);
    }
    
    /**
     * 移动 背景、土地、管道
     */
    private void moveBgLand() {
        FBImgIcon[] bgImages = frame.getBgImages();
        FBImgIcon[] landImages = frame.getLandImages();
        for (int i = 0; i < bgImages.length; i++) {
            bgImages[i].setX(bgImages[i].getX() - bgShift);
            if (bgImages[i].getX() <= -(bgImages[i].getWidth())) {
                bgImages[i].setX(2 * bgImages[i].getWidth());
            }
        }
        for (int i = 0; i < bgImages.length; i++) {
            landImages[i].setX(landImages[i].getX() - landShift);
            if (landImages[i].getX() <= -(landImages[i].getWidth())) {
                landImages[i].setX(2 * landImages[i].getWidth());
            }
        }
        LinkedList<Pipe> pipes = frame.getPipes();
        Pipe delPipe = null; // 待删除管道对
        for (Pipe pipe : pipes) {
            pipe.setX(pipe.getX() - pipeShift);
            if (pipe.getX() <= -pipe.getWidth()) { // 管道对一旦超出桌面范围就从管道对队列中删除
                delPipe = pipe;
            }
        }
        pipes.remove(delPipe);
    }
    
    /**
     * 确定开始和排名区域边界
     *  (x1, y1) - (x2, y1)       (x3, y1) - (x4, y1)
     *  |      play       |       |      rank       |
     *  (x1, y2) - (x2, y2)       (x3, y2) - (x4, y2)
     *  
     *  (x5, y3) - (x6, y3)
     *  |      tab       |
     *  (x5, y4) - (x6, y4)
     */
    private void confirmRange() {
        FBImgIcon play = frame.getPlayImage();
        FBImgIcon rank = frame.getRankImage();
        FBImgIcon tab = frame.getTabImage();
        y1 = play.getY();
        y2 = y1 + play.getHeight();
        x1 = play.getX();
        x2 = x1 + play.getWidth();
        x3 = rank.getX();
        x4 = x3 + rank.getWidth();
        x5 = tab.getX();
        x6 = x5 + tab.getWidth();
        y3 = tab.getY();
        y4 = y3 + tab.getHeight();
    }
    
    /**
     * 判断坐标(x, y)是否在开始游戏区域
     * @param x
     * @param y
     * @return
     */
    private boolean isInPlayRange(int x, int y) {
        if (x > x1 && x < x2 && y > y1 && y < y2) {
            return true;
        }
        return false;
    }
    
    /**
     * 判断坐标(x, y)是否在查看排名区域
     * @param x
     * @param y
     * @return
     */
    private boolean isInRankRange(int x, int y) {
        if (x > x3 && x < x4 && y > y1 && y <y2) {
            return true;
        }
        return false;
    }
    
    /**
     * 判断坐标(x, y)是否在点击区域
     * @param x
     * @param y
     * @return
     */
    private boolean isInTabRange(int x, int y) {
        if (x > x5 && x < x6 && y > y3 && y < y4) {
            return true;
        }
        return false;
    }
    
    /**
     * 鼠标指针形状设为手型
     */
    private void cursorHand() {
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    
    /**
     * 鼠标指针形状设为默认
     */
    private void cursorDefault() {
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * 确保小鸟在窗体内
     */
    private void insureBirdInRange() {
        Bird bird = frame.getBird();
        if (bird.getY() < 25) {
            bird.setY(25);
        }
        if (bird.getY() + bird.getHeight() > frame.getLandImages()[0].getY()) {
            bird.setY(frame.getLandImages()[0].getY() - bird.getHeight());
            frame.setGameOver(true);
        }
    }
    
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseDragged(MouseEvent e) {}
    
    /**
     * 鼠标移动过程中判断鼠标位置
     * 游戏处于未准备状态时，如果鼠标在开始游戏区域或查看排名区域上，鼠标指针改变为手指指针
     * 游戏处于未开始状态时，如果鼠标在tab区域上，鼠标指针改变为手指指针
     * 游戏处于未结束状态时，鼠标指针变为手指指针
     * 游戏处于结束状态时，，，
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (!frame.isGamePrepared()) { // 游戏尚处于未准备状态
            boolean canPlay = isInPlayRange(x, y);
            boolean canRank = isInRankRange(x, y);
            if (canPlay || canRank) {
                cursorHand();
            } else {
                cursorDefault();
            }
            return;
        }
        if (!frame.isGameStart()) {     // 游戏处于准备完成状态
            if (isInTabRange(x, y)) {
                cursorHand();
            } else {
                cursorDefault();
            }
            return;
        }
//        if (!frame.isGameOver()) {      // 游戏处于开始状态
//            cursorDefault();
//            return;
//        }
        if (frame.isGameOver()) {       // 游戏处于结束状态
            boolean canPlay = isInPlayRange(x, y);
            boolean canRank = isInRankRange(x, y);
            if (canPlay || canRank) {
                cursorHand();
            } else {
                cursorDefault();
            }
        }
    }    
    
}
