package sillenceSoft.schedulleCall.Service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import sillenceSoft.schedulleCall.Dto.StatusDto;
import sillenceSoft.schedulleCall.Repository.AccessRepository;
import sillenceSoft.schedulleCall.Repository.StatusRepository;
import sillenceSoft.schedulleCall.Repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatusService {
    private final StatusRepository statusRepository;
    private final UserRepository userRepository;
    private final AccessRepository accessRepository;
    private final SHA_256 sha256;


    public List<Map<String,String>> getAllStatus (Integer userNo) {

        List<Map<String,String>> allStatus = statusRepository.getAllStatus(userNo);
        return allStatus;
    }

    public ResponseEntity addStatus (Integer userNo, String newStatusMemo) {

        ResponseEntity responseEntity;
        StatusDto statusDto = StatusDto.builder()
                    .userNo(userNo)
                    .status(newStatusMemo)
                    .modDt(LocalDateTime.now())
                    .build();
        try {
            statusRepository.addStatus(statusDto);
            responseEntity = new ResponseEntity(statusDto, HttpStatus.OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            responseEntity = new ResponseEntity("fail to add status", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }


    public String deleteStatus ( int statusNo) {
        String msg;
        try {
            statusRepository.deleteStatus( statusNo);
            msg="success";
        } catch (Exception e) {
            e.printStackTrace();
            msg="fail to delete status";
        }
        return msg;
    }

    public String  updateStatus (String status, int statusNo) {
        String msg;
        try {
            statusRepository.updateStatus(status, statusNo, LocalDateTime.now());
            msg="success";
        }
        catch (Exception e) {
            e.printStackTrace();
            msg="fail to update status";
        }
        return msg;
    }

    public String statusOn (Integer userNo) {
        String msg;
        try {
            userRepository.setStatusOn(userNo);
            msg="success";
        }
        catch (Exception e) {
            e.printStackTrace();
            msg="fail to set auto status";
        }
        return msg;
    }

    public String statusOff (Integer userNo) {
        String msg;
        try {
            userRepository.setStatusOff(userNo);
            msg="success";
        }
        catch (Exception e) {
            e.printStackTrace();
            msg="fail to set auto status";
        }
        return msg;
    }

    public Map<String,String> getOthersStatus (Integer userNo, String phone, HttpServletResponse res) throws IOException {
        //userNo유저가  phone유저의 상태를 보려고 하는상황
        Integer check = accessRepository.checkAccessOrNot(sha256.encrypt( phone), userNo);
        System.out.println("check="+check);
        Map<String,String> result = null;
        if (check != null) {
            result = statusRepository.getNowStatus(check);
            if (result == null)  {
                res.sendError(404,"사용자의 현재 상태가 존재하지 않습니다");
                return null;
            }
            else return result;
        } else { //접근 자체가 불가할때
            res.sendError(404,"해당 사용자의 상태를 볼 수 없습니다");
            return null;
        }
    }

    public List<Map<String,String>> getAllOthersStatus (Integer thisUserNo) {
        List<Map<String, String>> allOthersStatus = statusRepository.getAllOthersStatus(thisUserNo);
        return allOthersStatus;
    }



}
