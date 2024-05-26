package me.flyray.bsin.server.curveexcel;

import com.alibaba.excel.EasyExcel;
import java.math.BigDecimal;

import java.math.BigInteger;
import java.util.*;
import org.junit.Test;

/**
 * @author leonard
 * @date 2023/07/17 16:19
 */
public class EasyExcelTest {

    String PATH = "./";

    private List<CurveData> data() {
        List<CurveData> list = new ArrayList<CurveData>();
        for (int i = 0; i < 10; i++) {
            CurveData data = new CurveData();
            data.setNum(String.valueOf(i));
            data.setDate(new Date());
            data.setMintAmount(BigDecimal.valueOf(1));
            list.add(data);
        }
        return list;
    }

    /**
     * 最简单的写
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link CurveData}
     * <p>
     * 2. 直接写即可
     */
    @Test
    public void simpleWrite() {
        String fileName = PATH + "test1.xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        EasyExcel.write(fileName, CurveData.class).sheet("Mint").doWrite(data());
    }

    /**
     * 根据参数只导出指定列
     * 1. 创建excel对应的实体对象 参照{@link CurveData}
     * 2. 根据自己或者排除自己需要的列
     * 3. 直接写即可
     * @since 2.1.1
     */
    @Test
    public void excludeOrIncludeWrite() {
        String fileName = PATH + "test2.xlsx";
        // 这里需要注意 在使用ExcelProperty注解的使用，如果想不空列则需要加入order字段，而不是index,order会忽略空列，然后继续往后，而index，不会忽略空列，在第几列就是第几列。

        // 根据用户传入字段 假设我们要忽略 date
        Set<String> excludeColumnFiledNames = new HashSet<String>();
        excludeColumnFiledNames.add("date");
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        EasyExcel.write(fileName, CurveData.class).excludeColumnFiledNames(excludeColumnFiledNames).sheet("Mint")
                .doWrite(data());
        fileName = PATH + "test3.xlsx";
        // 根据用户传入字段 假设我们只要导出 date
        Set<String> includeColumnFiledNames = new HashSet<String>();
        includeColumnFiledNames.add("date");
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        EasyExcel.write(fileName, CurveData.class).includeColumnFiledNames(includeColumnFiledNames).sheet("Mint")
                .doWrite(data());
    }

    /**
     * 最简单的读
     * 1. 创建excel对应的实体对象 参照{@link CurveData}
     * 2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link CurveDataListener}
     * 3. 直接读即可
     */
    @Test
    public void simpleRead() {
        String fileName = PATH + "测试.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        // 这里每次会读取3000条数据 然后返回过来 直接调用使用数据就行
//        EasyExcel.read(fileName, DemoData.class, new PageReadListener<DemoData>(dataList -> {
//            for (DemoData demoData : dataList) {
//                System.out.println(JSON.toJSONString(demoData));
//            }
//        })).sheet().doRead();

        // 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
        // 写法3：
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(fileName, CurveData.class, new CurveDataListener()).sheet().doRead();
    }
}

