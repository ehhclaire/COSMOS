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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nsnt.cosmos.api.request.CommentReq;
import com.nsnt.cosmos.api.response.CommentSearchDtoRes;
import com.nsnt.cosmos.api.service.CommentService;
import com.nsnt.cosmos.common.auth.SsafyUserDetails;
import com.nsnt.cosmos.common.model.response.BaseResponseBody;
import com.nsnt.cosmos.db.entity.Comment;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 스터디 모집 게시판 댓글 관련 API 요청 처리를 위한 컨트롤러 정의.
 */
@Api(value = "스터디 모집 게시판 댓글 API", tags = { "Comment" })
@RestController
@RequestMapping("/api/comment")
public class CommentController {
	public static final Logger logger = LoggerFactory.getLogger(BoardController.class);
	private static final String SUCCESS = "success";
	private static final String FAIL = "fail";
	
	@Autowired
	CommentService commentService;
	
	/** 댓글 등록 시 댓글 수 증가하기입니다. */
	@PostMapping("/register")
	@ApiOperation(value="댓글 등록 (token)", notes="<strong>댓글을 등록</strong>시켜줍니다. create_at은 빈 괄호(\"\")를 입력하여 주세요. ")
	@ApiResponses({ @ApiResponse(code = 200, message = "성공"), 
					@ApiResponse(code = 401, message = "인증 실패"),
					@ApiResponse(code = 404, message = "댓글 없음"), 
					@ApiResponse(code = 500, message = "서버 오류")})
	public ResponseEntity CommentRegister(@RequestBody CommentReq commentReq, @ApiIgnore Authentication authentication)
	{
		SsafyUserDetails userDetails = (SsafyUserDetails) authentication.getDetails();
		String user_id = userDetails.getUsername();
		
		Comment comment;
		try {
			commentReq.setUser_id(user_id);
			comment = commentService.createComment(commentReq);
		}catch(Exception E) {
			E.printStackTrace();
			return  ResponseEntity.status(500).body("디비 트랜잭션 오류로 인한 생성 실패");
		}
		return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success"));
	}
	
	/** 댓글 전체 조회 입니다. */
	@ApiOperation(value="댓글 전체 조회", notes="<strong>해당 게시글의 댓글을 전체 조회를</strong>시켜줍니다.")
	@ApiResponses({ @ApiResponse(code = 200, message = "성공"), 
					@ApiResponse(code = 401, message = "인증 실패"),
					@ApiResponse(code = 404, message = "댓글 없음"), 
					@ApiResponse(code = 500, message = "서버 오류")})
	@GetMapping("/searchAll/{board_no}")
    public ResponseEntity<List<CommentSearchDtoRes>> findAll(@PathVariable Long board_no){
        List<CommentSearchDtoRes> comments = commentService.findAllByBoardNo(board_no);
        return new ResponseEntity<List<CommentSearchDtoRes>>(comments,HttpStatus.OK);
    }

	/** 댓글 수정 입니다. */
	@ApiOperation(value = "댓글 정보 수정 (token)", notes = "댓글 정보 수정. user_id와 create_at은 빈 괄호(\"\")를 입력하여 주세요.")
	@ApiResponses({ @ApiResponse(code = 200, message = "성공"), 
					@ApiResponse(code = 401, message = "인증 실패"),
					@ApiResponse(code = 404, message = "댓글 없음"),
					@ApiResponse(code = 500, message = "서버 오류") })
	@PutMapping("/update")
	public ResponseEntity<String> boardupdate(@RequestBody CommentReq commentReq, @ApiIgnore Authentication authentication) throws Exception {
		SsafyUserDetails userDetails = (SsafyUserDetails) authentication.getDetails();
		String user_id = userDetails.getUsername();
		
		Comment comment;
		try {
			commentReq.setUser_id(user_id);
			comment = commentService.findByCommentNo(commentReq.getComment_no());
		}catch(NoSuchElementException E) {
			return  ResponseEntity.status(500).body("해당 댓글이 없어서 댓글 수정 실패");
		}
		Comment updateComment = commentService.updateComment(comment, commentReq);
		return new ResponseEntity<String>(SUCCESS+"\n"+updateComment.toString(), HttpStatus.OK);
	}
	
	/** 댓글 삭제및 해당 게시글 댓글 수 삭제 입니다. */
	@ApiOperation(value = "[해당 댓글] 삭제및, [해당 게시글 댓글 수] 삭제(comment_no, board_no) ", notes = "[해당 댓글] 삭제및, [해당 게시글 댓글 수] 삭제")
	@ApiResponses({ @ApiResponse(code = 200, message = "성공"), 
					@ApiResponse(code = 401, message = "인증 실패"),
					@ApiResponse(code = 404, message = "댓글 없음"), 
					@ApiResponse(code = 500, message = "서버 오류")})
	@DeleteMapping("/remove/{comment_no}/{board_no}")
	public ResponseEntity<String> boarddelete(@PathVariable("comment_no") int comment_no, @PathVariable("board_no") Long board_no) throws Exception {	
		Comment comment;
		try {
			comment = commentService.findByCommentNo(comment_no);
			commentService.deleteComment(comment, board_no);
		}catch(Exception e ) {
			e.printStackTrace();
			return  ResponseEntity.status(500).body("해당 댓글이 없어 삭제 "+FAIL);
		}
		return ResponseEntity.status(200).body(comment.getCommentNo()+"번 해당 댓글 삭제"+SUCCESS);
	}
	
}