package com.nsnt.cosmos.api.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nsnt.cosmos.api.request.PublicMemberRegisterDto;
import com.nsnt.cosmos.api.request.PublicStudyRoomRegisterDto;
import com.nsnt.cosmos.api.request.SavePublicStudyMemberDto;
import com.nsnt.cosmos.api.service.PublicRoomService;
import com.nsnt.cosmos.common.auth.SsafyUserDetails;
import com.nsnt.cosmos.common.model.response.BaseResponseBody;
import com.nsnt.cosmos.db.entity.PublicMember;
import com.nsnt.cosmos.db.entity.PublicStudyRoom;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 공개 스터디 룸 관련 API 요청 처리를 위한 컨트롤러 정의.
 */
@Api(value = "공개 스터디 룸 API", tags = { "PublicRoom" })
@RestController
@RequestMapping("/api/publicroom")
public class PublicRoomController {
	public static final Logger logger = LoggerFactory.getLogger(PublicRoomController.class);
	private static final String SUCCESS = "Success";
	private static final String FAIL = "fail";

	@Autowired
	PublicRoomService publicRoomService;
	
	@PostMapping("/register/publicRoom")
	@ApiOperation(value = "공개 스터디룸 생성 (token)", notes = "공개 스터디룸 생성")
	@ApiResponses({ @ApiResponse(code = 200, message = "성공"), 
					@ApiResponse(code = 401, message = "인증 실패"),
					@ApiResponse(code = 404, message = "원하는 정보 없음"), 
					@ApiResponse(code = 500, message = "서버 오류")})
	public ResponseEntity<? extends BaseResponseBody> PublicRoomRegister(@RequestBody PublicStudyRoomRegisterDto publicstudyroomdDto, @ApiIgnore Authentication authentication) {
		SsafyUserDetails userDetails = (SsafyUserDetails) authentication.getDetails();
		String user_id = userDetails.getUsername();
		
		PublicStudyRoom pubicstudyroom;
		try {
			System.out.println(publicstudyroomdDto.toString());
			pubicstudyroom = publicRoomService.createPublicStudyRoom(publicstudyroomdDto, user_id);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>"+pubicstudyroom.toString());
		}catch(Exception E) {
			E.printStackTrace();
			System.out.println("공개 스터디룸 생성 실패");
			return  ResponseEntity.status(500).body(BaseResponseBody.of(500, FAIL));
		}
		System.out.println("잘 됨?"+ pubicstudyroom.toString());
		return ResponseEntity.status(200).body(BaseResponseBody.of(200, SUCCESS));
	}
	
	@PostMapping("/register/publicMember")
	@ApiOperation(value="공개 스터디 참가자 등록 (token)", notes="<strong>공개 스터디 참가자  등록</strong>시켜줍니다.")
	@ApiResponses({ @ApiResponse(code = 200, message = "성공"), 
					@ApiResponse(code = 401, message = "인증 실패"),
					@ApiResponse(code = 404, message = "원하는 정보 없음"), 
					@ApiResponse(code = 500, message = "서버 오류")})
	public ResponseEntity<? extends BaseResponseBody> publicMemberRegister(@RequestBody PublicMemberRegisterDto publicMemberDto, @ApiIgnore Authentication authentication)
	{
		SsafyUserDetails userDetails = (SsafyUserDetails) authentication.getDetails();
		String user_id = userDetails.getUsername();
		
		try {
			publicRoomService.createPublicMember(publicMemberDto, user_id);
		}catch(Exception E) {
			E.printStackTrace();
			System.out.println("공개 스터디 참가자 추가 실패");
			return  ResponseEntity.status(500).body(BaseResponseBody.of(500, FAIL));
		}
		return ResponseEntity.status(200).body(BaseResponseBody.of(200, SUCCESS));
	}
	
