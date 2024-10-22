package teamcode;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

/**
 * This is sample code used to explain how to write an autonomous code
 *
 */

@Autonomous(name="2+0|RegRedBackboard", group="Worlds_Read")

public class A_20RegReadBackboard50 extends LinearOpMode {

    /* Declare OpMode members. */

    //Camera Stuff
    OpenCvCamera InitWebcam;
    AC_RedObjectDetector3 OD = new AC_RedObjectDetector3(telemetry);
   // public DistanceSensor sensorDistance;
  //  NormalizedColorSensor sensorColor;
    //final float[] hsvValues = new float[3];
    // private ElapsedTime AprilTagTimer = new ElapsedTime();


    //Doubles
    public double whileTrue = 0;
    public double waitTime = 0;
    public double WantedTagID = 1;

    //Boolean
    boolean prevDdown = false;
    boolean prevDleft = false;
    boolean prevDright = false;
    boolean prevDup = false;
    boolean prevX = false;
    boolean prevY = false;
    boolean prevA = false;
    boolean prevB = false;



    @Override
    public void runOpMode() {

       // sensorDistance = hardwareMap.get(DistanceSensor.class, "SenseDistance");
        //sensorColor = hardwareMap.get(NormalizedColorSensor.class, "SenseColor");

        //Rev2mDistanceSensor sensorTime0fFlight = (Rev2mDistanceSensor) sensorDistance;

      //  if(sensorColor instanceof SwitchableLight){
        //    ((SwitchableLight)sensorColor).enableLight(true);
        //}


        View relativeLayout;

        int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
        relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);

        relativeLayout.post(() -> relativeLayout.setBackgroundColor(Color.rgb(42, 42, 222)));


        Pose2d startPose = new Pose2d(0, 0, Math.toRadians(-90));// Starting position
        C_MecanumDrive drive = new C_MecanumDrive(hardwareMap, startPose);


        BO_Lift Lift = new BO_Lift();
        BO_Intake Intake = new BO_Intake();
        BO_Bucket BucketAuto = new BO_Bucket();

        Lift.init(hardwareMap);
        Intake.AutoInit(hardwareMap);
        BucketAuto.init(hardwareMap);


