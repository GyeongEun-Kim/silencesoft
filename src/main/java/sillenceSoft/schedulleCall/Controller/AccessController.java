package sillenceSoft.schedulleCall.Controller;

import io.jsonwebtoken.Claims;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import sillenceSoft.schedulleCall.Dto.UserDto;
import sillenceSoft.schedulleCall.Service.AccessService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Getter
@Setter
public class AccessController {
    private final AccessService accessService;

    @PostMapping("/access")
    public String canAccess (Authentication authentication, Integer accessUserNo) {

        return accessService.canAccess(authentication, accessUserNo);
    }

    @DeleteMapping("/access")
    public String cannotAccess (Authentication authentication, Integer accessUserNo)
    {
        return accessService.cannotAccess(authentication, accessUserNo);
    }

    @GetMapping("/access")
    public List<UserDto> getAccessList(Authentication authentication) {
        Claims principal = (Claims) authentication.getPrincipal();
        String id = (String) principal.get("id");
        return accessService.getAccessList(id);

    }
}