	 /** 해당 공개 스터디 참가자 전체 조회 입니다. */
	@ApiOperation(value="해당 공개 스터디 참가자 전체 조회(param)", notes="<strong>해당 공개 스터디 참가자  전체 조회를</strong>시켜줍니다.")
	@ApiResponses({ @ApiResponse(code = 200, message = "성공"), 
					@ApiResponse(code = 401, message = "인증 실패"),
					@ApiResponse(code = 404, message = "원하는 정보 없음"), 
					@ApiResponse(code = 500, message = "서버 오류")})
	@GetMapping("/search/publicMember")
    public ResponseEntity<List<PublicMember>> findAllPublicMember(@RequestParam String publicstudyroom_id){
        List<PublicMember> publicmember = publicRoomService.findAllPublicMember(publicstudyroom_id);
        return new ResponseEntity<List<PublicMember>>(publicmember,HttpStatus.OK);
    }
	
	 /** 공개 스터디 방 전체 조회 입니다. */
	@ApiOperation(value="공개 스터디 방 전체 조회", notes="<strong>공개 스터디 방 전체 조회를</strong>시켜줍니다.")
	@ApiResponses({ @ApiResponse(code = 200, message = "성공"), 
					@ApiResponse(code = 401, message = "인증 실패"),
					@ApiResponse(code = 404, message = "원하는 정보 없음"), 
					@ApiResponse(code = 500, message = "서버 오류")})
	@GetMapping("/search/searchAll/publicRoom")
	public ResponseEntity<List<PublicStudyRoom>> findAllPublicRoom(){
	     List<PublicStudyRoom> publicRoom = publicRoomService.findAllPublicStudyRoom();
	     return new ResponseEntity<List<PublicStudyRoom>>(publicRoom, HttpStatus.OK);
	}
		
	/** 해당 공개 스터디 참가자 명단 삭제 **/
	@ApiOperation(value = "해당 공개 스터디 참가자명단에서 해당 유저 삭제(param)", notes = "해당 공개 스터디 참가자명단에서 해당 유저 삭제")
	@ApiResponses({ @ApiResponse(code = 200, message = "해당 비공개 스터디 참가자 명단 삭제 성공"), 
					@ApiResponse(code = 401, message = "인증 실패"),
					@ApiResponse(code = 404, message = "사용자 없음"), 
					@ApiResponse(code = 500, message = "해당 회원 없음")})
	@DeleteMapping("/remove/publicMember")
	public ResponseEntity<String> removePublicMember(@RequestParam String publicstudyroom_id, @RequestParam String user_id) throws Exception {	
		try {	
			publicRoomService.deletePublicMember(publicstudyroom_id, user_id);
		}catch(Exception e ) {
			e.printStackTrace();
			System.out.println("해당 공개 스터디 참가자  제거 실패");
			return  ResponseEntity.status(500).body("해당 비공개 스터디 참가자 제거 "+FAIL);
		}
		logger.debug("해당 공개 스터디 참가자 명단에서 해당 user 삭제 성공");
		return ResponseEntity.status(200).body(user_id+"가 방에 나가서 공개 스터디 멤버에서 제거 함"+SUCCESS);
	}
	
	/** 해당 공개 스터디방에 참가자가 아무도 없으면 해당 공개 스터디방 삭제 **/
	@ApiOperation(value = "해당 공개 스터디방에 참가자가 아무도 없으면 해당 공개 스터디방 삭제(param)", notes = "해당 공개 스터디방에 참가자가 아무도 없으면 해당 공개 스터디방 삭제")
	@ApiResponses({ @ApiResponse(code = 200, message = "해당 공개 스터디 방 삭제 성공"), 
					@ApiResponse(code = 401, message = "인증 실패"),
					@ApiResponse(code = 404, message = "사용자 없음"), 
					@ApiResponse(code = 500, message = "해당 회원 없음")})
	@DeleteMapping("/remove/publicRoom")
	public ResponseEntity<String> removePublicRoom(@RequestParam String publicstudyroom_id) throws Exception {	
		try {	
			publicRoomService.deletePublicRoom(publicstudyroom_id);
		}catch(Exception e ) {
			e.printStackTrace();
			System.out.println("해당 공개 스터디 참가자  제거 실패");
			return  ResponseEntity.status(500).body("해당 공개 스터디방 제거 "+FAIL);
		}
		logger.debug("해당 공개 스터디 참가자 명단에서 해당 user 삭제 성공");
		return ResponseEntity.status(200).body("방에  남은 인원이 없어 "+ publicstudyroom_id +" 공개 스터디 방 삭제 함"+SUCCESS);
	}
	
