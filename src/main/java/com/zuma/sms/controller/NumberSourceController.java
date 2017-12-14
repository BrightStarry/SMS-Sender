package com.zuma.sms.controller;

import com.zuma.sms.form.NumberSourceForm;
import com.zuma.sms.config.store.ConfigStore;
import com.zuma.sms.controller.base.BaseController;
import com.zuma.sms.dto.PageVO;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.entity.NumberSource;
import com.zuma.sms.enums.db.IsDeleteEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.service.NumberSourceService;
import com.zuma.sms.util.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/12/7 0007 12:52
 * 号码源
 */
@Controller
@RequestMapping("/numberSource")
@Slf4j
public class NumberSourceController extends BaseController {
	@Autowired
	private NumberSourceService numberSourceService;
	@Autowired
	private ConfigStore configStore;


	/**
	 * 进入列表
	 */
	@GetMapping("/list")
	public String listView(Model model) {
		model.addAttribute(navTop2, "numberSource");
		return "numberSource/list";
	}

	/**
	 * 查询所有
	 */
	@PostMapping("/list/all")
	@ResponseBody
	public  ResultDTO<List<NumberSource>> listAll(Integer isDelete) {
		IsDeleteEnum isDeleteEnum = EnumUtil.getByCode(isDelete, IsDeleteEnum.class,
				"[numberSource]查询所有接口,传递的是否删除参数不存在.isDelete:{}", isDelete);
		return ResultDTO.success(numberSourceService.listAll(isDeleteEnum));
	}

	/**
	 * 查询单个
	 */
	@RequestMapping("/query")
	@ResponseBody
	public ResultDTO<NumberSource> findOne( Long id) {
		NumberSource numberSource = numberSourceService.findOne(id);
		return ResultDTO.success(numberSource);
	}

	/**
	 * 修改
	 */
	@PostMapping("/update")
	@ResponseBody
	public ResultDTO<?> update(@Valid NumberSourceForm numberSourceForm, BindingResult bindingResult) {
		isValid(bindingResult,log,"");
		numberSourceService.updateOne(numberSourceForm);
		return ResultDTO.success();
	}

	/**
	 * 根据名字模糊查询
	 */
	@GetMapping("/search/name/{name}")
	public String searchByName(@PathVariable String name,
							   @RequestParam(required = false, defaultValue = "0") Integer isDelete,Model model) {
		notEmptyOfString(name);
		PageVO<NumberSource> page = numberSourceService.searchByName(name,EnumUtil.getByCode(isDelete, IsDeleteEnum.class));
		model.addAttribute("page", page);
		return "numberSource/table";
	}


	/**
	 * 批量删除
	 */
	@PostMapping("/delete")
	@ResponseBody
	public ResultDTO<?> delete(@RequestParam("ids[]") Long[] ids) {
		if(ArrayUtils.isEmpty(ids))
			throw new SmsSenderException(ErrorEnum.ARRAY_EMPTY);
		numberSourceService.batchDelete(ids);
		return ResultDTO.success();
	}

	/**
	 * 文件下载
	 */
	@GetMapping("/download/{id:\\d+}")
	public void download(@PathVariable Long id, HttpServletResponse response) throws IOException {
		numberSourceService.findOne(id);

		commonDownload(id, response,numberSourceService.getInputStream(id));
	}

	/**
	 * 导入号码文件
	 */
	@PostMapping("/add")
	@ResponseBody
	public ResultDTO<?> add(MultipartHttpServletRequest multipartRequest, String name, String remark) {
		List<MultipartFile> files = multipartRequest.getFiles("file");
		if (CollectionUtils.isEmpty(files))
			throw new SmsSenderException(ErrorEnum.UPLOAD_FILE_EMPTY);
		String[] names = null;
		String[] remarks = null;
		//如果是多个号码源已通过导入,需要名称和备注也有对应个数
		if (files.size() > 1) {
			names = StringUtils.split(name, "|");
			remarks = StringUtils.split(remark, "|");
			if (files.size() != names.length || files.size() != remarks.length)
				throw new SmsSenderException(ErrorEnum.UPLOAD_MULTI_FORMAT_ERROR);
		}
		//参数不能为空
		notEmptyOfString(name,remark);

		numberSourceService.add(files,names,remarks);

		return ResultDTO.success();
	}

	/**
	 * 分页查询
	 */
	@GetMapping("/list/{pageNo}")
	public String list(@PathVariable int pageNo, Integer pageSize,
					   @RequestParam(required = false, defaultValue = "0") Integer isDelete, Model model) {
		Pageable pageRequest = getPageRequest(pageNo, pageSize);
		PageVO<NumberSource> page = numberSourceService.findPage(pageRequest, EnumUtil.getByCode(isDelete, IsDeleteEnum.class));
		model.addAttribute("page", page);
		return "numberSource/table";
	}

}
