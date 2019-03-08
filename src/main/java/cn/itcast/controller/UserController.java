package cn.itcast.controller;

import java.util.Collection;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
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
	
	@Autowired
	private SessionDAO sessionDao;
	
	@RequestMapping("/login")
	public String login(User user, Model model) {
		UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), 
				                                                user.getPassword().toCharArray());
		// 设置是否记住登陆状态
		token.setRememberMe(true);
		Subject subject = SecurityUtils.getSubject();
		Collection<Session> sessions;
		try {
			subject.login(token);
			
			//登陆成功以后，我们看一下内存中是否还保存着此用户的其他session 对象
			// 如果有的话，很可能是这个用户在别处也登陆了。 我们就删除原来的那个用户的 session ，
			// 保证每个时刻一个帐号只能在一个地方登陆
			// session 里面保存着两个重要的值： DefaultSubjectContext.AUTHENTICATED_SESSION_KEY  ====> 帐号是否校验过了，true 或者 false
			//                           DefaultSubjectContext.PRINCIPALS_SESSION_KEY     ====> 帐号信息，包含用户名的SimplePrincipalCollection
			Session curSession = subject.getSession();
			sessions = sessionDao.getActiveSessions();
			for (Session session : sessions) {
				// 如果用户名跟当前用户相同,但是sessioinId 又跟当前用户的 sessionId 不同，那么
				// 可以肯定是当前帐号在其他地方登陆了，我们只保留当前用户的session
				// 注意： session.getId() 返回值并没有重写equals 方法，我们必须转成字符串再来比较
				// 注意： session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY) 返回值并不是字符串
				//      SimplePrincipalCollection 对象，所以我们不能直接使用  user.getUsername.equals(xxx) 这样来比较
				if(curSession.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY).equals(session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY))) {
					if(session.getId().equals(curSession.getId())) {
						continue;
					}else {
						session.stop();
						break;
					}
				}
			}
			
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
		
		return "redirect:/success.jsp";
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
	
	@RequestMapping("/logout")
	public String logout() {
		SecurityUtils.getSubject().logout();
		return "success";
	}
}
