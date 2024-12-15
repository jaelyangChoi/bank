package project.jaeryang.bank.domain.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.jaeryang.bank.domain.common.BaseTimeEntity;

@NoArgsConstructor
@Getter
@Table(name = "user_tb")
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String username;

    @Column(nullable = false, length = 70) //패스워드 인코딩(BCrypt)
    private String password;

    @Column(nullable = false, length = 20)
    private String email;

    @Column(nullable = false, length = 20)
    private String fullname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserEnum role; //ADMIN, CUSTOMER

    @Builder
    public User(Long id, String username, String password, String email, String fullname, UserEnum role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullname = fullname;
        this.role = role;
    }
}
