package cn.itcast.shiro.test;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

public class TestAuthorization {
	@Test
	public void test() {
		// 读取shiro.ini 配置文件，创建Realm 对象
		IniRealm realm = new IniRealm("classpath:shiro.ini");
		
		// 把这个 realm 对象传入 securityManager 对象里面
		DefaultSecurityManager securityManager = new DefaultSecurityManager(realm);
		
		// 把 securityManager 设置成全局的处理对象
		SecurityUtils.setSecurityManager(securityManager);
		
		// 获取subject 对象
		Subject subject = SecurityUtils.getSubject();
		
		// 封装 token 对象，然后登陆
		UsernamePasswordToken token = new UsernamePasswordToken("王二狗", "123456");
		subject.login(token);
		
		// 判断这个用户是否拥有 食堂管理员的角色
		System.out.println("是否拥有 食堂管理员的角色:" + subject.hasRole("食堂管理员"));
		
		// 判断这个用户是否拥有打开食堂的权限
		System.out.println("是否拥有 打开食堂的权限：" + subject.isPermitted("食堂:开启"));
		
		// 判断这个用户是否拥有 打开 电脑的权限
		System.out.println("是否拥有 打开电脑的权限：" + subject.isPermitted("电脑:开启"));
	}
}
