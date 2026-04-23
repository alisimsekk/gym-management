package com.alisimsek.dto.request;

import com.alisimsek.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchRequest {
    private String firstName;
    private String lastName;
    private String username;
    private UserType userType;
}
