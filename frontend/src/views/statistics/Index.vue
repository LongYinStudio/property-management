<template>
  <div class="page-container statistics-page">
    <section class="section-block">
      <div class="section-heading">
        <div>
          <div class="section-heading__title">业务总览</div>
          <div class="section-heading__desc">把新增模块的关键数据集中放到一个入口里</div>
        </div>
      </div>
      <div v-loading="overviewLoading">
        <el-row :gutter="20" class="statistics-grid statistics-grid--overview">
          <el-col
            v-for="card in overviewCards"
            :key="card.key"
            :xs="12"
            :sm="12"
            :md="8"
            :xl="6"
            class="overview-col statistics-grid__col"
          >
            <el-card shadow="hover" class="overview-card" :class="`overview-card--${card.theme}`">
              <div class="overview-card__label">{{ card.label }}</div>
              <div class="overview-card__value">{{ formatCount(card.value) }}</div>
              <div class="overview-card__hint">{{ card.hint }}</div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </section>

    <section class="section-block">
      <div class="section-heading">
        <div>
          <div class="section-heading__title">收费分析</div>
          <div class="section-heading__desc">保留原有费用报表，并把筛选和展示整理成同一块</div>
        </div>
      </div>
      <el-row :gutter="20" class="statistics-grid">
        <el-col :xs="24" :xl="10" class="statistics-grid__col">
          <el-card v-loading="feeLoading" class="chart-card">
            <template #header>
              <div class="card-header">
                <span>费用收入统计</span>
                <el-date-picker
                  v-model="selectedYear"
                  type="year"
                  placeholder="选择年份"
                  format="YYYY"
                  value-format="YYYY"
                  @change="loadFeeData"
                  style="width: 120px"
                />
              </div>
            </template>
            <div :ref="(el) => setChartRef('fee', el)" class="chart-area chart-area--large"></div>
            <el-empty v-if="!feeLoading && isFeeEmpty" description="暂无数据" />
          </el-card>
        </el-col>
        <el-col :xs="24" :xl="14" class="statistics-grid__col">
          <el-card v-loading="incomeLoading" class="chart-card">
            <template #header>
              <div class="card-header">
                <span>月度收入趋势</span>
                <el-date-picker
                  v-model="incomeYear"
                  type="year"
                  placeholder="选择年份"
                  format="YYYY"
                  value-format="YYYY"
                  @change="loadIncomeData"
                  style="width: 120px"
                />
              </div>
            </template>
            <div :ref="(el) => setChartRef('income', el)" class="chart-area chart-area--large"></div>
            <el-empty v-if="!incomeLoading && isIncomeEmpty" description="暂无数据" />
          </el-card>
        </el-col>
      </el-row>
    </section>

    <section class="section-block">
      <div class="section-heading">
        <div>
          <div class="section-heading__title">服务工单</div>
          <div class="section-heading__desc">原有报修、投诉、清洁报表继续保留</div>
        </div>
      </div>
      <el-row :gutter="20" class="statistics-grid">
        <el-col
          v-for="chart in serviceCharts"
          :key="chart.key"
          :xs="24"
          :sm="12"
          :xl="6"
          class="statistics-grid__col"
        >
          <el-card v-loading="chart.loading" class="chart-card chart-card--compact">
            <template #header>
              <span>{{ chart.title }}</span>
            </template>
            <div :ref="(el) => setChartRef(chart.key, el)" class="chart-area"></div>
            <el-empty v-if="!chart.loading && isCountChartEmpty(chart)" description="暂无数据" />
          </el-card>
        </el-col>
      </el-row>
    </section>

    <section class="section-block">
      <div class="section-heading">
        <div>
          <div class="section-heading__title">新增业务</div>
          <div class="section-heading__desc">把访客、车位、巡检、合同、投票、公告都纳入统计页</div>
        </div>
      </div>
      <el-row :gutter="20" class="statistics-grid">
        <el-col
          v-for="chart in businessCharts"
          :key="chart.key"
          :xs="24"
          :sm="12"
          :xl="6"
          class="statistics-grid__col"
        >
          <el-card v-loading="chart.loading" class="chart-card chart-card--compact">
            <template #header>
              <span>{{ chart.title }}</span>
            </template>
            <div :ref="(el) => setChartRef(chart.key, el)" class="chart-area"></div>
            <el-empty v-if="!chart.loading && isCountChartEmpty(chart)" description="暂无数据" />
          </el-card>
        </el-col>
      </el-row>
    </section>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, reactive, ref } from "vue";
