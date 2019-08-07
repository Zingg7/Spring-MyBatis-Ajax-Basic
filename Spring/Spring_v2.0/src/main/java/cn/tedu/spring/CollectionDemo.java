package cn.tedu.spring;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CollectionDemo {

	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("string-2");
		list.add("string-1");
		list.add("string-5");
		list.add("string-3");
		list.add("string-4");
		list.add("string-2");
		list.add("string-1");
		list.add("string-5");
		list.add("string-3");
		list.add("string-4");
		for (String string : list) {
			System.out.println(string);
		}
		
		System.out.println("---------------");
		
		Set<String> set = new LinkedHashSet<String>();
		set.add("string-2");
		set.add("string-1");
		set.add("string-5");
		set.add("string-3");
		set.add("string-4");
		set.add("string-2");
		set.add("string-1");
		set.add("string-5");
		set.add("string-3");
		set.add("string-4");
		for (String string : set) {
			System.out.println(string);
		}
	}
	
}
