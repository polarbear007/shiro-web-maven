package cn.itcast.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.itcast.entity.User;

@Controller
@RequestMapping("/user")
public class UserController {
	@RequestMapping("/login")
	public String login(User user, Model model) {
		UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), 
				                                                user.getPassword().toCharArray());
		
		// 设置是否记住登陆状态
		token.setRememberMe(true);
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.login(token);
		}catch ( UnknownAccountException e ) { 
			System.out.println("帐户不存在 ！");
			model.addAttribute("message", "帐户不存在 ！");
			return "error";
		} catch ( IncorrectCredentialsException e ) {
			System.out.println("密码不正确！");
			model.addAttribute("message", "密码不正确！");
			return "error";
		} catch ( LockedAccountException e ) { 
			System.out.println("帐户被锁定 ！");
			model.addAttribute("message", "帐户被锁定 ！");
			return "error";
		} catch ( AuthenticationException e ) {
		   System.out.println("发生了其他异常！");
		   model.addAttribute("message", "发生了其他异常！");
		   return "error";
		}
		
		return "success";
	}
}
