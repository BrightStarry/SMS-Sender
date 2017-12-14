package com.zuma.sms.controller;

import com.zuma.sms.config.store.ChannelStore;
import com.zuma.sms.config.store.ConfigStore;
import com.zuma.sms.controller.base.BaseController;
import com.zuma.sms.dto.PageVO;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.SendTaskRecord;
import com.zuma.sms.enums.db.IsDeleteEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.form.SendTaskRecordAddForm;
import com.zuma.sms.form.SendTaskRecordUpdateForm;
import com.zuma.sms.security.CustomUser;
import com.zuma.sms.service.SendTaskRecordService;
import com.zuma.sms.util.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/12/12 0012 09:51
 * 发送任务记录
 */
@Controller
@RequestMapping("/sendTaskRecord")
@Slf4j
public class SendTaskRecordController extends BaseController{

	@Autowired
	private SendTaskRecordService sendTaskRecordService;

	@Autowired
	private ChannelStore channelStore;

	/**
	 * 获取所有通道
	 */
	@PostMapping("/channel/all")
	@ResponseBody
	public ResultDTO<List<Channel>> channelListAll() {
		return ResultDTO.success(channelStore.getAll());
	}

	/**
	 * 进入列表
	 */
	@GetMapping("/list")
	public String listView(Model model) {
		model.addAttribute(navTop2, "sendTaskRecord");
		return "sendTaskRecord/list";
	}

	/**
	 * 分页查询
	 */
	@GetMapping("/list/{pageNo}")
	public String list(@PathVariable int pageNo, Integer pageSize,
					   @RequestParam(required = false, defaultValue = "0") Integer isDelete, Model model) {
		Pageable pageRequest = getPageRequest(pageNo, pageSize);
		PageVO<SendTaskRecord> page = sendTaskRecordService.findPage(pageRequest, EnumUtil.getByCode(isDelete, IsDeleteEnum.class));
		model.addAttribute("page", page);
		return "SendTaskRecord/table";
	}

	/**
	 * 修改信息
	 */
	@PostMapping("/update")
	@ResponseBody
	public ResultDTO<?> update(@Valid SendTaskRecordUpdateForm form, BindingResult bindingResult) {
		isValid(bindingResult,log,"");
		//开始时间不能小于结束时间
		if(form.getExpectStartTime().after(form.getExpectEndTime())){
			log.error("[发送任务]开始时间小于当前时间.");
			throw new SmsSenderException("任务开始时间小于当前时间");
		}
		sendTaskRecordService.updateInfo(form);
		return ResultDTO.success();
	}

	/**
	 * 新增任务
	 */
	@PostMapping("/add")
	@ResponseBody
	public ResultDTO<?> addTask(@Valid SendTaskRecordAddForm form, BindingResult bindingResult,
								@AuthenticationPrincipal CustomUser user) {
		isValid(bindingResult,log,"");
//		开始时间不能小于结束时间
		if(form.getExpectStartTime().after(form.getExpectEndTime())){
			log.error("[发送任务]开始时间小于当前时间.");
			throw new SmsSenderException("任务开始时间小于当前时间");
		}
		//设置登录用户id
		form.setUserId(user.getId());
		sendTaskRecordService.addTask(form);
		return ResultDTO.success();
	}

	/**
	 * 查询单个
	 */
	@RequestMapping("/query")
	@ResponseBody
	public ResultDTO<SendTaskRecord> findOne( Long id) {
		SendTaskRecord result = sendTaskRecordService.findOne(id);
		return ResultDTO.success(result);
	}

	/**
	 * 根据名字模糊查询
	 */
	@GetMapping("/search/name/{name}")
	public String searchByName(@PathVariable String name,
							   @RequestParam(required = false, defaultValue = "0") Integer isDelete,Model model) {
		notEmptyOfString(name);
		PageVO<SendTaskRecord> page = sendTaskRecordService.searchByName(name,EnumUtil.getByCode(isDelete, IsDeleteEnum.class));
		model.addAttribute("page", page);
		return "sendTaskRecord/table";
	}

	/**
	 * 删除任务
	 */
	@PostMapping("/delete")
	@ResponseBody
	public ResultDTO<?> delete(@RequestParam("ids[]") Long[] ids) {
		if(ArrayUtils.isEmpty(ids))
			throw new SmsSenderException(ErrorEnum.ARRAY_EMPTY);
		if(ids.length > 1)
			throw new SmsSenderException("删除数量超出限制");
		sendTaskRecordService.delete(ids[0]);
		return ResultDTO.success();
	}

	/**
	 * 中断任务
	 */
	@PostMapping("/stop")
	@ResponseBody
	public ResultDTO<?> stop(Long id) {
		sendTaskRecordService.stop(id);
		return  ResultDTO.success();
	}

}
