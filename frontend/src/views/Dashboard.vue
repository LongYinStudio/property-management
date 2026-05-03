<template>
  <div class="dashboard-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <span class="hero-badge">{{ currentDateText }}</span>
        <h1>{{ greetingText }}，{{ displayName }}</h1>
        <p>
          当前以{{ roleLabel }}身份登录，系统待跟进事项共
          {{ pendingTaskCount }} 项，建议优先处理报修、投诉和清洁任务。
        </p>
        <div class="hero-actions">
          <el-button type="primary" size="large" @click="goTo('/repair')">
            进入报修管理
          </el-button>
          <el-button
            size="large"
            plain
            @click="goTo(canViewStatistics ? '/statistics' : '/notice')"
          >
            {{ canViewStatistics ? "查看统计报表" : "查看通知公告" }}
          </el-button>
        </div>
      </div>

      <div class="hero-side">
        <div class="hero-side-card spotlight-card">
          <div class="card-topline">年度收入概览</div>
          <div class="spotlight-value">
            {{ formatCurrency(yearlyIncomeTotal) }}
          </div>
          <div class="spotlight-meta">
            基于 {{ currentYear }} 年已缴物业相关费用统计
          </div>
          <div class="spotlight-trend">
            <span>本月收入</span>
            <strong>{{ formatCurrency(currentMonthIncome) }}</strong>
          </div>
        </div>

        <div class="hero-side-card action-card">
          <div class="card-topline">快捷入口</div>
          <div class="quick-grid">
            <button
              v-for="action in quickActions"
              :key="action.path"
              type="button"
              class="quick-action"
              @click="goTo(action.path)"
            >
              <el-icon><component :is="action.icon" /></el-icon>
              <span>{{ action.label }}</span>
            </button>
          </div>
        </div>
      </div>
    </section>

    <section class="stats-grid">
      <article
        v-for="card in statCards"
        :key="card.label"
        class="stat-card"
        :class="card.theme"
        v-loading="overviewLoading"
      >
        <div class="stat-icon">
          <el-icon><component :is="card.icon" /></el-icon>
        </div>
        <div class="stat-body">
          <span class="stat-label">{{ card.label }}</span>
          <strong class="stat-value">{{ card.value }}</strong>
          <span class="stat-note">{{ card.note }}</span>
        </div>
      </article>
    </section>

    <section class="insight-grid">
      <article class="panel panel-main" v-loading="incomeLoading">
        <div class="panel-header">
          <div>
            <span class="panel-eyebrow">Revenue Trend</span>
            <h3>月度收入趋势</h3>
          </div>
          <el-button text @click="goTo('/fee')">物业费管理</el-button>
        </div>
        <div ref="incomeChartRef" class="chart chart-lg"></div>
      </article>

      <article class="panel panel-side" v-loading="repairStatusLoading">
        <div class="panel-header">
          <div>
            <span class="panel-eyebrow">Repair Flow</span>
            <h3>报修状态分布</h3>
          </div>
          <el-button text @click="goTo('/repair')">全部报修</el-button>
        </div>
        <div ref="repairStatusChartRef" class="chart chart-md"></div>
      </article>

      <article class="panel panel-side" v-loading="complaintTypeLoading">
        <div class="panel-header">
          <div>
            <span class="panel-eyebrow">Complaint Mix</span>
            <h3>投诉建议分布</h3>
          </div>
          <el-button text @click="goTo('/complaint')">全部投诉</el-button>
        </div>
        <div ref="complaintChartRef" class="chart chart-md"></div>
      </article>

      <article class="panel panel-side" v-loading="cleaningLoading">
        <div class="panel-header">
          <div>
            <span class="panel-eyebrow">Cleaning Board</span>
            <h3>清洁任务看板</h3>
          </div>
          <el-button text @click="goTo('/cleaning')">清洁管理</el-button>
        </div>
        <div class="progress-list">
          <div v-for="item in cleaningProgress" :key="item.label" class="progress-item">
            <div class="progress-head">
              <span>{{ item.label }}</span>
              <strong>{{ item.count }}</strong>
            </div>
            <el-progress
              :percentage="item.percent"
              :stroke-width="10"
              :show-text="false"
              :color="item.color"
            />
          </div>
        </div>
      </article>
    </section>

    <section class="recent-grid">
      <article class="panel recent-panel" v-loading="repairLoading">
        <div class="panel-header">
          <div>
            <span class="panel-eyebrow">Latest Repairs</span>
            <h3>最近报修</h3>
          </div>
          <el-button text @click="goTo('/repair')">查看全部</el-button>
        </div>

        <div v-if="recentRepairs.length" class="activity-list">
          <div v-for="item in recentRepairs" :key="`repair-${item.id}`" class="activity-item">
            <div class="activity-main">
              <div class="activity-title-row">
                <h4>{{ item.title }}</h4>
                <el-tag :type="getRepairStatusType(item.status)" effect="dark" round>
                  {{ getRepairStatusText(item.status) }}
                </el-tag>
              </div>
              <p>{{ item.content || "报修内容未填写" }}</p>
              <div class="activity-meta">
                <span>提交人：{{ item.userName || "未命名用户" }}</span>
                <span>{{ item.createTime }}</span>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无报修记录" />
      </article>

      <article class="panel recent-panel" v-loading="complaintRecentLoading">
        <div class="panel-header">
          <div>
            <span class="panel-eyebrow">Latest Complaints</span>
            <h3>最近投诉建议</h3>
          </div>
          <el-button text @click="goTo('/complaint')">查看全部</el-button>
        </div>

        <div v-if="recentComplaints.length" class="activity-list">
          <div
            v-for="item in recentComplaints"
            :key="`complaint-${item.id}`"
            class="activity-item"
          >
            <div class="activity-main">
              <div class="activity-title-row">
                <h4>{{ item.title }}</h4>
                <el-tag :type="getComplaintStatusType(item.status)" effect="dark" round>
                  {{ getComplaintStatusText(item.status) }}
                </el-tag>
              </div>
              <p>{{ item.content || "投诉内容未填写" }}</p>
              <div class="activity-meta">
                <span>提交人：{{ item.userName || "未命名用户" }}</span>
                <span>{{ item.createTime }}</span>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无投诉建议" />
      </article>
    </section>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, ref } from "vue";
