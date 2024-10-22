package teamcode;

import com.acmerobotics.roadrunner.Time;
import com.acmerobotics.roadrunner.Twist2dDual;

public interface E_Localizer {
    Twist2dDual<Time> update();
}
