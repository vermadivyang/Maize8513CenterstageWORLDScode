package teamcode;
/*
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.AngularRampLogger;
import com.acmerobotics.roadrunner.ftc.DriveType;
import com.acmerobotics.roadrunner.ftc.DriveView;
import com.acmerobotics.roadrunner.ftc.DriveViewFactory;
import com.acmerobotics.roadrunner.ftc.Encoder;
import com.acmerobotics.roadrunner.ftc.ForwardPushTest;
import com.acmerobotics.roadrunner.ftc.ForwardRampLogger;
import com.acmerobotics.roadrunner.ftc.LateralPushTest;
import com.acmerobotics.roadrunner.ftc.LateralRampLogger;
import com.acmerobotics.roadrunner.ftc.ManualFeedforwardTuner;
import com.acmerobotics.roadrunner.ftc.MecanumMotorDirectionDebugger;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
/*
import org.firstinspires.ftc.teamcode.C_MecanumDrive;
import org.firstinspires.ftc.teamcode.TankDrive;
import org.firstinspires.ftc.teamcode.E_ThreeDeadWheelELocalizer;
import org.firstinspires.ftc.teamcode.TwoDeadWheelELocalizer;*/

/*
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Disabled

public final class TuningOpModes {
    public static final Class<?> DRIVE_CLASS = C_MecanumDrive.class;

    public static final String GROUP = "quickstart";
    public static final boolean DISABLED = false;

    private TuningOpModes() {}

    private static OpModeMeta metaForClass(Class<? extends OpMode> cls) {
        return new OpModeMeta.Builder()
                .setName(cls.getSimpleName())
                .setGroup(GROUP)
                .setFlavor(OpModeMeta.Flavor.TELEOP)
                .build();
    }

    @OpModeRegistrar
    public static void register(OpModeManager manager) {
        if (DISABLED) return;

        DriveViewFactory dvf;
        if (DRIVE_CLASS.equals(C_MecanumDrive.class)) {
            dvf = hardwareMap -> {
                C_MecanumDrive md = new C_MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));

                List<Encoder> leftEncs = new ArrayList<>(), rightEncs = new ArrayList<>();
                List<Encoder> parEncs = new ArrayList<>(), perpEncs = new ArrayList<>();
                if (md.ELocalizer instanceof C_MecanumDrive.DriveELocalizer) {
                    C_MecanumDrive.DriveELocalizer dl = (C_MecanumDrive.DriveELocalizer) md.ELocalizer;
                    leftEncs.add(dl.leftFront);
                    leftEncs.add(dl.leftRear);
                    rightEncs.add(dl.rightFront);
                    rightEncs.add(dl.rightRear);
                } else if (md.ELocalizer instanceof E_ThreeDeadWheelELocalizer) {
                    E_ThreeDeadWheelELocalizer dl = (E_ThreeDeadWheelELocalizer) md.ELocalizer;
                    parEncs.add(dl.par0);
                    parEncs.add(dl.par1);
                    perpEncs.add(dl.perp);
                } else if (md.ELocalizer instanceof TwoDeadWheelELocalizer) {
                    TwoDeadWheelELocalizer dl = (TwoDeadWheelELocalizer) md.ELocalizer;
                    parEncs.add(dl.par);
                    perpEncs.add(dl.perp);
                } else {
                    throw new IllegalArgumentException("unknown ELocalizer: " + md.ELocalizer.getClass().getName());
                }

                return new DriveView(
                    DriveType.MECANUM,
                        C_MecanumDrive.PARAMS.inPerTick,
                        C_MecanumDrive.PARAMS.maxWheelVel,
                        C_MecanumDrive.PARAMS.minProfileAccel,
                        C_MecanumDrive.PARAMS.maxProfileAccel,
                        hardwareMap.getAll(LynxModule.class),
                        Arrays.asList(
                                md.leftFront,
                                md.leftBack
                        ),
                        Arrays.asList(
                                md.rightFront,
                                md.rightBack
                        ),
                        leftEncs,
                        rightEncs,
                        parEncs,
                        perpEncs,
                        md.imu,
                        md.voltageSensor,
                        md.feedforward
                );
            };
        } else if (DRIVE_CLASS.equals(TankDrive.class)) {
            dvf = hardwareMap -> {
                TankDrive td = new TankDrive(hardwareMap, new Pose2d(0, 0, 0));

                List<Encoder> leftEncs = new ArrayList<>(), rightEncs = new ArrayList<>();
                List<Encoder> parEncs = new ArrayList<>(), perpEncs = new ArrayList<>();
                if (td.ELocalizer instanceof TankDrive.DriveELocalizer) {
                    TankDrive.DriveELocalizer dl = (TankDrive.DriveELocalizer) td.ELocalizer;
                    leftEncs.addAll(dl.leftEncs);
                    rightEncs.addAll(dl.rightEncs);
                } else if (td.ELocalizer instanceof E_ThreeDeadWheelELocalizer) {
                    E_ThreeDeadWheelELocalizer dl = (E_ThreeDeadWheelELocalizer) td.ELocalizer;
                    parEncs.add(dl.par0);
                    parEncs.add(dl.par1);
                    perpEncs.add(dl.perp);
                } else if (td.ELocalizer instanceof TwoDeadWheelELocalizer) {
                    TwoDeadWheelELocalizer dl = (TwoDeadWheelELocalizer) td.ELocalizer;
                    parEncs.add(dl.par);
                    perpEncs.add(dl.perp);
                } else {
                    throw new IllegalArgumentException("unknown ELocalizer: " + td.ELocalizer.getClass().getName());
                }

                return new DriveView(
                    DriveType.TANK,
                        TankDrive.PARAMS.inPerTick,
                        TankDrive.PARAMS.maxWheelVel,
                        TankDrive.PARAMS.minProfileAccel,
                        TankDrive.PARAMS.maxProfileAccel,
                        hardwareMap.getAll(LynxModule.class),
                        td.leftMotors,
                        td.rightMotors,
                        leftEncs,
                        rightEncs,
                        parEncs,
                        perpEncs,
                        td.imu,
                        td.voltageSensor,
                        td.feedforward
                );
            };
        } else {
            throw new AssertionError();
        }


        manager.register(metaForClass(AngularRampLogger.class), new AngularRampLogger(dvf));
        manager.register(metaForClass(ForwardPushTest.class), new ForwardPushTest(dvf));
        manager.register(metaForClass(ForwardRampLogger.class), new ForwardRampLogger(dvf));
        manager.register(metaForClass(LateralPushTest.class), new LateralPushTest(dvf));
        manager.register(metaForClass(LateralRampLogger.class), new LateralRampLogger(dvf));
        manager.register(metaForClass(ManualFeedforwardTuner.class), new ManualFeedforwardTuner(dvf));
        manager.register(metaForClass(MecanumMotorDirectionDebugger.class), new MecanumMotorDirectionDebugger(dvf));
        /*manager.register(metaForClass(ManualFeedbackTuner.class), ManualFeedbackTuner.class);
        manager.register(metaForClass(SplineTest.class), SplineTest.class);
   /*     manager.register(metaForClass(LocalizationTest.class), LocalizationTest.class);*/
/* }
}*/
