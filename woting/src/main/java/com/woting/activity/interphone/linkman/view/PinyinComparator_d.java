package com.woting.activity.interphone.linkman.view;

import com.woting.activity.home.program.fenlei.model.fenleiname;

import java.util.Comparator;

public class PinyinComparator_d implements Comparator<fenleiname> {

	public int compare(fenleiname o1, fenleiname o2) {
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
