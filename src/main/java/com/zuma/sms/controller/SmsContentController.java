package com.zuma.sms.controller;

import com.zuma.sms.form.SmsContentForm;
import com.zuma.sms.controller.base.BaseController;
import com.zuma.sms.dto.PageVO;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.entity.SmsContent;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.service.SmsContentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * author:ZhengXing
 * datetime:2017/12/8 0008 11:16
 * 话术
 */
@Controller
@RequestMapping("/smsContent")
@Slf4j
public class SmsContentController extends BaseController {

	@Autowired
	private SmsContentService smsContentService;

	/**
	 * 查询单个
	 */
	@RequestMapping("/query")
	@ResponseBody
	public ResultDTO<SmsContent> findOne(Long id) {
		SmsContent smscontent = smsContentService.findOne(id);
		return ResultDTO.success(smscontent);
	}

	/**
	 * 新增
	 */
	@PostMapping("/add")
	@ResponseBody
	public ResultDTO<?> add(@Valid SmsContentForm smsContentForm, BindingResult bindingResult) {
		isValid(bindingResult,log,"");
		smsContentService.save(smsContentForm);
		return ResultDTO.success();
	}

	/**
	 * 进入列表
	 */
	@GetMapping("/list")
	public String listView(Model model) {
		model.addAttribute(navTop2, "smsContent");
		return "smsContent/list";
	}

	/**
	 * 分页查询
	 */
	@GetMapping("/list/{pageNo}")
	public String list(@PathVariable int pageNo, Integer pageSize, Model model) {
		Pageable pageRequest = getPageRequest(pageNo, pageSize);
		PageVO<SmsContent> page = smsContentService.findPage(pageRequest);
		model.addAttribute("page", page);
		return "smsContent/table";
	}

	/**
	 * 批量删除
	 */
	@PostMapping("/delete")
	@ResponseBody
	@Transactional
	public ResultDTO<?> delete(@RequestParam("ids[]") Long[] ids) {
		if(ArrayUtils.isEmpty(ids))
			throw new SmsSenderException(ErrorEnum.ARRAY_EMPTY);
		smsContentService.batchDelete(ids);
		return ResultDTO.success();
	}

	/**
	 * 根据名字模糊查询
	 */
	@GetMapping("/search/name/{name}")
	public String searchByName(@PathVariable String name,Model model) {
		notEmptyOfString(name);
		PageVO<SmsContent> page = smsContentService.searchByName(name);
		model.addAttribute("page", page);
		return "smsContent/table";
	}


}
