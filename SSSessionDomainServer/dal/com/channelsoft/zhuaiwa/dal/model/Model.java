package com.channelsoft.zhuaiwa.dal.model;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class Model {
	public enum ModelType {
		SUPERCOLUMN,COLUMN
	}
	public enum KeyType {
		FIELD,CONSTANT
	}
	public enum ValueType {
		MAP,LIST
	}
	
	private ModelType modelType;
	private KeyType keyType;
	private ValueType valueType;
	
	// 
	private Class<?> clazz;
	private Field parentField;
	
	// key
	private Field keyField;
	private String keyConstant;
	
	// map
	@SuppressWarnings("unchecked")
	private Map<?, ?> map = new LinkedHashMap();
	private Class<?> mapKeyType;
	private Class<?> mapValueType;
	
	// list
	private Class<?> listNameType;
	private Class<?> listValueType;
	private Field listField;
	private Model listModel;
	
	public ModelType getModelType() {
		return modelType;
	}
	public void setModelType(ModelType modelType) {
		this.modelType = modelType;
	}
	public KeyType getKeyType() {
		return keyType;
	}
	public void setKeyType(KeyType keyType) {
		this.keyType = keyType;
	}
	public ValueType getValueType() {
		return valueType;
	}
	public void setValueType(ValueType valueType) {
		this.valueType = valueType;
	}
	public Field getKeyField() {
		return keyField;
	}
	public void setKeyField(Field keyField) {
		this.keyField = keyField;
	}
	public String getKeyConstant() {
		return keyConstant;
	}
	public void setKeyConstant(String keyConstant) {
		this.keyConstant = keyConstant;
	}
	@SuppressWarnings("unchecked")
	public Map getMap() {
		return map;
	}
	public void setMap(Map<?, ?> map) {
		this.map = map;
	}
	public Class<?> getMapKeyType() {
		return mapKeyType;
	}
	public void setMapKeyType(Class<?> mapKeyType) {
		this.mapKeyType = mapKeyType;
	}
	public Class<?> getMapValueType() {
		return mapValueType;
	}
	public void setMapValueType(Class<?> mapValueType) {
		this.mapValueType = mapValueType;
	}
	public Class<?> getListNameType() {
		return listNameType;
	}
	public void setListNameType(Class<?> listNameType) {
		this.listNameType = listNameType;
	}
	public Class<?> getListValueType() {
		return listValueType;
	}
	public void setListValueType(Class<?> listValueType) {
		this.listValueType = listValueType;
	}
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
	public Class<?> getClazz() {
		return clazz;
	}
	public void setListModel(Model listModel) {
		this.listModel = listModel;
	}
	public Model getListModel() {
		return listModel;
	}
	public Field getParentField() {
		return parentField;
	}
	public void setParentField(Field parentField) {
		this.parentField = parentField;
	}
	public Field getListField() {
		return listField;
	}
	public void setListField(Field listField) {
		this.listField = listField;
	}
}
