package org.cxw.nb.model;



/**
 *	用来传递分页对象的ThreadLocal对象； 
 *	说明：ThreadLocal类可用解决多线程的数据共享的问题；
 * 	@author cxw
 * 	方法：设定，获取，删除系统变量
 */

public class SystemContext {
	/*
	 * 分页大小
	 */
	private static ThreadLocal<Integer> pageSize = new ThreadLocal<Integer>();
	/*
	 * 分页的启示位置
	 */
	private static ThreadLocal<Integer> pageOffset = new ThreadLocal<Integer>();
	/*
	 * 排序的方式：does（降序） ase(升序)
	 * 
	 */
	private static ThreadLocal<String> sort = new ThreadLocal<String>();
	/*
	 * 排序字段
	 */
	private static ThreadLocal<String > order = new ThreadLocal<String>();
	/*
	 * 真实路径
	 */
	private static ThreadLocal<String> realPath = new ThreadLocal<String>();
	
	/*
	 * 定义的设定会哦去系统变量
	 */
	public static Integer getPageSize() {
		return pageSize.get();
	}
	public static void setPageSize(Integer pageSize) {
		SystemContext.pageSize.set(pageSize);
	}
	public static Integer getPageOffset() {
		return pageOffset.get();
	}
	public static void setPageOffset(Integer pageOffset) {
		SystemContext.pageOffset.set(pageOffset);
	}
	public static String getSort() {
		return sort.get();
	}
	public static void setSort(String sort) {
		SystemContext.sort.set(sort);
	}
	public static String getOrder() {
		return order.get();
	}
	public static void setOrder(String order) {
		SystemContext.order.set(order);
	}
	public static String getRealPath() {
		return realPath.get();
	}
	public static void setRealPath(String realPath) {
		SystemContext.realPath.set(realPath);
	}
	/*
	 * 定义删除系统变量
	 */
	
	public static void removePageSize(){
		pageSize.remove();
	}
	public static void removePageOffset(){
		pageOffset.remove();
	}
	public static void removeOrder(){
		order.remove();
	}
	public static void removeSort(){
		sort.remove();
	}
	public static void removeRealPath(){
		realPath.remove();
	}
	
}
