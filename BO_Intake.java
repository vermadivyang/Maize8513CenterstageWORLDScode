package teamcode;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class BO_Intake {

    DcMotor motor;
    static Servo RaiseI ;

    public final double INTAKE_SPEED = .84;
    public static final double IntakeDown = 0.1;
    public static final double IntakeUp = 0.25;
    public static boolean IsIntakeDown ;

    double intakePowerAuto = 0.4;
    int JOEY = 0;




    public void init(HardwareMap hwmap){

        motor = hwmap.get(DcMotor.class, "intakeM");
        motor.setPower(0);

        RaiseI = hwmap.get(Servo.class,"RaiseIntake");
        RaiseI.setPosition(IntakeDown);
        IsIntakeDown = true;


    }

    public void AutoInit(HardwareMap hwmap){

        motor = hwmap.get(DcMotor.class, "intakeM");
        motor.setPower(0);

        RaiseI = hwmap.get(Servo.class,"RaiseIntake");
        RaiseI.setPosition(IntakeUp);
        IsIntakeDown = false;


    }
    public void Intake_stop(){ motor.setPower(0); }
    public void Intake_start(){ motor.setPower(-INTAKE_SPEED); }
    public void Intake_startPLUS(){ motor.setPower(-1); }
    public void Intake_Sstart(){ motor.setPower(-INTAKE_SPEED*0.35); }
    public void Intake_SSstart(){ motor.setPower(INTAKE_SPEED*0.8); }
    public void Intake_SSSstart(){ motor.setPower(INTAKE_SPEED*0.37); }
    public void Intake_SSSSstart(){ motor.setPower(INTAKE_SPEED*0.275); }
    public void Intake_setPowerAuto(double AutoPowerFactor){ motor.setPower(INTAKE_SPEED*AutoPowerFactor); }


    public void Intake_inverse(){ motor.setPower(INTAKE_SPEED); }
    public void Intake_FULLinverse(){ motor.setPower(0.93); }

    public void Slow_Start(){ motor.setPower(-INTAKE_SPEED * 0.25); }
    public void Slow_Inverse(){ motor.setPower(INTAKE_SPEED * 0.25); }
    public int JOEY(){ return JOEY; }


    public static void MoveIDown(){
        RaiseI.setPosition(IntakeDown);
        IsIntakeDown = true;
    }
    public static void MoveIUp(){
        RaiseI.setPosition(IntakeUp);
        IsIntakeDown = false;
    }

    public static void ToggleIntakeUPDOWN(){
        if(IsIntakeDown){ // If the intake is down
            MoveIUp();
        }else{
            MoveIDown();
        }
    }

    public class EntakeBack implements Action {
        public void init() {Intake_Sstart();}
        public boolean loop(TelemetryPacket packet) {return false;}
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            Intake_Sstart();
            return false;}
    }

    public class EntakeFullInvers implements Action {
        public void init() {Intake_FULLinverse();}
        public boolean loop(TelemetryPacket packet) {return false;}
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            Intake_FULLinverse();
            return false;}
    }

    public class EntakeStartPlus implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            Intake_startPLUS();
            return false;}
    }
    public class RealEntakeBack implements Action {
        public void init() {Intake_SSstart();}
        public boolean loop(TelemetryPacket packet) {return false;}
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            Intake_SSstart();
            return false;}
    }

    public class PowerRealEntakeBack implements Action {
        public void init() {Intake_SSSstart();}
        public boolean loop(TelemetryPacket packet) {return false;}
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            Intake_SSSstart();
            return false;}
    }

    public class HalfPowerRealEntakeBack implements Action {
        public void init() {Intake_SSSstart();}
        public boolean loop(TelemetryPacket packet) {return false;}
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            Intake_SSSstart();
            return false;}
    }

    public class QuarterPowerRealEntakeBack implements Action {
        public void init() {Intake_SSSSstart();}
        public boolean loop(TelemetryPacket packet) {return false;}
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            Intake_SSSSstart();
            return false;}
    }

    public class EntakeNo implements Action {
        public void init() {Intake_stop();}
        public boolean loop(TelemetryPacket packet) {return false;}
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            Intake_stop();
            return false;}
    }

    public class EntakeSTART implements Action {
        public void init() {Intake_start();}
        public boolean loop(TelemetryPacket packet) {return false;}
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            Intake_start();
            return false;}
    }

    public class IntakeUpAuto implements Action {
        public void init() {}
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            MoveIUp();
            return false;}
    }

    public class IntakeDownAuto implements Action {
        public void init() {}
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            MoveIDown();
            return false;}
    }

    public Action inverse() {return new EntakeBack();}
    public Action XSTART() {return new EntakeStartPlus();}

    public Action START() {
        return new EntakeSTART();
    }
    public Action RealInverse() {
        return new RealEntakeBack();
    }
    public Action HalfPowerRealInverse() {
        return new HalfPowerRealEntakeBack();
    }
    public Action QuarterPowerRealInverse() {
        return new QuarterPowerRealEntakeBack();
    }
    public Action PowerRealInverse() {
        return new PowerRealEntakeBack();
    }
    public Action stop() {
        return new EntakeNo();
    }
    //public Action AutoIntake() {
      //  return new EntakeNo();
   // }

    public Action IntakeMoveUp() {
        return new IntakeUpAuto();
    }//EntakeFullInvers
    public Action IntakeRevserveFull() {
        return new EntakeFullInvers();
    }//EntakeFullInvers


    public Action IntakeMoveDown() {
        return new IntakeDownAuto();
    }

}
