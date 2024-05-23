package me.flyray.bsin.infrastructure.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;

/**
 * URL转Base64二维码
 * @ClassName QrCodeUtils
 * @Author Blue Email:2113438464@qq.com
 * @Date 2022
 */
public class QrCodeUtils {
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static String creatRrCode(String contents, int width, int height) {
        String base64 = "";

        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, width, height, hints);

            // 1、读取文件转换为字节数组
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedImage image = toBufferedImage(bitMatrix);

            //转换成png格式的IO流
            ImageIO.write(image, "png", out);
            byte[] bytes = out.toByteArray();

            // 2、将字节数组转为二进制
            base64 = Base64.encodeBase64String(bytes).trim();

        } catch (WriterException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return base64;
    }

    /**
     * image流数据处理
     */
    private static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }

//    public static void main(String[] args) {
//        // 测试代码
//        String base64Pic = QrCodeUtils.creatRrCode("http://zf.thxyy.cn/weixinmpPlus/byCodePay/list?dd=JC2101080005&ts=1610080940", 200,200);
//        System.out.println(base64Pic);
//    }
}
