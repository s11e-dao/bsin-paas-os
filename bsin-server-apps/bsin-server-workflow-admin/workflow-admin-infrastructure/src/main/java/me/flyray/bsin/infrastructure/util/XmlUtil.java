package me.flyray.bsin.infrastructure.util;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class XmlUtil {

    public static void main(String[] args) throws Exception {
        XmlUtil xu = new XmlUtil();
        String xmlString = xu.xmlChangeString("D:\\软件\\qq\\Tencent\\QQ下载文件\\holiday-request.bpmn20.xml");
        System.out.println(xmlString);
    }
    public String xmlChangeString(String fileName) {
        try {
            SAXReader saxReader = new SAXReader();// 新建一个解析类
            //Document tempDocument = saxReader.read(XmlUtil.class.getClassLoader().getResourceAsStream(fileName));// 读入一个文件
            Document tempDocument = saxReader.read(new File(fileName));
            return tempDocument.asXML();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 将字符串string类型转换成xml文件
    public void strChangeXML(String str) throws IOException {
        SAXReader saxReader = new SAXReader();
        Document document;
        try {
            document = saxReader.read(new ByteArrayInputStream(str.getBytes("UTF-8")));
            OutputFormat format = OutputFormat.createPrettyPrint();
            /** 将document中的内容写入文件中 */
            XMLWriter writer = new XMLWriter(new FileWriter(new File("E:\\11111.xml")),format);
            writer.write(document);
            writer.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}