        //OpenCV Team element Detection
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        InitWebcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        InitWebcam.setPipeline(OD);
        InitWebcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                InitWebcam.startStreaming(640, 480, OpenCvCameraRotation.UPSIDE_DOWN);
            }

            @Override
            public void onError(int errorCode) {
            }
        });



        //init LOOP
       while(!isStarted()) {
            if (OD.getIntLocation() == 2) {
                telemetry.addData("Location: RIGHT ;", OD.getIntLocation());
                telemetry.addLine("_");
                telemetry.addData("Delay :" , waitTime );
                telemetry.update();
                WantedTagID = 3;
                sleep(30);
            } else if (OD.getIntLocation() == 1) {
                telemetry.addData("Location: CENTER ;", OD.getIntLocation());
                telemetry.addLine("_");
                telemetry.addData("Delay :" , waitTime );
                telemetry.update();
                WantedTagID = 2;
                sleep(30);
            } else {
                telemetry.addData("Location: RIGHT ;", OD.getIntLocation());
                telemetry.addLine("_");
                telemetry.addData("Delay :" , waitTime );
                telemetry.update();
                WantedTagID = 3;
                sleep(30);
            }

            //SET WAIT TIME (Delay)
            if(gamepad1.dpad_down && !prevDdown){
                waitTime = 1;
                telemetry.addLine("1");
                telemetry.update();
            }
            prevDdown = gamepad1.dpad_down;

            if(gamepad1.dpad_left && !prevDleft){
                waitTime = 2;
                telemetry.addLine("2");
                telemetry.update();
            }
            prevDleft = gamepad1.dpad_left;

            if(gamepad1.dpad_right && !prevDright){
                waitTime = 3;
                telemetry.addLine("3");
                telemetry.update();
            }
            prevDright = gamepad1.dpad_right;

            if(gamepad1.dpad_up && !prevDup){
                waitTime = 4;
                telemetry.addLine("4");
                telemetry.update();
            }
            prevDup = gamepad1.dpad_up;

            if(gamepad1.x && !prevX){
                waitTime *= 2;
                telemetry.addLine("x2");
                telemetry.update();
            }
            prevX = gamepad1.x;

            if(gamepad1.a && !prevA){
                waitTime += 1;
                telemetry.addLine("+1");
                telemetry.update();
            }
            prevA = gamepad1.a;

            if(gamepad1.b && !prevB){
                waitTime -= 1;
                telemetry.addLine("-1");
                telemetry.update();
            }
            prevB = gamepad1.b;

            if(gamepad1.y && !prevY){
                waitTime = 0;
                telemetry.addLine("RESET waitTime");
                telemetry.update();
            }
            prevY = gamepad1.y;

            telemetry.update();

            sleep(10);
        }


        Action PushPixelIG = drive.actionBuilder(drive.pose) // Pushes and drops pixel
                .stopAndAdd(Intake.RealInverse())
                .waitSeconds(0.7475239)
                .stopAndAdd(Intake.stop())
                .build();


        Action DropPixelBB = drive.actionBuilder(drive.pose) // Drops the pixel on the Backdrop

                .waitSeconds(.2)
                .stopAndAdd(BucketAuto.BucketGateOpen())
                .waitSeconds(.4)
                .stopAndAdd(BucketAuto.BucketGateClose())
                .waitSeconds(.2)
                .stopAndAdd(BucketAuto.BucketClose())

                .build();

        //Waits for the opMode to start
        waitForStart();
            if (OD.getIntLocation() == 3) { // If the location is 3 then do ...
                //AprilTag Code; creates processor


                Actions.runBlocking(new SequentialAction(
                        //X+ robot goes towards the middle
                        //Y+ robot goes towards the backboard
                        //drive.waitS(waitTime),
                        drive.MoveXYACEL(-46, 0, -20, 20),//-20,50(10)
                        drive.MoveXY(0, -6.1),//-20,50(10)

                        PushPixelIG,
                        BucketAuto.BucketOpen(),
                        Lift.Pos1(),
                        drive.MoveXY(-16, 36),
                        DropPixelBB,
                        drive.MoveXY(40, -2),
                        Lift.Pos0(),
                        drive.waitS(1)));

              /* /* NormalizedRGBA colors = sensorColor.getNormalizedColors();
                Color.colorToHSV(colors.toColor(), hsvValues);*/
               /* double distance = sensorDistance.getDistance(DistanceUnit.INCH);
               /* if(distance < 3 || distance > 5){
                    Actions.runBlocking(new SequentialAction(drive.MoveXY(10, 38)));
                }*/
                /*while(whileTrue < 1000){

                    telemetry.addData("Distance: ", distance);
                    telemetry.update();
                    whileTrue++;
                    sleep(100);

                }*/

              /*  Actions.runBlocking(new SequentialAction(
                        DropPixelBB,
                        drive.MoveXY(-36, -2),
                        Lift.Pos0(),
                        drive.waitS(1)));*/


                //hsvValues[0] // gets color (ranges?)

            } else if (OD.getIntLocation() == 2 ) {//right

                Actions.runBlocking(new SequentialAction(
                        //X+ robot goes towards the middle
                        //Y+ robot goes towards the backboard
                        //drive.waitS(waitTime),
                        drive.MoveXYACEL(-45, 18, -20, 20),

                        PushPixelIG,
                        BucketAuto.BucketOpen(),
                        Lift.Pos1(),
                        drive.MoveXY(15, 20),
                        DropPixelBB,
                        drive.MoveXY(21.5, -2),
                        Lift.Pos0(),
                        drive.waitS(1.1)
                ));
            }
            else {//center

                Actions.runBlocking(new SequentialAction(
                        //X+ robot goes towards the middle
                        //Y+ robot goes towards the backboard
                        //drive.waitS(waitTime),
                        drive.MoveXYACEL(-57, 9, -20, 20),
                        PushPixelIG,
                        BucketAuto.BucketOpen(),
                        Lift.Pos1(),
                        drive.MoveXY(25.3, 30),
                        DropPixelBB,
                        drive.MoveXY(24, -2),
                        Lift.Pos0(),
                        drive.waitS(1.1)
                ));
            }
            if (isStopRequested()) return;
    }}