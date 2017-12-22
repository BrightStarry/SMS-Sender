package com.zuma.sms.controller;

import com.zuma.sms.config.ConfigStore;
import com.zuma.sms.controller.base.BaseController;
import com.zuma.sms.dto.PageVO;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.entity.NumberGroup;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.form.NumberGroupAddForm;
import com.zuma.sms.form.NumberGroupUpdateForm;
import com.zuma.sms.service.NumberGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * author:ZhengXing
 * datetime:2017/12/11 0011 10:09
 * 号码组
 */
@Controller
@RequestMapping("/numberGroup")
@Slf4j
public class NumberGroupController extends BaseController{
	@Autowired
	private NumberGroupService numberGroupService;
	@Autowired
	private ConfigStore configStore;

	/**
	 * 查询所有,根据号码组类别分类返回
	 */
	@PostMapping("/list/all-group")
	@ResponseBody
	public ResultDTO<Map<String, List<NumberGroup>>> listAllGroup() {
		return ResultDTO.success(numberGroupService.listAllGroup());
	}

	/**
	 * 查询所有
	 */
	@PostMapping("/list/all")
	@ResponseBody
	public  ResultDTO<List<NumberGroup>> listAll() {
		return ResultDTO.success(numberGroupService.listAll());
	}
	/**
	 * 文件下载
	 */
	@GetMapping("/download/{id:\\d+}")
	public void download(@PathVariable Long id, HttpServletResponse response) throws IOException {
		numberGroupService.findOne(id);
		commonDownload(id, response,numberGroupService.getInputStream(id));
	}

	/**
	 * 修改
	 */
	@PostMapping("/update")
	@ResponseBody
	public ResultDTO update(@Valid NumberGroupUpdateForm form, BindingResult bindingResult) {
		isValid(bindingResult);
		numberGroupService.update(form);
		return ResultDTO.success();
	}


	/**
	 * 查询单个
	 */
	@RequestMapping("/query")
	@ResponseBody
	public ResultDTO<NumberGroup> findOne(Long id) {
		NumberGroup numberGroup = numberGroupService.findOne(id);
		return ResultDTO.success(numberGroup);
	}

	/**
	 * 新增
	 */
	@PostMapping("/add")
	@ResponseBody
	public ResultDTO<?> add(@Valid NumberGroupAddForm form, BindingResult bindingResult) {
		isValid(bindingResult);
		numberGroupService.save(form);
		return ResultDTO.success();
	}

	/**
	 * 进入列表
	 */
	@GetMapping("/list")
	public String listView(Model model) {
		model.addAttribute(navTop2, "numberGroup");
		return "numberGroup/list";
	}

	/**
	 * 分页查询
	 */
	@GetMapping("/list/{pageNo}")
	public String list(@PathVariable int pageNo, Integer pageSize, Model model) {
		Pageable pageRequest = getPageRequest(pageNo, pageSize);
		PageVO<NumberGroup> page = numberGroupService.findPage(pageRequest);
		model.addAttribute("page", page);
		return "numberGroup/table";
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
		numberGroupService.batchDelete(ids);
		return ResultDTO.success();
	}

	/**
	 * 根据名字模糊查询
	 */
	@GetMapping("/search/name/{name}")
	public String searchByName(@PathVariable String name,Model model) {
		notEmptyOfString(name);
		PageVO<NumberGroup> page = numberGroupService.searchByName(name);
		model.addAttribute("page", page);
		return "numberGroup/table";
	}


}
