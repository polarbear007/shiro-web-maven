package cn.itcast.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.itcast.entity.User;
import cn.itcast.entity.UserExample;
import cn.itcast.mapper.UserMapper;

@Service
public class UserService {
	@Autowired
	private UserMapper userMapper;
	
	// 只通过 username 去获取 user 对象
	// 因为我们在数据库里加了唯一约束，所以实际上最多只能返回一个 user 对象
	public User getUserByUsername(String username) {
		UserExample example = new UserExample();
		example.createCriteria().andUsernameEqualTo(username);
		List<User> list = userMapper.selectByExample(example);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}else {
			return null;
		}
	}
}
