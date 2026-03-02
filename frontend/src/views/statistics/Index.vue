<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>费用收入统计</span>
          </template>
          <div ref="feeChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>报修类型统计</span>
          </template>
          <div ref="repairChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" class="mt-20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>月度收入趋势</span>
          </template>
          <div ref="trendChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import * as echarts from "echarts";

const feeChartRef = ref(null);
const repairChartRef = ref(null);
const trendChartRef = ref(null);

let feeChart = null;
let repairChart = null;
let trendChart = null;

const initFeeChart = () => {
  feeChart = echarts.init(feeChartRef.value);
  const option = {
    tooltip: { trigger: "item" },
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
        data: [
          { value: 1048, name: "物业费" },
          { value: 735, name: "停车费" },
          { value: 580, name: "水费" },
          { value: 484, name: "电费" },
        ],
      },
    ],
  };
  feeChart.setOption(option);
};

const initRepairChart = () => {
  repairChart = echarts.init(repairChartRef.value);
  const option = {
    tooltip: { trigger: "item" },
    legend: { bottom: "5%", left: "center" },
    series: [
      {
        type: "pie",
        radius: "50%",
        data: [
          { value: 35, name: "水管" },
          { value: 28, name: "电路" },
          { value: 22, name: "门窗" },
          { value: 15, name: "其他" },
        ],
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
  trendChart = echarts.init(trendChartRef.value);
  const option = {
    tooltip: { trigger: "axis" },
    grid: { left: "3%", right: "4%", bottom: "3%", containLabel: true },
    xAxis: {
      type: "category",
      boundaryGap: false,
      data: [
        "1月",
        "2月",
        "3月",
        "4月",
        "5月",
        "6月",
        "7月",
        "8月",
        "9月",
        "10月",
        "11月",
        "12月",
      ],
    },
    yAxis: { type: "value" },
    series: [
      {
        name: "收入",
        type: "line",
        smooth: true,
        areaStyle: { color: "rgba(64, 158, 255, 0.3)" },
        lineStyle: { color: "#409eff" },
        itemStyle: { color: "#409eff" },
        data: [
          820, 932, 901, 934, 1290, 1330, 1320, 1150, 1080, 1200, 1350, 1500,
        ],
      },
    ],
  };
  trendChart.setOption(option);
};

const handleResize = () => {
  feeChart?.resize();
  repairChart?.resize();
  trendChart?.resize();
};

onMounted(() => {
  initFeeChart();
  initRepairChart();
  initTrendChart();
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
  feeChart?.dispose();
  repairChart?.dispose();
  trendChart?.dispose();
});
</script>
