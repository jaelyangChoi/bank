package project.jaeryang.bank.config.dummy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import project.jaeryang.bank.domain.user.UserRepository;

@Configuration
public class DummyDevInit extends DummyObject {

    @Profile("dev")
    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            userRepository.save(newUser("cjl0701", "최재량"));
        };
    }
}
