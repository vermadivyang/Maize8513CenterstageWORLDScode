package teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;


/**
 * This is sample code used to explain how to write an autonomous code
 *
 */

@Autonomous(name="WING_Red50", group="red")

public class A_RedWing50 extends LinearOpMode {

    /* Declare OpMode members. */
    OpenCvCamera webcam;
    AC_ReadObjectDetector2 OD = new AC_ReadObjectDetector2(telemetry);
    private ElapsedTime     runtime = new ElapsedTime();



    @Override
    public void runOpMode() {

        Pose2d startPose = new Pose2d(0, 0, Math.toRadians(-90));
        Pose2d startPoseR3 = new Pose2d(0, 0, Math.toRadians(110));
        Pose2d startPoseR2 = new Pose2d(0, 0, Math.toRadians(110));
        Pose2d startPose2STRANGE = new Pose2d(20, 4, Math.toRadians(-80));
        Pose2d startPoseR1 = new Pose2d(0, 0, Math.toRadians(110));

        Pose2d Pose1ll = new Pose2d(48.6, 22.8, Math.toRadians(-83));//48.594,26.7

        Pose2d Pose2l = new Pose2d(52.55,-77.545, Math.toRadians(106));
        Pose2d Pose3l = new Pose2d(51.5,-101.9, Math.toRadians(105));
        Pose2d Pose4l = new Pose2d(23.45,-79.8, Math.toRadians(104));
        Pose2d Pose5l = new Pose2d(25.3851,-103.7629341, Math.toRadians(104));

        BO_Intake OIntake = new BO_Intake();
        BO_Bucket OBucketAuto = new BO_Bucket();


        C_MecanumDrive drive = new C_MecanumDrive(hardwareMap, startPose);
        C_MecanumDrive driveR3 = new C_MecanumDrive(hardwareMap, startPoseR3);
        C_MecanumDrive driveR2 = new C_MecanumDrive(hardwareMap, startPoseR2);
        C_MecanumDrive drive2STANGE = new C_MecanumDrive(hardwareMap, startPose2STRANGE);
        C_MecanumDrive driveR1 = new C_MecanumDrive(hardwareMap, startPoseR1);


        C_MecanumDrive drive2l = new C_MecanumDrive(hardwareMap, Pose2l);
        C_MecanumDrive drive3l = new C_MecanumDrive(hardwareMap, Pose3l);
        C_MecanumDrive drive4l = new C_MecanumDrive(hardwareMap, Pose4l);
        C_MecanumDrive drive5l = new C_MecanumDrive(hardwareMap, Pose5l);
        C_MecanumDrive drive1ll = new C_MecanumDrive(hardwareMap, Pose1ll);

        boolean prevDdown = false;
        boolean prevDleft = false;
        boolean prevDright = false;
        boolean prevDup = false;

        boolean prevX = false;
        boolean prevY = false;
        boolean prevA = false;
        boolean prevB = false;

        boolean parkl = true;
        boolean RB = false;
        boolean LB = false;
        int x = 13;
        int y = -33;
        int h = 100;


        double waitTime = 0.1;

        BO_Lift OLift = new BO_Lift();

        OLift.init(hardwareMap);
        OIntake.init(hardwareMap);
        OBucketAuto.init(hardwareMap);

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        webcam.setPipeline(OD);
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {webcam.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);}
            @Override
            public void onError(int errorCode) {}});

        while(!isStarted()) {

            if (OD.getIntLocation() == 2) {
                telemetry.addData("Location", OD.getIntLocation());
                telemetry.addLine("Center");
                telemetry.addLine("Center");
                telemetry.addLine("_");
                telemetry.addData("Delay :" , waitTime );
                telemetry.update();
                sleep(30);
            } else if (OD.getIntLocation() == 1) {
                telemetry.addData("Location", OD.getIntLocation());
                telemetry.addLine("Left");
                telemetry.addLine("Center");
                telemetry.addLine("_");
                telemetry.addData("Delay :" , waitTime );
                telemetry.update();

                sleep(30);

            } else {
                telemetry.addData("Location", OD.getIntLocation());
                telemetry.addLine("Right");
                telemetry.addLine("Center");
                telemetry.addLine("_");
                telemetry.addData("Delay :" , waitTime );
                telemetry.update();
                sleep(30);
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

                if(gamepad2.y && !RB){
                    parkl = false;
                    telemetry.addLine("Park: right");
                    telemetry.update();
                }
                RB = gamepad2.y;

                if(gamepad2.y && !LB){
                    parkl = true;
                    telemetry.addLine("Park: Left");
                    telemetry.update();
                }
                LB = gamepad2.y;

                if (parkl){
                    x=13;
                    y=-33;
                    h=100;
                }else if (!parkl){
                    x=10;
                    y=23;
                    h=90;
                }

                telemetry.update();
            }
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

                            .strafeToLinearHeading(new Vector2d(44,-3),Math.toRadians(-85))

                            .strafeToLinearHeading(new Vector2d(43.5,-7.1),Math.toRadians(-85))
                            .build(),
                    // Drop the purple Pixel
                    OIntake.RealInverse(),

                    drive.actionBuilder(drive.pose)
                            .waitSeconds(1)
                            .setTangent(0)
                            .strafeToLinearHeading(new Vector2d(-1,13),Math.toRadians(-85))
                            // Move the robot to the center
                            .strafeToLinearHeading(new Vector2d(30,14),Math.toRadians(-74)) //x = 85,80
                            // Mover the robot near the Backdrop
                            .strafeToLinearHeading(new Vector2d(40,-86),Math.toRadians(-73)) //x = 85,80
                            // Turn the robot so that the bucket faces the backdrop
                            .strafeToLinearHeading(new Vector2d(10,-95),Math.toRadians(110))
                            .build(),

                    OLift.Pos1(),
                    OBucketAuto.BucketToggle(),
                    OIntake.stop(),

                    drive.actionBuilder(driveR3.pose)
                            .waitSeconds(1)

                            // Move the robot forward to drop the pixel
                            .strafeToLinearHeading(new Vector2d(-8,10),Math.toRadians(100))

                            .strafeToLinearHeading(new Vector2d(-19,24),Math.toRadians(100))

                            .waitSeconds(1)
                            .build(),

                    // DROPS PIXEL
                    OBucketAuto.BucketGateToggle(),

                    //Parks
                    drive.actionBuilder(driveR3.pose)
                            .waitSeconds(1)

                            .strafeToLinearHeading(new Vector2d(x,y),Math.toRadians(h))
                            .build(),

                    OLift.Pos0(),
                    OBucketAuto.BucketToggle(),
                    OBucketAuto.BucketGateToggle(),

                    drive.actionBuilder(drive.pose)
                            .waitSeconds(1.2)
                            .build()
            ));

        } else if (OD.getIntLocation() == 2) { // Center

            Actions.runBlocking( new SequentialAction (

                    OBucketAuto.BucketGateToggle(),

                    drive.actionBuilder(drive.pose)
                            .setTangent(0)
                            .strafeToLinearHeading(new Vector2d(58,12),Math.toRadians(-80))//54,12

                            .build(),
                    // Drop the purple Pixel
                    OIntake.RealInverse(),

                    drive.actionBuilder(drive.pose)
                            .waitSeconds(1)
                            .build(),


                    drive.actionBuilder(drive.pose)
                            .waitSeconds(1)

                            // Move the robot to the center
                            .strafeToLinearHeading(new Vector2d(20,4),Math.toRadians(-80))
                            .build(),

                    OIntake.START(),

                    drive.actionBuilder(drive2STANGE.pose)
                            .waitSeconds(1)

                            // Mover the robot near the Backdrop
                            .strafeToLinearHeading(new Vector2d(20,-100),Math.toRadians(-79))
                            // Turn the robot so that the bucket faces the backdrop
                            .strafeToLinearHeading(new Vector2d(-10,-115),Math.toRadians(110))
                            .build(),

                    OLift.Pos1(),
                    OBucketAuto.BucketToggle(),


                    drive.actionBuilder(driveR2.pose)
                            .waitSeconds(1)

                            .strafeToLinearHeading(new Vector2d(-18.75,13.5),Math.toRadians(95))

                            .waitSeconds(1)
                            .build(),


                    // DROPS PIXEL
                    OBucketAuto.BucketGateToggle(),


                    // Align to Park
                    drive.actionBuilder(drive4l.pose)
                            .waitSeconds(1)
                            .strafeToLinearHeading(new Vector2d(29.75,-105),Math.toRadians(104))
                            .build(),

                    OLift.Pos0(),
                    OBucketAuto.BucketToggle(),
                    OBucketAuto.BucketGateToggle(),
                    // Parks
                    drive.actionBuilder(drive5l.pose)
                            .waitSeconds(2)
                            .build()));


        } else { // Left


            Actions.runBlocking( new SequentialAction (

                    OBucketAuto.BucketGateToggle(),


                    drive.actionBuilder(drive.pose)

                            // Take the robot to the Spike mark
                            .strafeToLinearHeading(new Vector2d(49,21),Math.toRadians(-85))
                            .build(),

                    // Drop the purple Pixel
                    OIntake.RealInverse(),

                    drive.actionBuilder(drive.pose)
                            .waitSeconds(1)

                            // Move the robot to the center
                            .strafeToLinearHeading(new Vector2d(30,-6),Math.toRadians(-70))
                            // Mover the robot near the Backdrop
                            .strafeToLinearHeading(new Vector2d(27,-98),Math.toRadians(-70))
                            // Turn the robot so that the bucket faces the backdrop
                            .strafeToLinearHeading(new Vector2d(7,-107),Math.toRadians(120))

                            .build(),

                    OLift.Pos1(),
                    OBucketAuto.BucketToggle(),
                    OIntake.stop(),


                    drive.actionBuilder(driveR3.pose)
                            .waitSeconds(1)

                            .strafeToLinearHeading(new Vector2d(-10,10),Math.toRadians(80))
                            .strafeToLinearHeading(new Vector2d(-43,11),Math.toRadians(80))

                            .waitSeconds(1)
                            .build(),

                    // Opens the bucket gate
                    OBucketAuto.BucketGateToggle(),


                    //Parks
                    drive.actionBuilder(driveR3.pose)
                            .waitSeconds(1)

                            .strafeToLinearHeading(new Vector2d(10,-30),Math.toRadians(100))
                            .build(),

                    OLift.Pos0(),
                    OBucketAuto.BucketToggle(),
                    OBucketAuto.BucketGateToggle(),

                    drive.actionBuilder(drive.pose)
                            .waitSeconds(1.2)
                            .build()

                    ));
/*

            int run1=0;

            while (run1 <= 11){
                drive.actionBuilder(drive.pose)
                        .strafeToLinearHeading(new Vector2d(Cords.RedB1(run1, "x"), Cords.RedB1(run1, "y")),Math.toRadians(Cords.RedB1(run1, "deg")))
                        .build();

                run1+=1;
            }

            drive.actionBuilder(driveWait.pose)
                    .waitSeconds(2)
                    .build();

*/

        } if(isStopRequested()) return;}}