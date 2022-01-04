package editor;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.IOException;
import java.lang.Math;

public class Image {
  int width, height;
  static final int maxPixelVal = 255;
  static final int embossVal = 128;
  static final double avgPixelVal = maxPixelVal/2.0;
  public Pixel[][] pixels;

  public Image() {
    pixels = null;
    width = 0;
    height = 0;
  }

  public boolean imageImport(String fileName) {
    if (!fileName.contains(".ppm"))
      return false;

    Scanner fileScan = null;
    try {
      fileScan = new Scanner(new File(fileName));
      // scanner needs to ignore possible "separators"
      fileScan.useDelimiter("(\\s+)(#[^\\n]*\\n)?(\\s*)|(#[^\\n]*\\n)(\\s*)");
    } catch (FileNotFoundException e) {
      return false;
    }

    // parse through file and construct pixels
    String MagicNumber = fileScan.next();
    width = fileScan.nextInt();
    height = fileScan.nextInt();
    pixels = new Pixel[height][width];
    int maxPV = fileScan.nextInt();

    int width_i = 0, height_i = 0;

    while (fileScan.hasNext()) {
      int r = fileScan.nextInt();
      int g = fileScan.nextInt();
      int b = fileScan.nextInt();

      pixels[height_i][width_i] = new Pixel(r, g, b);

      width_i++;
      if (width_i == width) {
        width_i = 0;
        height_i++;
      }
    }

    return true;
  }

  public void imageExport(String fileName) {
    try {
      PrintWriter writer = new PrintWriter(fileName);

      writer.println("P3");
      writer.printf("%d %d\n", width, height);
      writer.println(255);

      for (Pixel[] row : pixels) {
        for (Pixel pixel : row) {
          writer.printf("%d\n%d\n%d\n", pixel.red, pixel.green, pixel.blue);
        }
      }

      writer.close();
    } catch (IOException e) {
      System.out.println("Unable to export file.");
    }
  }

  public void grayscale() {
    for (Pixel[] row : pixels) {
      for (Pixel pixel : row) {
        int newGray = (pixel.red + pixel.green + pixel.blue) / 3;
        pixel.red = newGray;
        pixel.green = newGray;
        pixel.blue = newGray;
      }
    }
  }

  public void invert() {
    for (Pixel[] row : pixels) {
      for (Pixel pixel : row) {
        double redDiff = pixel.red - avgPixelVal;
        pixel.red -= (int)(2*redDiff);
        double greenDiff = pixel.green - avgPixelVal;
        pixel.green -= (int)(2*greenDiff);
        double blueDiff = pixel.blue - avgPixelVal;
        pixel.blue -= (int)(2*blueDiff);
      }
    }
  }

  public void emboss() {
    // Create a reference set of pixels
    Pixel[][] tempPixels = new Pixel[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int newVal = 0;
        if (i == 0 || j == 0) // upper left corner not accessible
          newVal = embossVal;
        else {
          int redDiff = pixels[i][j].red - pixels[i-1][j-1].red;
          int greenDiff = pixels[i][j].green - pixels[i-1][j-1].green;
          int blueDiff = pixels[i][j].blue - pixels[i-1][j-1].blue;
          int maxDiff = redDiff;
          if (Math.abs(greenDiff) > Math.abs(maxDiff))
            maxDiff = greenDiff;
          if (Math.abs(blueDiff) > Math.abs(maxDiff))
            maxDiff = blueDiff;
          newVal = maxDiff + embossVal;
          if (newVal < 0)
            newVal = 0;
          if (newVal > maxPixelVal)
            newVal = maxPixelVal;
        }
        tempPixels[i][j] = new Pixel(newVal, newVal, newVal);
      }
    }
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        pixels[i][j] = tempPixels[i][j];
      }
    }
  }

  public void motionBlur(int blurLength) {
    // Create a reference set of pixels
    Pixel[][] tempPixels = new Pixel[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int total = blurLength;
        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;
        if (j + blurLength > width) // length reaches out of bounds
          total = width - j;
        for (int k = 0; k < total; k++) {
          redSum += pixels[i][j+k].red;
          greenSum += pixels[i][j+k].green;
          blueSum += pixels[i][j+k].blue;
        }
        tempPixels[i][j] = new Pixel(redSum/total, greenSum/total, blueSum/total);
      }
    }
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        pixels[i][j] = tempPixels[i][j];
      }
    }
  }
}
