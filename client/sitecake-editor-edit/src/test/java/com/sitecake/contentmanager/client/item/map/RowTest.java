package com.sitecake.contentmanager.client.item.map;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class RowTest {

	@Test
	public void test() {
		System.out.println(rows(1));
		System.out.println(rows(2));
		System.out.println(rows(3));
		System.out.println(rows(4));
		System.out.println(rows(5));
		System.out.println(rows(6));
		System.out.println(rows(7));
		System.out.println(rows(8));
		System.out.println(rows(9));
		System.out.println(rows(10));
		System.out.println(rows(11));
		System.out.println(rows(12));
		System.out.println(rows(13));
		System.out.println(rows(14));
		System.out.println(rows(15));
	}
	
	private List<Integer> rows(int numElements) {
		List<Integer> rows = new ArrayList<Integer>();
		int i = 0, rest, rowIdx = 0, rowCnt;
		while (i < numElements) {
			rest = numElements - i;
			
			if (rowIdx % 2 == 0) {
				rowCnt = Math.min(2, rest);
				if ((rest - rowCnt) == 1) {
					rowCnt = 3;
				}
			} else {
				rowCnt = Math.min(3, rest);
				if ((rest - rowCnt) == 1) {
					rowCnt = 2;
				}
			}
			i += rowCnt;
			rowIdx += 1;
			rows.add(rowCnt);
		}
		return rows;
	}	

}
