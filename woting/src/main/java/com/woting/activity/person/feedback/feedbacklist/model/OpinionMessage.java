package com.woting.activity.person.feedback.feedbacklist.model;

import java.io.Serializable;
import java.util.List;

public class OpinionMessage implements Serializable {
	private String Opinion;
	private String OpinionId;
	private String OpinionTime;
	private List<OpinionMessageInside> ReList;
	
	public String getOpinionTime() {
		return OpinionTime;
	}
	public void setOpinionTime(String opinionTime) {
		OpinionTime = opinionTime;
	}
	public String getOpinion() {
		return Opinion;
	}
	public void setOpinion(String opinion) {
		Opinion = opinion;
	}
	public String getOpinionId() {
		return OpinionId;
	}
	public void setOpinionId(String opinionId) {
		OpinionId = opinionId;
	}
	public List<OpinionMessageInside> getReList() {
		return ReList;
	}
	public void setReList(List<OpinionMessageInside> reList) {
		ReList = reList;
	}
}
