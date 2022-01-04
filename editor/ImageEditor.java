package editor;

public class ImageEditor {
  public static void main(String[] args) {
    // =========================================================================
    // process args and check for validity
    boolean validInput = false; // only set to true if the program reaches the "deepest" options
    String inFile = "", outFile = "", command = "";
    int blurLength = -1;
    Image image = new Image();

    // + CHECK for number of input arguments
    if (args.length == 3 || args.length == 4) {
      inFile = args[0];
      outFile = args[1];

      boolean imported = image.imageImport(inFile);

      // ++ CHECK for file validity
      if (imported && outFile.contains(".ppm")) {
        if (args.length == 3) {
          // +++ CHECK for correct non-blur command name
          if ("grayscale".equals(args[2]) || "invert".equals(args[2]) || "emboss".equals(args[2])) {
            command = args[2];
            validInput = true;
          }
        } else {
          // +++ CHECK for correct non-blur command name
          if ("motionblur".equals(args[2])) {
            command = args[2];
            // ++++ CHECK for correct integer spec
            try {
              blurLength = Integer.parseInt(args[3]);
              if (blurLength > 0)
                validInput = true;
            } catch(NumberFormatException e) {
              // do nothing
            }
          }
        }
      }
    }
    // =========================================================================
    // carry out commands
    if (validInput) {
      if ("grayscale".equals(args[2]))
        image.grayscale();
      else if ("invert".equals(args[2]))
        image.invert();
      else if ("emboss".equals(args[2]))
        image.emboss();
      else // motionblur
        image.motionBlur(blurLength);

      image.imageExport(outFile);
    }
    else { // print usage statement
      System.out.println("usage: simple-image-editor <input.ppm> <output.ppm> [grayscale|invert|emboss|motionblur motion-blur-length]");
    }
  }
}
