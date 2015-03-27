package org.cxw.nb.dao;

import java.util.List;
import java.util.Map;


import org.cxw.nb.model.Pager;
import org.cxw.nb.model.User;



public interface IUserDao extends IBaseDao<User> {
	/*
	 * 别名
	 */
	public List<User> list(String hql,Object[] args,Map<String,Object> alias);
	
	/*
	 * 参数
	 */
	public List<User> listByParameter(String hql,Object[] args);
	/*
	 * 别名
	 */
	public List<User> list(String hql,Object obj);
	/*
	 * 对象查询
	 */
	public List<User> listByAlias(String hql,Map<String,Object> alias);
	/*
	 * 分页
	 */
	public Pager<User> find(String hql,Object[] args,Map<String, Object> alias);
	
	

}
