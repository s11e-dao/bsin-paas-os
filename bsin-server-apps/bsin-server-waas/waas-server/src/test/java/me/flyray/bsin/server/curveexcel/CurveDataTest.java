package me.flyray.bsin.server.curveexcel;

import com.alibaba.excel.EasyExcel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 * @author leonard
 * @date 2023/07/17 16:52
 */
public class CurveDataTest {

    String PATH = "./";

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    @Test
    public void testToRead(){
        String fileName = PATH + "dataInspect.json";

        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            List<DeviceInspect> deviceInspects = new ArrayList<DeviceInspect>();
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                System.out.println("line " + line + ": " + tempString);
                List<DeviceInspect> list = new Gson().fromJson(tempString, new TypeToken<List<DeviceInspect>>() {
                }.getType());
                deviceInspects.addAll(list);
                line++;
            }
            reader.close();
            //写入excel
            String excelName = PATH + "deviceInspect.xlsx";
            // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
            // 如果这里想使用03 则 传入excelType参数即可
            EasyExcel.write(excelName, DeviceInspect.class).sheet("模板").doWrite(deviceInspects);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

}
