<template>
  <canvas ref="canvasRef" class="particle-canvas"></canvas>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

/**
 * 粒子背景组件
 *
 * 功能说明：
 * - 动态粒子漂浮动画效果
 * - 鼠标交互：粒子会响应鼠标移动
 * - 粒子间连线：距离较近的粒子会自动连线
 * - 响应式：窗口大小改变时自动调整
 *
 * 视觉效果：
 * - 粒子颜色：蓝紫色系渐变
 * - 粒子大小：0.5px - 3px 随机
 * - 连线透明度：根据距离动态变化
 * - 鼠标交互范围：120px
 *
 * @component ParticleBackground
 * @example
 * <ParticleBackground />
 */
const canvasRef = ref(null)
let animationId = null
let particles = []
let mouseX = 0
let mouseY = 0

/**
 * 粒子类
 *
 * @class Particle
 * @property {number} x - 粒子X坐标
 * @property {number} y - 粒子Y坐标
 * @property {number} size - 粒子大小
 * @property {number} speedX - X方向速度
 * @property {number} speedY - Y方向速度
 * @property {number} opacity - 透明度
 * @property {string} color - 颜色RGB值
 */
class Particle {
  constructor(canvas) {
    this.canvas = canvas
    this.x = Math.random() * canvas.width
    this.y = Math.random() * canvas.height
    this.size = Math.random() * 2 + 0.5
    this.speedX = Math.random() * 0.5 - 0.25
    this.speedY = Math.random() * 0.5 - 0.25
    this.opacity = Math.random() * 0.5 + 0.2
    this.color = this.getRandomColor()
  }

  /**
   * 获取随机颜色
   * 蓝紫色系渐变，与登录页面背景协调
   * @returns {string} RGB颜色值
   */
  getRandomColor() {
    const colors = [
      '102, 126, 234',  // 蓝紫色
      '118, 75, 162',   // 深紫色
      '106, 144, 128',  // 绿色
      '166, 124, 82',   // 棕色
      '122, 134, 150'   // 灰色
    ]
    return colors[Math.floor(Math.random() * colors.length)]
  }

  /**
   * 更新粒子位置
   * - 移动粒子
   * - 响应鼠标交互（推开效果）
   * - 边界检测和环绕
   */
  update() {
    this.x += this.speedX
    this.y += this.speedY

    // 鼠标交互：计算鼠标与粒子的距离
    const dx = mouseX - this.x
    const dy = mouseY - this.y
    const distance = Math.sqrt(dx * dx + dy * dy)

    // 鼠标靠近时推开粒子
    if (distance < 120) {
      const force = (120 - distance) / 120
      this.x -= dx * force * 0.02
      this.y -= dy * force * 0.02
    }

    // 边界环绕
    if (this.x < 0) this.x = this.canvas.width
    if (this.x > this.canvas.width) this.x = 0
    if (this.y < 0) this.y = this.canvas.height
    if (this.y > this.canvas.height) this.y = 0
  }

  /**
   * 绘制粒子
   * @param {CanvasRenderingContext2D} ctx - Canvas绘图上下文
   */
  draw(ctx) {
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2)
    ctx.fillStyle = `rgba(${this.color}, ${this.opacity})`
    ctx.fill()
  }
}

/**
 * 初始化Canvas和事件监听
 */
const initCanvas = () => {
  const canvas = canvasRef.value
  if (!canvas) return

  const resizeCanvas = () => {
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
    initParticles()
  }

  resizeCanvas()
  window.addEventListener('resize', resizeCanvas)

  // 鼠标移动事件
  canvas.addEventListener('mousemove', (e) => {
    mouseX = e.clientX
    mouseY = e.clientY
  })

  // 鼠标离开事件
  canvas.addEventListener('mouseleave', () => {
    mouseX = -1000
    mouseY = -1000
  })
}

/**
 * 初始化粒子数组
 * 粒子数量根据画布面积动态计算，最多150个
 */
const initParticles = () => {
  const canvas = canvasRef.value
  if (!canvas) return

  particles = []
  const particleCount = Math.min(150, Math.floor((canvas.width * canvas.height) / 8000))

  for (let i = 0; i < particleCount; i++) {
    particles.push(new Particle(canvas))
  }
}

/**
 * 动画循环
 * - 更新所有粒子位置
 * - 绘制粒子
 * - 绘制粒子间的连线
 */
const animate = () => {
  const canvas = canvasRef.value
  if (!canvas) return

  const ctx = canvas.getContext('2d')
  ctx.clearRect(0, 0, canvas.width, canvas.height)

  particles.forEach((particle, index) => {
    particle.update()
    particle.draw(ctx)

    // 绘制粒子间连线
    for (let j = index + 1; j < particles.length; j++) {
      const dx = particles[j].x - particle.x
      const dy = particles[j].y - particle.y
      const distance = Math.sqrt(dx * dx + dy * dy)

      if (distance < 100) {
        ctx.beginPath()
        ctx.moveTo(particle.x, particle.y)
        ctx.lineTo(particles[j].x, particles[j].y)
        ctx.strokeStyle = `rgba(102, 126, 234, ${0.15 * (1 - distance / 100)})`
        ctx.lineWidth = 0.5
        ctx.stroke()
      }
    }
  })

  animationId = requestAnimationFrame(animate)
}

// 组件挂载时启动动画
onMounted(() => {
  initCanvas()
  animate()
})

// 组件卸载时清理资源
onUnmounted(() => {
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
  window.removeEventListener('resize', initCanvas)
})
</script>

<style scoped>
.particle-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: auto;
}
</style>
