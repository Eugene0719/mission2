/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package egovframework.example.sample.web;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springmodules.validation.commons.DefaultBeanValidator;

import egovframework.example.sample.service.EgovSampleService;
import egovframework.example.sample.service.SampleDefaultVO;
import egovframework.example.sample.service.SampleVO;
import egovframework.rte.fdl.property.EgovPropertyService;
import egovframework.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

/**
 * @Class Name : EgovSampleController.java
 * @Description : EgovSample Controller Class
 * @Modification Information
 * @ @ 수정일 수정자 수정내용 @ --------- --------- ------------------------------- @
 *   2009.03.16 최초생성
 *
 * @author 개발프레임웍크 실행환경 개발팀
 * @since 2009. 03.16
 * @version 1.0
 * @see
 *
 * 		Copyright (C) by MOPAS All right reserved.
 */

@Controller
public class EgovSampleController {
	/** EgovSampleService */
	@Resource(name = "sampleService")
	private EgovSampleService sampleService;

	/** EgovPropertyService */
	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;

	/** Validator */
	@Resource(name = "beanValidator")
	protected DefaultBeanValidator beanValidator;

	
	
	@RequestMapping(value = "/excelDown.do")
	   public void excelDown(HttpServletResponse response, @ModelAttribute("searchVO") SampleDefaultVO searchVO) throws Exception {

	      // 게시판 목록조회
	      /** EgovPropertyService.sample */
	      searchVO.setPageUnit(propertiesService.getInt("pageUnit"));
	      searchVO.setPageSize(propertiesService.getInt("pageSize"));

	      /** pageing setting */
	      PaginationInfo paginationInfo = new PaginationInfo();
	      paginationInfo.setCurrentPageNo(searchVO.getPageIndex());
	      paginationInfo.setRecordCountPerPage(searchVO.getPageUnit());
	      paginationInfo.setPageSize(searchVO.getPageSize());

	      searchVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
	      searchVO.setLastIndex(paginationInfo.getLastRecordIndex());
	      searchVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());
	      
	      //List<?> sampleList = sampleService.selectSampleList(searchVO);
	      List<SampleVO> list = sampleService.excelDown(searchVO);
	      
	      // 워크북 생성
	      Workbook wb = new HSSFWorkbook();
	      Sheet sheet = wb.createSheet("게시판");
	      Row row = null;
	      Cell cell = null;
	      int rowNo = 0;

	      // 테이블 헤더용 스타일
	      CellStyle headStyle = wb.createCellStyle();
	      // 가는 경계선을 가집니다.
	      headStyle.setBorderTop(BorderStyle.THIN);
	      headStyle.setBorderBottom(BorderStyle.THIN);
	      headStyle.setBorderLeft(BorderStyle.THIN);
	      headStyle.setBorderRight(BorderStyle.THIN);

