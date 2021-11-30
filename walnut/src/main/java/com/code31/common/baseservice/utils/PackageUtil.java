package com.code31.common.baseservice.utils;

import com.google.common.base.Predicate;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;


public class PackageUtil {
	/**
	 * 类默认构造器
	 * 
	 */
	private PackageUtil() {
	}

	/**
	 * 使用连接符连接字符串数组
	 *
	 * @param strs
	 * @param conn
	 * @return
	 */
	public static String join(String[] strs, char conn) {
		return join(strs, String.valueOf(conn));
	}

	/**
	 * 使用连接符连接字符串数组
	 *
	 * @param strs
	 * @param conn
	 * @return
	 *
	 */
	public static String join(String[] strs, String conn) {
		if (strs == null ||
				strs.length <= 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < strs.length; i++) {
			if (i > 0) {
				// 添加连接符
				sb.append(conn);
			}

			// 添加字符串
			sb.append(strs[i]);
		}

		return sb.toString();
	}

	/**
	 * 清除源字符串左边的字符串
	 *
	 * @param src
	 * @param s
	 * @return
	 *
	 */
	public static String trimLeft(String src, String s) {
		if (src == null ||
				src.isEmpty()) {
			return "";
		}

		if (s == null ||
				s.isEmpty()) {
			return src;
		}

		if (src.equals(s)) {
			return "";
		}

		while (src.startsWith(s)) {
			src = src.substring(s.length());
		}

		return src;
	}

	/**
	 * 清除源字符串右边的字符串
	 *
	 * @param src
	 * @param s
	 * @return
	 *
	 */
	public static String trimRight(String src, String s) {
		if (src == null ||
				src.isEmpty()) {
			return "";
		}

		if (s == null ||
				s.isEmpty()) {
			return src;
		}

		if (src.equals(s)) {
			return "";
		}

		while (src.endsWith(s)) {
			src = src.substring(0, src.length() - s.length());
		}

		return src;
	}

	public static boolean isEmptyString(String str){
		if(str==null||"".equals(str.trim())) return true;
		return false;
	}

	/**
	 * 读取包内所有的类获取clazz类的子类class对象
	 * 
	 * @param pname
	 * @return
	 */
	public static Set<Class<?>> getSubClass(String packageName, final Class<?> clazz) {
		return getPackageClasses(packageName, new Predicate<Class<?>>() {

			@Override
			public boolean apply(Class<?> arg) {
				return !arg.equals(clazz) && clazz.isAssignableFrom(arg);
			}

		});
	}
	
	/**
	 * 读取包内所有的类获取class对象
	 * 
	 * @param pname
	 * @return
	 */
	public static Set<Class<?>> getPackageClasses(String pname) {
		return getPackageClasses(pname, null);
	}

