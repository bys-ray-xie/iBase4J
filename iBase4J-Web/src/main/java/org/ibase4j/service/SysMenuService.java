package org.ibase4j.service;

import java.util.Map;

import org.ibase4j.core.config.Resources;
import org.ibase4j.facade.sys.SysMenuFacade;
import org.ibase4j.mybatis.generator.model.SysMenu;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;

/**
 * @author ShenHuaJie
 * @version 2016年5月20日 下午3:16:07
 */
@Service
public class SysMenuService {
	@Reference
	private SysMenuFacade sysMenuFacade;

	public void update(SysMenu record) {
		Assert.notNull(record.getId(), Resources.getMessage("ID_IS_NULL"));
		sysMenuFacade.update(record);
	}

	public void add(SysMenu record) {
		Assert.notNull(record.getMenuName(), Resources.getMessage("NAME_IS_NULL"));
		sysMenuFacade.update(record);
	}

	public void delete(Integer id) {
		Assert.notNull(id, Resources.getMessage("ID_IS_NULL"));
		sysMenuFacade.delete(id);
	}

	public PageInfo<SysMenu> query(Map<String, Object> params) {
		return sysMenuFacade.query(params);
	}

	public SysMenu queryById(Integer id) {
		Assert.notNull(id, Resources.getMessage("ID_IS_NULL"));
		return sysMenuFacade.queryById(id);
	}
}
