package com.zuma.sms.controller;

import com.zuma.sms.config.ConfigStore;
import com.zuma.sms.controller.base.BaseController;
import com.zuma.sms.dto.PageVO;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.entity.Platform;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.form.PlatformAddForm;
import com.zuma.sms.form.PlatformUpdateForm;
import com.zuma.sms.service.PlatformService;
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
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/12/8 0008 16:27
 * 平台
 */
@Controller
@RequestMapping("/platform")
@Slf4j
public class PlatformController extends BaseController{

	@Autowired
	private PlatformService platformService;
	@Autowired
	private ConfigStore configStore;

	/**
	 * 查询所有
	 */
	@PostMapping("/list/all")
	@ResponseBody
	public ResultDTO<List<Platform>> listAll() {
		return ResultDTO.success(platformService.listAll());
	}

	/**
	 * 查询单个
	 */
	@RequestMapping("/query")
	@ResponseBody
	public ResultDTO<Platform> findOne(Long id) {

		Platform platform = platformService.findOne(id);
		return ResultDTO.success(platform);
	}

	/**
	 * 修改
	 */
	@PostMapping("/update")
	@ResponseBody
	public ResultDTO update(@Valid PlatformUpdateForm form, BindingResult bindingResult) {
		isValid(bindingResult,log,"");
		platformService.update(form);
		return ResultDTO.success();
	}

	/**
	 * 新增
	 */
	@PostMapping("/add")
	@ResponseBody
	public ResultDTO<?> add(@Valid PlatformAddForm form, BindingResult bindingResult) {
		isValid(bindingResult,log,"");
		platformService.save(form);
		return ResultDTO.success();
	}

	/**
	 * 进入列表
	 */
	@GetMapping("/list")
	public String listView(Model model) {
		model.addAttribute(navTop2, "platform");
		return "platform/list";
	}

	/**
	 * 分页查询
	 */
	@GetMapping("/list/{pageNo}")
	public String list(@PathVariable int pageNo, Integer pageSize, Model model) {
		Pageable pageRequest = getPageRequest(pageNo, pageSize);
		PageVO<Platform> page = platformService.findPage(pageRequest);
		model.addAttribute("page", page);
		return "platform/table";
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
		platformService.batchDelete(ids);
		return ResultDTO.success();
	}

	/**
	 * 根据名字模糊查询
	 */
	@GetMapping("/search/name/{name}")
	public String searchByName(@PathVariable String name,Model model) {
		notEmptyOfString(name);
		PageVO<Platform> page = platformService.searchByName(name);
		model.addAttribute("page", page);
		return "platform/table";
	}
}
