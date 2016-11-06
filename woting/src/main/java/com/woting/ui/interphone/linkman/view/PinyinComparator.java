package com.woting.ui.interphone.linkman.view;

import com.woting.ui.interphone.linkman.model.TalkPersonInside;

import java.util.Comparator;
public class PinyinComparator implements Comparator<TalkPersonInside> {

	public int compare(TalkPersonInside o1, TalkPersonInside o2) {
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