import { useRouter } from "vue-router";
import * as echarts from "echarts";
import dayjs from "dayjs";
import "dayjs/locale/zh-cn";
import {
  Bell,
  Brush,
  ChatDotSquare,
  DataAnalysis,
  Document,
  Tools,
  User,
  Wallet,
} from "@element-plus/icons-vue";
import {
  getCleaningStats,
  getComplaintTypeStats,
  getDashboardStats,
  getMonthlyIncome,
  getRepairStatusStats,
} from "@/api/statistics";
import { getComplaintPage } from "@/api/complaint";
import { getRepairPage } from "@/api/repair";
import { useUserStore } from "@/stores/user";

const router = useRouter();
const userStore = useUserStore();
dayjs.locale("zh-cn");

const currentYear = new Date().getFullYear();

const overviewLoading = ref(false);
const incomeLoading = ref(false);
const repairStatusLoading = ref(false);
const complaintTypeLoading = ref(false);
const cleaningLoading = ref(false);
const repairLoading = ref(false);
const complaintRecentLoading = ref(false);

const statistics = ref({
  userCount: 0,
  pendingRepair: 0,
  unpaidFee: 0,
  pendingComplaint: 0,
  pendingCleaning: 0,
});
const incomeData = ref([]);
const repairStatusData = ref([]);
const complaintTypeData = ref([]);
const cleaningData = ref([]);
const recentRepairs = ref([]);
const recentComplaints = ref([]);

const incomeChartRef = ref(null);
const repairStatusChartRef = ref(null);
const complaintChartRef = ref(null);

let incomeChart = null;
let repairStatusChart = null;
let complaintChart = null;

const displayName = computed(
  () => userStore.userInfo?.realName || userStore.userInfo?.username || "管理员",
);

const roleLabel = computed(() => userStore.userInfo?.roleName || "系统用户");
const canViewStatistics = computed(() => userStore.userInfo?.role !== 3);

