package com.drone.patrol.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.drone.patrol.config.ZhipuConfig;
import com.drone.patrol.entity.PatrolResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * AI识别服务类
 * 用于分析巡防结果，调用智谱AI API进行智能分析
 */
@Service
public class AiRecognitionService {

    private static final Logger logger = LoggerFactory.getLogger(AiRecognitionService.class);

    /**
     * 智谱AI配置类
     */
    @Autowired
    private ZhipuConfig zhipuConfig;

    /**
     * JSON对象映射器
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 分析巡防结果
     * @param result 巡防结果对象
     * @return 分析结果，包含AI分析结果、风险评分和风险等级
     */
    public Map<String, Object> analyzePatrolResult(PatrolResult result) {
        String prompt = buildPrompt(result);
        Map<String, Object> analysis = new HashMap<>();

        try {
            String aiResponse = callZhipuApi(prompt);

            // 解析AI返回的JSON响应
            Map<String, Object> aiResultMap = parseAiResponse(aiResponse);

            int riskScore = (int) aiResultMap.getOrDefault("riskScore", 0);
            String riskLevel = (String) aiResultMap.getOrDefault("riskLevel", "低风险");
            String aiAnalysisResult = (String) aiResultMap.getOrDefault("analysis", aiResponse);

            analysis.put("aiResult", aiAnalysisResult);
            analysis.put("riskScore", riskScore);
            analysis.put("riskLevel", riskLevel);
            logger.info("AI分析成功，结果: {}, 风险等级: {}", result.getResultCode(), riskLevel);
        } catch (Exception e) {
            logger.error("AI分析失败: {}", e.getMessage(), e);
            Map<String, Object> fallbackAnalysis = fallbackAnalyze(result);
            analysis.put("aiResult", fallbackAnalysis.get("aiResult"));
            analysis.put("riskScore", fallbackAnalysis.get("riskScore"));
            analysis.put("riskLevel", fallbackAnalysis.get("riskLevel"));
            analysis.put("fallback", true);
        }

        return analysis;
    }

    /**
     * 解析AI返回的响应
     * @param aiResponse AI返回的原始响应
     * @return 解析后的结果，包含风险评分、风险等级和分析内容
     */
    private Map<String, Object> parseAiResponse(String aiResponse) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 清理响应内容，移除可能的markdown代码块标记
            String cleanedResponse = aiResponse.trim();
            logger.info("AI原始响应: {}", cleanedResponse);
            
            if (cleanedResponse.startsWith("```json")) {
                cleanedResponse = cleanedResponse.substring(7);
            } else if (cleanedResponse.startsWith("```")) {
                cleanedResponse = cleanedResponse.substring(3);
            }
            if (cleanedResponse.endsWith("```")) {
                cleanedResponse = cleanedResponse.substring(0, cleanedResponse.length() - 3);
            }
            cleanedResponse = cleanedResponse.trim();

            // 尝试解析JSON
            JsonNode jsonNode = objectMapper.readTree(cleanedResponse);
            int riskScore = jsonNode.path("riskScore").asInt(0);
            String riskLevel = jsonNode.path("riskLevel").asText("低风险");
            String analysis = jsonNode.path("analysis").asText(cleanedResponse);
            
            // 验证结果
            if (riskScore < 0 || riskScore > 100) {
                riskScore = 50;
                riskLevel = "中风险";
            }
            
            if (analysis.length() < 50) {
                analysis = cleanedResponse; // 如果分析内容太短，使用原始响应
            }
            
