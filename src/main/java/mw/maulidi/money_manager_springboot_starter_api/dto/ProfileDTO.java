package mw.maulidi.money_manager_springboot_starter_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDTO {
    private Long Id;
    private String fullName;
    private String email;
    private String password;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
