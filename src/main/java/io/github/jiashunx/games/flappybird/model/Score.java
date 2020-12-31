package io.github.jiashunx.games.flappybird.model;

import java.awt.Graphics;

import javax.swing.ImageIcon;

import org.dom4j.Element;

import io.github.jiashunx.games.flappybird.xml.InXMLAnalysis;
import io.github.jiashunx.games.flappybird.xml.XMLRoot;

/**
 * 积分
 */
public class Score implements InXMLAnalysis, InDrawImage {

	private int score = 0;
	
	private String[] numsUrl = new String[10];
	
	private ImageIcon[] numIcons = new ImageIcon[1];
	
	private int x = 20, y = 40, space = 5;
	
	public Score() {
		xmlAnalysis(XMLRoot.getConfigRootElement());
	}
	
	@Override
	public void drawImage(Graphics g) {
		int spp = 0; // 每个图标前面所有图标的宽度和
		for (int i = 0; i < numIcons.length; i++) {
			if (i > 0) {
				spp += numIcons[i - 1].getIconWidth();
			}
			g.drawImage(numIcons[i].getImage(), x + spp + i * space, y, null);
		}
	}
	
	@Override
	public void xmlAnalysis(Element root) {
		Element scoreNode = root.element("FlappyBird").element("model").element("Score");
		for (int i = 0; i < numsUrl.length; i++) {
			StringBuilder uuu = new StringBuilder("png_num");
			uuu.append(i);
			uuu.append("_url");
			String uu = new String(uuu);
			numsUrl[i] = scoreNode.element(uu).getText();
		}
		numIcons[0] = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(numsUrl[0]));
	}

	/**
	 * 将数字转换为相应的图标
	 * @param cs
	 */
	private void exchange(char[] cs) {
		if (null != cs && cs.length > 0) {
			numIcons = new ImageIcon[cs.length];
			for (int i = 0; i < cs.length; i++) {
				numIcons[i] = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(numsUrl[cs[i] - 48]));
			}
		}
	}
	
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		char[] cs = String.valueOf(score).toCharArray();
		exchange(cs);
		this.score = score;
	}

}
