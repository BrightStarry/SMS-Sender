package com.zuma.sms.controller;

import com.zuma.sms.controller.base.BaseController;
import com.zuma.sms.dto.PageVO;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.entity.Dict;
import com.zuma.sms.form.DictUpdateForm;
import com.zuma.sms.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * author:ZhengXing
 * datetime:2017/12/8 0008 16:27
 * 字典表
 */
@Controller
@RequestMapping("/dict")
@Slf4j
public class DictController extends BaseController{

	@Autowired
	private DictService dictService;




	/**
	 * 查询单个
	 */
	@RequestMapping("/query")
	@ResponseBody
	public ResultDTO<Dict> findOne(Long id) {

		Dict dict = dictService.findOne(id);
		return ResultDTO.success(dict);
	}

	/**
	 * 修改
	 */
	@PostMapping("/update")
	@ResponseBody
	public ResultDTO update(@Valid DictUpdateForm form, BindingResult bindingResult) {
		isValid(bindingResult,log,"");
		dictService.update(form);
		return ResultDTO.success();
	}


	/**
	 * 进入列表
	 */
	@GetMapping("/list")
	public String listView(Model model) {
		model.addAttribute(navTop2, "dict");
		return "dict/list";
	}

	/**
	 * 分页查询
	 */
	@GetMapping("/list/{pageNo}")
	public String list(@PathVariable int pageNo, Integer pageSize, Model model) {
		Pageable pageRequest = getPageRequest(pageNo, pageSize);
		PageVO<Dict> page = dictService.findPage(pageRequest);
		model.addAttribute("page", page);
		return "dict/table";
	}


	/**
	 * 根据备注模糊查询
	 */
	@GetMapping("/search/remark/{remark}")
	public String searchByName(@PathVariable String remark,Model model) {
		notEmptyOfString(remark);
		PageVO<Dict> page = dictService.searchByRemark(remark);
		model.addAttribute("page", page);
		return "dict/table";
	}
}
