package com.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.common.Result;
import com.property.dto.PropertyFeeRequest;
import com.property.service.PropertyFeeService;
import com.property.vo.PropertyFeeVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 物业费管理
 */
@RestController
@RequestMapping("/property-fee")
@RequiredArgsConstructor
public class PropertyFeeController {
    
    private final PropertyFeeService propertyFeeService;
    
    /**
     * 新增物业费（管理员/物业人员）
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<PropertyFeeVO> create(@Valid @RequestBody PropertyFeeRequest request) {
        return Result.success("创建成功", propertyFeeService.create(request));
    }
    
    /**
     * 分页查询物业费列表
     */
    @GetMapping("/page")
    public Result<Page<PropertyFeeVO>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer year) {
        return Result.success(propertyFeeService.getPage(pageNum, pageSize, status, type, year));
    }
    
    /**
     * 获取物业费详情
     */
    @GetMapping("/{id}")
    public Result<PropertyFeeVO> getById(@PathVariable Long id) {
        return Result.success(propertyFeeService.getById(id));
    }
    
    /**
     * 支付物业费
     */
    @PostMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id) {
        propertyFeeService.pay(id);
        return Result.success("支付成功", null);
    }
    
    /**
     * 删除物业费（管理员/物业人员）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> delete(@PathVariable Long id) {
        propertyFeeService.delete(id);
        return Result.success("删除成功", null);
    }
    
    /**
     * 导出物业费Excel
     */
    @GetMapping("/export")
    public void exportExcel(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer year,
            HttpServletResponse response) throws IOException {
        byte[] data = propertyFeeService.exportExcel(status, type, year);
        
        String fileName = "物业费记录_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }
}
