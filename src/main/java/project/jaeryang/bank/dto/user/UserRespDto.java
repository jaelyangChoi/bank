package project.jaeryang.bank.dto.user;

import lombok.Getter;
import lombok.Setter;
import project.jaeryang.bank.domain.user.User;

public class UserRespDto {
    @Getter
    @Setter
    public static class JoinRespDto {
        private Long id;
        private String username;
        private String fullname;

        public JoinRespDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.fullname = user.getFullname();
        }
    }
}
