package teamcode;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;


/**
 * This is sample code used to explain how to write an autonomous code
 *
 */

@Autonomous(name="BACKBOARD_Red50", group="red")

public class A_RedBackboard50 extends LinearOpMode {

    /* Declare OpMode members. */
    OpenCvCamera webcam;
    AC_RedObjectDetector3 OD = new AC_RedObjectDetector3(telemetry);
    private ElapsedTime     runtime = new ElapsedTime();

    boolean prevDdown = false;
    boolean prevDleft = false;
    boolean prevDright = false;
    boolean prevDup = false;

    boolean prevX = false;
    boolean prevY = false;
    boolean prevA = false;
    boolean prevB = false;

    double waitTime = 0.1;

    //Center
    double CenterOneX = 5;
    double CenterOneY = 5;

    double CenterDropX = 5;
    double CenterDropY = 5;



    //Left
    double LeftOneX = 5;
    double LeftOneY = 5;

    double LeftDropX = 5;
    double LeftDropY = 5;


    //Right
    double RightOneX = 5;
    double RightOneY = 5;

    double RightDropX = 5;
    double RightDropY = 5;



    @Override
    public void runOpMode() {
        View relativeLayout;

        int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
        relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);

        relativeLayout.post(() -> relativeLayout.setBackgroundColor(Color.rgb(214, 41, 41)));


        Pose2d startPose = new Pose2d(0, 0, Math.toRadians(-90));// Starting position
        Pose2d startPoseR = new Pose2d(0, 0, Math.toRadians(-198));// Starting position rotated

        Pose2d dropPose = new Pose2d(0,0, Math.toRadians(-90)); // Where it goes before it drops
        Pose2d waitPose = new Pose2d(0,0, Math.toRadians(-90)); // Can be anything

        Pose2d Pose1r = new Pose2d(0.1,0.1, Math.toRadians(-90));//+ 0.1
        Pose2d Pose1c = new Pose2d(0.1,0.1, Math.toRadians(-192)); //+ 0.1
        Pose2d Pose1l = new Pose2d(0.1,0.1, Math.toRadians(-85));//+ 0.1

        Pose2d Pose1rD = new Pose2d(0.2,0.2, Math.toRadians(-90));//+ 0.2
        Pose2d Pose1lD = new Pose2d(0.2,0.2, Math.toRadians(-90));//+ 0.2
        Pose2d Pose1cD = new Pose2d(0.2,0.2, Math.toRadians(-90));//+ 0.2



        C_MecanumDrive drive = new C_MecanumDrive(hardwareMap, startPose);
        C_MecanumDrive driveC = new C_MecanumDrive(hardwareMap, startPoseR);

        C_MecanumDrive driveWait = new C_MecanumDrive(hardwareMap, waitPose);
        C_MecanumDrive driveDrop= new C_MecanumDrive(hardwareMap, dropPose);
        C_MecanumDrive driveEnd= new C_MecanumDrive(hardwareMap, dropPose);

        C_MecanumDrive drive1r = new C_MecanumDrive(hardwareMap, Pose1r);
        C_MecanumDrive drive1c = new C_MecanumDrive(hardwareMap, Pose1c);
        C_MecanumDrive drive1l = new C_MecanumDrive(hardwareMap, Pose1l);

        C_MecanumDrive drive1rD = new C_MecanumDrive(hardwareMap, Pose1rD);
        C_MecanumDrive drive1cD = new C_MecanumDrive(hardwareMap, Pose1cD);
        C_MecanumDrive drive1lD = new C_MecanumDrive(hardwareMap, Pose1lD);


        BO_Lift OLift = new BO_Lift();
        BO_Intake OIntake = new BO_Intake();
        BO_Bucket OBucketAuto = new BO_Bucket();

