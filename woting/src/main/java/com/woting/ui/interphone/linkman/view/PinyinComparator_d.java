package com.woting.ui.interphone.linkman.view;

import com.woting.ui.home.program.fenlei.model.CatalogName;

import java.util.Comparator;

public class PinyinComparator_d implements Comparator<CatalogName> {

	public int compare(CatalogName o1, CatalogName o2) {
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
