package com.gymtracker.config;

import com.gymtracker.entity.AppUser;
import com.gymtracker.entity.SubscriptionPlan;
import com.gymtracker.enums.Role;
import com.gymtracker.repository.SubscriptionPlanRepository;
import com.gymtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataInitializer: Initializes default admin user, roles, and subscription plans
 * on application startup.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");
        
        initializeAdminUser();
        initializeSubscriptionPlans();
        
        log.info("Data initialization completed.");
    }

    /**
     * Initialize default admin user if it doesn't exist.
     * Default credentials: admin / admin123
     * 
     * IMPORTANT: Change these credentials in production!
     */
    private void initializeAdminUser() {
        try {
            if (!userRepository.existsByUsername("admin")) {
                AppUser adminUser = AppUser.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .roles(Role.ROLE_ADMIN.toString())
                        .build();
                
                userRepository.save(adminUser);
                log.info("✓ Admin user created. Username: admin, Password: admin123 (CHANGE THIS IN PRODUCTION!)");
            } else {
                log.info("✓ Admin user already exists.");
            }
        } catch (Exception e) {
            log.error("Error initializing admin user: {}", e.getMessage(), e);
        }
    }

    /**
     * Initialize default subscription plans if they don't exist.
     */
    private void initializeSubscriptionPlans() {
        try {
            // Check if plans already exist
            long existingPlans = subscriptionPlanRepository.count();
            
            if (existingPlans == 0) {
                // 1-Month Basic Plan
                SubscriptionPlan basicMonthly = SubscriptionPlan.builder()
                        .name("Basic Monthly")
                        .description("Access to gym facilities for 1 month")
                        .durationDays(30)
                        .priceCents(2999) // $29.99
                        .includesClasses(false)
                        .active(true)
                        .build();
                subscriptionPlanRepository.save(basicMonthly);
                log.info("✓ Created subscription plan: Basic Monthly ($29.99)");

                // 3-Month Plan
                SubscriptionPlan threeMonth = SubscriptionPlan.builder()
                        .name("3-Month Plan")
                        .description("Access to gym facilities for 3 months with 10% discount")
                        .durationDays(90)
                        .priceCents(8099) // $80.99 (10% off)
                        .includesClasses(false)
                        .active(true)
                        .build();
                subscriptionPlanRepository.save(threeMonth);
                log.info("✓ Created subscription plan: 3-Month Plan ($80.99)");

                // 6-Month Plan
                SubscriptionPlan sixMonth = SubscriptionPlan.builder()
                        .name("6-Month Plan")
                        .description("Access to gym facilities for 6 months with 15% discount")
                        .durationDays(180)
                        .priceCents(15299) // $152.99 (15% off)
                        .includesClasses(false)
                        .active(true)
                        .build();
                subscriptionPlanRepository.save(sixMonth);
                log.info("✓ Created subscription plan: 6-Month Plan ($152.99)");

                // Annual Plan
                SubscriptionPlan annual = SubscriptionPlan.builder()
                        .name("Annual Plan")
                        .description("Access to gym facilities for 1 year with 20% discount")
                        .durationDays(365)
                        .priceCents(28799) // $287.99 (20% off)
                        .includesClasses(false)
                        .active(true)
                        .build();
                subscriptionPlanRepository.save(annual);
                log.info("✓ Created subscription plan: Annual Plan ($287.99)");

                // Premium Monthly (with classes)
                SubscriptionPlan premiumMonthly = SubscriptionPlan.builder()
                        .name("Premium Monthly")
                        .description("Access to gym facilities + unlimited group classes for 1 month")
                        .durationDays(30)
                        .priceCents(4999) // $49.99
                        .includesClasses(true)
                        .active(true)
                        .build();
                subscriptionPlanRepository.save(premiumMonthly);
                log.info("✓ Created subscription plan: Premium Monthly ($49.99)");

                // Premium Annual (with classes)
                SubscriptionPlan premiumAnnual = SubscriptionPlan.builder()
                        .name("Premium Annual")
                        .description("Access to gym facilities + unlimited group classes for 1 year with 25% discount")
                        .durationDays(365)
                        .priceCents(44999) // $449.99 (25% off)
                        .includesClasses(true)
                        .active(true)
                        .build();
                subscriptionPlanRepository.save(premiumAnnual);
                log.info("✓ Created subscription plan: Premium Annual ($449.99)");

                log.info("✓ All default subscription plans created successfully.");
            } else {
                log.info("✓ Subscription plans already exist ({} plans found).", existingPlans);
            }
        } catch (Exception e) {
            log.error("Error initializing subscription plans: {}", e.getMessage(), e);
        }
    }
}
