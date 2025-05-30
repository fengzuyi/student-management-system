package com.student.management.service.impl;

import com.student.management.common.Result;
import com.student.management.dto.UserDTO;
import com.student.management.entity.User;
import com.student.management.mapper.UserMapper;
import com.student.management.service.UserService;
import com.student.management.util.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Result createUser(UserDTO userDTO) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(userDTO.getUsername()) != null) {
            return Result.error("用户名已存在");
        }

        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        
        // 设置默认密码（如果未提供）
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            user.setPassword("123456"); // 默认密码
        }

        userMapper.insert(user);
        return Result.success(null);
    }

    @Override
    @Transactional
    public Result updateUser(Long id, UserDTO userDTO) {
        User existingUser = userMapper.findById(id);
        if (existingUser == null) {
            return Result.error("用户不存在");
        }

        // 如果要修改用户名，检查新用户名是否已存在
        if (!existingUser.getUsername().equals(userDTO.getUsername()) &&
            userMapper.findByUsername(userDTO.getUsername()) != null) {
            return Result.error("用户名已存在");
        }

        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setId(id);

        // 如果密码为空，保持原密码不变
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            user.setPassword(existingUser.getPassword());
        }

        userMapper.update(user);
        return Result.success(null);
    }

    @Override
    @Transactional
    public Result deleteUser(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 获取当前登录用户ID
        Long currentUserId = SecurityUtil.getCurrentUserId();
        // 不允许删除自己
        if (id.equals(currentUserId)) {
            return Result.error("不能删除当前登录用户");
        }

        userMapper.deleteById(id);
        return Result.success(null);
    }

    @Override
    public Result getUserById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return Result.success(userDTO);
    }

    @Override
    public Result<Map<String, Object>> listUsers(String username, String realName, String role, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<User> users = userMapper.findList(username, realName, role, offset, pageSize);
        int total = userMapper.findCount(username, realName, role);
        
        List<UserDTO> userDTOs = users.stream()
            .map(user -> {
                UserDTO dto = new UserDTO();
                BeanUtils.copyProperties(user, dto);
                return dto;
            })
            .collect(Collectors.toList());
            
        Map<String, Object> data = new HashMap<>();
        data.put("list", userDTOs);
        data.put("total", total);
        
        return Result.success(data);
    }
} 