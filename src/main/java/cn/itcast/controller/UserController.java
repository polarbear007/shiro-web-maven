package cn.itcast.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.itcast.entity.User;
import cn.itcast.service.ShiroTestService;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private ShiroTestService shiroTestService;
	
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
	
	@RequestMapping("/queryOrders")
	public String queryOrders(Model model) {
		try {
			shiroTestService.queryOrders();
		}catch(Exception e) {
			model.addAttribute("message", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	@RequestMapping("/deleteOrders")
	public String deleteOrders(Model model) {
		try {
			shiroTestService.deleteOrders();
		}catch(Exception e) {
			model.addAttribute("message", e.getMessage());
			return "error";
		}
		
		return "success";
	}
	
	@RequestMapping("/updateOrders")
	public String updateOrders(Model model) {
		System.out.println("成功执行  updateOrders()");
		return "success";
	}
	
	@RequestMapping("/addOrder")
	public String addOrder() {
		Subject subject = SecurityUtils.getSubject();
		
		// 校验一下是否已经登陆
		if(subject.isAuthenticated()) {
			// 如果已经登陆了，那么我们就继续校验是否拥有admin 角色
			if(subject.hasRole("admin")) {
				System.out.println("成功执行了 addOrder()");
				return "success";
			}else {
				return "unauthorized";
			}
		}else {
			// 如果还没有登陆的话，那么我们就跳转到 login.jsp 页面
			return "login";
		}
	}
}
