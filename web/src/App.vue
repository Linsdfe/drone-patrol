<template>
  <router-view />
</template>

<script setup>
</script>

<style>
/* 企业级灰白风格主题色变量定义 - Light Theme */
:root {
  /* ========== 现代化配色方案 ========== */
  /* 主色调 - 渐变蓝紫色系 */
  --color-primary: #667eea;
  --color-primary-light: #764ba2;
  --color-primary-dark: #5a6fdb;
  --color-primary-bg: #f0f2ff;
  --color-primary-hover: #764ba2;
  --color-primary-active: #5a6fdb;

  /* 功能色 - 柔和莫兰迪色系 */
  --color-success: #6b9080;
  --color-success-light: #7da090;
  --color-success-bg: #f0f5f2;
  
  --color-warning: #a67c52;
  --color-warning-light: #b68d62;
  --color-warning-bg: #f5f0e8;
  
  --color-danger: #a65252;
  --color-danger-light: #b66262;
  --color-danger-bg: #f5e8e8;
  
  --color-info: #7a8696;

  /* 背景色 - 灰白系列 */
  --color-bg-base: #f0f2f5;
  --color-bg-white: #ffffff;
  --color-bg-light: #fafafa;
  --color-bg-dark: #e8ecef;

  /* 文字颜色 */
  --color-text-primary: #49494a;
  --color-text-regular: #4e5969;
  --color-text-secondary: #86909c;
  --color-text-placeholder: #b2b5ba;

  /* 边框颜色 */
  --color-border-base: #e5e6eb;
  --color-border-light: #e5e6eb;
  --color-border-lighter: #f2f3f5;

  /* 阴影 - 柔和分层 */
  --shadow-base: 0 2px 12px 0 rgba(0, 0, 0, 0.04);
  --shadow-light: 0 1px 4px 0 rgba(0, 0, 0, 0.03);
  --shadow-dark: 0 6px 24px 0 rgba(0, 0, 0, 0.08);

  /* 圆角 - 企业级规范 */
  --border-radius-base: 6px;
  --border-radius-small: 4px;
  --border-radius-large: 8px;

  /* 间距 */
  --spacing-base: 20px;
  --spacing-small: 12px;
  --spacing-large: 24px;

  /* 按钮专属变量 */
  --button-height: 32px;
  --button-height-large: 40px;
  --button-height-small: 24px;
  --button-font-size: 14px;
  --button-padding-horizontal: 16px;
  --button-padding-horizontal-small: 12px;
}

/* 全局基础样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  width: 100%;
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-size: 14px;
  color: var(--color-text-primary);
  background-color: var(--color-bg-base);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  transition: background-color 0.3s ease, color 0.3s ease;
}

/* ========== 企业级按钮系统 - 参考阿里云/腾讯云/飞书设计 ========== */

/* 基础按钮样式 */
.el-button {
  border-radius: var(--border-radius-base) !important;
  font-weight: 400 !important;
  font-size: var(--button-font-size) !important;
  height: var(--button-height) !important;
  padding: 0 var(--button-padding-horizontal) !important;
  transition: all 0.15s ease-in-out !important;
  border: 1px solid transparent !important;
  display: inline-flex !important;
  align-items: center !important;
  justify-content: center !important;
}

/* 按钮禁用状态 */
.el-button.is-disabled,
.el-button.is-disabled:hover,
.el-button.is-disabled:focus,
.el-button.is-disabled:active {
  background-color: var(--color-bg-light) !important;
  border-color: var(--color-border-base) !important;
  color: var(--color-text-placeholder) !important;
  opacity: 0.5 !important;
  cursor: not-allowed !important;
  box-shadow: none !important;
}

/* 主按钮 - 主要操作（新增、保存、提交） */
.el-button--primary {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-light) 100%) !important;
  border: none !important;
  color: #ffffff !important;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3) !important;
}

.el-button--primary:hover {
  background: linear-gradient(135deg, var(--color-primary-light) 0%, var(--color-primary) 100%) !important;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4) !important;
  transform: translateY(-1px);
}

.el-button--primary:active {
  background: linear-gradient(135deg, var(--color-primary-dark) 0%, var(--color-primary) 100%) !important;
  box-shadow: 0 2px 6px rgba(102, 126, 234, 0.2) !important;
  transform: translateY(0);
}

/* 默认按钮 - 次要操作（编辑、查看） */
.el-button--default {
  background-color: var(--color-bg-white) !important;
  border: 1px solid var(--color-border-base) !important;
  color: var(--color-text-regular) !important;
}

.el-button--default:hover {
  background-color: var(--color-primary-bg) !important;
  border-color: var(--color-primary) !important;
  color: var(--color-primary) !important;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.1) !important;
}

