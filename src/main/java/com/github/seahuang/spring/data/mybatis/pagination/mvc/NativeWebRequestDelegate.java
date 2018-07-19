package com.github.seahuang.spring.data.mybatis.pagination.mvc;

import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.springframework.web.context.request.NativeWebRequest;

public class NativeWebRequestDelegate implements NativeWebRequest {
	protected NativeWebRequest deletegate;
	
	public NativeWebRequestDelegate(NativeWebRequest deletegate){
		this.deletegate = deletegate;
	}

	public String getHeader(String headerName) {
		return deletegate.getHeader(headerName);
	}

	public Object getNativeRequest() {
		return deletegate.getNativeRequest();
	}

	public Object getNativeResponse() {
		return deletegate.getNativeResponse();
	}

	public String[] getHeaderValues(String headerName) {
		return deletegate.getHeaderValues(headerName);
	}

	public <T> T getNativeRequest(Class<T> requiredType) {
		return deletegate.getNativeRequest(requiredType);
	}

	public Iterator<String> getHeaderNames() {
		return deletegate.getHeaderNames();
	}

	public String getParameter(String paramName) {
		return deletegate.getParameter(paramName);
	}

	public <T> T getNativeResponse(Class<T> requiredType) {
		return deletegate.getNativeResponse(requiredType);
	}

	public Object getAttribute(String name, int scope) {
		return deletegate.getAttribute(name, scope);
	}

	public String[] getParameterValues(String paramName) {
		return deletegate.getParameterValues(paramName);
	}

	public void setAttribute(String name, Object value, int scope) {
		deletegate.setAttribute(name, value, scope);
	}

	public Iterator<String> getParameterNames() {
		return deletegate.getParameterNames();
	}

	public Map<String, String[]> getParameterMap() {
		return deletegate.getParameterMap();
	}

	public void removeAttribute(String name, int scope) {
		deletegate.removeAttribute(name, scope);
	}

	public Locale getLocale() {
		return deletegate.getLocale();
	}

	public String getContextPath() {
		return deletegate.getContextPath();
	}

	public String[] getAttributeNames(int scope) {
		return deletegate.getAttributeNames(scope);
	}

	public String getRemoteUser() {
		return deletegate.getRemoteUser();
	}

	public void registerDestructionCallback(String name, Runnable callback, int scope) {
		deletegate.registerDestructionCallback(name, callback, scope);
	}

	public Principal getUserPrincipal() {
		return deletegate.getUserPrincipal();
	}

	public boolean isUserInRole(String role) {
		return deletegate.isUserInRole(role);
	}

	public boolean isSecure() {
		return deletegate.isSecure();
	}

	public boolean checkNotModified(long lastModifiedTimestamp) {
		return deletegate.checkNotModified(lastModifiedTimestamp);
	}

	public Object resolveReference(String key) {
		return deletegate.resolveReference(key);
	}

	public String getSessionId() {
		return deletegate.getSessionId();
	}

	public Object getSessionMutex() {
		return deletegate.getSessionMutex();
	}

	public boolean checkNotModified(String etag) {
		return deletegate.checkNotModified(etag);
	}

	public boolean checkNotModified(String etag, long lastModifiedTimestamp) {
		return deletegate.checkNotModified(etag, lastModifiedTimestamp);
	}

	public String getDescription(boolean includeClientInfo) {
		return deletegate.getDescription(includeClientInfo);
	}
}
