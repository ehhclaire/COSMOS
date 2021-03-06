package com.nsnt.cosmos.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 공개 스터디 룸 모델 정의.
 */
@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(
	// 속성에 UNIQUE 제약조건 설정
	uniqueConstraints = {
		@UniqueConstraint(
			columnNames = {"url", "image"}
		)
	}
)
public class PublicStudyRoom {
	@Id
	@Column(name = "publicstudyroom_id")
	String publicstudyroomId;
	
	@Column(name = "study_name", nullable = false)
	String studyName;
	
	@Column(name = "url", length = 100, nullable = false)
	String url;
	
	@Column(name = "image", length = 100, nullable = false)
	String image;
	
	@Column(name = "number_of_member")
	int numberOfMember;
	
	@Column(name = "study_rule")
	String studyRule;

	@ManyToOne
	@JoinColumn(name = "studytype_no", referencedColumnName = "studytype_no")
	private StudyType studyType;
}
