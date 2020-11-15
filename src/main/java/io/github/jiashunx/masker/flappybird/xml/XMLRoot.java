package io.github.jiashunx.masker.flappybird.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 单例实现得到xml文件根节点
 */
public class XMLRoot  {

	private XMLRoot() {
		
	}
	
	public static Element getRootElement(String url) {
		Element root = null;
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(new File(url));
		} catch (DocumentException e) {
			document = null;
		}
		if (null != document) {
			root = document.getRootElement();
		}
		return root;
	}

	public static Element getRootElement(InputStream inputStream) {
        Element root = null;
        SAXReader reader = new SAXReader();
        Document document;
        try {
            document = reader.read(inputStream);
        } catch (DocumentException e) {
            document = null;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (null != document) {
            root = document.getRootElement();
        }
        return root;
    }

	public static Element getConfigRootElement() {
//		return getRootElement("data\\Config.xml");
	    return getRootElement(Thread.currentThread().getContextClassLoader().getResourceAsStream("Config.xml"));
	}
	
}
