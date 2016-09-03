package com.woting.activity.person.feedback.feedbacklist.model;

import java.io.Serializable;

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