const greetingText = computed(() => {
  const hour = dayjs().hour();
  if (hour < 6) return "凌晨好";
  if (hour < 11) return "早上好";
  if (hour < 14) return "中午好";
  if (hour < 18) return "下午好";
  return "晚上好";
});

const currentDateText = computed(() =>
  dayjs().format("YYYY年MM月DD日 dddd"),
);

const pendingTaskCount = computed(
  () =>
    Number(statistics.value.pendingRepair || 0) +
    Number(statistics.value.pendingComplaint || 0) +
    Number(statistics.value.pendingCleaning || 0),
);

const yearlyIncomeTotal = computed(() =>
  incomeData.value.reduce((sum, item) => sum + Number(item.amount || 0), 0),
);

const currentMonthIncome = computed(() => {
  const currentMonth = new Date().getMonth() + 1;
  const currentMonthRecord = incomeData.value.find(
    (item) => Number(item.month) === currentMonth,
  );
  return Number(currentMonthRecord?.amount || 0);
});

const statCards = computed(() => [
  {
    label: "住户与用户",
    value: statistics.value.userCount || 0,
    note: "系统内启用账号总量",
    icon: User,
    theme: "theme-sky",
  },
  {
    label: "待处理报修",
    value: statistics.value.pendingRepair || 0,
    note: "需要尽快派单或跟进",
    icon: Tools,
    theme: "theme-coral",
  },
  {
    label: "待缴费用",
    value: statistics.value.unpaidFee || 0,
    note: "本期仍未完成缴费",
    icon: Wallet,
    theme: "theme-gold",
  },
  {
    label: "待处理投诉",
    value: statistics.value.pendingComplaint || 0,
    note: "需要及时回复住户反馈",
    icon: ChatDotSquare,
    theme: "theme-mint",
  },
  {
    label: "清洁待办",
    value: statistics.value.pendingCleaning || 0,
    note: "待执行的清洁工作任务",
    icon: Brush,
    theme: "theme-indigo",
  },
]);

const cleaningProgress = computed(() => {
  const total = cleaningData.value.reduce(
    (sum, item) => sum + Number(item.count || 0),
    0,
  );

  const colorMap = {
    待处理: "#f59e0b",
    处理中: "#2563eb",
    已完成: "#0f766e",
  };

  return cleaningData.value.map((item) => ({
    label: item.statusName,
    count: Number(item.count || 0),
    percent: total ? Math.round((Number(item.count || 0) / total) * 100) : 0,
    color: colorMap[item.statusName] || "#94a3b8",
  }));
});

const quickActions = computed(() => {
  const actions = [
    { label: "通知公告", path: "/notice", icon: Bell },
    { label: "报修管理", path: "/repair", icon: Tools },
    { label: "投诉建议", path: "/complaint", icon: ChatDotSquare },
    { label: "合同管理", path: "/contract", icon: Document },
  ];

  if (userStore.userInfo?.role !== 3) {
    actions.push({ label: "统计报表", path: "/statistics", icon: DataAnalysis });
  }

  return actions;
});

const formatCurrency = (value) =>
  new Intl.NumberFormat("zh-CN", {
    style: "currency",
    currency: "CNY",
    maximumFractionDigits: 0,
  }).format(Number(value || 0));

const goTo = (path) => {
  router.push(path);
};

const getRepairStatusType = (status) => {
  const types = { 0: "warning", 1: "primary", 2: "success", 3: "info" };
  return types[status] || "info";
};

const getRepairStatusText = (status) => {
  const texts = { 0: "待处理", 1: "处理中", 2: "已完成", 3: "已关闭" };
  return texts[status] || "未知";
};

const getComplaintStatusType = (status) => {
  const types = { 0: "warning", 1: "success", 2: "info" };
  return types[status] || "info";
};

const getComplaintStatusText = (status) => {
  const texts = { 0: "待处理", 1: "已回复", 2: "已关闭" };
  return texts[status] || "未知";
};

const loadOverview = async () => {
  overviewLoading.value = true;
  try {
    const { data } = await getDashboardStats();
    statistics.value = data || statistics.value;
  } finally {
    overviewLoading.value = false;
  }
};

