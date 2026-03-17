<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card v-loading="feeLoading">
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
          <div ref="feeChartRef" style="height: 300px"></div>
          <el-empty v-if="!feeLoading && feeData.length === 0" description="暂无数据" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card v-loading="repairTypeLoading">
          <template #header>
            <span>报修类型统计</span>
          </template>
          <div ref="repairChartRef" style="height: 300px"></div>
          <el-empty v-if="!repairTypeLoading && repairTypeData.length === 0" description="暂无数据" />
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" class="mt-20">
      <el-col :span="24">
        <el-card v-loading="incomeLoading">
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
          <div ref="trendChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" class="mt-20">
      <el-col :span="8">
        <el-card v-loading="repairStatusLoading">
          <template #header>
            <span>报修状态分布</span>
          </template>
          <div ref="repairStatusChartRef" style="height: 250px"></div>
          <el-empty v-if="!repairStatusLoading && repairStatusData.length === 0" description="暂无数据" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card v-loading="complaintLoading">
          <template #header>
            <span>投诉建议分布</span>
          </template>
          <div ref="complaintChartRef" style="height: 250px"></div>
          <el-empty v-if="!complaintLoading && complaintData.length === 0" description="暂无数据" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card v-loading="cleaningLoading">
          <template #header>
            <span>清洁任务分布</span>
          </template>
          <div ref="cleaningChartRef" style="height: 250px"></div>
          <el-empty v-if="!cleaningLoading && cleaningData.length === 0" description="暂无数据" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from "vue";
import * as echarts from "echarts";
import {
  getFeeTypeStats,
  getRepairTypeStats,
  getMonthlyIncome,
  getRepairStatusStats,
  getComplaintTypeStats,
  getCleaningStats,
} from "@/api/statistics";

const feeChartRef = ref(null);
const repairChartRef = ref(null);
const trendChartRef = ref(null);
const repairStatusChartRef = ref(null);
const complaintChartRef = ref(null);
const cleaningChartRef = ref(null);

const selectedYear = ref(new Date().getFullYear().toString());
const incomeYear = ref(new Date().getFullYear().toString());

const feeLoading = ref(false);
const repairTypeLoading = ref(false);
const incomeLoading = ref(false);
const repairStatusLoading = ref(false);
const complaintLoading = ref(false);
const cleaningLoading = ref(false);

const feeData = ref([]);
const repairTypeData = ref([]);
const incomeData = ref([]);
const repairStatusData = ref([]);
const complaintData = ref([]);
const cleaningData = ref([]);

let feeChart = null;
let repairChart = null;
let trendChart = null;
let repairStatusChart = null;
let complaintChart = null;
let cleaningChart = null;

const loadFeeData = async () => {
  feeLoading.value = true;
  try {
    const year = selectedYear.value ? parseInt(selectedYear.value) : null;
    const { data } = await getFeeTypeStats({ year });
    feeData.value = data || [];
    await nextTick();
    initFeeChart();
  } catch (error) {
    console.error("加载费用数据失败:", error);
  } finally {
    feeLoading.value = false;
  }
};

const loadRepairTypeData = async () => {
  repairTypeLoading.value = true;
  try {
    const { data } = await getRepairTypeStats();
    repairTypeData.value = data || [];
    await nextTick();
    initRepairChart();
  } catch (error) {
    console.error("加载报修类型数据失败:", error);
  } finally {
    repairTypeLoading.value = false;
  }
};

const loadIncomeData = async () => {
  incomeLoading.value = true;
  try {
    const year = incomeYear.value ? parseInt(incomeYear.value) : null;
    const { data } = await getMonthlyIncome({ year });
    incomeData.value = data || [];
    await nextTick();
    initTrendChart();
  } catch (error) {
    console.error("加载收入数据失败:", error);
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
  } catch (error) {
    console.error("加载报修状态数据失败:", error);
  } finally {
    repairStatusLoading.value = false;
  }
};

const loadComplaintData = async () => {
  complaintLoading.value = true;
  try {
    const { data } = await getComplaintTypeStats();
    complaintData.value = data || [];
    await nextTick();
    initComplaintChart();
  } catch (error) {
    console.error("加载投诉数据失败:", error);
  } finally {
    complaintLoading.value = false;
  }
};

const loadCleaningData = async () => {
  cleaningLoading.value = true;
  try {
    const { data } = await getCleaningStats();
    cleaningData.value = data || [];
    await nextTick();
    initCleaningChart();
  } catch (error) {
    console.error("加载清洁数据失败:", error);
  } finally {
    cleaningLoading.value = false;
  }
};

