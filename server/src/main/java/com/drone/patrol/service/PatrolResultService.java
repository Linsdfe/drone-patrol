package com.drone.patrol.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.drone.patrol.entity.PatrolResult;
import com.drone.patrol.mapper.PatrolResultMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 巡防结果管理服务类
 *
 * 功能说明：
 * - 提供巡防结果的分页查询功能
 * - 支持多条件筛选（结果编号、执行人、时间范围等）
 * - 提供Excel导出功能，便于数据备份和报表生成
 *
 * 使用示例：
 * - 分页查询：getResultPage(1, 10, null, null, null, null, null)
 * - 导出Excel：exportToExcel(response, resultList)
 *
 * @author Drone Patrol Team
 */
@Service
public class PatrolResultService extends ServiceImpl<PatrolResultMapper, PatrolResult> {

    /**
     * 分页查询巡防结果
     *
     * @param pageNum       页码（从1开始）
     * @param pageSize      每页记录数
     * @param resultCode    结果编号（模糊查询）
     * @param taskName      任务名称（模糊查询）
     * @param executorName  执行人姓名（模糊查询）
     * @param startTime     开始时间（格式：yyyy-MM-dd）
     * @param endTime       结束时间（格式：yyyy-MM-dd）
     * @return 分页后的巡防结果
     *
     * @example
     * // 查询第1页，每页10条记录
     * getResultPage(1, 10, null, null, null, null, null)
     *
     * // 查询特定执行人的记录
     * getResultPage(1, 10, null, null, "张三", "2024-01-01", "2024-12-31")
     */
    public Page<PatrolResult> getResultPage(Integer pageNum, Integer pageSize, String resultCode, String taskName, String executorName, String startTime, String endTime) {
        Page<PatrolResult> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PatrolResult> wrapper = new LambdaQueryWrapper<>();

        if (resultCode != null && !resultCode.isEmpty()) {
            wrapper.like(PatrolResult::getResultCode, resultCode);
        }
        if (executorName != null && !executorName.isEmpty()) {
            wrapper.like(PatrolResult::getExecutorName, executorName);
        }
        if (startTime != null && !startTime.isEmpty()) {
            wrapper.ge(PatrolResult::getCompleteTime, startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            wrapper.le(PatrolResult::getCompleteTime, endTime);
        }

        wrapper.orderByDesc(PatrolResult::getCreateTime);
        return page(page, wrapper);
    }

    /**
     * 导出巡防结果到Excel
     * @param response HTTP响应对象
     * @param resultList 巡防结果列表
     * @throws IOException IO异常
     */
    public void exportToExcel(HttpServletResponse response, List<PatrolResult> resultList) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("巡防结果");

        // 设置列宽
        int[] columnWidths = {4000, 3000, 3000, 3000, 3000, 5000, 6000, 8000, 8000, 3000, 10000, 3000};
        for (int i = 0; i < columnWidths.length; i++) {
            sheet.setColumnWidth(i, columnWidths[i]);
        }

        // 创建表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 11);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        // 创建数据样式
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dataStyle.setWrapText(true);

        // 创建表头行
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "结果编号", "任务ID", "设备ID", "航线ID", "执行人", "完成时间",
            "巡防概述", "发现情况", "处理情况", "风险评分", "AI分析结果", "备注"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 时间格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 填充数据
        for (int i = 0; i < resultList.size(); i++) {
            PatrolResult result = resultList.get(i);
            Row row = sheet.createRow(i + 1);
            row.setHeight((short) 600); // 设置行高

            // 结果编号
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(result.getResultCode() != null ? result.getResultCode() : "");
            cell0.setCellStyle(dataStyle);

            // 任务ID
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(result.getTaskId() != null ? result.getTaskId().toString() : "");
            cell1.setCellStyle(dataStyle);

            // 设备ID
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(result.getDeviceId() != null ? result.getDeviceId().toString() : "");
            cell2.setCellStyle(dataStyle);

            // 航线ID
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(result.getRouteId() != null ? result.getRouteId().toString() : "");
            cell3.setCellStyle(dataStyle);

            // 执行人
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(result.getExecutorName() != null ? result.getExecutorName() : "");
            cell4.setCellStyle(dataStyle);

            // 完成时间
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(result.getCompleteTime() != null ? result.getCompleteTime().format(formatter) : "");
            cell5.setCellStyle(dataStyle);

            // 巡防概述
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(result.getSummary() != null ? result.getSummary() : "");
            cell6.setCellStyle(dataStyle);

            // 发现情况
            Cell cell7 = row.createCell(7);
            cell7.setCellValue(result.getDiscovery() != null ? result.getDiscovery() : "");
            cell7.setCellStyle(dataStyle);

            // 处理情况
            Cell cell8 = row.createCell(8);
            cell8.setCellValue(result.getHandling() != null ? result.getHandling() : "");
            cell8.setCellStyle(dataStyle);

            // 风险评分（从AI结果中提取）
            Cell cell9 = row.createCell(9);
            String riskScore = extractRiskScore(result.getAiResult());
            cell9.setCellValue(riskScore);
            cell9.setCellStyle(dataStyle);

            // AI分析结果
            Cell cell10 = row.createCell(10);
            cell10.setCellValue(result.getAiResult() != null ? result.getAiResult() : "");
            cell10.setCellStyle(dataStyle);

            // 备注
            Cell cell11 = row.createCell(11);
            cell11.setCellValue(result.getRemark() != null ? result.getRemark() : "");
            cell11.setCellStyle(dataStyle);
        }

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("巡防结果导出", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * 从AI分析结果中提取风险评分
     * @param aiResult AI分析结果
     * @return 风险评分字符串
     */
    private String extractRiskScore(String aiResult) {
        if (aiResult == null || aiResult.isEmpty()) {
            return "未评估";
        }
        // 尝试从AI结果中提取风险评分
        if (aiResult.contains("风险评分")) {
            int index = aiResult.indexOf("风险评分");
            String substring = aiResult.substring(index);
            // 查找数字
            for (int i = 0; i < substring.length() && i < 20; i++) {
                char c = substring.charAt(i);
                if (Character.isDigit(c)) {
                    StringBuilder sb = new StringBuilder();
                    while (i < substring.length() && Character.isDigit(substring.charAt(i))) {
                        sb.append(substring.charAt(i));
                        i++;
                    }
                    return sb.toString() + "分";
                }
            }
        }
        return "已评估";
    }
}