import * as echarts from "echarts/core";
import { PieChart, LineChart } from "echarts/charts";
import { GridComponent, LegendComponent, TooltipComponent } from "echarts/components";
import { CanvasRenderer } from "echarts/renderers";
import {
  getBusinessOverview,
  getCleaningStats,
  getComplaintTypeStats,
  getContractStatusStats,
  getFeeTypeStats,
  getInspectionStatusStats,
  getMonthlyIncome,
  getNoticeTypeStats,
  getParkingRentalStatusStats,
  getParkingSpaceStatusStats,
  getRepairStatusStats,
  getRepairTypeStats,
  getVisitorStatusStats,
  getVoteTypeStats,
} from "@/api/statistics";

echarts.use([PieChart, LineChart, GridComponent, LegendComponent, TooltipComponent, CanvasRenderer]);

const currentYear = String(new Date().getFullYear());
const monthLabels = Array.from({ length: 12 }, (_, index) => `${index + 1}月`);

const selectedYear = ref(currentYear);
const incomeYear = ref(currentYear);

const overviewLoading = ref(false);
const feeLoading = ref(false);
const incomeLoading = ref(false);

const overviewData = ref({
  ownerCount: 0,
  parkingSpaceCount: 0,
  paidParkingRentalCount: 0,
  pendingVisitorCount: 0,
  abnormalInspectionCount: 0,
  activeContractCount: 0,
  openVoteCount: 0,
  publishedNoticeCount: 0,
});

const feeData = ref([]);
const incomeData = ref([]);

const createCountChart = (key, title, api, labelKey) =>
  reactive({
    key,
    title,
    api,
    labelKey,
    data: [],
    loading: false,
  });

const serviceCharts = [
  createCountChart("repairType", "报修类型统计", getRepairTypeStats, "typeName"),
  createCountChart("repairStatus", "报修状态分布", getRepairStatusStats, "statusName"),
  createCountChart("complaintType", "投诉建议分布", getComplaintTypeStats, "typeName"),
  createCountChart("cleaningStatus", "清洁任务分布", getCleaningStats, "statusName"),
];

const businessCharts = [
  createCountChart("visitorStatus", "访客通行状态", getVisitorStatusStats, "label"),
  createCountChart("parkingSpaceStatus", "车位状态分布", getParkingSpaceStatusStats, "label"),
  createCountChart("parkingRentalStatus", "车位租赁状态", getParkingRentalStatusStats, "label"),
  createCountChart("inspectionStatus", "巡检结果分布", getInspectionStatusStats, "label"),
  createCountChart("contractStatus", "合同状态分布", getContractStatusStats, "label"),
  createCountChart("voteType", "活动类型分布", getVoteTypeStats, "label"),
  createCountChart("noticeType", "公告类型分布", getNoticeTypeStats, "label"),
];

const allCountCharts = [...serviceCharts, ...businessCharts];

const overviewCards = computed(() => [
  {
    key: "ownerCount",
    label: "业主总数",
    value: overviewData.value.ownerCount,
    hint: "启用中的业主账号",
    theme: "blue",
  },
  {
    key: "parkingSpaceCount",
    label: "车位总数",
    value: overviewData.value.parkingSpaceCount,
    hint: "当前系统登记车位",
    theme: "green",
  },
  {
    key: "paidParkingRentalCount",
    label: "已支付租赁",
    value: overviewData.value.paidParkingRentalCount,
    hint: "已完成支付的租赁记录",
    theme: "gold",
  },
  {
    key: "pendingVisitorCount",
    label: "待通行访客",
    value: overviewData.value.pendingVisitorCount,
    hint: "尚未核销的访客申请",
    theme: "cyan",
  },
  {
    key: "abnormalInspectionCount",
    label: "异常巡检",
    value: overviewData.value.abnormalInspectionCount,
    hint: "待持续跟进的异常记录",
    theme: "red",
  },
  {
    key: "activeContractCount",
    label: "生效合同",
    value: overviewData.value.activeContractCount,
    hint: "状态为生效中的合同",
    theme: "purple",
  },
  {
    key: "openVoteCount",
    label: "进行中活动",
    value: overviewData.value.openVoteCount,
    hint: "投票和意见征集活动",
    theme: "orange",
  },
  {
    key: "publishedNoticeCount",
    label: "已发布公告",
    value: overviewData.value.publishedNoticeCount,
    hint: "当前处于发布状态的公告",
    theme: "slate",
  },
]);

const isFeeEmpty = computed(() =>
  feeData.value.every((item) => Number(item.amount || 0) === 0)
);
const isIncomeEmpty = computed(() =>
  incomeData.value.every((item) => Number(item.amount || 0) === 0)
);

const chartRefs = {};
const chartInstances = {};

