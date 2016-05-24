package org.ibase4j.service.sys;

import java.util.List;
import java.util.Map;

import org.ibase4j.core.support.BaseService;
import org.ibase4j.core.support.dubbo.spring.annotation.DubboService;
import org.ibase4j.core.util.InstanceUtil;
import org.ibase4j.facade.sys.SysAuthorizeFacade;
import org.ibase4j.mybatis.generator.dao.SysRoleMenuMapper;
import org.ibase4j.mybatis.generator.dao.SysUserMenuMapper;
import org.ibase4j.mybatis.generator.dao.SysUserRoleMapper;
import org.ibase4j.mybatis.generator.model.SysMenu;
import org.ibase4j.mybatis.generator.model.SysRoleMenu;
import org.ibase4j.mybatis.generator.model.SysUserMenu;
import org.ibase4j.mybatis.generator.model.SysUserRole;
import org.ibase4j.mybatis.sys.dao.SysAuthorizeMapper;
import org.ibase4j.mybatis.sys.model.SysMenuBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ShenHuaJie
 * @version 2016年5月20日 下午3:19:19
 */
@DubboService(interfaceClass = SysAuthorizeFacade.class)
public class SysAuthorizeService extends BaseService<SysMenu> implements SysAuthorizeFacade {
	@Autowired
	private SysUserMenuMapper sysUserMenuMapper;
	@Autowired
	private SysUserRoleMapper sysUserRoleMapper;
	@Autowired
	private SysRoleMenuMapper sysRoleMenuMapper;
	@Autowired
	private SysAuthorizeMapper sysAuthorizeMapper;
	@Autowired
	private SysMenuService sysMenuService;

	@Transactional
	@CacheEvict(value = "getAuthorize", allEntries = true)
	public void updateUserMenu(List<SysUserMenu> sysUserMenus) {
		sysAuthorizeMapper.deleteUserMenu(sysUserMenus.get(0).getUserId());
		for (SysUserMenu sysUserMenu : sysUserMenus) {
			sysUserMenuMapper.insert(sysUserMenu);
		}
	}

	@Transactional
	@CacheEvict(value = "getAuthorize", allEntries = true)
	public void updateUserRole(List<SysUserRole> sysUserRoles) {
		sysAuthorizeMapper.deleteUserRole(sysUserRoles.get(0).getUserId());
		for (SysUserRole sysUserRole : sysUserRoles) {
			sysUserRoleMapper.insert(sysUserRole);
		}
	}

	@Transactional
	@CacheEvict(value = "getAuthorize", allEntries = true)
	public void updateRoleMenu(List<SysRoleMenu> sysRoleMenus) {
		sysAuthorizeMapper.deleteRoleMenu(sysRoleMenus.get(0).getRoleId());
		for (SysRoleMenu sysRoleMenu : sysRoleMenus) {
			sysRoleMenuMapper.insert(sysRoleMenu);
		}
	}

	@Cacheable(value = "getAuthorize")
	public List<SysMenuBean> queryAuthorizeByUserId(Integer userId) {
		List<Integer> menuIds = sysAuthorizeMapper.getAuthorize(userId);
		List<SysMenuBean> menus = getList(menuIds, SysMenuBean.class);
		Map<Integer, List<SysMenuBean>> map = InstanceUtil.newHashMap();
		for (SysMenuBean sysMenuBean : menus) {
			if (map.get(sysMenuBean.getParentId()) == null) {
				List<SysMenuBean> menuBeans = InstanceUtil.newArrayList();
				map.put(sysMenuBean.getParentId(), menuBeans);
			}
			map.get(sysMenuBean.getParentId()).add(sysMenuBean);
		}
		List<SysMenuBean> result = InstanceUtil.newArrayList();
		for (SysMenuBean sysMenuBean : menus) {
			if (sysMenuBean.getParentId() == 0) {
				sysMenuBean.setMenuBeans(getChildMenu(map, sysMenuBean.getId()));
				result.add(sysMenuBean);
			}
		}
		return result;
	}

	// 递归获取子菜单
	private List<SysMenuBean> getChildMenu(Map<Integer, List<SysMenuBean>> map, Integer id) {
		List<SysMenuBean> menus = map.get(id);
		if (menus != null) {
			for (SysMenuBean sysMenuBean : menus) {
				sysMenuBean.setMenuBeans(getChildMenu(map, sysMenuBean.getId()));
			}
		}
		return menus;
	}

	public SysMenu queryById(Integer id) {
		return sysMenuService.queryById(id);
	}
}
