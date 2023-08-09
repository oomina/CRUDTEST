package kr.or.ddit.service;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import kr.or.ddit.ServiceResult;
import kr.or.ddit.dao.IMemberDAO;
import kr.or.ddit.vo.MemberVO;

@Service
public class MemberServiceImpl implements IMemberService {
	
	@Inject
	private IMemberDAO memberDao;

	@Override
	public ServiceResult insertMember(MemberVO memberVO) {
		ServiceResult result = null;
		int status = memberDao.insertMember(memberVO);
		if(status > 0) {
			result = ServiceResult.OK;
		}else {
			result = ServiceResult.FAILED;
		}
		return result;
	}

	@Override
	public MemberVO loginCheck(MemberVO memberVO) {
		return memberDao.loginCheck(memberVO);
	}
}