const loadIncomeData = async () => {
  incomeLoading.value = true;
  try {
    const { data } = await getMonthlyIncome({ year: currentYear });
    incomeData.value = data || [];
    await nextTick();
    initIncomeChart();
  } finally {
    incomeLoading.value = false;
  }
};

const loadRepairStatusData = async () => {
  repairStatusLoading.value = true;
  try {
    const { data } = await getRepairStatusStats();
    repairStatusData.value = data || [];
    await nextTick();
    initRepairStatusChart();
  } finally {
    repairStatusLoading.value = false;
  }
};

const loadComplaintTypeData = async () => {
  complaintTypeLoading.value = true;
  try {
    const { data } = await getComplaintTypeStats();
    complaintTypeData.value = data || [];
    await nextTick();
    initComplaintChart();
  } finally {
    complaintTypeLoading.value = false;
  }
};

const loadCleaningData = async () => {
  cleaningLoading.value = true;
  try {
    const { data } = await getCleaningStats();
    cleaningData.value = data || [];
  } finally {
    cleaningLoading.value = false;
  }
};

const loadRecentItems = async () => {
  repairLoading.value = true;
  complaintRecentLoading.value = true;
  try {
    const [repairRes, complaintRes] = await Promise.all([
      getRepairPage({ pageNum: 1, pageSize: 4 }),
      getComplaintPage({ pageNum: 1, pageSize: 4 }),
    ]);
    recentRepairs.value = repairRes.data.records || [];
    recentComplaints.value = complaintRes.data.records || [];
  } finally {
    repairLoading.value = false;
    complaintRecentLoading.value = false;
  }
};

const initIncomeChart = () => {
  if (!incomeChartRef.value) return;

  if (!incomeChart) {
    incomeChart = echarts.init(incomeChartRef.value);
  }

  incomeChart.setOption({
    tooltip: {
      trigger: "axis",
      formatter: (params) => {
        const item = params[0];
        return `${item.axisValue}<br />收入：${formatCurrency(item.data)}`;
      },
    },
    grid: { top: 24, right: 18, bottom: 24, left: 18, containLabel: true },
    xAxis: {
      type: "category",
      boundaryGap: false,
      data: incomeData.value.map((item) => `${item.month}月`),
      axisLine: { lineStyle: { color: "#cbd5e1" } },
      axisLabel: { color: "#64748b" },
    },
    yAxis: {
      type: "value",
      axisLabel: {
        color: "#64748b",
        formatter: (value) => `¥${value}`,
      },
      splitLine: { lineStyle: { color: "rgba(148, 163, 184, 0.18)" } },
    },
    series: [
      {
        type: "line",
        smooth: true,
        symbolSize: 8,
        data: incomeData.value.map((item) => Number(item.amount || 0)),
        lineStyle: { width: 4, color: "#0f766e" },
        itemStyle: { color: "#0f766e" },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: "rgba(15, 118, 110, 0.38)" },
            { offset: 1, color: "rgba(15, 118, 110, 0.02)" },
          ]),
        },
      },
    ],
  });
};

const initRepairStatusChart = () => {
  if (!repairStatusChartRef.value) return;

  if (!repairStatusChart) {
    repairStatusChart = echarts.init(repairStatusChartRef.value);
  }

  repairStatusChart.setOption({
    tooltip: { trigger: "item" },
    color: ["#f59e0b", "#2563eb", "#10b981", "#94a3b8"],
    legend: {
      bottom: 0,
      icon: "circle",
      itemWidth: 10,
      textStyle: { color: "#475569" },
    },
    series: [
      {
        type: "pie",
        radius: ["52%", "74%"],
        center: ["50%", "45%"],
        label: {
          color: "#334155",
          formatter: "{b}\n{c}",
        },
        labelLine: { length: 10, length2: 10 },
        itemStyle: {
          borderRadius: 12,
          borderColor: "#ffffff",
          borderWidth: 4,
        },
        data: repairStatusData.value.map((item) => ({
          name: item.statusName,
          value: Number(item.count || 0),
        })),
      },
    ],
  });
};

