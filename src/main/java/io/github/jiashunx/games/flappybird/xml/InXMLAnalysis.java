package io.github.jiashunx.games.flappybird.xml;

import org.dom4j.Element;

/**
 * 解析XML文件的顶级接口
 */
public interface InXMLAnalysis {
    
    /**
     * 根据给定文档的根节点取得节点中元素
     * @param root 配置文件根节点
     */
    public void xmlAnalysis(Element root);

}
