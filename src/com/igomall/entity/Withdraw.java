package com.igomall.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "lx_withdraw")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "lx_withdraw_sequence")
public class Withdraw extends BaseEntity {

	private String number;

	public enum Status {
		wait,
		success,
		failure
	}

	private static final long serialVersionUID = 7771489648414525176L;

	private BigDecimal balance;// 提现金额

	private Admin operator;// 操作人

	private String memo;// 备注

	private Member member;// 提现的会员

	private MemberBank memberBank;// 提现到的银行卡

	private BigDecimal fee;// 提现手续费

	private Status status;// 状态

	private BigDecimal realBalance;// 实到金额金额

	
	@JsonProperty
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	@JsonProperty
	@ManyToOne(fetch = FetchType.LAZY)
	public MemberBank getMemberBank() {
		return memberBank;
	}

	public void setMemberBank(MemberBank memberBank) {
		this.memberBank = memberBank;
	}

	@JsonProperty
	@Column(nullable = false, updatable = false, precision = 21, scale = 6)
	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	@JsonProperty
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@JsonProperty
	@Column(nullable = false, updatable = false, precision = 21, scale = 6)
	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	@JsonProperty
	@Column(precision = 21, scale = 6)
	public BigDecimal getRealBalance() {
		return realBalance;
	}

	public void setRealBalance(BigDecimal realBalance) {
		this.realBalance = realBalance;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Admin getOperator() {
		return operator;
	}

	public void setOperator(Admin operator) {
		this.operator = operator;
	}

	@JsonProperty
	@Column(updatable = false,length=8000)
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = false)
	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

}