package sample;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;

/**
 * Created by HastuneMiku on 2017/8/14.
 */
public class Client_TCP extends Thread {
    private int width;
    private int height;
    BufferedImage screenshot;

    private static final int TIMEOUT = 5000;  //设置接收数据的超时时间
    private static final int MAXNUM = 5;      //设置重发数据的最多次数

    public void run() {
        try {
            Socket client = null;
            System.out.println("Connecting server");
            client = new Socket("183.175.12.157", 9001);
            PrintStream out = new PrintStream(client.getOutputStream());
            byte[] buf = null;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while (true) {
                BufferedImage image = getScreenshot();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(image, "png", os);
                buf = os.toByteArray();
                out.println(buf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //截取并压缩
    private BufferedImage getScreenshot() throws AWTException, IOException {
        final Robot robot = new Robot();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        width = (int) dimension.getWidth();
        height = (int) dimension.getHeight();
        screenshot = robot.createScreenCapture(new Rectangle(0, 0, width, height));
        screenshot = resizeFix(640, 320);
        //File outputfile = new File("saved.png");
        // ImageIO.write(screenshot, "png", outputfile);
        return screenshot;
    }

    /**
     * 按照宽度还是高度进行压缩
     *
     * @param w int 最大宽度
     * @param h int 最大高度
     */
    private BufferedImage resizeFix(int w, int h) throws IOException {
        if (width / height > w / h) {
            return resizeByWidth(w);
        } else {
            return resizeByHeight(h);
        }
    }

    /**
     * 以宽度为基准，等比例放缩图片
     *
     * @param w int 新宽度
     */
    private BufferedImage resizeByWidth(int w) throws IOException {
        int h = (int) (height * w / width);
        return resize(w, h);
    }

    /**
     * 以高度为基准，等比例缩放图片
     *
     * @param h int 新高度
     */
    private BufferedImage resizeByHeight(int h) throws IOException {
        int w = (int) (width * h / height);
        return resize(w, h);
    }

    /**
     * 强制压缩/放大图片到固定的大小
     *
     * @param w int 新宽度
     * @param h int 新高度
     */
    private BufferedImage resize(int w, int h) throws IOException {
        // SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        image.getGraphics().drawImage(screenshot, 0, 0, w, h, null); // 绘制缩小后的图
        return image;
    }
}
