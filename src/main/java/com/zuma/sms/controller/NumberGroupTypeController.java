package com.zuma.sms.controller;

import com.zuma.sms.config.store.ConfigStore;
import com.zuma.sms.controller.base.BaseController;
import com.zuma.sms.dto.PageVO;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.entity.NumberGroupType;
import com.zuma.sms.entity.SmsContent;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.form.NumberGroupTypeForm;
import com.zuma.sms.form.SmsContentForm;
import com.zuma.sms.service.NumberGroupTypeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * author:ZhengXing
 * datetime:2017/12/8 0008 16:27
 * 号码组分类
 */
@Controller
@RequestMapping("/numberGroupType")
@Slf4j
public class NumberGroupTypeController extends BaseController{

	@Autowired
	private NumberGroupTypeService numberGroupTypeService;
	@Autowired
	private ConfigStore configStore;

	/**
	 * 查询单个
	 */
	@RequestMapping("/query")
	@ResponseBody
	public ResultDTO<NumberGroupType> findOne(Long id) {
		NumberGroupType numberGroupType = numberGroupTypeService.findOne(id);
		return ResultDTO.success(numberGroupType);
	}

	/**
	 * 新增
	 */
	@PostMapping("/add")
	@ResponseBody
	public ResultDTO<?> add(@Valid NumberGroupTypeForm form, BindingResult bindingResult) {
		isValid(bindingResult,log,"");
		numberGroupTypeService.save(form);
		return ResultDTO.success();
	}

	/**
	 * 进入列表
	 */
	@GetMapping("/list")
	public String listView(Model model, HttpSession session) {
		model.addAttribute(navTop2, "numberGroupType");
		return "numberGroupType/list";
	}

	/**
	 * 分页查询
	 */
	@GetMapping("/list/{pageNo}")
	public String list(@PathVariable int pageNo, Integer pageSize, Model model) {
		Pageable pageRequest = getPageRequest(pageNo, pageSize);
		PageVO<NumberGroupType> page = numberGroupTypeService.findPage(pageRequest);
		model.addAttribute("page", page);
		return "numberGroupType/table";
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
		numberGroupTypeService.batchDelete(ids);
		return ResultDTO.success();
	}

	/**
	 * 根据名字模糊查询
	 */
	@GetMapping("/search/name/{name}")
	public String searchByName(@PathVariable String name,Model model) {
		notEmptyOfString(name);
		PageVO<NumberGroupType> page = numberGroupTypeService.searchByName(name);
		model.addAttribute("page", page);
		return "numberGroupType/table";
	}
}
