package kr.or.ddit.dao;

import kr.or.ddit.vo.MemberVO;

public interface IMemberDAO {
	public int insertMember(MemberVO memberVO);
	public MemberVO loginCheck(MemberVO memberVO);
}
