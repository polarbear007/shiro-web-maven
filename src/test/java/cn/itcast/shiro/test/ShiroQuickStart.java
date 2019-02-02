package cn.itcast.shiro.test;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

public class ShiroQuickStart {
	
	// 演示一下最基本的获取 subject 的过程
	@Test
	public void test() {
		// 加载配置文件, 创建工厂对象
			IniSecurityManagerFactory factory = new IniSecurityManagerFactory("classpath:shiro.ini");
			// 创建安全管理器对象，这是shiro 的核心对象
			SecurityManager securityManager = factory.getInstance();
			System.out.println(securityManager.getClass().getName());
			// 因为我们这里是javase ，所以我们得手动地把  securityManger 对象设置成全局的管理对象
			// 在web 项目中，spring 会自动生成并配置这个管理器的
			SecurityUtils.setSecurityManager(securityManager);
			// 拿到了这个管理器对象以后，我们就可以通过这个管理器对象来创建  subject 对象
			// subject 对象我们可以简单地理解为一个 User 对象
			Subject subject = SecurityUtils.getSubject();
			System.out.println(subject.getClass().getName());
			// 拿到了 subject 对象以后，我们可以获取 session 对象
			// 这个 session 对象的作用跟以前我们学的 httpSession 对象差不多，方法都都几乎一样
			// 唯一不同的是： 这个 session 对象不仅在web 环境下可以使用，在javase 的环境下也是可以使用的
			Session session = subject.getSession();
			System.out.println(session.getClass().getName());
			
			// 拿到session 对象以后，我们可以往这个 session 对象里面存值和取值
			session.setAttribute("message", "hello world");
			System.out.println("message :" + session.getAttribute("message"));
			
			// 这个 subject 不仅仅可以获取 session 对象，还可以判断是否已经登陆
			// 【注意】其实这个 subject 的这些功能其实都是由背后的  securityManager 来完成的 
			boolean authenticated = subject.isAuthenticated();
			System.out.println("这个用户是否已经登陆： " + authenticated);
			
			// 如果还没有登陆的话，那么我们就登陆一下
			// 登陆的流程大概是这样：
			//		先把帐号和密码封装成一个 token 对象
			//		然后再使用那个 subject 对象的 login() 方法把 token 对象传进去即可
			if(!authenticated) {
				// 把帐号和密码封装成一个 token 对象
				// 【注意】 随便写个帐号密码就可以登陆的，我们的帐号已经在配置文件   shiro.ini 上面配置好了
				//        如果我们的帐号密码不对，那么执行 login 方法的时候可能会报如下的异常：
				//               UnknownAccountException ：   			说明这个帐号没有在shiro.ini 里面配置过
				//               IncorrectCredentialsException：		说明帐号虽然有配置过，但是密码是错误的
				// 				 LockedAccountException ：			说明帐号密码都对，但是这个帐号已经被锁定了
				//                .......................           shiro 还提供了其他很多种异常
				// 				 AuthenticationException ：         		上面的所有异常都是  AuthenticationException  的子类，所以最后我们用这个来接
				//		当然，上面的异常只是服务器这里看的，不要把这些异常显示给用户，只要统一显示说帐号或者密码出错了就好了。 说得太详细，方便了黑客而已。
				UsernamePasswordToken token = new UsernamePasswordToken("王二狗", "123456");
				token.setRememberMe(true);
				// 然后再使用那个 subject 对象的 login() 方法把 token 对象传进去即可
				subject.login(token);
				// 现在应该已经登陆成功了，我们再看一下登陆状态
				System.out.println("这个用户是否已经登陆： " + subject.isAuthenticated());
			}
			
			// 登陆了以后，我们还可以做更多事情 
			// 比如获取用户名
			System.out.println("用户名： " + subject.getPrincipal());
			
			// 判断用户是否有某个角色----> 这个角色你可以理解成一系列权限的集合
			// 比如说这个用户是个超级用户，拥有所有权限，可以做任何事。
			// 比如另一个用户只是个游客，只拥有很小的权限，只能做指定的某些事情。
			// 一个用户可以有多个角色或者说多种权限集合。
			// shiro 框架本身最大的功能就是作权限管理，具体怎么管理呢？  
			//  就是在做某件事之前，先看一下你有没有登陆，如果登陆了，再看一下你是不是对应的role；  
			//   而具体哪个 role 能做哪些事情，这些都是可以事先约定好。 就本案例来说，这些都是事先配置在 shiro.ini 文件中的
			//   当然，随着以后我们学习的深入，还会学习更多的配置方式。
			System.out.println("是否有   食堂管理员   这个角色：" + subject.hasRole("食堂管理员"));
			
			System.out.println(subject.getPrincipal() + "是否拥有以下的权限呢？");
			System.out.println("食堂:进入=====>" + subject.isPermitted("食堂:进入"));
			System.out.println("食堂:开启=====>" + subject.isPermitted("食堂:开启"));
			System.out.println("食堂:关闭=====>" + subject.isPermitted("食堂:关闭"));
			System.out.println("食堂:爆破=====>" + subject.isPermitted("食堂:爆破"));
			System.out.println("==============================================");
			
			// 其实如果我们分类做得好的话，只要一看王二狗不是程序员，那么关于电脑的权限，可以直接都不用校验了
			System.out.println("是否有   程序员   这个角色：" + subject.hasRole("程序员"));
			
			System.out.println(subject.getPrincipal() + "是否拥有以下的权限呢？");
			System.out.println("电脑:开启=====>" + subject.isPermitted("电脑:开启"));
			System.out.println("电脑:关闭=====>" + subject.isPermitted("电脑:关闭"));
			System.out.println("电脑:使用=====>" + subject.isPermitted("电脑:使用"));
			System.out.println("电脑:带回家=====>" + subject.isPermitted("电脑:带回家"));
			
			// 用户登出
			subject.logout();
	}
}
