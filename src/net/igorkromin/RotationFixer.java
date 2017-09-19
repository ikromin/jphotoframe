package net.igorkromin;

import mediautil.image.jpeg.AbstractImageInfo;
import mediautil.image.jpeg.Entry;
import mediautil.image.jpeg.Exif;
import mediautil.image.jpeg.LLJTran;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class RotationFixer {

    String dir;

    public RotationFixer(String dir) {
        this.dir = dir;
    }

    /**
     * Starts a recursive directory traversal and fixing of all incorrectly rotated images
     */
    public void start() {
        Log.info("Running image rotation correction utility");
        fixImages(dir);
    }

    public void fixImages(String directory) {
        Log.info("Scanning directory: " + directory);

        Path imageDirPath = FileSystems.getDefault().getPath(directory);
        File imageDirFile = imageDirPath.toFile();
        if (!(imageDirFile.exists() && imageDirFile.isDirectory() && imageDirFile.canRead())) {
            throw new RuntimeException("Image directory not available: " + imageDirFile.getAbsolutePath());
        }

        int processedFiles = 0;
        int fixedFiles = 0;

        File[] files = imageDirFile.listFiles();
        for (File f : files) {
            if (f.isFile() && f.canRead() && !f.isHidden()) {
                processedFiles++;
                Log.info("Reading file - " + f.getAbsolutePath());

                try {
                    LLJTran llj = new LLJTran(f);
                    llj.read(LLJTran.READ_INFO, true);
                    AbstractImageInfo imageInfo = llj.getImageInfo();
                    if (!(imageInfo instanceof Exif)) {
                        throw new Exception("Image has no EXIF data");
                    }

                    Log.verbose("EXIF data found in image");

                    Exif exif = (Exif) imageInfo;
                    int orientation = 1;
                    Entry orientationTag = exif.getTagValue(Exif.ORIENTATION, true);
                    if (orientationTag != null) {
                        orientation = (Integer) orientationTag.getValue(0);
                    }

                    int operation = 0;
                    if (orientation > 0 && orientation < Exif.opToCorrectOrientation.length) {
                        operation = Exif.opToCorrectOrientation[orientation];
                    }
                    if (operation != 0) {
                        Log.info("\tTransforming image to correct orientation");

                        llj.read(LLJTran.READ_ALL, true);
                        llj.transform(operation, LLJTran.OPT_DEFAULTS  | LLJTran.OPT_XFORM_ORIENTATION);

                        Log.info("\tWriting image to disk");

                        OutputStream output = new BufferedOutputStream(new FileOutputStream(f));
                        llj.save(output, LLJTran.OPT_WRITE_ALL);
                        output.close();

                        llj.freeMemory();

                        fixedFiles++;

                    }
                    else {
                        Log.verbose("Image orientation is already correct");
                    }
                }
                catch (Exception e) {
                    Log.error("\tUnable to process file (" + e.getMessage() + ")");
                }
            }
            else if (f.isDirectory() && !f.isHidden()) {
                fixImages(f.getAbsolutePath());
            }
        }

        Log.info("Processed directory = " + directory + "; files = " + processedFiles + "; fixed files = " + fixedFiles);
    }
}
