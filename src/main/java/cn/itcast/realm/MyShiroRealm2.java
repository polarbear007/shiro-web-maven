package cn.itcast.realm;

import java.util.Scanner;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import cn.itcast.entity.User;
import cn.itcast.service.UserService;

public class MyShiroRealm2 extends AuthenticatingRealm {
	@Autowired
	private UserService userService;
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken upToken = (UsernamePasswordToken)token;
		// 我们只通过 username 去获取数据库的记录，当然，真实的开发中，我们可以通过 username + password去获取
		// 这里只是为了演示异常信息的抛出
		User user = userService.getUserByUsername(upToken.getUsername());
		// 如果 user 为null ，说明数据库中根本没有这个 username
		// UnknownAccountException
		if(user == null) {
			throw new UnknownAccountException("用户名不存在");
		}
		
		// username 存在，但是密码不正确
		// IncorrectCredentialsException
//		if(!user.getPassword().equals(upToken.getPassword().toString())) {
//			throw new IncorrectCredentialsException("密码不正确");
//		}
		// username 和 password 都正确，但是帐号被锁定了
		// LockedAccountException
		if(user.getStatus().equals("lock")) {
			throw new LockedAccountException("帐号被锁定");
		}
		
		// 如果果都没有问题，那么我们就封装  AuthenticationInfo 对象并返回
		// 这个方法的主要作用并不是去校验前台的数据对不对，而是根据前台输入的数据去数据库查询对应的数据
		// 然后把数据库的数据封装成  AuthenticationInfo 对象
		// 后面，shiro 框架会自动调用方法校验   AuthenticationInfo 对象 和 token 是否真的匹配
		// 当然，前面抛的异常一定程度上也可以说是校验了，但是那是最简单的校验了
		
		// 【注意】 这个 AuthenticationInfo 对象的 principal 和  credentials 成员变量千万不能直接通过 token 
		//        那里的值为获取，那样的话，后面的校验肯定都是 true 啦，完全没有意义了 
 		Object principal = user.getUsername();
		Object credentials = user.getPassword();
		String realmName = this.getName();
		// 设置加密的盐，一般来说我们都是取一个唯一的字符串
		// 这样子，就算多个用户的密码设置成一样，加密了以后得到的字符串也不一样
		// 当然，这个唯一的字符串我们也不能随便乱取，应该能根据用户目前数据库的其他信息来推算出来
		// 比如说，你可以使用数据库中 的 username 字段来作为盐
		//       也可以使用数据库中的  username + 邮箱 来作为盐
		//       也可以使用数据库中的 username 的前三位 + 邮箱的后5位 ，然后进行 md5 加密后的前5位作为盐
		// 总之，不管怎么说，这个盐值需要唯一；   又要可获取。
		//      你别生成一个随机的字符串来作为盐，那么下次登陆的时候，你原来的盐拿不到了，这就很无语了。
		ByteSource credentialsSalt = ByteSource.Util.bytes(user.getUsername());
		
		//AuthenticationInfo info = new SimpleAuthenticationInfo(principal, credentials, realmName);
		AuthenticationInfo info = new SimpleAuthenticationInfo(principal, credentials, credentialsSalt, realmName);
		return info;
	}

	// 因为我们暂时不想去写什么注册的方法，所以数据库里面的数据都是用手去添加的
	// 又因为我们数据库中密码不是明码保存的，需要进行 md5 加密，还要加盐，还要多次加密
	// 所以我们不能直接在数据库里面保存明码，这里写个main 方法，模拟注册的时候对密码进行 md5 加密 
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("请输入用户名：");
		String username = sc.nextLine();
		System.out.println("请输入密码：");
		String password = sc.nextLine();
		
		String newPassword = new Md5Hash(password, username, 3).toString();
		
		System.out.println("加密以后的密码为：" + newPassword);
	}
}
