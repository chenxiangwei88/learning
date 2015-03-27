package org.cxw.nb.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.ObjectUtils;
import org.cxw.nb.model.User;
import org.cxw.nb.util.AbstractDbUnitTestCase;
import org.cxw.nb.util.EntitiesHelper;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
//指定使用的单元测试执行类，这里我们使用SpringJunit4ClassRunner.class类
@RunWith(SpringJUnit4ClassRunner.class)
//指定Spring的配置文件路径，可以指定多个，用逗号隔开；
@ContextConfiguration("/beans.xml")
//指定测试类之前，要执行的操作：DependencyInjectionTestExecutionListener.class可以实现测试类之前的依赖注入
@TestExecutionListeners({DbUnitTestExecutionListener.class, 
		DependencyInjectionTestExecutionListener.class})
public class TestUserDao extends AbstractDbUnitTestCase{
	
	@Inject
	private SessionFactory sessionFactory;
	@Inject
	private IUserDao userDao;
	
	@Before
	public void setUp() throws DataSetException, SQLException, IOException {
		Session s = sessionFactory.openSession();
		//把当前线程与事务绑定
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(s));
		bakcupOneTable("tb_user");
	}
	
	@Test
	public void testSelect() throws DatabaseUnitException, SQLException, IOException {
		IDataSet ds = createDateSet("tb_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,ds);
		User u = userDao.select(1);
		EntitiesHelper.assertUser(u);
	}
/*	@Test
	public void testDelete() throws DatabaseUnitException, SQLException, IOException {
		IDataSet ds = createDateSet("tb_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,ds);
		userDao.delete(1);
		User tu = userDao.delete(id);
		System.out.println(tu.getUsername());
	}*/
	@Test
	public void testAdd() throws DatabaseUnitException, SQLException, IOException {
		IDataSet ds = createDateSet("tb_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,ds);
		userDao.delete(1);
		User user = new User(2,"李四");
		User tu = userDao.add(user);
		System.out.println(tu.getUsername());
	}
	@Test
	public void testUpdate() throws DatabaseUnitException, SQLException, IOException {
		IDataSet ds = createDateSet("tb_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,ds);
		userDao.delete(1);
		User user = new User(2,"李四");
		userDao.update(user);
		
	} 
	@Test
	public void testlist() throws IOException, DatabaseUnitException, SQLException {
		IDataSet ds = createDateSet("tb_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,ds);
		String hql = "from User u where u.id =? and u .username = :name";
		Object[] args = new Object[]{1};
		Map<String,Object> alias = new HashMap<String, Object>(); 
		alias.put("name", "admin1");
		List<User> list = userDao.list(hql, args,alias);
		Assert.assertEquals("admin1",list.get(0).getUsername());
	}	
	/*@Test
	public void testlistByParameter() throws IOException, DatabaseUnitException, SQLException{
		IDataSet ds = createDateSet("tb_user");
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,ds);
		String hql = "from User u where u.id=?";
		Object[] args = new Object[]{2};
		List<User> list = userDao.list(hql, args);
		Assert.assertEquals("admin2",list.get(0).getUsername());
	
	}*/
	
	@After
	public void tearDown() throws DatabaseUnitException, SQLException, IOException {
		//
		SessionHolder holder = (SessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
		Session s = holder.getSession(); 
		s.flush();
		TransactionSynchronizationManager.unbindResource(sessionFactory);
		resumesTable();
	}
}
