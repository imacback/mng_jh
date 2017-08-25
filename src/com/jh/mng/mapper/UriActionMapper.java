package com.jh.mng.mapper;

import java.util.List;

import com.jh.mng.pojo.UriAction;


public interface UriActionMapper {
	public List<UriAction> queryAllUriAction();
	
	public List<UriAction> queryUriActionByRoleId(Long roleId);
}
