package mw.maulidi.money_manager_springboot_starter_api.repository;

import mw.maulidi.money_manager_springboot_starter_api.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository <ProfileEntity,Long> {
    /**
     * this one will automatically provide the CRUD operations for profile entity
     * custom query are added here
     * */

    // Jpa is going to execute a query select * from tbl_profile where email = ?1
    Optional<ProfileEntity>findByEmail(String email);
}
