package com.alisimsek.converter.trainer;

import com.alisimsek.dto.response.TrainerBasicInfoDto;
import com.alisimsek.dto.response.TrainerProfileResponse;
import com.alisimsek.dto.response.TrainerUpdateResponse;
import com.alisimsek.dto.response.UserRegistrationResponse;
import com.alisimsek.model.Trainer;

public interface TrainerConverter {
    UserRegistrationResponse toUserRegistrationResponse(Trainer trainer, String rawPassword);

    TrainerBasicInfoDto toTrainerBasicInfoDto(Trainer trainer);

    TrainerProfileResponse toTrainerProfileResponse(Trainer trainer);

    TrainerUpdateResponse toTrainerUpdateResponse(Trainer save);
}
