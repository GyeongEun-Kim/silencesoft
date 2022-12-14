package sillenceSoft.schedulleCall.Controller;

import com.sun.net.httpserver.Headers;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.catalina.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.header.Header;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import sillenceSoft.schedulleCall.Dto.UserDto;
import sillenceSoft.schedulleCall.Dto.UserRequestDto;
import sillenceSoft.schedulleCall.Repository.RefreshTokenRepository;
import sillenceSoft.schedulleCall.Service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Getter
@Setter
public class UserController {

    private final UserService userService;
    private final GoogleUserInfo googleUserInfo;
    private final KakaoUserInfo kakaoUserInfo;
    private final JWTProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final StatusService statusService;


    @PostMapping(value = "/login", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login (@ModelAttribute UserRequestDto userRequestDto,HttpServletResponse response) throws NoSuchAlgorithmException {

        UserDto userDto = userService.login(userRequestDto); //유저 정보
        String accessToken = jwtProvider.createAccessToken(userDto.getId(), userDto.getRegTime().toString());
        String refreshToken = jwtProvider.createRefreshToken(userDto.getId(), userDto.getRegTime().toString());

        jwtProvider.setHeaderAccessToken(accessToken,response );
        jwtProvider.setHeaderRefreshToken(refreshToken, response);


        System.out.println("accessToken = " + accessToken);

        System.out.println("refreshToken = " + refreshToken);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/jwt-check") //리프레시 토큰의 유효성 확인
    public void jwtCheck (HttpServletRequest request,HttpServletResponse response) throws IOException {
        userService.jwtCheck(request,response);
    }

    @PostMapping("/nowStatus") //현재 상태글 변경
    public String setNowStatus (Authentication authentication,
                              @RequestParam(name = "statusNo") int statusNo) {
        Integer userNo = jwtProvider.getUserNo(authentication);
        return userService.setNowStatus(userNo, statusNo);
    }


    @GetMapping("/user/status/show") //statusOn 이 1인지 0인지 확인. 공개상태인지 비공개인지
    public Map<String,Object> getStatusOnOff (Authentication authentication) {
        Integer userNo = jwtProvider.getUserNo(authentication);
        return userService.getStatusOn(userNo);

    }

    @PostMapping("/user/status/show") //statusOn을 1또는 0으로 바꿈
    public String statusOn (Authentication authentication) {
        Integer userNo = jwtProvider.getUserNo(authentication);
        return userService.statusShow(userNo);
    }

    }
