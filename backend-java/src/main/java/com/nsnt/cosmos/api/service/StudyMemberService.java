package com.nsnt.cosmos.api.service;

import java.util.List;

import com.nsnt.cosmos.api.request.SaveBoardDto;
import com.nsnt.cosmos.api.request.SaveStudyMemberDto;
import com.nsnt.cosmos.db.entity.Board;
import com.nsnt.cosmos.db.entity.StudyMember;


/**
 *	스터디 멤버 관리 관련 비즈니스 로직 처리를 위한 서비스 인터페이스 정의.
 */
public interface StudyMemberService {
	/** 스터디 멤버 테이블에 해당 스터디원 추가하는 insertStudyMember 입니다. */
    public StudyMember insertStudyMember(SaveStudyMemberDto saveStudyMemberDto);
     

    /** 해당 회원들의 정보를 가져오는 findStudyMmeber 입니다. (목록 부분에 사용)*/
    public List<StudyMember> findStudyMmeber(Long study_no);

    /** 스터디 멤버 점수 수정하는 updateStudyMemberScore 입니다.*/
    public StudyMember updateStudyMemberScore(StudyMember studymember, SaveBoardDto saveBoardDto);
      

    /** 스터디 멤버 회원을삭제하는 deleteStudyMember 입니다. */
    public void deleteStudyMember(StudyMember studymember);
}