const setChartRef = (key, el) => {
  if (el) {
    chartRefs[key] = el;
    return;
  }
  delete chartRefs[key];
};

const getChartInstance = (key) => {
  const element = chartRefs[key];
  if (!element) {
    return null;
  }
  if (!chartInstances[key]) {
    chartInstances[key] = echarts.init(element);
  }
  return chartInstances[key];
};

const formatCount = (value) => Number(value || 0).toLocaleString("zh-CN");
const formatMoney = (value) =>
  Number(value || 0).toLocaleString("zh-CN", {
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  });

const isCountChartEmpty = (chart) =>
  (chart.data || []).every((item) => Number(item.count || 0) === 0);

const renderFeeChart = () => {
  const chart = getChartInstance("fee");
  if (!chart) {
    return;
  }

  const totalAmount = feeData.value.reduce(
    (sum, item) => sum + Number(item.amount || 0),
    0
  );
  const chartData =
    totalAmount > 0
      ? feeData.value.map((item) => ({
          value: Number(item.amount || 0),
          name: item.typeName,
        }))
      : [{ value: 1, name: "暂无数据", itemStyle: { color: "#dcdfe6" } }];

  chart.setOption({
    color: ["#3b82f6", "#22c55e", "#f59e0b", "#6366f1"],
    tooltip:
      totalAmount > 0
        ? {
            trigger: "item",
            formatter: ({ name, value, percent }) =>
              `${name}: ¥${formatMoney(value)} (${percent}%)`,
          }
        : { show: false },
    legend: {
      bottom: 0,
      left: "center",
      type: "scroll",
    },
    series: [
      {
        type: "pie",
        radius: ["42%", "70%"],
        center: ["50%", "42%"],
        stillShowZeroSum: false,
        itemStyle: {
          borderRadius: 12,
          borderColor: "#fff",
          borderWidth: 2,
        },
        label: {
          show: totalAmount > 0,
          formatter: "{b}\n¥{c}",
        },
        data: chartData,
      },
    ],
  });
};

const renderIncomeChart = () => {
  const chart = getChartInstance("income");
  if (!chart) {
    return;
  }

  chart.setOption({
    color: ["#409eff"],
    tooltip: {
      trigger: "axis",
      formatter: (params) => {
        const [first] = params || [];
        if (!first) {
          return "";
        }
        return `${first.axisValue}<br/>收入: ¥${formatMoney(first.value)}`;
      },
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "4%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      boundaryGap: false,
      data: monthLabels,
    },
    yAxis: {
      type: "value",
      axisLabel: {
        formatter: (value) => `¥${formatMoney(value)}`,
      },
      splitLine: {
        lineStyle: {
          color: "#ebeef5",
        },
      },
    },
    series: [
      {
        name: "收入",
        type: "line",
        smooth: true,
        symbolSize: 8,
        lineStyle: {
          width: 3,
        },
        areaStyle: {
          color: "rgba(64, 158, 255, 0.18)",
        },
        data: incomeData.value.map((item) => Number(item.amount || 0)),
      },
    ],
  });
};

const renderCountChart = (chartConfig) => {
  const chart = getChartInstance(chartConfig.key);
  if (!chart) {
    return;
  }

  const totalCount = chartConfig.data.reduce(
    (sum, item) => sum + Number(item.count || 0),
    0
  );
  const chartData =
    totalCount > 0
      ? chartConfig.data.map((item) => ({
          value: Number(item.count || 0),
          name: item[chartConfig.labelKey] || item.label || "未命名",
        }))
      : [{ value: 1, name: "暂无数据", itemStyle: { color: "#dcdfe6" } }];

  chart.setOption({
    color: ["#3b82f6", "#22c55e", "#f59e0b", "#ef4444", "#8b5cf6", "#06b6d4"],
    tooltip:
      totalCount > 0
        ? {
            trigger: "item",
            formatter: "{b}: {c} ({d}%)",
          }
        : { show: false },
    legend: {
      bottom: 0,
      left: "center",
      type: "scroll",
    },
    series: [
      {
        type: "pie",
        radius: ["40%", "68%"],
        center: ["50%", "42%"],
        stillShowZeroSum: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: "#fff",
          borderWidth: 2,
        },
        label: {
          show: totalCount > 0,
          formatter: "{b}: {c}",
        },
        labelLine: {
          show: totalCount > 0,
        },
        data: chartData,
      },
    ],
  });
};

const loadOverviewData = async () => {
  overviewLoading.value = true;
  try {
    const { data } = await getBusinessOverview();
    overviewData.value = {
      ...overviewData.value,
      ...(data || {}),
    };
  } catch (error) {
    console.error("加载业务总览失败:", error);
  } finally {
    overviewLoading.value = false;
  }
};