        OLift.init(hardwareMap);
        OIntake.init(hardwareMap);
        OBucketAuto.init(hardwareMap);

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "AprilTagCam"), cameraMonitorViewId);
        webcam.setPipeline(OD);
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {webcam.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);}
            @Override
            public void onError(int errorCode) {}});

        while(!isStarted()) {
            if (OD.getIntLocation() == 2) {
                telemetry.addData("Location", OD.getIntLocation());
                telemetry.addLine("Left");
                telemetry.addLine("_");
                telemetry.addData("Wait Time:", waitTime);

                telemetry.update();
                sleep(30);
            } else if (OD.getIntLocation() == 1) {
                telemetry.addData("Location", OD.getIntLocation());
                telemetry.addLine("Center");
                telemetry.addLine("_");
                telemetry.addData("Wait Time:", waitTime);

                telemetry.update();
                sleep(30);

            } else {
                telemetry.addData("Location", OD.getIntLocation());
                telemetry.addLine("Right");
                telemetry.addLine("_");
                telemetry.addData("Wait Time:", waitTime);

                telemetry.update();
                sleep(30);
            }


            //Dpad Left for Regular
            if(gamepad1.dpad_down && !prevDdown){
                waitTime = 1;
                telemetry.addLine("1");
                telemetry.update();
            }
            prevDdown = gamepad1.dpad_down;

            //Dpad Down for Serve
            if(gamepad1.dpad_left && !prevDleft){
                waitTime = 2;
                telemetry.addLine("2");
                telemetry.update();
            }
            prevDleft = gamepad1.dpad_left;

            //Dpad Right for Rocket League
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
                telemetry.addLine("RESET WAIT TIME");
                telemetry.update();
            }
            prevY = gamepad1.y;


            telemetry.update();

        }

        waitForStart();
        telemetry.addData("Running Auto with a delay of:" , waitTime );
        telemetry.update();

        //

        //sleep(5000);

        if (OD.getIntLocation() == 3){ // Right (GONE)

            Actions.runBlocking( new SequentialAction (

                    OBucketAuto.BucketGateToggle(),

                    drive.actionBuilder(drive.pose)
                            .waitSeconds(waitTime)
                            .setTangent(0)

                            //-X move the robot toward the middle
                            //+Y move the robot toward the truss
                            //-Deg moves the robot clockwise

                            //In position to drop the pixel
                            .strafeToLinearHeading(new Vector2d(-48,-7.5),Math.toRadians(-115))


                            .build(),

                    //Drops the pixel
                    OIntake.RealInverse(),

                    drive.actionBuilder(drive.pose)
                            //This lets the OIntake run for enough time for the pixel to drop
                            .waitSeconds(2)
                            .build(),

                    //Sets all the things
                    OLift.Pos1(),
                    OBucketAuto.BucketToggle(),
                    OIntake.stop(),


                    drive.actionBuilder(drive.pose)
                            .setTangent(0)
                            //Drop position
                            .strafeToLinearHeading(new Vector2d(-17,55),Math.toRadians(-90))//-23.5
                            .waitSeconds(1.5)
                            .build(),

                    //Drops the pixel on the backboard
                    OBucketAuto.BucketGateToggle(),

                    drive.actionBuilder(drive.pose)
                            .waitSeconds(0.5)
                            .setTangent(0)
                            //Parks
                            .strafeToLinearHeading(new Vector2d(-1,-7),Math.toRadians(-93))
                            .strafeToLinearHeading(new Vector2d(56,-8),Math.toRadians(-95))
                            .build(),

                    OLift.Pos0(),
                    OBucketAuto.BucketToggle(),
                    OBucketAuto.BucketGateToggle(),

                    drive.actionBuilder(drive.pose)
                            //Waits to OLift to come down
                            .waitSeconds(1.5)
                            .build()

            ));

        } else if (OD.getIntLocation() == 1) { // Center

            Actions.runBlocking( new SequentialAction (

                   // OBucketAuto.BucketGateToggle(),

                    drive.actionBuilder(drive.pose)
                            .waitSeconds(waitTime)
                            .setTangent(0)

                            //-X move the robot toward the middle
                            //+Y move the robot toward the truss
                            //-Deg moves the robot clockwise

                            //In position to drop the pixel
                            .strafeToLinearHeading(new Vector2d(-45.5, 15),Math.toRadians(-210))//41.5
                            .build(),

                    //Drops the pixel
                    OIntake.RealInverse(),

                    drive.actionBuilder(drive.pose)
                            //This lets the OIntake run for enough time for the pixel to drop
                            .waitSeconds(2)
                            .build(),

                    OLift.Pos1(),
                    OBucketAuto.BucketToggle(),
                    OIntake.stop(),


                    drive.actionBuilder(driveC.pose)
                            .setTangent(0)

                            .strafeToLinearHeading(new Vector2d(6, -1),Math.toRadians(-198))

                            .strafeToLinearHeading(new Vector2d(10,10),Math.toRadians(-117))

                            //Drop position
                            .strafeToLinearHeading(new Vector2d(-5,46),Math.toRadians(-107))//-3.5,37

                            .waitSeconds(1.5)
                            .build(),

                    OBucketAuto.BucketGateToggle(),
                    OBucketAuto.BucketGateToggle(),

                    drive.actionBuilder(drive.pose)
                            .waitSeconds(1.2)
                            .setTangent(0)
                            //Parks
                            .strafeToLinearHeading(new Vector2d(5,-6),Math.toRadians(-85))
                            .strafeToLinearHeading(new Vector2d(40.5,-8),Math.toRadians(-85))
                            .build(),

                    OLift.Pos0(),
                    OBucketAuto.BucketToggle(),
                    OBucketAuto.BucketGateToggle(),

                    drive.actionBuilder(driveEnd.pose)
                            //Waits to OLift to come down
                            .waitSeconds(1.5)
                            .build()));
        } else { // Left

            Actions.runBlocking( new SequentialAction (

                    OBucketAuto.BucketGateToggle(),

                    drive.actionBuilder(drive.pose)
                            .waitSeconds(waitTime)

                            .setTangent(0)
                            //In position to drop the pixel
                            .strafeToLinearHeading(new Vector2d(-54,27.75),Math.toRadians(-99))//29
                            .build(),

                    //Drops the pixel
                    OIntake.RealInverse(),

                    drive.actionBuilder(driveWait.pose)
                            //This lets the OIntake run  enough time for the pixel to drop
                            .waitSeconds(1.5)
                            .build(),

                    //Puts all the things up and ready
                    OLift.Pos1(),
                    OBucketAuto.BucketToggle(),
                    OIntake.stop(),

                    drive.actionBuilder(drive.pose)
                            .setTangent(0)
                            //Drop position
                            .strafeToLinearHeading(new Vector2d(15,25),Math.toRadians(-93.5))
                            .strafeToLinearHeading(new Vector2d(12,35),Math.toRadians(-93.5))
                            .waitSeconds(0.5)
                            .build(),

                    OBucketAuto.BucketGateToggle(),

                    drive.actionBuilder(drive.pose)
                            .waitSeconds(1.5)
                            .setTangent(0)
                            //Parks
                            .strafeToLinearHeading(new Vector2d(1,-5),Math.toRadians(-85))
                            .strafeToLinearHeading(new Vector2d(45,-8),Math.toRadians(-85))
                            .build(),

                    OLift.Pos0(),
                    OBucketAuto.BucketToggle(),
                    OBucketAuto.BucketGateToggle(),

                    drive.actionBuilder(driveEnd.pose)
                            //Waits to OLift to come down
                            .waitSeconds(1.5)
                            .build()));

        } if(isStopRequested()) return;}}