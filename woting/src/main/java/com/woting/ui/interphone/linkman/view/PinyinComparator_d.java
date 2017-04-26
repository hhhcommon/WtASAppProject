package com.woting.ui.interphone.linkman.view;


import com.woting.ui.music.citylist.citysmodel.secondaryCity;

import java.util.Comparator;

public class PinyinComparator_d implements Comparator<secondaryCity> {

	public int compare(secondaryCity o1, secondaryCity o2) {
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