const initComplaintChart = () => {
  if (!complaintChartRef.value) return;

  if (!complaintChart) {
    complaintChart = echarts.init(complaintChartRef.value);
  }

  complaintChart.setOption({
    tooltip: {
      trigger: "axis",
      axisPointer: { type: "shadow" },
    },
    grid: { top: 16, right: 12, bottom: 8, left: 12, containLabel: true },
    xAxis: {
      type: "value",
      splitLine: { lineStyle: { color: "rgba(148, 163, 184, 0.18)" } },
      axisLabel: { color: "#64748b" },
    },
    yAxis: {
      type: "category",
      data: complaintTypeData.value.map((item) => item.typeName),
      axisTick: { show: false },
      axisLine: { show: false },
      axisLabel: { color: "#334155" },
    },
    series: [
      {
        type: "bar",
        barWidth: 18,
        data: complaintTypeData.value.map((item, index) => ({
          value: Number(item.count || 0),
          itemStyle: {
            borderRadius: 999,
            color: index === 0 ? "#0ea5e9" : "#fb7185",
          },
        })),
        label: {
          show: true,
          position: "right",
          color: "#334155",
        },
      },
    ],
  });
};

const handleResize = () => {
  incomeChart?.resize();
  repairStatusChart?.resize();
  complaintChart?.resize();
};

const loadDashboard = async () => {
  await Promise.allSettled([
    loadOverview(),
    loadIncomeData(),
    loadRepairStatusData(),
    loadComplaintTypeData(),
    loadCleaningData(),
    loadRecentItems(),
  ]);
};

onMounted(() => {
  loadDashboard();
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
  incomeChart?.dispose();
  repairStatusChart?.dispose();
  complaintChart?.dispose();
});
</script>

<style lang="scss" scoped>
.dashboard-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
  color: #0f172a;
}

.hero-panel {
  display: grid;
  grid-template-columns: minmax(0, 1.65fr) minmax(320px, 1fr);
  gap: 20px;
  padding: 28px;
  border-radius: 28px;
  background:
    radial-gradient(circle at top right, rgba(251, 191, 36, 0.22), transparent 28%),
    radial-gradient(circle at left bottom, rgba(14, 165, 233, 0.22), transparent 28%),
    linear-gradient(135deg, #0f172a 0%, #134e4a 52%, #ecfeff 160%);
  box-shadow: 0 28px 80px rgba(15, 23, 42, 0.18);
  overflow: hidden;

  .hero-copy {
    position: relative;
    z-index: 1;
    display: flex;
    flex-direction: column;
    gap: 16px;
    min-width: 0;
    color: #f8fafc;
  }

  .hero-badge {
    width: fit-content;
    padding: 8px 12px;
    border-radius: 999px;
    background: rgba(255, 255, 255, 0.12);
    font-size: 13px;
    letter-spacing: 0.04em;
  }

  h1 {
    font-size: 36px;
    line-height: 1.1;
    letter-spacing: -0.04em;
  }

  p {
    max-width: 620px;
    color: rgba(248, 250, 252, 0.8);
    font-size: 15px;
    line-height: 1.8;
  }

  .hero-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin-top: 8px;
  }
}

.hero-side {
  display: grid;
  gap: 16px;
}

.hero-side-card {
  padding: 20px;
  border-radius: 24px;
  backdrop-filter: blur(14px);
  background: rgba(255, 255, 255, 0.86);
  color: #0f172a;
}

.card-topline {
  color: #64748b;
  font-size: 13px;
  letter-spacing: 0.06em;
  text-transform: uppercase;
}

.spotlight-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.spotlight-value {
  font-size: 34px;
  font-weight: 700;
  line-height: 1;
  letter-spacing: -0.04em;
}

.spotlight-meta {
  color: #475569;
  line-height: 1.7;
}

.spotlight-trend {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid rgba(148, 163, 184, 0.24);
  color: #334155;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 14px;
}

