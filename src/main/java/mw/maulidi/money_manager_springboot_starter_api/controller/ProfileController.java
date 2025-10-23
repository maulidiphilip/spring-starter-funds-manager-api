package mw.maulidi.money_manager_springboot_starter_api.controller;

import lombok.RequiredArgsConstructor;
import mw.maulidi.money_manager_springboot_starter_api.dto.ProfileDTO;
import mw.maulidi.money_manager_springboot_starter_api.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO) {
        ProfileDTO registeredProfile = profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
    }
}
