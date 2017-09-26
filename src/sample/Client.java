package sample;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;
import java.nio.Buffer;

/**
 * Created by HastuneMiku on 2017/8/14.
 */
public class Client extends Thread {
    private int width;
    private int height;
    BufferedImage screenshot;

    private static final int TIMEOUT = 5000;  //设置接收数据的超时时间
    private static final int MAXNUM = 5;      //设置重发数据的最多次数

    public void run() {
        try {
            DatagramSocket ds = null;
            System.out.println("Connecting server");
            ds = new DatagramSocket(9001);
            while (true) {
                int tries = 0;
                try {
                    byte[] buf;
                    byte[] data;
                    InetAddress server = InetAddress.getByName("183.175.12.157");
                    BufferedImage image = getScreenshot();
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(image, "png", os);
                    buf = os.toByteArray();
                    System.out.println("开始传输，总长度："+(buf.length));
                    ds.setSoTimeout(TIMEOUT);
                    boolean receivedResponse = false;
//                    if(buf.length>=60000){
//                        //ds.send(dp_send);                                                                            //send
//                        System.out.println("cut");
//                    }
//                    else {
//                        System.out.println("send image directly");
//                        DatagramPacket dp_send = new DatagramPacket(buf, buf.length, server, 3000);
//                        ds.send(dp_send);                                                                                   //send
//                    }

                    //分包传输
                    int numOfBlock=buf.length/50000;
                    int lastSize=buf.length%50000;
                    //System.out.println("numOfBlock:"+numOfBlock+",lastSize:"+lastSize);

                    for(int i=0;i<=numOfBlock;i++){
                        int m_intLength=0;
                        if(i==numOfBlock){
                            m_intLength=lastSize;
                        }else {
                            m_intLength=50000;
                        }
                        data=new byte[m_intLength+16];
                        int place=0;
                        System.arraycopy(toBytes(i),0,data,place,4);
                        place+=4;
                        System.arraycopy(toBytes(numOfBlock),0,data,place,4);
                        place+=4;
                        System.arraycopy(toBytes(m_intLength),0,data,place,4);
                        place+=4;
                        System.arraycopy(toBytes(buf.length),0,data,place,4);
                        place+=4;
                        System.arraycopy(buf,50000*i,data,place,m_intLength);
                        DatagramPacket dp_send=new DatagramPacket(data,data.length,server,3000);
                        ds.send(dp_send);
                        sleep(1);
                        System.out.println("传输第"+i+"块，长度："+data.length);
                    }
                    System.out.println("传输结束-------------------");

                } catch (AWTException | IOException e) {
                    System.out.println("retry "+(tries+1));
                    tries += 1;
                    e.printStackTrace();
                }
                sleep(500);
            }
            //ds.close();
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
        screenshot = resizeFix(1600, 900);
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
    private byte[] toBytes(int i)
    {
        byte[] result = new byte[4];

        result[0] = (byte) (i >> 24);
        result[1] = (byte) (i >> 16);
        result[2] = (byte) (i >> 8);
        result[3] = (byte) (i /*>> 0*/);

        return result;
    }

}
