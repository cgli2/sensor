package com.cnjson.sensor.db.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReflectionUtils {

	/**
	 * 通过反射，获得定义Class时声明的父类的泛型参数类型 如：public EmploeeDao extends BaseDao
	 * <Empolyee,String>
	 * 
	 * @param clazz
	 * @param index
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Class getSuperClassGenricType(Class clazz, int index) {
		Type genType = clazz.getGenericSuperclass(); // 拿到clazz表示的实体的类型（类、接口、基本类型或void）

		if (!(genType instanceof ParameterizedType)) { // 如果clazz的类型不是ParameterizedType（表示参数化类型）的一个实例
			return Object.class; // 返回clazz
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments(); // 获得clazz实际类型参数的数组

		if (index >= params.length || index < 0) {
			return Object.class;
		}

		if (!((params[index]) instanceof Class)) {
			return Object.class;
		}
		return (Class) params[index];
	}

	/**
	 * 通过反射，获得Class定义中的声明的父类的泛型参数类型
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Class<T> getSuperGenericType(Class clazz) {
		return getSuperClassGenricType(clazz, 0);
	}

	/**
	 * 循环向上转型, 获取对象的 DeclaredMethod
	 * 
	 * @param object
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 */
	public static Method getDeclaredMethod(Object object, String methodName, Class<?>[] parameterTypes) {

		for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				// superClass.getMethod(methodName, parameterTypes);
				return superClass.getDeclaredMethod(methodName, parameterTypes);
			} catch (NoSuchMethodException e) {
				// Method 不在当前类定义, 继续向上转型
			}
			// ..
		}

		return null;
	}

	/**
	 * 使 filed 变为可访问
	 * 
	 * @param field
	 */
	public static void makeAccessible(Field field) {
		if (!Modifier.isPublic(field.getModifiers())) {
			field.setAccessible(true);
		}
	}

	/**
	 * 循环向上转型, 获取对象的 DeclaredField
	 * 
	 * @param object
	 * @param filedName
	 * @return
	 */
	public static Field getDeclaredField(Object object, String filedName) {

		for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				return superClass.getDeclaredField(filedName);
			} catch (NoSuchFieldException e) {
				// Field 不在当前类定义, 继续向上转型
			}
		}
		return null;
	}

	private static boolean isEffectField(Field field) {
		if (Modifier.isPrivate(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
			field.setAccessible(true);
			return true;
		}
		return false;
	}

	/**
	 * 获取指定类的所有非静态字段（包括继承的父类）
	 * 
	 * @param object
	 * @return
	 */
	public static List<Field> getDeclareFields(Object object) {
		List<Field> array = new ArrayList<>();
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (isEffectField(field)) {
				// System.out.println("-->" + field.getName());
				array.add(field);
			}
			// System.out.println("--->"+field.getDouble(obj));
		}
		// 向上转型，找到父类属性。
		Class<?> clazz = object.getClass();
		while (!clazz.equals(Object.class)) {
			clazz = clazz.getSuperclass();
			for (Field field : clazz.getDeclaredFields()) {
				if (isEffectField(field)) {
					// System.out.println("-->" + field.getName());
					array.add(field);
				}
			}
		}
		return array;

	}

	/**
	 * 直接调用对象方法, 而忽略修饰符(private, protected)
	 * 
	 * @param object
	 * @param methodName
	 * @param parameterTypes
	 * @param parameters
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */
	public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes, Object[] parameters)
			throws InvocationTargetException {

		Method method = getDeclaredMethod(object, methodName, parameterTypes);

		if (method == null) {
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + object + "]");
		}

		method.setAccessible(true);

		try {
			return method.invoke(object, parameters);
		} catch (IllegalAccessException e) {
			System.out.println("不可能抛出的异常");
		}
		return null;
	}

	/**
	 * 直接设置对象属性值，忽略private/protected 修饰符，也不经过setter方法
	 * 
	 * @param object
	 * @param fieldName
	 * @param value
	 */
	public static void setFieldValue(Object object, String fieldName, Object value) {
		Field field = getDeclaredField(object, fieldName);

		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
		}

		makeAccessible(field);

		try {
			field.set(object, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			System.out.println("不可能抛出的异常");
			e.printStackTrace();
		}
	}

	/**
	 * 直接读取对象的属性值，忽略private/protected，也不经过getter方法
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 */
	public static Object getFieldValue(Object object, String fieldName) {
		Field field = getDeclaredField(object, fieldName);

		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
		}

		makeAccessible(field);

		Object result = null;
		try {
			result = field.get(object);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			System.out.println("不可能抛出的异常");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 把数据库的值映射成JAVA对应的属性值
	 * 
	 * @param object
	 *            相应类 数据库类型
	 * @param methodName
	 *            set的方法，如setName
	 * @param colnumValue
	 *            数据对应列值
	 */
	public static void reflectSetInfo(Object object, String methodName, Object colnumValue) {
		try {
			Class<? extends Object> ptype = colnumValue.getClass();
			if (colnumValue.getClass().getSimpleName().equals("Timestamp")) {
				ptype = Date.class;
			}
			Method method = object.getClass().getMethod(methodName, ptype);
			method.invoke(object, colnumValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 把java 中的get方法映射成数据库对应列
	 * @param object
	 * @param methodName
	 * @return
	 */
	public static Object reflectGetInfo(Object object, String methodName) {
		Object value = null;
		try {
			Method method = object.getClass().getMethod(methodName);
			Object returnValue = method.invoke(object);
			if (returnValue != null) {
				value = returnValue.toString();
			} else {
				value = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	public static String columnToBean(String column) {
		if (column.contains("_")) {
			int index = column.indexOf("_");
			String beanName = column.substring(0, index) + column.substring(index + 1, index + 2).toUpperCase()
					+ column.substring(index + 2, column.length());
			return beanName;
		}
		return column;
	}

	public static String changeColumnToBean(String column, String ext) {
		if (column.indexOf("_") >= 0) {
			String[] col = column.split("_");
			for (int i = 0; i < col.length; i++) {
				column = columnToBean(column);
			}
		}
		String first = column.substring(0, 1);
		column = column.replaceFirst(first, first.toUpperCase());
		column = ext + column;
		return column;
	}

}