const initFeeChart = () => {
  if (!feeChartRef.value) return;
  feeChart = echarts.init(feeChartRef.value);
  const chartData = feeData.value.map((item) => ({
    value: item.amount || 0,
    name: item.typeName,
  }));

  const option = {
    tooltip: {
      trigger: "item",
      formatter: "{b}: ¥{c} ({d}%)",
    },
    legend: { bottom: "5%", left: "center" },
    series: [
      {
        type: "pie",
        radius: ["40%", "70%"],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 10, borderColor: "#fff", borderWidth: 2 },
        label: { show: false, position: "center" },
        emphasis: {
          label: { show: true, fontSize: 20, fontWeight: "bold" },
        },
        labelLine: { show: false },
        data: chartData.length > 0 ? chartData : [{ value: 0, name: "暂无数据" }],
      },
    ],
  };
  feeChart.setOption(option);
};

const initRepairChart = () => {
  if (!repairChartRef.value) return;
  repairChart = echarts.init(repairChartRef.value);
  const chartData = repairTypeData.value.map((item) => ({
    value: item.count || 0,
    name: item.typeName,
  }));

  const option = {
    tooltip: { trigger: "item" },
    legend: { bottom: "5%", left: "center" },
    series: [
      {
        type: "pie",
        radius: "50%",
        data: chartData.length > 0 ? chartData : [{ value: 0, name: "暂无数据" }],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)",
          },
        },
      },
    ],
  };
  repairChart.setOption(option);
};

const initTrendChart = () => {
  if (!trendChartRef.value) return;
  trendChart = echarts.init(trendChartRef.value);
  const months = incomeData.value.map((item) => `${item.month}月`);
  const values = incomeData.value.map((item) => item.amount || 0);

  const option = {
    tooltip: {
      trigger: "axis",
      formatter: "{b}: ¥{c}",
    },
    grid: { left: "3%", right: "4%", bottom: "3%", containLabel: true },
    xAxis: {
      type: "category",
      boundaryGap: false,
      data: months.length > 0 ? months : ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"],
    },
    yAxis: {
      type: "value",
      axisLabel: {
        formatter: "¥{value}",
      },
    },
    series: [
      {
        name: "收入",
        type: "line",
        smooth: true,
        areaStyle: { color: "rgba(64, 158, 255, 0.3)" },
        lineStyle: { color: "#409eff" },
        itemStyle: { color: "#409eff" },
        data: values.length > 0 ? values : [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
      },
    ],
  };
  trendChart.setOption(option);
};

const initRepairStatusChart = () => {
  if (!repairStatusChartRef.value) return;
  repairStatusChart = echarts.init(repairStatusChartRef.value);
  const chartData = repairStatusData.value.map((item) => ({
    value: item.count || 0,
    name: item.statusName,
  }));

  const option = {
    tooltip: { trigger: "item" },
    legend: { orient: "vertical", left: "left" },
    series: [
      {
        type: "pie",
        radius: "60%",
        center: ["60%", "50%"],
        data: chartData.length > 0 ? chartData : [{ value: 0, name: "暂无数据" }],
        label: {
          show: true,
          formatter: "{b}: {c}",
        },
      },
    ],
  };
  repairStatusChart.setOption(option);
};

const initComplaintChart = () => {
  if (!complaintChartRef.value) return;
  complaintChart = echarts.init(complaintChartRef.value);
  const chartData = complaintData.value.map((item) => ({
    value: item.count || 0,
    name: item.typeName,
  }));

  const option = {
    tooltip: { trigger: "item" },
    legend: { orient: "vertical", left: "left" },
    series: [
      {
        type: "pie",
        radius: "60%",
        center: ["60%", "50%"],
        data: chartData.length > 0 ? chartData : [{ value: 0, name: "暂无数据" }],
        label: {
          show: true,
          formatter: "{b}: {c}",
        },
      },
    ],
  };
  complaintChart.setOption(option);
};

const initCleaningChart = () => {
  if (!cleaningChartRef.value) return;
  cleaningChart = echarts.init(cleaningChartRef.value);
  const chartData = cleaningData.value.map((item) => ({
    value: item.count || 0,
    name: item.statusName,
  }));

  const option = {
    tooltip: { trigger: "item" },
    legend: { orient: "vertical", left: "left" },
    series: [
      {
        type: "pie",
        radius: "60%",
        center: ["60%", "50%"],
        data: chartData.length > 0 ? chartData : [{ value: 0, name: "暂无数据" }],
        label: {
          show: true,
          formatter: "{b}: {c}",
        },
      },
    ],
  };
  cleaningChart.setOption(option);
};

const handleResize = () => {
  feeChart?.resize();
  repairChart?.resize();
  trendChart?.resize();
  repairStatusChart?.resize();
  complaintChart?.resize();
  cleaningChart?.resize();
};

onMounted(() => {
  loadFeeData();
  loadRepairTypeData();
  loadIncomeData();
  loadRepairStatusData();
  loadComplaintData();
  loadCleaningData();
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
  feeChart?.dispose();
  repairChart?.dispose();
  trendChart?.dispose();
  repairStatusChart?.dispose();
  complaintChart?.dispose();
  cleaningChart?.dispose();
});
</script>

<style lang="scss" scoped>
.page-container {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>