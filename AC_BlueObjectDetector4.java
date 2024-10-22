package teamcode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class AC_BlueObjectDetector4 extends OpenCvPipeline { //LEFT Camera (not plane side)
    Telemetry telemetry;
    Mat mat = new Mat();
    Mat bluemat = new Mat();
    public enum Location {
        LEFT,
        RIGHT,
        NOT_FOUND
    }
    public Location location;
    private int IntLocation=-1;

    //draws rectangles(make sure x max is 320 and y is 240(in ObjectDetectAuto webcam.startstreaming))
    //Rectangles for detecting if object of a certain color is in it and what percentage it is at
    static final Rect LEFT_ROI_B = new Rect(
            new Point(150, 120),
            new Point(260, 245)
            //new Point(0, 120),
            //new Point(130, 300)

    );
    static final Rect RIGHT_ROI_B = new Rect(
            new Point(480, 140),
            new Point(610, 315));

    static final Rect RIGHT_BLUE_B = new Rect(
            new Point(170, 130),
            new Point(280, 255));

    static final Rect LEFT_BLUE_B = new Rect(
            new Point(490, 135),
            new Point(600, 267));


    //determines what percentage has to fill box
    static double PERCENT_COLOR_THRESHOLD = 0.23;

    public AC_BlueObjectDetector4(Telemetry t) { telemetry = t; }

    @Override
    public Mat processFrame(Mat input) {
        //color stuff
        Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV);
        Imgproc.cvtColor(input, bluemat, Imgproc.COLOR_RGB2HSV);
        //Scalar lowHSV = new Scalar(23, 50, 70);
       // Scalar highHSV = new Scalar(32, 255, 255);

        Scalar bluelowHSV = new Scalar(100 ,80 , 0);   //s was 5
        Scalar bluehighHSV = new Scalar(140, 230, 255);


        Scalar redlowHSV = new Scalar(0, 50, 50);
        Scalar redhighHSV = new Scalar(10, 2555, 255);




//160,250
        //V was 150

        Core.inRange(mat, redlowHSV, redhighHSV, mat);
        Core.inRange(bluemat, bluelowHSV, bluehighHSV, bluemat);

        Mat left = mat.submat(LEFT_BLUE_B);
        Mat right = mat.submat(RIGHT_BLUE_B);

        Mat blueleft = bluemat.submat(LEFT_BLUE_B);
        Mat blueright = bluemat.submat(RIGHT_BLUE_B);

        double leftValue = Core.sumElems(left).val[0] / LEFT_BLUE_B.area() / 255;
        double rightValue = Core.sumElems(right).val[0] / RIGHT_BLUE_B.area() / 255;
        double blueleftValue = Core.sumElems(blueleft).val[0] / LEFT_BLUE_B.area() / 255;
        double bluerightValue = Core.sumElems(blueright).val[0] / RIGHT_BLUE_B.area() / 255;

        left.release();
        right.release();

        blueleft.release();
        blueright.release();

        //telemtry for values and percents
        /*telemetry.addData("Left raw value", (int) Core.sumElems(left).val[0]);
        telemetry.addData("Right raw value", (int) Core.sumElems(right).val[0]);
        telemetry.addData("Left percentage", Math.round(leftValue * 100) + "%");
        telemetry.addData("Right percentage", Math.round(rightValue * 100) + "%");
        telemetry.addData("Left percentage", Math.round(blueleftValue * 100) + "%");
        telemetry.addData("Right percentage", Math.round(bluerightValue * 100) + "%");
        telemetry.update();*/

        //boolean stating that element are either right or left if the box is filled to more than 'PERCENT_COLOR_THRESHOLD'
        boolean elementLeft = leftValue > PERCENT_COLOR_THRESHOLD;
        boolean elementRight = rightValue > PERCENT_COLOR_THRESHOLD;
        boolean blueelementLeft = blueleftValue > PERCENT_COLOR_THRESHOLD;
        boolean blueelementRight = bluerightValue > PERCENT_COLOR_THRESHOLD;

        //tells location of element(left right or not found(looks for stuff in rectangles))
        if (!blueelementLeft && !blueelementRight){
            //if element not found, it say not found
            location = Location.NOT_FOUND;
            IntLocation=3;
            //  telemetry.addData("Element Location", "not found; on left side");
        }
        else if (elementLeft || blueelementLeft) {
            //if 40% or more of element in left rectangle, it say left
            location = Location.LEFT;
            IntLocation=1;
            //telemetry.addData("Element Location", "middle");
        }
        else {
            //if 40% or more of element in right rectangle, it say right
            //center
            location = Location.RIGHT;
            IntLocation=2;
            // telemetry.addData("Element Location", "right");
        }
        //telemetry.update();

        //turns everything that not the color we look for into black

        //blue mat first for blue side
        Imgproc.cvtColor(bluemat, mat, Imgproc.COLOR_GRAY2RGB);

        // color of rectangles in camera stream
        Scalar colorStone = new Scalar(255, 0, 0);
        Scalar colorSkystone = new Scalar(0, 255, 0);

        Imgproc.rectangle(mat, LEFT_BLUE_B, location == Location.LEFT? colorSkystone:colorStone);
        Imgproc.rectangle(mat, RIGHT_BLUE_B, location == Location.RIGHT? colorSkystone:colorStone);
        Imgproc.rectangle(bluemat, LEFT_BLUE_B, location == Location.LEFT? colorSkystone:colorStone);
        Imgproc.rectangle(bluemat, RIGHT_BLUE_B, location == Location.RIGHT? colorSkystone:colorStone);

        return mat;
    }

    //takes location of element and gives it to us
    public Location getLocation() {
        return location;
    }
    public int getIntLocation() {return IntLocation;}
}



