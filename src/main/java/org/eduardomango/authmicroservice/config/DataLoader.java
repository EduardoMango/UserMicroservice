package org.eduardomango.authmicroservice.config;

import jakarta.annotation.PostConstruct;
import org.eduardomango.authmicroservice.models.CredentialsEntity;
import org.eduardomango.authmicroservice.models.Enum.UserPermit;
import org.eduardomango.authmicroservice.models.Enum.UserProfile;
import org.eduardomango.authmicroservice.models.Enum.UserRole;
import org.eduardomango.authmicroservice.models.PermitEntity;
import org.eduardomango.authmicroservice.models.ProfileEntity;
import org.eduardomango.authmicroservice.models.RoleEntity;
import org.eduardomango.authmicroservice.repositories.CredentialsRepository;
import org.eduardomango.authmicroservice.repositories.PermitRepository;
import org.eduardomango.authmicroservice.repositories.ProfileRepository;
import org.eduardomango.authmicroservice.repositories.RoleRepository;
import org.eduardomango.authmicroservice.services.impl.JwtServiceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

// Loads basic data
@Component
public class DataLoader {

    private final PermitRepository permitRepository;
    private final RoleRepository roleRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final CredentialsRepository credentialsRepository;
    private final JwtServiceImpl jwtService;

    public DataLoader(PermitRepository permitRepository, RoleRepository roleRepository, ProfileRepository profileRepository, PasswordEncoder passwordEncoder, CredentialsRepository credentialsRepository, JwtServiceImpl jwtService) {
        this.permitRepository = permitRepository;
        this.roleRepository = roleRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
        this.credentialsRepository = credentialsRepository;
        this.jwtService = jwtService;
    }

