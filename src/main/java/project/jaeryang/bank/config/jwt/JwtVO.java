package project.jaeryang.bank.config.jwt;

/**
 * SECRET은 노출되면 안된다.
 * 환경 변수나 파일을 읽거나, 클라우드에 등록
 * refresh token 구현 x
 */
public interface JwtVO {
    public static final String JWT_SECRET = "secret"; //HS256(대칭키. 토큰 생성과 검증을 서버에서 하므로 비대칭키 필요x)
    public static final int EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; //일주일
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

}
