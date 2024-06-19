package com.ust.invoice.extract.serdeser;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.ust.invoice.extract.annotations.FieldAlias;
import com.ust.invoice.extract.util.DateTimeUtil;

public class FieldAliasMapConverter {

	public static Map<String, String> objectToMap(Object obj) {
		Map<String, String> resultMap = new HashMap<>();
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			boolean canAccess = field.canAccess(obj);
			field.setAccessible(true);
			try {
				FieldAlias aliasAnnotation = field.getAnnotation(FieldAlias.class);
				String fieldName = field.getName();
				if (aliasAnnotation != null && aliasAnnotation.value() != null) {
					fieldName = aliasAnnotation.value(); // Use the first alias
				}
				resultMap.put(fieldName, convertToString(field, obj));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			field.setAccessible(canAccess);
		}
		return resultMap;
	}

	public static <T> T mapToObject(Map<String, Object> map, Class<T> cls) {
		try {
			T instance = cls.getDeclaredConstructor().newInstance();

			for (Field field : cls.getDeclaredFields()) {
				boolean canAccess = field.canAccess(instance);
				field.setAccessible(true);
				FieldAlias aliasAnnotation = field.getAnnotation(FieldAlias.class);
				String fieldName = field.getName();
				if (aliasAnnotation != null) {
					String alias = aliasAnnotation.value();
					if (alias != null && map.containsKey(alias)) {
						fieldName = alias;
					}
				}
				if (map.containsKey(fieldName)) {
					Object value = map.get(fieldName);
					field.set(instance, convertValue(field.getType(), value));
				}
				field.setAccessible(canAccess);
			}
			return instance;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String convertToString(Field field, Object object)
			throws IllegalArgumentException, IllegalAccessException {
		Class<?> type = field.getType();
		String stringValue = null;
		Object value = field.get(object);
		if (type == LocalDate.class) {
			stringValue = DateTimeUtil.toDateString((LocalDate) value);
		} else {
			stringValue = String.valueOf(value);
		}
		return stringValue;
	}

	private static Object convertValue(Class<?> type, Object value) {
		if (type == String.class)
			return value.toString();
		if (type == int.class || type == Integer.class)
			return Integer.parseInt(value.toString());
		if (type == long.class || type == Long.class)
			return Long.parseLong(value.toString());
		if (type == double.class || type == Double.class)
			return Double.parseDouble(value.toString());
		if (type == boolean.class || type == Boolean.class)
			return Boolean.parseBoolean(value.toString());
		return value;
	}
}
