package cn.itcast.shiro.test;

import java.util.Arrays;

import org.apache.shiro.codec.Base64;
import org.junit.Test;

public class Base64Test {
	@Test
	public void test() {
		byte[] decode = Base64.decode("4AvVhmFLUs0KTA3Kprsdag==");
		System.out.println(Arrays.toString(decode));
		
	}
}