	      // 배경색은 노란색입니다.
	      headStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.YELLOW.getIndex());
	      headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

	      // 데이터는 가운데 정렬합니다.
	      headStyle.setAlignment(HorizontalAlignment.CENTER);

	      // 데이터용 경계 스타일 테두리만 지정
	      CellStyle bodyStyle = wb.createCellStyle();
	      bodyStyle.setBorderTop(BorderStyle.THIN);
	      bodyStyle.setBorderBottom(BorderStyle.THIN);
	      bodyStyle.setBorderLeft(BorderStyle.THIN);
	      bodyStyle.setBorderRight(BorderStyle.THIN);

	      // 헤더 생성
	      row = sheet.createRow(rowNo++);
	      cell = row.createCell(0);
	      cell.setCellStyle(headStyle);
	      cell.setCellValue("유형구분");
	      cell = row.createCell(1);
	      cell.setCellStyle(headStyle);
	      cell.setCellValue("봉사단계");
	      cell = row.createCell(2);
	      cell.setCellStyle(headStyle);
	      cell.setCellValue("이름");
	      cell = row.createCell(3);
	      cell.setCellStyle(headStyle);
	      cell.setCellValue("생년월일");
	      cell = row.createCell(4);
	      cell.setCellStyle(headStyle);
	      cell.setCellValue("아이디");
	      cell = row.createCell(5);
	      cell.setCellStyle(headStyle);
	      cell.setCellValue("이메일");
	      cell = row.createCell(6);
	      cell.setCellStyle(headStyle);
	      cell.setCellValue("번역구분");
	      cell = row.createCell(7);
	      cell.setCellStyle(headStyle);
	      cell.setCellValue("담당직원");
	      cell = row.createCell(8);
	      cell.setCellStyle(headStyle);
	      cell.setCellValue("가입일");

	      // 데이터 부분 생성
	      for(SampleVO vo : list) {  
	         row = sheet.createRow(rowNo++);
	         cell = row.createCell(0);
	         cell.setCellStyle(bodyStyle);
	         cell.setCellValue(vo.getType());
	         cell = row.createCell(1);
	         cell.setCellStyle(bodyStyle);
	         cell.setCellValue(vo.getVolunteerStep());
	         cell = row.createCell(2);
	         cell.setCellStyle(bodyStyle);
	         cell.setCellValue(vo.getName());
	         cell = row.createCell(3);
	         cell.setCellStyle(bodyStyle);
	         cell.setCellValue(vo.getBirth());
	         cell = row.createCell(4);
	         cell.setCellStyle(bodyStyle);
	         cell.setCellValue(vo.getEngName());
	         cell = row.createCell(5);
	         cell.setCellStyle(bodyStyle);
	         cell.setCellValue(vo.getEmail());
	         cell = row.createCell(6);
	         cell.setCellStyle(bodyStyle);
	         cell.setCellValue(vo.getTranslation());
	         cell = row.createCell(7);
	         cell.setCellStyle(bodyStyle);
	         cell.setCellValue(vo.getEmp());
	         cell = row.createCell(8);
	         cell.setCellStyle(bodyStyle);
	         cell.setCellValue(vo.getRegdate());
	      }

	      // 컨텐츠 타입과 파일명 지정
	      response.setContentType("ms-vnd/excel");
	      response.setHeader("Content-Disposition", "attachment;filename=ListSample.xls");

	      // 엑셀 출력
	      wb.write(response.getOutputStream());
	      wb.close();

	   }
	
	/**
	 * 글 목록을 조회한다. (pageing)
	 * 
	 * @param searchVO
	 *            - 조회할 정보가 담긴 SampleDefaultVO
	 * @param model
	 * @return "egovSampleList"
	 * @exception Exception
	 */
	@RequestMapping(value = "/egovSampleList.do")
	public String selectSampleList(@ModelAttribute("searchVO") SampleDefaultVO searchVO, ModelMap model)
			throws Exception {

		/** EgovPropertyService.sample */
		searchVO.setPageUnit(propertiesService.getInt("pageUnit"));
		searchVO.setPageSize(propertiesService.getInt("pageSize"));

		/** pageing setting */
		PaginationInfo paginationInfo = new PaginationInfo();
		paginationInfo.setCurrentPageNo(searchVO.getPageIndex());
		paginationInfo.setRecordCountPerPage(searchVO.getPageUnit());
		paginationInfo.setPageSize(searchVO.getPageSize());

		searchVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
		searchVO.setLastIndex(paginationInfo.getLastRecordIndex());
		searchVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

		List<?> sampleList = sampleService.selectSampleList(searchVO);
		model.addAttribute("resultList", sampleList);

		int totCnt = sampleService.selectSampleListTotCnt(searchVO);
		paginationInfo.setTotalRecordCount(totCnt);
		model.addAttribute("paginationInfo", paginationInfo);

		return "sample/egovSampleList";
	}

	/**
	 * 글 등록 화면을 조회한다.
	 * 
	 * @param searchVO
	 *            - 목록 조회조건 정보가 담긴 VO
	 * @param model
	 * @return "egovSampleRegister"
	 * @exception Exception
	 */
	@RequestMapping(value = "/addSample1.do", method = RequestMethod.POST)
	public String addSampleView(@ModelAttribute("searchVO") SampleDefaultVO searchVO, Model model) throws Exception {
		model.addAttribute("sampleVO", new SampleVO());
		return "sample/egovSampleRegister";
	}

	/**
	 * 글을 등록한다.
	 * 
	 * @param sampleVO
	 *            - 등록할 정보가 담긴 VO
	 * @param searchVO
	 *            - 목록 조회조건 정보가 담긴 VO
	 * @param status
	 * @return "forward:/egovSampleList.do"
	 * @exception Exception
	 */
	@RequestMapping(value = "/addSample.do", method = RequestMethod.POST)
	public String addSample(@ModelAttribute("searchVO") SampleDefaultVO searchVO, SampleVO sampleVO,
			BindingResult bindingResult, Model model, SessionStatus status, @RequestPart MultipartFile files) throws Exception {
		
		String sourceFileName = files.getOriginalFilename(); 
        String sourceFileNameExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase(); 
        File destinationFile;
        String destinationFileName;
        String fileUrl = "C:\\eGovFrameDev-3.8.0-64bit\\workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\sample\\upload\\";
        /*String fileUrl = "C:\\eGovFrameDev-3.8.0-64bit\\workspace\\sample\\src\\main\\webapp\\upload\\";*/
 
        do { 
            destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + sourceFileNameExtension; 
            destinationFile = new File(fileUrl + destinationFileName); 
            System.out.println("file경로"+destinationFile);
            sampleVO.setFileUrl(fileUrl+destinationFile);
            sampleVO.setFileOriName(sourceFileName);
            sampleVO.setFileName(destinationFileName);
        } while (destinationFile.exists());
        
        destinationFile.getParentFile().mkdirs(); 
        files.transferTo(destinationFile);
		
		// Server-Side Validation
		beanValidator.validate(sampleVO, bindingResult);

		if (bindingResult.hasErrors()) {
			model.addAttribute("sampleVO", sampleVO);
			return "sample/egovSampleRegister";
		}

		sampleService.insertSample(sampleVO);
		status.setComplete();
		return "forward:/egovSampleList.do";
	}

	/**
	 * 글 수정화면을 조회한다.
	 * 
	 * @param id
	 *            - 수정할 글 id
	 * @param searchVO
	 *            - 목록 조회조건 정보가 담긴 VO
	 * @param model
	 * @return "egovSampleRegister"
	 * @exception Exception
	 */
	@RequestMapping("/updateSampleView.do")
	public String updateSampleView(@RequestParam("selectedId") int id, @ModelAttribute("searchVO") SampleDefaultVO searchVO, Model model) throws Exception {
		SampleVO sampleVO = new SampleVO();
		sampleVO.setId(id);
		// 변수명은 CoC 에 따라 sampleVO
		model.addAttribute(selectSample(sampleVO, searchVO));
		return "sample/egovSampleRegister";
	}

	/**
	 * 글을 조회한다.
	 * 
	 * @param sampleVO
	 *            - 조회할 정보가 담긴 VO
	 * @param searchVO
	 *            - 목록 조회조건 정보가 담긴 VO
	 * @param status
	 * @return @ModelAttribute("sampleVO") - 조회한 정보
	 * @exception Exception
	 */
	public SampleVO selectSample(SampleVO sampleVO, @ModelAttribute("searchVO") SampleDefaultVO searchVO) throws Exception {
		return sampleService.selectSample(sampleVO);
	}

	/**
	 * 글을 수정한다.
	 * 
	 * @param sampleVO
	 *            - 수정할 정보가 담긴 VO
	 * @param searchVO
	 *            - 목록 조회조건 정보가 담긴 VO
	 * @param status
	 * @return "forward:/egovSampleList.do"
	 * @exception Exception
	 */
	@RequestMapping("/updateSample.do")
	public String updateSample(@ModelAttribute("searchVO") SampleDefaultVO searchVO, SampleVO sampleVO,BindingResult bindingResult, Model model, SessionStatus status) throws Exception {

		beanValidator.validate(sampleVO, bindingResult);

		if (bindingResult.hasErrors()) {
			model.addAttribute("sampleVO", sampleVO);
			return "sample/egovSampleRegister";
		}

		sampleService.updateSample(sampleVO);
		status.setComplete();
		return "forward:/egovSampleList.do";
	}

	/**
	 * 글을 삭제한다.
	 * 
	 * @param sampleVO
	 *            - 삭제할 정보가 담긴 VO
	 * @param searchVO
	 *            - 목록 조회조건 정보가 담긴 VO
	 * @param status
	 * @return "forward:/egovSampleList.do"
	 * @exception Exception
	 */
	@RequestMapping("/deleteSample.do")
	public String deleteSample(SampleVO sampleVO, @ModelAttribute("searchVO") SampleDefaultVO searchVO,
			SessionStatus status) throws Exception {
		sampleService.deleteSample(sampleVO);
		status.setComplete();
		return "forward:/egovSampleList.do";
	}

}