            result.put("riskScore", riskScore);
            result.put("riskLevel", riskLevel);
            result.put("analysis", analysis);
            logger.info("AI响应解析成功，风险评分: {}, 风险等级: {}", riskScore, riskLevel);
        } catch (Exception e) {
            logger.error("JSON解析失败: {}", e.getMessage());
            // 如果JSON解析失败，返回原始响应作为分析内容
            result.put("riskScore", 50);
            result.put("riskLevel", "中风险");
            result.put("analysis", aiResponse);
        }
        return result;
    }

    /**
     * 构建AI分析的提示词
     * @param result 巡防结果对象
     * @return 构建好的提示词
     */
    private String buildPrompt(PatrolResult result) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位专业的无人机巡防安全分析师。请根据以下巡防数据进行分析，并以JSON格式返回结果：\n\n");

        if (StrUtil.isNotBlank(result.getSummary())) {
            prompt.append("【巡防概述】\n").append(result.getSummary()).append("\n\n");
        }
        if (StrUtil.isNotBlank(result.getDiscovery())) {
            prompt.append("【发现情况】\n").append(result.getDiscovery()).append("\n\n");
        }
        if (StrUtil.isNotBlank(result.getHandling())) {
            prompt.append("【处理情况】\n").append(result.getHandling()).append("\n\n");
        }

        prompt.append("请分析以上数据，返回以下JSON格式（必须严格是有效JSON，不要包含markdown代码块标记）：\n");
        prompt.append("{\n");
        prompt.append("  \"riskScore\": 风险评分(0-100的数字),\n");
        prompt.append("  \"riskLevel\": \"风险等级(无风险/低风险/中风险/高风险/极高风险)\",\n");
        prompt.append("  \"analysis\": \"详细的分析报告内容，包含安全状况评估、异常分析、处理评估和建议等\"\n");
        prompt.append("}\n\n");
        prompt.append("注意：\n");
        prompt.append("- riskScore根据风险程度为0-100的数字，0表示无风险，100表示极高风险\n");
        prompt.append("- 风险等级对应：0分为无风险，1-39为低风险，40-69为中风险，70-89为高风险，90-100为极高风险\n");
        prompt.append("- analysis字段要包含详细的分析内容，不少于100字\n");
        prompt.append("请直接返回JSON，不要有其他内容。");

        return prompt.toString();
    }

    /**
     * 调用智谱AI API
     * @param prompt 提示词
     * @return AI返回的响应内容
     * @throws Exception 调用异常
     */
    private String callZhipuApi(String prompt) throws Exception {
        String apiKey = zhipuConfig.getApiKey();
        if (StrUtil.isBlank(apiKey)) {
            throw new RuntimeException("请在application.yml中配置有效的智谱API密钥");
        }

        String url = zhipuConfig.getBaseUrl();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", zhipuConfig.getModel());
        requestBody.put("temperature", 0.3);
        requestBody.put("stream", false);
        requestBody.put("max_tokens", 1000);

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        requestBody.put("messages", new Object[]{message});

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        // 优化：增加重试机制
        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                HttpResponse response = HttpRequest.post(url)
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .body(jsonBody)
                        .timeout(15000) // 增加超时时间到15秒
                        .execute();

                if (!response.isOk()) {
                    throw new RuntimeException("API调用失败: " + response.getStatus() + " - " + response.body());
                }

                JsonNode responseJson = objectMapper.readTree(response.body());
                // 检查是否有错误
                if (responseJson.has("error")) {
                    throw new RuntimeException("API调用失败: " + responseJson.path("error").path("message").asText());
                }
                JsonNode choices = responseJson.path("choices");
                if (choices.isArray() && choices.size() > 0) {
                    JsonNode messageNode = choices.get(0).path("message");
                    return messageNode.path("content").asText();
                }

                throw new RuntimeException("API返回格式异常");
            } catch (Exception e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    throw e;
                }
                // 等待一段时间后重试
                Thread.sleep(1000 * retryCount);
            }
        }

        throw new RuntimeException("API调用失败");
    }

    /**
     * 计算风险评分
     * @param result 巡防结果对象
     * @return 风险评分（0-100）
     */
    private int calculateRiskScore(PatrolResult result) {
        int riskScore = 0;

        if (StrUtil.isNotBlank(result.getDiscovery())) {
            String discovery = result.getDiscovery();
            if (discovery.contains("异常")) riskScore += 20;
            if (discovery.contains("危险")) riskScore += 30;
            if (discovery.contains("违规")) riskScore += 25;
            if (discovery.contains("问题")) riskScore += 15;
            if (discovery.contains("严重")) riskScore += 35;
            if (discovery.contains("紧急")) riskScore += 40;
        }

        if (StrUtil.isNotBlank(result.getHandling())) {
            String handling = result.getHandling();
            if (handling.contains("未处理") || handling.contains("待处理")) {
                riskScore += 25;
            }
        }

        if (StrUtil.isNotBlank(result.getRemark())) {
            String remark = result.getRemark();
            if (remark.contains("高风险")) riskScore += 30;
            if (remark.contains("风险")) riskScore += 15;
            if (remark.contains("危险")) riskScore += 25;
            if (remark.contains("紧急")) riskScore += 30;
        }

        // 增加基础分数，确保有异常情况时风险分数能体现出来
        if (riskScore > 0) {
            riskScore = Math.min(riskScore + 10, 100);
        }

        return Math.min(riskScore, 100);
    }

    /**
     * 根据风险评分获取风险等级
     * @param score 风险评分
     * @return 风险等级
     */
    private String getRiskLevel(int score) {
        if (score == 0) {
            return "无风险";
        } else if (score < 40) {
            return "低风险";
        } else if (score < 70) {
            return "中风险";
        } else if (score < 90) {
            return "高风险";
        } else {
            return "极高风险";
        }
    }

    /**
     * 备用分析方法，当AI调用失败时使用
     * @param result 巡防结果对象
     * @return 分析结果
     */
    private Map<String, Object> fallbackAnalyze(PatrolResult result) {
        Map<String, Object> analysis = new HashMap<>();
        StringBuilder aiResult = new StringBuilder();

        aiResult.append("【AI智能分析报告】\n\n");

        if (StrUtil.isNotBlank(result.getSummary())) {
            aiResult.append("1. 巡防概述分析：\n");
            aiResult.append("   ").append(analyzeSummary(result.getSummary())).append("\n\n");
        }

        if (StrUtil.isNotBlank(result.getDiscovery())) {
            aiResult.append("2. 发现情况分析：\n");
            String[] discoveries = result.getDiscovery().split("[，,。]");
            int alertCount = 0;
            for (String discovery : discoveries) {
                if (discovery.contains("异常") || discovery.contains("危险") ||
                    discovery.contains("违规") || discovery.contains("问题")) {
                    alertCount++;
                }
            }
            aiResult.append("   检测到 ").append(alertCount).append(" 项潜在风险点\n");
            aiResult.append("   建议：").append(getDiscoveryAdvice(alertCount)).append("\n\n");
        }

        if (StrUtil.isNotBlank(result.getHandling())) {
            aiResult.append("3. 处理情况评估：\n");
            aiResult.append("   ").append(analyzeHandling(result.getHandling())).append("\n\n");
        }

        int riskScore = calculateRiskScore(result);
        aiResult.append("4. 综合风险评估：\n");
        aiResult.append("   风险等级：").append(getRiskLevel(riskScore)).append("\n");
        aiResult.append("   风险评分：").append(riskScore).append("/100\n");
        aiResult.append("   建议：").append(getOverallAdvice(riskScore)).append("\n\n");

        aiResult.append("5. 后续行动建议：\n");
        aiResult.append("   ").append(getActionSuggestions(result));

        analysis.put("aiResult", aiResult.toString());
        analysis.put("riskScore", riskScore);
        analysis.put("riskLevel", getRiskLevel(riskScore));

        return analysis;
    }

    /**
     * 分析巡防概述
     * @param summary 巡防概述
     * @return 分析结果
     */
    private String analyzeSummary(String summary) {
        if (summary.contains("正常") || summary.contains("安全") || summary.contains("无异常")) {
            return "本次巡防区域整体运行正常，未发现重大安全隐患。";
        } else if (summary.contains("异常") || summary.contains("问题")) {
            return "巡防区域存在一定异常情况，建议重点关注。";
        }
        return "巡防任务已完成，具体情况详见详细报告。";
    }

    /**
     * 根据发现的异常数量获取建议
     * @param alertCount 异常数量
     * @return 建议内容
     */
    private String getDiscoveryAdvice(int alertCount) {
        if (alertCount == 0) {
            return "未发现明显异常，继续保持常规巡防频次。";
        } else if (alertCount <= 2) {
            return "发现少量异常，建议增加巡防频次，重点关注相关区域。";
        } else {
            return "发现多项异常，建议立即进行现场核查，并考虑增加巡防力量。";
        }
    }

    /**
     * 分析处理情况
     * @param handling 处理情况
     * @return 分析结果
     */
    private String analyzeHandling(String handling) {
        if (handling.contains("已处理") || handling.contains("已解决") || handling.contains("已完成")) {
            return "处理措施得当，问题已得到有效解决。";
        } else if (handling.contains("处理中") || handling.contains("进行中")) {
            return "问题正在处理中，建议持续跟踪处理进度。";
        } else if (handling.contains("待处理") || handling.contains("未处理")) {
            return "问题尚未处理，建议尽快安排处理并跟踪结果。";
        }
        return "处理情况记录完整，建议持续关注后续发展。";
    }

    /**
     * 根据风险评分获取整体建议
     * @param riskScore 风险评分
     * @return 建议内容
     */
    private String getOverallAdvice(int riskScore) {
        if (riskScore < 30) {
            return "区域安全状况良好，维持现有巡防计划即可。";
        } else if (riskScore < 60) {
            return "存在一定风险，建议适当增加巡防频次。";
        } else if (riskScore < 80) {
            return "风险较高，建议制定专项整改方案。";
        } else {
            return "风险极高，建议立即启动应急预案，进行全面检查。";
        }
    }

    /**
     * 获取行动建议
     * @param result 巡防结果对象
     * @return 行动建议
     */
    private String getActionSuggestions(PatrolResult result) {
        StringBuilder suggestions = new StringBuilder();

        if (result.getDiscovery() != null &&
            (result.getDiscovery().contains("异常") || result.getDiscovery().contains("违规"))) {
            suggestions.append("- 建议安排人员对异常区域进行现场复核\n");
        }

        if (result.getHandling() == null || result.getHandling().isEmpty() ||
            result.getHandling().contains("待处理")) {
            suggestions.append("- 建议优先处理尚未解决的问题\n");
        }

        suggestions.append("- 建议将本次巡防结果归档保存\n");
        suggestions.append("- 建议对比历史巡防数据，分析变化趋势\n");

        if (suggestions.length() == 0) {
            suggestions.append("- 继续保持常规巡防频次\n");
            suggestions.append("- 建议定期进行数据分析，积累巡防经验\n");
        }

        return suggestions.toString();
    }
}