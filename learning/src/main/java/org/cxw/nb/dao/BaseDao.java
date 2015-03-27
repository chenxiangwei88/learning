package org.cxw.nb.dao;

import java.lang.reflect.ParameterizedType;
import java.net.CookieHandler;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder.In;

import org.cxw.nb.model.Pager;
import org.cxw.nb.model.SystemContext;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
public class BaseDao<T> implements IBaseDao<T> {

	// 通过依赖注入获得SessionFactory
	@Inject
	private SessionFactory sessionFactory;

	// 使用了inject注解过后可以省去getter 和setter方法
	/*
	 * public SessionFactory getSessionFactory() { return sessionFactory; }
	 * public void setSessionFactory(SessionFactory sessionFactory) {
	 * this.sessionFactory = sessionFactory; }
	 */
	/**
	 * 获取当前的Session
	 * 
	 * @return
	 */
	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	/**
	 * 定义了一个class，获取泛型的class；
	 */
	private Class<?> clz;

	public Class<?> getClz() {
		if (clz == null) {
			// 获取泛型的Class对象
			clz = ((Class<?>) (((ParameterizedType) (this.getClass()
					.getGenericSuperclass())).getActualTypeArguments()[0]));
		}
		return clz;
	}

	/**
	 * 增加一个对象
	 */
	@Override
	public T add(T t) {
		getSession().save(t);
		return t;
	}

	/**
	 * 更新一个对象
	 */
	@Override
	public void update(T t) {
		getSession().update(t);
	}

	/**
	 * 根据id删除一个对象
	 */
	@Override
	public void delete(Integer id) {
		getSession().delete(this.select(id));
	}
	

	/**
	 * 根据id查询一个对象
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T select(Integer id) {
		return (T) getSession().load(getClz(), id);
	}
	/**
	 * 根据页码数和总条数。查询
	 */
	

	/**
	 * 初始化排序的字段和排序的方式
	 * @param hql
	 * @return
	 */
	private String initSort(String hql) {
		String order = SystemContext.getOrder();
		String sort = SystemContext.getSort();
		if(sort != null && !"".equals(sort.trim())){
			hql += " order by " + sort;
			if(!"desc".equals(order)){
				hql += " asc; ";
			}
				else {
					hql += "desc";	
			}
		}
		return hql;
	}
	
	/**
	 * 设置别名参数
	 * @param query
	 * @param alias
	 */
	private void setAliasParameter(Query query,Map<String,Object> alias){
		if(alias != null){
			Set<String> keys = alias.keySet();
			for(String Key : keys){
				Object val = alias.get(Key);
				if(val instanceof Collection){
					query.setParameterList(Key,(Collection) val);
				}
				else {
					query.setParameter(Key, val);
				}
			}
		}
	}
	
	/**
	 *	设置带参数的查询 
	 * @param query
	 * @param args
	 */
	private void setParameter(Query query,Object[] args){
		if(args != null && args.length>0){
			//从0开始
			int index = 0;
			for (Object arg : args) {
				query.setParameter(index++, arg);	
			}
		}
	}
	
	/**
	 * 根据hql的查询，使用参数对象，别名查询
	 * @param hql
	 * @param args
	 * @param alias
	 * @return
	 */
	public List<T> list(String hql,Object[] args,Map<String,Object> alias){
		hql = initSort(hql);
		Query query = getSession().createQuery(hql);
		setAliasParameter(query, alias);
		setParameter(query, args);
		return query.list();
	}
	/*
	 * 使用参数查询
	 */
	public List<T> listByParameter(String hql,Object[] args){
		return this.list(hql, args, null);
	}
	/*
	 * 基于别名查询
	 */
	public List<T> list(String hql,Object obj){
		return this.list(hql, new Object[]{obj});
	}
	/*
	 * 基于单个参数对象的查询
	 */
	public List<T> listByAlias(String hql,Map<String,Object> alias){
		return this.list(hql, null, alias);
	}
	/**
	 * 设置分页参数
	 * @param query
	 * @param pager
	 */
	private void setPagers(Query query,Pager pager){
		Integer pageSize=SystemContext.getPageSize();
		Integer pageOffset=SystemContext.getPageOffset();
		if(pageOffset==null||pageOffset<0){
			pageOffset=0;
		}
		if(pageSize==null || pageSize<0){
			pageSize=10;
		}
		pager.setPageOffset(pageOffset);
		pager.setPageSize(pageSize);
		query.setFirstResult(pageOffset).setMaxResults(pageSize);
		
	}
	private String getCounHql(String hql,boolean isHql){
		String e = hql.substring(hql.indexOf("from"));
		String c = "select count(*) " +e;
		if(isHql){
			c= c.replaceAll("fetch", "");
		}
		return c;
	}
	/**
	 * 实现分页查找的方法
	 */
	@SuppressWarnings("unchecked")
	public Pager<T> find(String hql,Object[] args,Map<String, Object> alias){
		hql = initSort(hql);
		String cq = getCounHql(hql, true);
		Query  cquery = getSession().createQuery(cq);
		Query query = getSession().createQuery(hql);
		setAliasParameter(query, alias);
		setAliasParameter(cquery, alias);
		setParameter(query, args);
		setParameter(cquery, args);
		Pager<T> pages = new Pager<T>();
		setPagers(query, pages);
		List<T> datas = query.list();
		pages.setDatas(datas);
		Long total = (Long) cquery.uniqueResult();
		pages.setTotal(total);
		return pages;
		
 	}
}

