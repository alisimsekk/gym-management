package com.alisimsek.converter.trainee;

import com.alisimsek.dto.response.TraineeProfileResponse;
import com.alisimsek.dto.response.TraineeUpdateResponse;
import com.alisimsek.dto.response.UserRegistrationResponse;
import com.alisimsek.model.Trainee;

public interface TraineeConverter {
    UserRegistrationResponse toUserRegistrationResponse(Trainee trainee, String rawPassword);

    TraineeProfileResponse toTraineeProfileResponse(Trainee trainee);

    TraineeUpdateResponse toTraineeUpdateResponse(Trainee trainee);
}
