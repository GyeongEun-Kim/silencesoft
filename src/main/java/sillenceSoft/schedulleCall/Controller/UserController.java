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

        UserDto userDto = userService.login(userRequestDto); //?????? ??????
        String accessToken = jwtProvider.createAccessToken(userDto.getId(), userDto.getRegTime().toString());
        String refreshToken = jwtProvider.createRefreshToken(userDto.getId(), userDto.getRegTime().toString());

        jwtProvider.setHeaderAccessToken(accessToken,response );
        jwtProvider.setHeaderRefreshToken(refreshToken, response);


        System.out.println("accessToken = " + accessToken);

        System.out.println("refreshToken = " + refreshToken);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/jwt-check") //???????????? ????????? ????????? ??????
    public void jwtCheck (HttpServletRequest request,HttpServletResponse response) throws IOException {
        userService.jwtCheck(request,response);
    }

    @PostMapping("/nowStatus") //?????? ????????? ??????
    public String setNowStatus (Authentication authentication,
                              @RequestParam(name = "statusNo") int statusNo) {
        Integer userNo = jwtProvider.getUserNo(authentication);
        return userService.setNowStatus(userNo, statusNo);
    }


    @GetMapping("/user/status/show") //statusOn ??? 1?????? 0?????? ??????. ?????????????????? ???????????????
    public Map<String,Object> getStatusOnOff (Authentication authentication) {
        Integer userNo = jwtProvider.getUserNo(authentication);
        return userService.getStatusOn(userNo);

    }

    @PostMapping("/user/status/show") //statusOn??? 1?????? 0?????? ??????
    public String statusOn (Authentication authentication) {
        Integer userNo = jwtProvider.getUserNo(authentication);
        return userService.statusShow(userNo);
    }

    }