	/** 공개 스터디방 강퇴 유저 히스토리 **/
	@PostMapping("/register/bannedUser")
	@ApiOperation(value="강퇴 유저 히스토리 등록 (token)(param)", notes="<strong>해당 공개 스터디방으로부터 강퇴 당한 유저를 히스토리</strong>에 추가시켜줍니다.")
	@ApiResponses({ @ApiResponse(code = 200, message = "성공"), 
					@ApiResponse(code = 401, message = "인증 실패"),
					@ApiResponse(code = 404, message = "원하는 정보 없음"), 
					@ApiResponse(code = 500, message = "서버 오류")})
	public ResponseEntity<? extends BaseResponseBody> bannedUserRegister(@RequestParam String publicstudyroom_id, @RequestParam String user_id) {
		try {
			publicRoomService.createBannedUser(publicstudyroom_id, user_id);
		}catch(Exception E) {
			E.printStackTrace();
			System.out.println("강퇴 유저 추가 실패");
			return  ResponseEntity.status(500).body(BaseResponseBody.of(500, FAIL));
		}
		return ResponseEntity.status(200).body(BaseResponseBody.of(200, SUCCESS));
	}
	
	/** 현재 유저에 대해서 해당 공개 스터디 강퇴 여부 **/
	@GetMapping("/bannedCheck")
	@ApiOperation(value = "회원 강퇴 여부 체크(token)(param)", notes = "회원 오픈 채팅방 입장시 이전 강퇴 여부 체크. 해당 오픈 채팅에 대해 강퇴된 유저라면 true, 아니라면 false를 리턴한다.")
	@ApiResponses({ @ApiResponse(code = 200, message = "성공"),
					@ApiResponse(code = 401, message = "인증 실패"),
					@ApiResponse(code = 404, message = "사용자 없음"),
					@ApiResponse(code = 500, message = "서버 오류") 
					})
	public ResponseEntity<Boolean> bannedCheck(@RequestParam("publicstudyroom_id") String publicstudyroom_id, @ApiIgnore Authentication authentication) throws Exception {	
		if(authentication==null) return  ResponseEntity.status(401).body(false);
		
		SsafyUserDetails userDetails = (SsafyUserDetails) authentication.getDetails();
		String user_id = userDetails.getUsername();
		
		boolean isBanned = publicRoomService.isBannedCheck(publicstudyroom_id, user_id);
		
		if (isBanned == true) {
			System.out.println("이전에 강퇴된 유저입니다. 해당 채팅방에 들어갈 수 없습니다.");
		} else {
			System.out.println("이전에 강퇴된 기록이 없습니다. 해당 채팅방에 들어갈 수 있습니다.");
		}
		return ResponseEntity.status(200).body(isBanned);
	}
	
	
	@ApiOperation(value = "해당 공개 스터디 멤버에게 강퇴 임시 권한 수정 기능 (publicmember_no, leader)2개  인자 (param)", notes = "해당 공개 스터디 멤버에게 강퇴 임시 권한  수정 기능")
	@ApiResponses({ @ApiResponse(code = 200, message = "성공"), 
					@ApiResponse(code = 401, message = "인증 실패"),
					@ApiResponse(code = 404, message = "사용자 없음"),
					@ApiResponse(code = 500, message = "서버 오류") })
	@PutMapping("/updateAuthority")
	public ResponseEntity<String> updateAuthority(@RequestBody SavePublicStudyMemberDto savePublicStudyMemberDto) throws Exception {
		PublicMember publicstudymember;
	
		try {
			publicstudymember = publicRoomService.findOnePublicStudyMember(savePublicStudyMemberDto.getPublicmember_no());
		} catch (NoSuchElementException E) {
			return ResponseEntity.status(500).body("공개 스터디 멤버 권한 수정 실패");
		}
		PublicMember updateStudyMember = publicRoomService.updatePublicStudyMemberAuthority(publicstudymember, savePublicStudyMemberDto);
		System.out.println("업데이트 됨");
		return new ResponseEntity<String>(SUCCESS + "\n" + updateStudyMember.toString(), HttpStatus.OK);
	}
}