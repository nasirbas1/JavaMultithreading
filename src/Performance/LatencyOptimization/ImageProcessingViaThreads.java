// The idea of the task is to convert an image which has some white lillies into an image where all the lillies are white
// we will be optimizing for the latency here by breaking it into multithreaded solution later
package Performance.LatencyOptimization;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageProcessingViaThreads {
    public static final String SOURCE_FILE = "./resources/many-flowers.jpg";
    public static final String DESTINATION_FILE = "./out/many-flowers.jpg";
    public static void main(String[] args) throws IOException {
        //Buffered image class contains methods to represent the image as pixels, it also
        // gives helper methods to work on the pixels
        BufferedImage originalImage = ImageIO.read(new File(SOURCE_FILE));
        BufferedImage resultImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        long startTime = System.currentTimeMillis();
//        singleThreadedSolutionRecolor(originalImage, resultImage);
        multiThreadedSolutionRecolor(originalImage, resultImage, 13);
        long endTime = System.currentTimeMillis();
        long duration = endTime-startTime;
        System.out.println("Duration is" + duration);
        File outputFile = new File(DESTINATION_FILE);
        ImageIO.write(resultImage, "jpg", outputFile);
    }

    public static void singleThreadedSolutionRecolor(BufferedImage originalImage, BufferedImage resultImage){
        recolorImage(originalImage, resultImage, 0, 0, originalImage.getWidth(), originalImage.getHeight());
    }

    public static void multiThreadedSolutionRecolor(BufferedImage originalImage, BufferedImage resultImage, int numberOfThreads){
        // Main aim here would be to partition the image and process it in parallel, Break into the no equal to
        // no of threads. Once all the threads are done, we write the buffer to a file.
        List<Thread> threads = new ArrayList<>();
        int width = originalImage.getWidth();
        int height = originalImage.getHeight()/numberOfThreads;
        for(int i =0;i < numberOfThreads; i++){
             final int threadMultiplierFactor = i;
             Thread thread = new Thread(()->{
                 int leftCorner = 0;
                 int topCorner = height * threadMultiplierFactor;
                 recolorImage(originalImage, resultImage, leftCorner, topCorner, width, height);
             });
             threads.add(thread);
        }
        for(Thread thread: threads){
            thread.start();
        }
        for(Thread thread: threads){
            try{
                thread.join();
            }catch (InterruptedException e){

            }
        }
    }

    public static void recolorImage(BufferedImage originalImage, BufferedImage resultImage, int leftCorner, int topCorner,
                                    int width, int height){
        for(int x = leftCorner ; x < leftCorner + width && x < originalImage.getWidth() ; x++) {
            for(int y = topCorner ; y < topCorner + height && y < originalImage.getHeight() ; y++) {
                recolorPixels(originalImage, resultImage, x , y);
            }
        }
    }

    public static void recolorPixels(BufferedImage originalImage, BufferedImage resultImage, int x, int y){
        int rgb = originalImage.getRGB(x,y);
        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);
        int newRed;
        int newGreen;
        int newBlue;
        if(isShadeOfGray(red, green, blue)){
            // if shade of grey convert to purple , which is a shade of red and blue, hence decrease green largely
            // and blue little becasue this shade of blue is tilted towards the red side
            // which is done by increasing red and decreasing green largely and blue a little
            // also check if by increasing or decreasing upper and lower limits of bytes are overflowed
            // increasing and decreasing values are by hit and trial
            newRed = Math.min(255, red + 10);
            newGreen = Math.max(0, green - 80 );
            newBlue = Math.max(0, blue - 20);
        }else {
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }
        int newRGBFromColors = createRGBFromColors(newRed, newGreen, newBlue);
        setRGB(resultImage, x, y, newRGBFromColors);
    }

    public static void setRGB(BufferedImage image, int x, int y, int rgb){
        // populate a pixedl with rgb values with buffered image methods
        image.getRaster().setDataElements(x, y , image.getColorModel().getDataElements(rgb, null));
    }

    public static boolean isShadeOfGray(int red, int green, int blue){
        // check if all colors are similar shades over a distance of 30 by hit & trial
        return Math.abs(red-green) < 30 && Math.abs(red-blue) < 30 && Math.abs(green-blue) < 30;
    }

    public static int createRGBFromColors(int red, int green, int blue){
        int rgb = 0;
        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;
        rgb |= 0xFF000000;
        return rgb;
    }

    public static int getRed(int rgb){
        // mask all other bits and shift the result 2 bytes to the right
        return (rgb & 0x00FF0000) >> 16;
    }

    public static int getGreen(int rgb){
        return (rgb & 0x0000FF00) >> 8;
    }

    public static int getBlue(int rgb){
        return (rgb & 0x000000FF);
    }

}