package quadtree;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Stack;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author faculty
 */
public class QuadTreeImageRepresent {

    public static String[] imageQTreerepresentation(String fname, int decomposeSizethreshold, boolean isBoundary ) {
        
        StringBuilder treeRepString   = new StringBuilder ();
        StringBuilder objectRepString = new StringBuilder ();
        
        BufferedImage originalImage   = ImageIo.readImage(fname);
        BufferedImage grayImage       = ImageIo.toGray(originalImage);
        
        byte[][] grayImageArray       = ImageIo.getByteImageArray2DFromBufferedImage(grayImage);
        byte[][] grayImageArray2      = ImageIo.getByteImageArray2DFromBufferedImage(grayImage);
        
        byte[][] binaryImageArray     = ImageIo.threshold(grayImageArray, 128);
        byte[][] boundaryImageArray   = ImageIo.threshold(grayImageArray2, 128);
        
        String[] splitString = fname.split("\\.");
        System.out.println("Name = " + splitString[0]);
        System.out.println("Ext = "  + splitString[1]);
        
        // Do the qTree decomposition
        Point topLeftLocation = new Point(0, 0);
        int currentSize = grayImage.getWidth(); //assumed square!
        if (decomposeSizethreshold < 4)
            decomposeSizethreshold =4;
        
        decomposeQTree(binaryImageArray, boundaryImageArray, topLeftLocation, treeRepString, objectRepString, currentSize, decomposeSizethreshold, isBoundary);
        
        //
        if (isBoundary)
        {
            BufferedImage qtreeDecomposedImage = ImageIo.setByteImageArray2DToBufferedImage(boundaryImageArray, currentSize, currentSize);
            ImageIo.writeImage(qtreeDecomposedImage, "jpg", splitString[0]+"qtreeDecomposed" + ".jpg");
        }

        //Assemble results
        String[] qTreeRepresentation = new String[2];
        qTreeRepresentation[0] = treeRepString.toString();
        qTreeRepresentation[1] = objectRepString.toString();
        return qTreeRepresentation;
    }

    public static void decomposeQTree(byte[][] binaryImageArray, byte[][] boundaryImageArray, Point topLeftLocation, StringBuilder treeRepString, StringBuilder objectRepString, int currentSize, int decomposeSizethreshold, boolean isBoundary) {
        //System.out.println("Tree Code: " + treeRepString);

        if( !binaryPredicate(binaryImageArray,topLeftLocation,currentSize) && currentSize > decomposeSizethreshold)
        {
            //Split
            treeRepString.append("0");
            currentSize /=2;
            decomposeQTree(binaryImageArray,boundaryImageArray, new Point(topLeftLocation),treeRepString, objectRepString,currentSize,decomposeSizethreshold, isBoundary);
            decomposeQTree(binaryImageArray,boundaryImageArray, new Point(topLeftLocation.x + currentSize, topLeftLocation.y),treeRepString, objectRepString,currentSize,decomposeSizethreshold, isBoundary);
            decomposeQTree(binaryImageArray,boundaryImageArray, new Point(topLeftLocation.x + currentSize, topLeftLocation.y + currentSize),treeRepString, objectRepString,currentSize,decomposeSizethreshold, isBoundary);
            decomposeQTree(binaryImageArray,boundaryImageArray, new Point(topLeftLocation.x, topLeftLocation.y + currentSize),treeRepString, objectRepString,currentSize,decomposeSizethreshold, isBoundary);
        }
        else
        {
            treeRepString.append("1");
            objectRepString.append(binaryColor(binaryImageArray,topLeftLocation,currentSize));
            delineateBoundary(boundaryImageArray,topLeftLocation,currentSize);
        }  
    }
    
        public static boolean binaryPredicate(byte[][] binaryImageArray, Point topLeftLocation,int currentSize) 
        {
            byte startValue = binaryImageArray[topLeftLocation.x][topLeftLocation.y];
            for (int i = 0; i < currentSize; i++) {
                for (int j = 0; j < currentSize; j++) {
                    if (startValue != binaryImageArray[i+topLeftLocation.x][j+topLeftLocation.y])
                        return false;  
                }
            }
            return true;
        }
        
        public static String binaryColor(byte[][] binaryImageArray, Point topLeftLocation,int currentSize) 
        {
            byte startValue = binaryImageArray[topLeftLocation.x][topLeftLocation.y];
            if( (int) (startValue & 0xFF) ==0)
                return "1";
            else
                return "0";
            
        }
        
        public static void delineateBoundary(byte[][] boundaryImageArray,Point topLeftLocation,int currentSize)
        {
            for (int w = 0; w < currentSize; w++) {
                boundaryImageArray [topLeftLocation.x][w+ topLeftLocation.y]= (byte) 128;
            }
            for (int w = 0; w < currentSize; w++) {
                boundaryImageArray[topLeftLocation.x + currentSize - 1][topLeftLocation.y + w]=(byte) 128;
            }

            for (int h = 0; h < currentSize; h++) {
                boundaryImageArray[topLeftLocation.x +h] [topLeftLocation.y]=(byte) 128;
            }
            for (int h = 0; h < currentSize; h++) {
                boundaryImageArray[topLeftLocation.x +h][ topLeftLocation.y +currentSize - 1]=(byte) 128;
            }
        }
        
}