.el-button--default:active {
  background-color: var(--color-bg-dark) !important;
  border-color: var(--color-primary-dark) !important;
  color: var(--color-primary-dark) !important;
}

/* 文字按钮 - 辅助操作（重置、清除） */
.el-button--text {
  background-color: transparent !important;
  border: none !important;
  color: var(--color-text-secondary) !important;
  padding: 0 8px !important;
  height: auto !important;
}

.el-button--text:hover {
  background-color: var(--color-bg-light) !important;
  color: var(--color-primary) !important;
}

.el-button--text:active {
  background-color: var(--color-bg-dark) !important;
  color: var(--color-primary-dark) !important;
}

/* 成功按钮 - 确认操作（确认、同意、启用） */
.el-button--success {
  background: linear-gradient(135deg, var(--color-success) 0%, var(--color-success-light) 100%) !important;
  border: none !important;
  color: #ffffff !important;
  box-shadow: 0 2px 8px rgba(107, 144, 128, 0.3) !important;
}

.el-button--success:hover {
  background: linear-gradient(135deg, var(--color-success-light) 0%, var(--color-success) 100%) !important;
  box-shadow: 0 4px 16px rgba(107, 144, 128, 0.4) !important;
  transform: translateY(-1px);
}

.el-button--success:active {
  background: linear-gradient(135deg, #5b8070 0%, var(--color-success) 100%) !important;
  box-shadow: 0 2px 6px rgba(107, 144, 128, 0.2) !important;
  transform: translateY(0);
}

/* 警告按钮 - 警告操作（警告、提示） */
.el-button--warning {
  background: linear-gradient(135deg, var(--color-warning) 0%, var(--color-warning-light) 100%) !important;
  border: none !important;
  color: #ffffff !important;
  box-shadow: 0 2px 8px rgba(166, 124, 82, 0.3) !important;
}

.el-button--warning:hover {
  background: linear-gradient(135deg, var(--color-warning-light) 0%, var(--color-warning) 100%) !important;
  box-shadow: 0 4px 16px rgba(166, 124, 82, 0.4) !important;
  transform: translateY(-1px);
}

.el-button--warning:active {
  background: linear-gradient(135deg, #966c42 0%, var(--color-warning) 100%) !important;
  box-shadow: 0 2px 6px rgba(166, 124, 82, 0.2) !important;
  transform: translateY(0);
}

/* 危险按钮 - 取消/删除操作（删除、禁用） */
.el-button--danger {
  background: linear-gradient(135deg, var(--color-danger) 0%, var(--color-danger-light) 100%) !important;
  border: none !important;
  color: #ffffff !important;
  box-shadow: 0 2px 8px rgba(166, 82, 82, 0.3) !important;
}

.el-button--danger:hover {
  background: linear-gradient(135deg, var(--color-danger-light) 0%, var(--color-danger) 100%) !important;
  box-shadow: 0 4px 16px rgba(166, 82, 82, 0.4) !important;
  transform: translateY(-1px);
}

.el-button--danger:active {
  background: linear-gradient(135deg, #964242 0%, var(--color-danger) 100%) !important;
  box-shadow: 0 2px 6px rgba(166, 82, 82, 0.2) !important;
  transform: translateY(0);
}

/* 信息按钮 - 信息操作（详情、帮助、导出） */
.el-button--info {
  background: linear-gradient(135deg, var(--color-info) 0%, #8a96a6 100%) !important;
  border: none !important;
  color: #ffffff !important;
  box-shadow: 0 2px 8px rgba(122, 134, 150, 0.3) !important;
}

.el-button--info:hover {
  background: linear-gradient(135deg, #8a96a6 0%, var(--color-info) 100%) !important;
  box-shadow: 0 4px 16px rgba(122, 134, 150, 0.4) !important;
  transform: translateY(-1px);
}

.el-button--info:active {
  background: linear-gradient(135deg, #6a7686 0%, var(--color-info) 100%) !important;
  box-shadow: 0 2px 6px rgba(122, 134, 150, 0.2) !important;
  transform: translateY(0);
}

/* 按钮过渡效果 */
.el-button {
  transition: all 0.2s ease-in-out !important;
}

/* 禁用状态按钮 */
.el-button.is-disabled,
.el-button.is-disabled:hover,
.el-button.is-disabled:focus,
.el-button.is-disabled:active {
  background-color: var(--color-bg-light) !important;
  border-color: var(--color-border-base) !important;
  color: var(--color-text-placeholder) !important;
  opacity: 0.5 !important;
  cursor: not-allowed !important;
  box-shadow: none !important;
}

/* 大尺寸按钮 */
.el-button--large {
  height: var(--button-height-large) !important;
  padding: 0 var(--spacing-base) !important;
  font-size: 15px !important;
}

/* 小尺寸按钮 */
.el-button--small {
  height: var(--button-height-small) !important;
  padding: 0 var(--button-padding-horizontal-small) !important;
  font-size: 12px !important;
}

/* 按钮组间距优化 */
.el-button + .el-button {
  margin-left: 8px !important;
}

/* ========== 其他全局组件样式 ========== */

/* 全局输入框样式覆盖 */
.el-input__wrapper {
  background-color: var(--color-bg-white) !important;
  border-radius: var(--border-radius-base) !important;
  box-shadow: 0 0 0 1px var(--color-border-base) !important;
  transition: all 0.15s ease-in-out !important;
}

.el-input__wrapper:hover {
  box-shadow: 0 0 0 1px var(--color-primary) !important;
}

.el-input__wrapper.is-focus {
  box-shadow: 0 0 0 2px var(--color-primary-bg), 0 0 0 1px var(--color-primary) !important;
}

/* 全局卡片样式覆盖 */
.el-card {
  border-radius: var(--border-radius-large) !important;
  border: 1px solid var(--color-border-lighter) !important;
  box-shadow: var(--shadow-light) !important;
  background-color: var(--color-bg-white) !important;
  transition: all 0.2s ease-in-out !important;
}

.el-card:hover {
  box-shadow: var(--shadow-base) !important;
  border-color: var(--color-border-base) !important;
}

/* 全局表格样式覆盖 */
.el-table {
  border-radius: var(--border-radius-large) !important;
  background-color: var(--color-bg-white) !important;
  border: 1px solid var(--color-border-lighter) !important;
}

.el-table th {
  background-color: var(--color-bg-light) !important;
  color: var(--color-text-primary) !important;
  font-weight: 500 !important;
}

.el-table tr {
  background-color: var(--color-bg-white) !important;
}

.el-table__row:hover > td {
  background-color: var(--color-bg-light) !important;
}

/* 全局对话框样式覆盖 */
.el-dialog {
  border-radius: var(--border-radius-large) !important;
  box-shadow: var(--shadow-dark) !important;
}

.el-dialog__header {
  border-bottom: 1px solid var(--color-border-lighter);
  padding: var(--spacing-base) !important;
}

.el-dialog__body {
  padding: var(--spacing-large) !important;
}

.el-dialog__footer {
  padding: 12px 24px !important;
  border-top: 1px solid var(--color-border-lighter);
  background-color: var(--color-bg-light);
}

/* 全局表单标签样式 */
.el-form-item__label {
  color: var(--color-text-regular) !important;
  font-weight: 400 !important;
}

/* 全局分页样式覆盖 */
.el-pagination {
  color: var(--color-text-regular) !important;
}

.el-pagination button,
.el-pager li {
  background-color: var(--color-bg-white) !important;
  border-radius: var(--border-radius-base) !important;
  border: 1px solid var(--color-border-base) !important;
  transition: all 0.15s ease-in-out !important;
}

.el-pagination button:hover,
.el-pager li:hover {
  color: var(--color-primary) !important;
  border-color: var(--color-primary) !important;
}

.el-pager li.is-active {
  background-color: var(--color-primary) !important;
  border-color: var(--color-primary) !important;
  color: #ffffff !important;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: var(--color-bg-light);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: var(--color-border-base);
  border-radius: 4px;
  transition: background 0.2s ease;
}

::-webkit-scrollbar-thumb:hover {
  background: var(--color-text-secondary);
}

/* 全局菜单样式覆盖 */
.el-menu {
  border-right: none !important;
}

.el-menu-item {
  border-radius: var(--border-radius-base);
  margin: 4px 8px;
  color: var(--color-text-regular) !important;
  transition: all 0.15s ease-in-out !important;
}

.el-menu-item:hover {
  background-color: var(--color-bg-light) !important;
  color: var(--color-text-primary) !important;
}

.el-menu-item.is-active {
  background-color: var(--color-primary-bg) !important;
  color: var(--color-primary) !important;
}

/* Tag 标签样式 */
.el-tag {
  border-radius: var(--border-radius-base) !important;
  padding: 0 10px !important;
  height: 24px !important;
  line-height: 22px !important;
  font-size: 12px !important;
}

/* Select 选择器样式 */
.el-select .el-input__wrapper {
  background-color: var(--color-bg-white) !important;
}

/* Message 消息提示 */
.el-message {
  border-radius: var(--border-radius-large) !important;
  box-shadow: var(--shadow-dark) !important;
  padding: 14px 20px !important;
}

/* MessageBox 确认框 */
.el-message-box {
  border-radius: var(--border-radius-large) !important;
  box-shadow: var(--shadow-dark) !important;
}

/* Drawer 抽屉 */
.el-drawer {
  border-radius: 0 var(--border-radius-large) var(--border-radius-large) 0 !important;
}


</style>
