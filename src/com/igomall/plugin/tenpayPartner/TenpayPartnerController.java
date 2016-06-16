/*
 * 
 * 
 * 
 */
package com.igomall.plugin.tenpayPartner;

import java.math.BigDecimal;

import javax.annotation.Resource;

import com.igomall.Message;
import com.igomall.controller.admin.BaseController;
import com.igomall.entity.PluginConfig;
import com.igomall.plugin.PaymentPlugin;
import com.igomall.plugin.PaymentPlugin.FeeType;
import com.igomall.service.PluginConfigService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminTenpayPartnerController")
@RequestMapping("/admin/payment_plugin/tenpay_partner")
public class TenpayPartnerController extends BaseController {

	@Resource(name = "tenpayPartnerPlugin")
	private TenpayPartnerPlugin tenpayPartnerPlugin;
	@Resource(name = "pluginConfigServiceImpl")
	private PluginConfigService pluginConfigService;

	@RequestMapping(value = "/install", method = RequestMethod.POST)
	public @ResponseBody
	Message install() {
		if (!tenpayPartnerPlugin.getIsInstalled()) {
			PluginConfig pluginConfig = new PluginConfig();
			pluginConfig.setPluginId(tenpayPartnerPlugin.getId());
			pluginConfig.setIsEnabled(false);
			pluginConfigService.save(pluginConfig);
		}
		return SUCCESS_MESSAGE;
	}

	@RequestMapping(value = "/uninstall", method = RequestMethod.POST)
	public @ResponseBody
	Message uninstall() {
		if (tenpayPartnerPlugin.getIsInstalled()) {
			PluginConfig pluginConfig = tenpayPartnerPlugin.getPluginConfig();
			pluginConfigService.delete(pluginConfig);
		}
		return SUCCESS_MESSAGE;
	}

	@RequestMapping(value = "/setting", method = RequestMethod.GET)
	public String setting(ModelMap model) {
		PluginConfig pluginConfig = tenpayPartnerPlugin.getPluginConfig();
		model.addAttribute("feeTypes", FeeType.values());
		model.addAttribute("pluginConfig", pluginConfig);
		return "/com/igomall/plugin/tenpayPartner/setting";
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(String paymentName, String partner, String key, FeeType feeType, BigDecimal fee, String logo, String description, @RequestParam(defaultValue = "false") Boolean isEnabled, Integer order, RedirectAttributes redirectAttributes) {
		PluginConfig pluginConfig = tenpayPartnerPlugin.getPluginConfig();
		pluginConfig.setAttribute(PaymentPlugin.PAYMENT_NAME_ATTRIBUTE_NAME, paymentName);
		pluginConfig.setAttribute("partner", partner);
		pluginConfig.setAttribute("key", key);
		pluginConfig.setAttribute(PaymentPlugin.FEE_TYPE_ATTRIBUTE_NAME, feeType.toString());
		pluginConfig.setAttribute(PaymentPlugin.FEE_ATTRIBUTE_NAME, fee.toString());
		pluginConfig.setAttribute(PaymentPlugin.LOGO_ATTRIBUTE_NAME, logo);
		pluginConfig.setAttribute(PaymentPlugin.DESCRIPTION_ATTRIBUTE_NAME, description);
		pluginConfig.setIsEnabled(isEnabled);
		pluginConfig.setOrder(order);
		pluginConfigService.update(pluginConfig);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:/admin/payment_plugin/list.jhtml";
	}

}