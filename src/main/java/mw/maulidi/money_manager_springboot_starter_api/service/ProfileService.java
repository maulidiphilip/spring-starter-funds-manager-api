package mw.maulidi.money_manager_springboot_starter_api.service;

import lombok.RequiredArgsConstructor;
import mw.maulidi.money_manager_springboot_starter_api.dto.ProfileDTO;
import mw.maulidi.money_manager_springboot_starter_api.entity.ProfileEntity;
import mw.maulidi.money_manager_springboot_starter_api.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        ProfileEntity newProfile = toEntity(profileDTO);

        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile.setIsActive(false); // ensure default inactive

        profileRepository.save(newProfile);
        return toDTO(newProfile);
    }

    // Convert DTO -> Entity
    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .Id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(profileDTO.getPassword())
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    // Convert Entity -> DTO
    public ProfileDTO toDTO(ProfileEntity entity) {
        return ProfileDTO.builder()
                .Id(entity.getId())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .profileImageUrl(entity.getProfileImageUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
