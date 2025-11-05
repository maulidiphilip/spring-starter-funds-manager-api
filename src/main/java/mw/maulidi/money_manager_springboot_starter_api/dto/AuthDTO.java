package mw.maulidi.money_manager_springboot_starter_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthDTO {
    private String email;
    private String password;
    private String token;
}
