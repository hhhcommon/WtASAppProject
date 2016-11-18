package com.woting.ui.mine.feedback.feedbacklist.model;

import java.io.Serializable;
/**
 * 意见反馈列表对象
 * 作者：xinlong on 2016/8/1 21:18
 * 邮箱：645700751@qq.com
 */
public class OpinionMessageInside implements Serializable {
	private String OpinionReId;
	private String ReOpinion;

	public String getOpinionReId() {
		return OpinionReId;
	}
	public void setOpinionReId(String opinionReId) {
		OpinionReId = opinionReId;
	}
	public String getReOpinion() {
		return ReOpinion;
	}
	public void setReOpinion(String reOpinion) {
		ReOpinion = reOpinion;
	}
}
