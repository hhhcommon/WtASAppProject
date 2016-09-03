package com.woting.activity.interphone.linkman.view;

import com.woting.activity.interphone.creatgroup.groupcontrol.transferauthority.model.UserInfo;

import java.util.Comparator;


public class PinyinComparator_c implements Comparator<UserInfo> {

	public int compare(UserInfo o1, UserInfo o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