	/**
	 * 读取包内所有的类获取class对象，并根据指定的条件过滤
	 * 
	 * @param pname
	 * @param predicate
	 * @return
	 */
	public static Set<Class<?>> getPackageClasses(String pname, Predicate<Class<?>> predicate) {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		String packageDirName = pname.replace('.', '/');
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		ClassLoader cl1 =ClassLoader.getSystemClassLoader();
		try {
			//当打成jar包的时候会需要使用系统默认加载器来加载jar包中的类
			Enumeration<URL> dirs = cl1.getResources(packageDirName);
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				String protocol = url.getProtocol();
				if ("file".equals(protocol))
					findByFile(cl, pname, URLDecoder.decode(url.getFile(), "utf-8"), classes, predicate);
				else if ("jar".equals(protocol))
					findInJar(cl, pname, packageDirName, url, classes, predicate);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return classes;
	}
	
	
	/**
	 * 从文件获取java类
	 * 
	 * @param cl
	 * @param packageName
	 * @param filePath
	 * @param classes
	 * @param predicate
	 */
	private static void findByFile(ClassLoader cl, String packageName, String filePath,
			Set<Class<?>> classes, Predicate<Class<?>> predicate) {
		File dir = new File(filePath);
		if (!dir.exists() || !dir.isDirectory())
			return;

		File[] dirFiles = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.isDirectory() || file.getName().endsWith(".class");
			}
		});

		for (File file : dirFiles)
			if (file.isDirectory())
				findByFile(cl, packageName + "." + file.getName(), file.getAbsolutePath(), classes, predicate);
			else
				try {
					Class<?> clazz = cl.loadClass(packageName + "."
							+ file.getName().substring(0, file.getName().length() - 6));
					if (predicate == null || predicate.apply(clazz))
						classes.add(clazz);
				} catch (ExceptionInInitializerError e) {
					// 这个没关系 是无法初始化类
				} catch (NoClassDefFoundError e) {
					// 这个没关系 是无法初始化类
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
	}

	/**
	 * 读取jar中的java类
	 * 
	 * @param cl
	 * @param pname
	 * @param packageDirName
	 * @param url
	 * @param classes
	 * @param predicate
	 */
	private static void findInJar(ClassLoader cl, String pname, String packageDirName, URL url,
			Set<Class<?>> classes, Predicate<Class<?>> predicate) {
		try {
			JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();

			Enumeration<JarEntry> entries = jar.entries();

			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();

				if (entry.isDirectory())
					continue;

				String name = entry.getName();
				if (name.charAt(0) == '/')
					name = name.substring(0);

				if (name.startsWith(packageDirName) && name.contains("/") && name.endsWith(".class")) {
					name = name.substring(0, name.length() - 6).replace('/', '.');
					try {
						Class<?> clazz = cl.loadClass(name);
						if (predicate == null || predicate.apply(clazz))
							classes.add(clazz);
					} catch (Throwable e) {
						System.out.println("无法直接加载的类：" + name);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Set<String> filterNestedPackages(String pname, Predicate<String> predicate) {
		Set<String> packages = new HashSet<String>();
		String packageDirName = pname.replace('.', '/');

		ClassLoader cl = new ClassLoader() {
		};

		try {
			Enumeration<URL> dirs = cl.getResources(packageDirName);

			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();

				String protocol = url.getProtocol();

				if ("file".equals(protocol))
					filterPackageByFile(pname, URLDecoder.decode(url.getFile(), "utf-8"), packages, predicate);
				else if ("jar".equals(protocol))
					filterPackageInJar(pname, packageDirName, url, packages, predicate);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return packages;
	}

	/**
	 * 从文件获取java类
	 * 
	 * @param cl
	 * @param packageName
	 * @param filePath
	 * @param classes
	 * @param predicate
	 */
	private static void filterPackageByFile(String packageName, String filePath, Set<String> packages,
			Predicate<String> predicate) {
		File dir = new File(filePath);
		if (!dir.exists() || !dir.isDirectory())
			return;

		File[] dirFiles = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});

		for (File file : dirFiles) {
			String name = packageName + "." + file.getName();
			if (predicate.apply(name))
				packages.add(name);

			filterPackageByFile(name, file.getAbsolutePath(), packages, predicate);
		}
	}

	/**
	 * 读取jar中的java类
	 * 
	 * @param cl
	 * @param pname
	 * @param packageDirName
	 * @param url
	 * @param classes
	 * @param predicate
	 */
	private static void filterPackageInJar(String pname, String packageDirName, URL url,
			Set<String> packages, Predicate<String> predicate) {
		try {
			JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();

			Enumeration<JarEntry> entries = jar.entries();

			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();

				if (entry.isDirectory()) {
					String name = entry.getName();
					
					if (name.contains("/") && predicate.apply(name))
						packages.add(name);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	/**
	 * 列表指定 URL 文件、指定包中的所有类
	 * 
	 * @param filePath
	 * @param recursive 
	 * @param filter
	 * @return 
	 * 
	 */

	public static Set<Class<?>> listClazz(
		List<String> filePath, 
		boolean recursive, 
		IClazzFilter filter) {

		if (filePath == null || 
			filePath.isEmpty()) {
			return null;
		}
		
		Set<Class<?>> result = new HashSet<>();
		filePath.forEach((path) -> {
			listPath(path,result,recursive,filter);
		});
		
		return result;
	}
	
	private static void listPath(String filePath,Set<Class<?>> result
			,boolean recursive, IClazzFilter filter){
		// 创建文件对象
		File fileObj = new File(filePath);

		Set<Class<?>> tempSet = null;
		if (fileObj.isDirectory()) {
			// 给定参数是目录, 
			// 那么从目录中获取类列表
			tempSet = listClazzFromDir(fileObj, recursive, filter);
		}
		
		if (fileObj.isFile() && filePath.endsWith(".jar")) {
			// 给定参数是 jar 文件, 
			// 那么从 jar 文件中获取类列表
			tempSet = listClazzFromJar(fileObj, recursive, filter);
		} 
		
		if(tempSet == null || tempSet.isEmpty()){
			return;
		}
		
		result.addAll(tempSet);
	}

	/**
	 * 从目录中获取类列表
	 * 
	 * @param dir
	 * @param recursive 
	 * @param filter 
	 * @return 
	 * 
	 */
	private static Set<Class<?>> listClazzFromDir(
		File dir, 
		boolean recursive, 
		IClazzFilter filter) {

		if (!dir.exists() || 
			!dir.isDirectory()) {
			return null;
		}

		// 获取子文件列表
		File[] subFiles = dir.listFiles();

		if (subFiles == null || 
			subFiles.length <= 0) {
			return null;
		}

		// 文件队列
		Queue<File> fq = new LinkedList<File>();
		// 将子文件列表添加到队列
		fq.addAll(Arrays.asList(subFiles));

		// 结果对象
		Set<Class<?>> resultSet = new HashSet<Class<?>>();

		while (!fq.isEmpty()) {
			// 从队列中获取文件
			File currFile = fq.poll();
			
			if (currFile.isDirectory()) {
				// 如果当前文件是目录, 
				// 获取子文件列表
				subFiles = currFile.listFiles();
				if(subFiles == null){
					continue;
				}
				// 添加文件到队列
				fq.addAll(Arrays.asList(subFiles));
				continue;
			}

			if (!currFile.isFile() || 
				!currFile.getName().endsWith(".class")) {
				// 如果当前文件不是文件, 
				// 或者文件名不是以 .class 结尾, 
				// 则直接跳过
				continue;
			}

			// 类名称
			String clazzName;

			// 设置类名称
			clazzName = currFile.getAbsolutePath();
			// 清除最后的 .class 结尾 和‘com’字符串之前的部分
			clazzName = clazzName.substring(clazzName.indexOf("com"), clazzName.lastIndexOf('.'));
			// 转换目录斜杠
			clazzName = clazzName.replace('\\', '/');
			// 清除开头的 /

			clazzName = trimLeft(clazzName, "/");
			// 将所有的 / 修改为 .
			clazzName = StringUtils.join(clazzName.split("/"), ".");

			try {
				// 加载类定义
				Class<?> clazzObj = Class.forName(clazzName);
	
				if ((filter != null) && 
				    !filter.accept(clazzObj)) {
					// 如果过滤器不为空, 
					// 且过滤器不接受当前类, 
					// 则直接跳过!
					continue;
				}

				// 添加类定义到集合
				resultSet.add(clazzObj);
			} catch (Exception ex) {
				// 抛出异常
				throw new Error(ex);
			}
		}

		return resultSet;
	}

	/**
	 * 从 .jar 文件中获取类列表
	 * 
	 * @param jarFilePath
	 * @param recursive 
	 * @param filter 
	 * @return 
	 * 
	 */
	private static Set<Class<?>> listClazzFromJar(
		File jarFilePath, 
		boolean recursive, 
		IClazzFilter filter) {
		
		if (jarFilePath == null || 
			jarFilePath.isDirectory()) {
			return null;
		}
		
		// 结果对象
		Set<Class<?>> resultSet = new HashSet<Class<?>>();

		try {
			// 创建 .jar 文件读入流
			JarInputStream jarIn = new JarInputStream(new FileInputStream(jarFilePath));
			// 进入点
			JarEntry entry;

			while ((entry = jarIn.getNextJarEntry()) != null) {
				if (entry.isDirectory()) {
					continue;
				}

				// 获取进入点名称
				String entryName = entry.getName();

				if (!entryName.endsWith(".class")) {
					// 如果不是以 .class 结尾, 
					// 则说明不是 JAVA 类文件, 直接跳过!
					continue;
				}

				String clazzName;

				// 清除最后的 .class 结尾
				clazzName = entryName.substring(0, entryName.lastIndexOf('.'));
				// 将所有的 / 修改为 .
				clazzName = join(clazzName.split("/"), ".");

				// 加载类定义
				Class<?> clazzObj = Class.forName(clazzName);

				if ((filter != null) && 
				    !filter.accept(clazzObj)) {
					// 如果过滤器不为空, 
					// 且过滤器不接受当前类, 
					// 则直接跳过!
					continue;
				}

				// 添加类定义到集合
				resultSet.add(clazzObj);
			}

			// 关闭 jar 输入流
			jarIn.close();
		} catch (Exception ex) {
			// 抛出异常
			throw new Error(ex);
		}

		return resultSet;
	}

	/**
	 * 类名称过滤器
	 * 
	 * @author hjj2019
	 *
	 */
	@FunctionalInterface
	public static interface IClazzFilter {
		/**
		 * 是否接受当前类?
		 * 
		 * @param clazz
		 * @return 
		 * 
		 */
		boolean accept(Class<?> clazz);
	}
}
