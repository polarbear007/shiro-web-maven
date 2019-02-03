package cn.itcast.service;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShiroTestService {
	
	@RequiresAuthentication
	@RequiresRoles(value= {"user", "admin"}, logical=Logical.OR)
	public void queryOrders() {
		System.out.println("成功执行   queryOrders()");
	}
	
	@Transactional
	@RequiresAuthentication
	@RequiresRoles(value= {"admin"})
	public void deleteOrders() {
		System.out.println("成功执行  deleteOrders()");
	}
}