    /**
     * Loads basic data for testing
     */
    @PostConstruct
    public void init() {
        List<PermitEntity> permits = List.of(
                PermitEntity.builder()
                        .permit(UserPermit.USER_READ)
                        .description("View user information")
                        .build(),
                PermitEntity.builder()
                        .permit(UserPermit.USER_READ_SELF)
                        .description("View self user information")
                        .build(),
                PermitEntity.builder()
                        .permit(UserPermit.USER_UPDATE)
                        .description("Update user information")
                        .build(),
                PermitEntity.builder()
                        .permit(UserPermit.USER_DELETE)
                        .description("Delete user accounts")
                        .build(),
                PermitEntity.builder()
                        .permit(UserPermit.PRODUCT_CREATE)
                        .description("Create products")
                        .build(),
                PermitEntity.builder()
                        .permit(UserPermit.PRODUCT_READ)
                        .description("View products")
                        .build(),
                PermitEntity.builder()
                        .permit(UserPermit.PRODUCT_UPDATE)
                        .description("Update products")
                        .build(),
                PermitEntity.builder()
                        .permit(UserPermit.PRODUCT_DELETE)
                        .description("Delete products")
                        .build(),
                PermitEntity.builder()
                        .permit(UserPermit.ORDER_CREATE)
                        .description("Create orders")
                        .build(),
                PermitEntity.builder()
                        .permit(UserPermit.ORDER_READ)
                        .description("View orders")
                        .build(),
                PermitEntity.builder()
                        .permit(UserPermit.ORDER_UPDATE)
                        .description("Update orders")
                        .build(),
                PermitEntity.builder()
                        .permit(UserPermit.ORDER_DELETE)
                        .description("Cancel orders")
                        .build(),
                PermitEntity.builder()
                        .permit(UserPermit.INVENTORY_READ)
                        .description("View inventory")
                        .build(),
                PermitEntity.builder()
                        .permit(UserPermit.INVENTORY_UPDATE)
                        .description("Update inventory")
                        .build(),
                PermitEntity.builder()
                        .permit(UserPermit.REVIEW_MODERATE)
                        .description("Moderate reviews")
                        .build(),
                PermitEntity.builder()
                        .permit(UserPermit.DISPUTE_MANAGE)
                        .description("Manage disputes")
                        .build(),
                PermitEntity.builder()
                        .permit(UserPermit.NOTIFICATION_SEND)
                        .description("Send notifications")
                        .build(),
                PermitEntity.builder()
                        .permit(UserPermit.SUPPORT_RESPOND)
                        .description("Respond to support tickets")
                        .build(),
                PermitEntity.builder()
                        .permit(UserPermit.CONTENT_CREATE)
                        .description("Create content (banners, pages, etc.)")
                        .build(),
                PermitEntity.builder()
                        .permit(UserPermit.CONTENT_READ)
                        .description("Read content")
                        .build(),
                PermitEntity.builder()
                        .permit(UserPermit.CONTENT_UPDATE)
                        .description("Update content")
                        .build(),
                PermitEntity.builder()
                        .permit(UserPermit.CONTENT_DELETE)
                        .description("Delete content")
                        .build()
        );
        permitRepository.saveAll(permits);

        RoleEntity roleGuest = new RoleEntity(UserRole.ROLE_GUEST);
        roleGuest.addPermit(permits.get(5)); // INVENTORY_READ

        roleRepository.save(roleGuest);

        RoleEntity roleCustomer = new RoleEntity(UserRole.ROLE_CUSTOMER);
        roleCustomer.addPermit(permits.get(1));  // USER_READ_SELF
        roleCustomer.addPermit(permits.get(2));  // USER_UPDATE
        roleCustomer.addPermit(permits.get(5));  // PRODUCT_READ
        roleCustomer.addPermit(permits.get(8));  // ORDER_CREATE
        roleCustomer.addPermit(permits.get(9));  // ORDER_READ
        roleCustomer.addPermit(permits.get(10)); // ORDER_UPDATE
        roleCustomer.addPermit(permits.get(11)); // ORDER_DELETE

        roleRepository.save(roleCustomer);

        RoleEntity roleSeller = new RoleEntity(UserRole.ROLE_SELLER);
        roleSeller.addPermit(permits.get(5));  // PRODUCT_READ
        roleSeller.addPermit(permits.get(6));  // PRODUCT_CREATE
        roleSeller.addPermit(permits.get(7));  // PRODUCT_UPDATE
        roleSeller.addPermit(permits.get(9));  // ORDER_READ
        roleSeller.addPermit(permits.get(10)); // ORDER_UPDATE
        roleSeller.addPermit(permits.get(11)); // ORDER_DELETE
        roleSeller.addPermit(permits.get(12)); // INVENTORY_READ
        roleSeller.addPermit(permits.get(13)); // INVENTORY_UPDATE

        roleRepository.save(roleSeller);

        RoleEntity roleAdmin = new RoleEntity(UserRole.ROLE_ADMIN);
        roleAdmin.addPermit(permits.get(1));   // USER_READ_SELF
        roleAdmin.addPermit(permits.get(2));   // USER_UPDATE
        roleAdmin.addPermit(permits.get(3));   // USER_DELETE
        roleAdmin.addPermit(permits.get(5));   // PRODUCT_READ
        roleAdmin.addPermit(permits.get(6));   // PRODUCT_CREATE
        roleAdmin.addPermit(permits.get(7));   // PRODUCT_UPDATE
        roleAdmin.addPermit(permits.get(8));   // ORDER_CREATE
        roleAdmin.addPermit(permits.get(9));   // ORDER_READ
        roleAdmin.addPermit(permits.get(10));  // ORDER_UPDATE
        roleAdmin.addPermit(permits.get(11));  // ORDER_DELETE
        roleAdmin.addPermit(permits.get(12));  // INVENTORY_READ
        roleAdmin.addPermit(permits.get(13));  // INVENTORY_UPDATE
        roleAdmin.addPermit(permits.get(14));  // REVIEW_MODERATE
        roleAdmin.addPermit(permits.get(15));  // DISPUTE_MANAGE
        roleAdmin.addPermit(permits.get(17));  // SUPPORT_RESPOND
        roleAdmin.addPermit(permits.get(18));  // CONTENT_CREATE
        roleAdmin.addPermit(permits.get(19));  // CONTENT_READ
        roleAdmin.addPermit(permits.get(20));  // CONTENT_UPDATE
        roleAdmin.addPermit(permits.get(21));  // CONTENT_DELETE

        roleRepository.save(roleAdmin);

        RoleEntity roleDeliveryStaff = new RoleEntity(UserRole.ROLE_DELIVERY_STAFF);
        roleDeliveryStaff.addPermit(permits.get(9));  // ORDER_READ
        roleDeliveryStaff.addPermit(permits.get(10)); // ORDER_UPDATE

        roleRepository.save(roleDeliveryStaff);

        RoleEntity roleSupportAgent = new RoleEntity(UserRole.ROLE_SUPPORT_AGENT);
        roleSupportAgent.addPermit(permits.get(17));  // SUPPORT_RESPOND
        roleSupportAgent.addPermit(permits.get(14));  // REVIEW_MODERATE

        roleRepository.save(roleSupportAgent);

        RoleEntity roleUserManager = new RoleEntity(UserRole.ROLE_USER_MANAGER);        roleUserManager.addPermit(permits.get(1));   // USER_READ_SELF
        roleUserManager.addPermit(permits.get(0));   // USER_READ
        roleUserManager.addPermit(permits.get(2));   // USER_UPDATE
        roleUserManager.addPermit(permits.get(3));   // USER_DELETE

        roleRepository.save(roleUserManager);

        RoleEntity roleOrderManager = new RoleEntity(UserRole.ROLE_ORDER_MANAGER);
        roleOrderManager.addPermit(permits.get(9));  // ORDER_READ
        roleOrderManager.addPermit(permits.get(10)); // ORDER_UPDATE
        roleOrderManager.addPermit(permits.get(11)); // ORDER_DELETE

        roleRepository.save(roleOrderManager);

        RoleEntity roleProductManager = new RoleEntity(UserRole.ROLE_PRODUCT_MANAGER);
        roleProductManager.addPermit(permits.get(5));  // PRODUCT_READ
        roleProductManager.addPermit(permits.get(6));  // PRODUCT_CREATE
        roleProductManager.addPermit(permits.get(7));  // PRODUCT_UPDATE
        roleProductManager.addPermit(permits.get(12)); // INVENTORY_READ
        roleProductManager.addPermit(permits.get(13)); // INVENTORY_UPDATE

        roleRepository.save(roleProductManager);

        RoleEntity roleContentModerator = new RoleEntity(UserRole.ROLE_CONTENT_MODERATOR);
        roleContentModerator.addPermit(permits.get(18));  // CONTENT_CREATE
        roleContentModerator.addPermit(permits.get(19));  // CONTENT_READ
        roleContentModerator.addPermit(permits.get(20));  // CONTENT_UPDATE
        roleContentModerator.addPermit(permits.get(21));  // CONTENT_DELETE;

        roleRepository.save(roleContentModerator);

        ProfileEntity profileCustomer = new ProfileEntity(UserProfile.CUSTOMER);
        profileCustomer.addRole(roleCustomer);
        profileRepository.save(profileCustomer);

        ProfileEntity profileSeller = new ProfileEntity(UserProfile.SELLER);
        profileSeller.addRole(roleSeller);
        profileRepository.save(profileSeller);

        ProfileEntity profileAdmin = new ProfileEntity(UserProfile.ADMIN);
        profileAdmin.addRole(roleAdmin);
        profileRepository.save(profileAdmin);

        ProfileEntity profileCourier = new ProfileEntity(UserProfile.COURIER);
        profileCourier.addRole(roleDeliveryStaff);
        profileRepository.save(profileCourier);

        ProfileEntity profileGuest = new ProfileEntity(UserProfile.GUEST);
        profileGuest.addRole(roleGuest);
        profileRepository.save(profileGuest);

    CredentialsEntity credentialsCustomer = CredentialsEntity.builder()
            .profile(profileCustomer)
            .username("customer")
            .email("customer@email.com")
            .password(passwordEncoder.encode("customer"))
            .createdAt(LocalDateTime.now())
            .refreshToken("refresh")
            .build();

    credentialsCustomer.setRefreshToken(jwtService.generateRefreshToken(credentialsCustomer));

    credentialsRepository.save(credentialsCustomer);

    CredentialsEntity credentialsSeller = CredentialsEntity.builder()
            .username("sellerTest")
            .email("seller@email.com")
            .password(passwordEncoder.encode("sellerTest"))
            .profile(profileSeller)
            .createdAt(LocalDateTime.now())
            .build();

    credentialsSeller.setRefreshToken(jwtService.generateRefreshToken(credentialsSeller));

    credentialsRepository.save(credentialsSeller);
    }
}