const loadFeeData = async () => {
  feeLoading.value = true;
  try {
    const year = selectedYear.value ? parseInt(selectedYear.value, 10) : null;
    const { data } = await getFeeTypeStats({ year });
    feeData.value = data || [];
    await nextTick();
    renderFeeChart();
  } catch (error) {
    console.error("加载费用数据失败:", error);
  } finally {
    feeLoading.value = false;
  }
};

const loadIncomeData = async () => {
  incomeLoading.value = true;
  try {
    const year = incomeYear.value ? parseInt(incomeYear.value, 10) : null;
    const { data } = await getMonthlyIncome({ year });
    incomeData.value = data || [];
    await nextTick();
    renderIncomeChart();
  } catch (error) {
    console.error("加载收入趋势失败:", error);
  } finally {
    incomeLoading.value = false;
  }
};

const loadCountChart = async (chartConfig) => {
  chartConfig.loading = true;
  try {
    const { data } = await chartConfig.api();
    chartConfig.data = data || [];
    await nextTick();
    renderCountChart(chartConfig);
  } catch (error) {
    console.error(`加载${chartConfig.title}失败:`, error);
  } finally {
    chartConfig.loading = false;
  }
};

const handleResize = () => {
  Object.values(chartInstances).forEach((instance) => {
    instance?.resize();
  });
};

onMounted(async () => {
  await Promise.all([
    loadOverviewData(),
    loadFeeData(),
    loadIncomeData(),
    ...allCountCharts.map((chart) => loadCountChart(chart)),
  ]);
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
  Object.values(chartInstances).forEach((instance) => {
    instance?.dispose();
  });
});
</script>

<style lang="scss" scoped>
.statistics-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.section-block {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.section-heading {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 12px;

  &__title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
  }

  &__desc {
    margin-top: 4px;
    font-size: 13px;
    color: #909399;
  }
}

.statistics-grid {
  row-gap: 20px;
}

.statistics-grid--overview {
  row-gap: 18px;
}

.statistics-grid__col {
  display: flex;
}

.overview-col {
  margin-bottom: 0;
}

.overview-card {
  position: relative;
  overflow: hidden;
  width: 100%;
  min-height: 146px;
  border: none;
  border-radius: 22px;
  box-shadow: 0 14px 34px rgba(15, 23, 42, 0.08);

  :deep(.el-card__body) {
    position: relative;
    z-index: 1;
    padding: 24px 22px;
  }

  &::after {
    content: "";
    position: absolute;
    inset: auto -24px -28px auto;
    width: 110px;
    height: 110px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.22);
  }

  &__label {
    font-size: 13px;
    color: #5f6b7a;
  }

  &__value {
    margin-top: 16px;
    font-size: 30px;
    font-weight: 700;
    line-height: 1;
    color: #1f2937;
  }

  &__hint {
    margin-top: 12px;
    font-size: 12px;
    color: #6b7280;
  }

  &--blue {
    background: linear-gradient(135deg, #eef5ff 0%, #dcecff 100%);
  }

  &--green {
    background: linear-gradient(135deg, #eefcf4 0%, #d7f7e3 100%);
  }

  &--gold {
    background: linear-gradient(135deg, #fff8e6 0%, #ffefc3 100%);
  }

  &--cyan {
    background: linear-gradient(135deg, #eaf9ff 0%, #d4f0ff 100%);
  }

  &--red {
    background: linear-gradient(135deg, #fff0f0 0%, #ffdada 100%);
  }

  &--purple {
    background: linear-gradient(135deg, #f5efff 0%, #e7dcff 100%);
  }

  &--orange {
    background: linear-gradient(135deg, #fff4e8 0%, #ffe4c6 100%);
  }

  &--slate {
    background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
  }
}

.chart-card {
  width: 100%;
  height: 100%;
  border: 1px solid #edf1f7;
  border-radius: 22px;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.06);

  :deep(.el-card__body) {
    display: flex;
    flex-direction: column;
  }
}

.chart-card--compact {
  min-height: 380px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.chart-area {
  height: 280px;
}

.chart-area--large {
  height: 320px;
}

@media (max-width: 767px) {
  .statistics-page {
    gap: 20px;
  }

  .statistics-grid {
    row-gap: 16px;
  }

  .section-heading {
    align-items: flex-start;
  }

  .overview-card {
    min-height: 132px;

    &__value {
      font-size: 26px;
    }
  }

  .chart-area {
    height: 240px;
  }

  .chart-area--large {
    height: 260px;
  }
}
</style>