.quick-action {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  border: 1px solid rgba(148, 163, 184, 0.22);
  border-radius: 18px;
  background: #fff;
  color: #0f172a;
  font: inherit;
  cursor: pointer;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease,
    border-color 0.2s ease;

  .el-icon {
    width: 34px;
    height: 34px;
    border-radius: 12px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #dbeafe, #ecfeff);
    color: #0f766e;
    font-size: 18px;
  }

  &:hover {
    transform: translateY(-2px);
    border-color: rgba(14, 165, 233, 0.3);
    box-shadow: 0 16px 30px rgba(14, 165, 233, 0.12);
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 16px;
}

.stat-card {
  position: relative;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 22px 20px;
  border-radius: 24px;
  overflow: hidden;
  background: #ffffff;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);

  &::after {
    content: "";
    position: absolute;
    inset: auto -20px -36px auto;
    width: 110px;
    height: 110px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.28);
  }
}

.stat-icon {
  position: relative;
  z-index: 1;
  width: 58px;
  height: 58px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.22);
  color: #ffffff;
  font-size: 24px;
}

.stat-body {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.stat-label {
  color: rgba(255, 255, 255, 0.78);
  font-size: 13px;
}

.stat-value {
  font-size: 30px;
  color: #ffffff;
  line-height: 1;
  letter-spacing: -0.04em;
}

.stat-note {
  color: rgba(255, 255, 255, 0.78);
  font-size: 12px;
}

.theme-sky {
  background: linear-gradient(135deg, #0f766e, #14b8a6);
}

.theme-coral {
  background: linear-gradient(135deg, #ea580c, #fb7185);
}

.theme-gold {
  background: linear-gradient(135deg, #ca8a04, #f59e0b);
}

.theme-mint {
  background: linear-gradient(135deg, #0284c7, #22d3ee);
}

.theme-indigo {
  background: linear-gradient(135deg, #4338ca, #818cf8);
}

.insight-grid,
.recent-grid {
  display: grid;
  grid-template-columns: repeat(12, minmax(0, 1fr));
  gap: 20px;
}

.panel {
  padding: 22px;
  border-radius: 26px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 20px 48px rgba(15, 23, 42, 0.08);
}

.panel-main {
  grid-column: span 7;
}

.panel-side {
  grid-column: span 5;
}

.recent-panel {
  grid-column: span 6;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 18px;

  h3 {
    font-size: 22px;
    margin-top: 4px;
    letter-spacing: -0.03em;
  }
}

.panel-eyebrow {
  color: #64748b;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.chart {
  width: 100%;
}

.chart-lg {
  height: 320px;
}

.chart-md {
  height: 250px;
}

.progress-list {
  display: grid;
  gap: 18px;
  padding-top: 4px;
}

.progress-item {
  padding: 16px;
  border-radius: 18px;
  background: linear-gradient(180deg, #f8fafc, #eef2ff);
}

.progress-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  color: #1e293b;
}

.activity-list {
  display: grid;
  gap: 14px;
}

.activity-item {
  padding: 18px;
  border-radius: 20px;
  border: 1px solid rgba(148, 163, 184, 0.14);
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.92), rgba(248, 250, 252, 0.95)),
    #ffffff;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 18px 34px rgba(15, 23, 42, 0.08);
  }
}

.activity-main {
  display: flex;
  flex-direction: column;
  gap: 10px;

  p {
    color: #475569;
    line-height: 1.7;
    display: -webkit-box;
    overflow: hidden;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }
}

.activity-title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;

  h4 {
    font-size: 17px;
    line-height: 1.4;
  }
}

.activity-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 18px;
  color: #64748b;
  font-size: 13px;
}

@media (max-width: 1400px) {
  .stats-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .panel-main,
  .panel-side {
    grid-column: span 6;
  }
}

@media (max-width: 1080px) {
  .hero-panel,
  .insight-grid,
  .recent-grid {
    grid-template-columns: 1fr;
  }

  .panel-main,
  .panel-side,
  .recent-panel {
    grid-column: auto;
  }

  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .dashboard-page {
    gap: 18px;
  }

  .hero-panel,
  .panel,
  .hero-side-card {
    border-radius: 22px;
  }

  .hero-panel {
    padding: 20px;

    h1 {
      font-size: 28px;
    }
  }

  .quick-grid,
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .activity-title-row {
    flex-direction: column;
  }
}
</style>
