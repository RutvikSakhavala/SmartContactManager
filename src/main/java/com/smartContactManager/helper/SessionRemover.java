package com.smartContactManager.helper;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Component
public class SessionRemover {

	public void removeAttribute(String attribute)
	{
		try {
			HttpSession session = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getSession();
			session.removeAttribute(attribute);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
