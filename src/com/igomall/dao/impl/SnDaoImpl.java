/*
 * 
 * 
 * 
 */
package com.igomall.dao.impl;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import com.igomall.dao.SnDao;
import com.igomall.entity.Sn;
import com.igomall.entity.Sn.Type;
import com.igomall.util.FreemarkerUtils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import freemarker.template.TemplateException;

@Repository("snDaoImpl")
public class SnDaoImpl implements SnDao, InitializingBean {

	private HiloOptimizer productHiloOptimizer;
	private HiloOptimizer orderHiloOptimizer;
	private HiloOptimizer paymentHiloOptimizer;
	private HiloOptimizer refundsHiloOptimizer;
	private HiloOptimizer shippingHiloOptimizer;
	private HiloOptimizer returnsHiloOptimizer;

	private HiloOptimizer rechargeHiloOptimizer;
	private HiloOptimizer withdrawHiloOptimizer;
	
	@PersistenceContext
	private EntityManager entityManager;
	@Value("${sn.product.prefix}")
	private String productPrefix;
	@Value("${sn.product.maxLo}")
	private int productMaxLo;
	@Value("${sn.order.prefix}")
	private String orderPrefix;
	@Value("${sn.order.maxLo}")
	private int orderMaxLo;
	@Value("${sn.payment.prefix}")
	private String paymentPrefix;
	@Value("${sn.payment.maxLo}")
	private int paymentMaxLo;
	@Value("${sn.refunds.prefix}")
	private String refundsPrefix;
	@Value("${sn.refunds.maxLo}")
	private int refundsMaxLo;
	@Value("${sn.shipping.prefix}")
	private String shippingPrefix;
	@Value("${sn.shipping.maxLo}")
	private int shippingMaxLo;
	@Value("${sn.returns.prefix}")
	private String returnsPrefix;
	@Value("${sn.returns.maxLo}")
	private int returnsMaxLo;
	
	@Value("${sn.recharge.maxLo}")
	private int rechargeMaxLo;
	@Value("${sn.withdraw.maxLo}")
	private int withdrawMaxLo;

	public void afterPropertiesSet() throws Exception {
		productHiloOptimizer = new HiloOptimizer(Type.product, productPrefix, productMaxLo);
		orderHiloOptimizer = new HiloOptimizer(Type.order, orderPrefix, orderMaxLo);
		paymentHiloOptimizer = new HiloOptimizer(Type.payment, paymentPrefix, paymentMaxLo);
		refundsHiloOptimizer = new HiloOptimizer(Type.refunds, refundsPrefix, refundsMaxLo);
		shippingHiloOptimizer = new HiloOptimizer(Type.shipping, shippingPrefix, shippingMaxLo);
		returnsHiloOptimizer = new HiloOptimizer(Type.returns, returnsPrefix, returnsMaxLo);
		rechargeHiloOptimizer = new HiloOptimizer(Type.recharge, returnsPrefix, rechargeMaxLo);
		withdrawHiloOptimizer = new HiloOptimizer(Type.withdraw, returnsPrefix, withdrawMaxLo);
	}

	public String generate(Type type) {
		Assert.notNull(type);
		if (type == Type.product) {
			return productHiloOptimizer.generate();
		} else if (type == Type.order) {
			return orderHiloOptimizer.generate();
		} else if (type == Type.payment) {
			return paymentHiloOptimizer.generate();
		} else if (type == Type.refunds) {
			return refundsHiloOptimizer.generate();
		} else if (type == Type.shipping) {
			return shippingHiloOptimizer.generate();
		} else if (type == Type.returns) {
			return returnsHiloOptimizer.generate();
		} else if (type == Type.recharge) {
			return rechargeHiloOptimizer.generate();
		} else if (type == Type.withdraw) {
			return withdrawHiloOptimizer.generate();
		}
		return null;
	}

	private Long getLastValue(Type type) {
		String jpql = "select sn from Sn sn where sn.type = :type";
		Sn sn = entityManager.createQuery(jpql, Sn.class).setFlushMode(FlushModeType.COMMIT).setLockMode(LockModeType.PESSIMISTIC_WRITE).setParameter("type", type).getSingleResult();
		Long lastValue = null;
		if(sn==null){
			sn = new Sn();
			lastValue = 1L;
			sn.setType(type);
			sn.setLastValue(lastValue);
			entityManager.merge(sn);
		}else{
			lastValue = sn.getLastValue();
			sn.setLastValue(lastValue + 1);
			entityManager.merge(sn);
		}
		
		
		
		return lastValue;
	}

	private class HiloOptimizer {

		private Type type;
		private String prefix;
		private int maxLo;
		private int lo;
		private long hi;
		private long lastValue;

		public HiloOptimizer(Type type, String prefix, int maxLo) {
			this.type = type;
			this.prefix = prefix != null ? prefix.replace("{", "${") : "";
			this.maxLo = maxLo;
			this.lo = maxLo + 1;
		}

		public synchronized String generate() {
			if (lo > maxLo) {
				lastValue = getLastValue(type);
				lo = lastValue == 0 ? 1 : 0;
				hi = lastValue * (maxLo + 1);
			}
			try {
				return FreemarkerUtils.process(prefix, null) + (hi + lo++);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TemplateException e) {
				e.printStackTrace();
			}
			return String.valueOf(hi + lo++);
		}
	}

}