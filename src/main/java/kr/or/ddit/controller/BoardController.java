package kr.or.ddit.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import kr.or.ddit.ServiceResult;
import kr.or.ddit.service.IBoardService;
import kr.or.ddit.vo.BoardVO;
import kr.or.ddit.vo.MemberVO;
import kr.or.ddit.vo.PaginationInfoVO;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/board")
@Slf4j
public class BoardController {
	
	@Inject
	private IBoardService boardService;
	
	@RequestMapping(value ="/list.do")
	public String list(
			@RequestParam(name="page", required = false, defaultValue = "1") int currentPage,
			@RequestParam(required = false, defaultValue = "title") String searchType,
			@RequestParam(required = false) String searchWord,
			Model model) {
		
		PaginationInfoVO<BoardVO> pagingVO = new PaginationInfoVO<BoardVO>();
		
		if(StringUtils.isNotBlank(searchWord)) {
			pagingVO.setSearchType(searchType);
			pagingVO.setSearchWord(searchWord);
			model.addAttribute("searchType", searchType);
			model.addAttribute("searchWord", searchWord);
		}
		
		pagingVO.setCurrentPage(currentPage);
		int totalRecord = boardService.selectBoardCount(pagingVO);
		pagingVO.setTotalRecord(totalRecord);
		List<BoardVO> dataList = boardService.selectBoardList(pagingVO);
		pagingVO.setDataList(dataList);
		model.addAttribute("pagingVO", pagingVO);
		
		return "list";
	}
	
	@RequestMapping(value = "/detail.do", method = RequestMethod.GET)
	public String detail(int boNo, Model model) {
		BoardVO boardVO = boardService.selectBoard(boNo);
		model.addAttribute("board", boardVO);
		return "detail";
	}
	
	@RequestMapping(value = "/form.do", method = RequestMethod.GET)
	public String form() {
		return "form";
	}
	
	@RequestMapping(value ="/insert.do", method = RequestMethod.POST)
	public String boardInsert(BoardVO boardVO, Model model, HttpSession session) {
		log.info("boardVO.getBoTitle : " + boardVO.getBoTitle());
		log.info("boardVO.getBoContent : " + boardVO.getBoContent());
		
		MemberVO memberVO = (MemberVO)session.getAttribute("member");
		String goPage = "";
		Map<String, String> errors = new HashMap<String, String>();
		
		if(StringUtils.isBlank(boardVO.getBoTitle())){
			errors.put("boTitle", "제목을 입력해주세요!");
		}
		if(StringUtils.isBlank(boardVO.getBoContent())){
			errors.put("boContent", "내용을 입력해주세요!");
		}
		
		if(errors.size() > 0) {
			model.addAttribute("errors", errors);
			model.addAttribute("board", boardVO);
			goPage = "form";
		}else {
			boardVO.setBoWriter(memberVO.getMemId());
			ServiceResult result = boardService.insertBoard(boardVO);
			if(result.equals(ServiceResult.OK)) {
				goPage = "redirect:/board/detail.do?boNo=" + boardVO.getBoNo();
			} else {
				errors.put("msg", "서버에러! 다시 시도해주세요!");
				model.addAttribute("errors", errors);
				goPage = "form";
			}
		}
		return goPage;
	}
	
	@RequestMapping(value = "/update.do", method = RequestMethod.GET)
	public String boardUpdateForm(int boNo, Model model) {
		BoardVO boardVO = boardService.selectBoard(boNo);
		model.addAttribute("board", boardVO);
		model.addAttribute("status", "u");
		return "form";
	}
	
	@RequestMapping(value = "/update.do", method = RequestMethod.POST)
	public String boardUpdate(BoardVO boardVO, Model model) {
		String goPage = "";
		ServiceResult result = boardService.updateBoard(boardVO);
		if(result.equals(ServiceResult.OK)) {
			goPage = "redirect:/board/detail.do?boNo=" + boardVO.getBoNo();
		}else {
			model.addAttribute("board", boardVO);
			model.addAttribute("status", "u");
			goPage = "form";
		}
		return goPage;
	}
	
	@RequestMapping(value = "/delete.do", method = RequestMethod.POST)
	public String boardDelete(int boNo, Model model) {
		String goPage = "";
		ServiceResult result = boardService.deleteBoard(boNo);
		if(result.equals(ServiceResult.OK)) {
			goPage = "redirect:/board/list.do";
		}else {
			goPage = "redirect:/board/detail.do?boNo="+boNo;
		}
		return goPage;
	}
	
}
