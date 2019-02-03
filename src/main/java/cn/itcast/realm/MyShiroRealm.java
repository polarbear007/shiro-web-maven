package cn.itcast.realm;

import java.util.Scanner;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import cn.itcast.entity.Permission;
import cn.itcast.entity.Role;
import cn.itcast.entity.User;
import cn.itcast.service.UserService;

public class MyShiroRealm extends AuthorizingRealm {
	@Autowired
	private UserService userService;
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken upToken = (UsernamePasswordToken)token;
		User user = userService.getUserByUsername(upToken.getUsername());
		
		if(user == null) {
			// 如果是查不到对应的数据，直接返回 null 就好了，shiro 会自动帮我们抛异常
			return null;
		}
		
		// 帐号的状态异常的话，就得我们自己来抛了
		if(user.getStatus().equals("lock")) {
			throw new LockedAccountException("帐号被锁定");
		}
		
		// 如果都没有什么问题的话，这里就可以开始封装并返回  AuthenticationInfo 对象了
 		Object principal = user.getUsername();
		Object credentials = user.getPassword();
		String realmName = this.getName();
		// 加盐，我们是使用用户的用户名作为盐，你也可以使用其他的字段或者其他字符串
		// 但是注意，这个盐最好是唯一；  而且这个盐最好是可以从数据库的字段中推算出来；
		ByteSource credentialsSalt = ByteSource.Util.bytes(user.getUsername());
		
		// 创建 AuthenticationInfo 对象
		AuthenticationInfo info = new SimpleAuthenticationInfo(principal, credentials, credentialsSalt, realmName);
		return info;
	}

	// 从数据库里面获取当前用户的权限和角色信息
	// 【注意】 当我们进行身份校验的时候，我们并不需要把权限信息也一起都获取出来，只有在需要校验权限的时候
	//        我们再去连接数据库查询此用户相关的角色和权限信息
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// 【注意】 这里的 principals 是一个集合，但是我们一般就用一个 principal 就好了，这里是拿集合的第一个元素
		//        一般来说，principals 集合里面也只有一个元素，我们就用这个元素来标识当前用户。
		//       （用户名、邮箱、电话啥的其实都可以作为 principal）
		// =====> 什么时候会有多个 principal 呢？ 就是当我们配置了多个realm的时候，这些东西暂时不去深究了。
		String username = principals.getPrimaryPrincipal().toString();
		// 再次调用 userService 方法连接数据库，获取角色和权限相关的数据
		User user = userService.getUserWithRolesAndPermissionsByUsername(username);
		SimpleAuthorizationInfo info = null;
		if(user != null && user.getRoleList() != null && user.getRoleList().size() > 0 ) {
			// 如果使用 username 确实查到相关的数据了，我们再创建  AuthorizationInfo 对象，然后把数据封装到此对象
			info = new SimpleAuthorizationInfo();
			for (Role role : user.getRoleList()) {
				// 封装角色信息
				info.addRole(role.getRoleName());
				
				// 如果我们想要再详细一点，把我们还可以再把 role 里面的 permission 对象也放进 info
				for (Permission permission : role.getPermissionList()) {
					info.addStringPermission(permission.getPermissionName());
				}
			}
			// 最后都封装完了以后，直接返回 info 对象
			return info;
		}else {
			// 如果使用 username 无法从数据库中获取相关信息，那么就
			return null;
		}
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
		sc.close();
	}
}
