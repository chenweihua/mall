
package com.igomall.wechat.controller.member;

import java.util.Collections;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.igomall.Message;
import com.igomall.Page;
import com.igomall.Pageable;
import com.igomall.controller.shop.BaseController;
import com.igomall.entity.Member;
import com.igomall.entity.Receiver;
import com.igomall.service.AreaService;
import com.igomall.service.MemberService;
import com.igomall.service.ReceiverService;

@Controller("wechatMemberReceiverController")
@RequestMapping("/wechat/member/receiver")
public class ReceiverController extends BaseController {

	private static final int PAGE_SIZE = 10;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	@Resource(name = "areaServiceImpl")
	private AreaService areaService;
	@Resource(name = "receiverServiceImpl")
	private ReceiverService receiverService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Integer pageNumber, ModelMap model) {
		Member member = memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		if(member==null){
			model.addAttribute("page", new Page<Receiver>(Collections.<Receiver> emptyList(), 0, pageable));
		}else{
			model.addAttribute("page", receiverService.findPage(member, pageable));
		}
		
		return "wechat/member/receiver/list";
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(RedirectAttributes redirectAttributes) {
		Member member = memberService.getCurrent();
		if (Receiver.MAX_RECEIVER_COUNT != null && member.getReceivers().size() >= Receiver.MAX_RECEIVER_COUNT) {
			addFlashMessage(redirectAttributes, Message.warn("shop.member.receiver.addCountNotAllowed", Receiver.MAX_RECEIVER_COUNT));
			return "redirect:list.jhtml";
		}
		return "wechat/member/receiver/add";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(Receiver receiver, Long areaId, RedirectAttributes redirectAttributes) {
		receiver.setArea(areaService.find(areaId));
		if (!isValid(receiver)) {
			return ERROR_VIEW;
		}
		Member member = memberService.getCurrent();
		if (Receiver.MAX_RECEIVER_COUNT != null && member.getReceivers().size() >= Receiver.MAX_RECEIVER_COUNT) {
			return ERROR_VIEW;
		}
		receiver.setMember(member);
		receiverService.save(receiver);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit() {
		return "wechat/member/receiver/edit";
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(Receiver receiver, Long id, Long areaId, RedirectAttributes redirectAttributes) {
		receiver.setArea(areaService.find(areaId));
		if(receiver.getIsDefault()==null){
			receiver.setIsDefault(false);
		}
		if (!isValid(receiver)) {
			return ERROR_VIEW;
		}
		Receiver pReceiver = receiverService.find(id);
		if (pReceiver == null) {
			return ERROR_VIEW;
		}
		Member member = memberService.getCurrent();
		if (!member.equals(pReceiver.getMember())) {
			return ERROR_VIEW;
		}
		
		receiverService.update(receiver, "member");
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody
	Message delete(Long id) {
		Receiver receiver = receiverService.find(id);
		if (receiver == null) {
			return ERROR_MESSAGE;
		}
		Member member = memberService.getCurrent();
		if (!member.equals(receiver.getMember())) {
			return ERROR_MESSAGE;
		}
		receiverService.delete(id);
		return SUCCESS_MESSAGE;
	}

}