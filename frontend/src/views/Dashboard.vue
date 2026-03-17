<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card" v-loading="loading">
          <div class="stat-content">
            <div class="stat-icon user-icon">
              <el-icon :size="40"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.userCount || 0 }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" v-loading="loading">
          <div class="stat-content">
            <div class="stat-icon repair-icon">
              <el-icon :size="40"><Tools /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.pendingRepair || 0 }}</div>
              <div class="stat-label">待处理报修</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" v-loading="loading">
          <div class="stat-content">
            <div class="stat-icon fee-icon">
              <el-icon :size="40"><Wallet /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.unpaidFee || 0 }}</div>
              <div class="stat-label">待缴费用</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" v-loading="loading">
          <div class="stat-content">
            <div class="stat-icon complaint-icon">
              <el-icon :size="40"><ChatDotSquare /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">
                {{ statistics.pendingComplaint || 0 }}
              </div>
              <div class="stat-label">待处理投诉</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近报修</span>
              <el-button type="primary" link @click="goToRepair">
                查看全部
              </el-button>
            </div>
          </template>
          <el-table :data="recentRepairs" style="width: 100%" v-loading="repairLoading">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{
                  getStatusText(row.status)
                }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="提交时间" width="180" />
          </el-table>
          <el-empty v-if="!repairLoading && recentRepairs.length === 0" description="暂无数据" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近投诉</span>
              <el-button type="primary" link @click="goToComplaint">
                查看全部
              </el-button>
            </div>
          </template>
          <el-table :data="recentComplaints" style="width: 100%" v-loading="complaintLoading">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getComplaintStatusType(row.status)">{{
                  getComplaintStatusText(row.status)
                }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="提交时间" width="180" />
          </el-table>
          <el-empty v-if="!complaintLoading && recentComplaints.length === 0" description="暂无数据" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getDashboardStats } from "@/api/statistics";
import { getRepairPage } from "@/api/repair";
import { getComplaintPage } from "@/api/complaint";

const router = useRouter();

const loading = ref(false);
const repairLoading = ref(false);
const complaintLoading = ref(false);

const statistics = ref({
  userCount: 0,
  pendingRepair: 0,
  unpaidFee: 0,
  pendingComplaint: 0,
  pendingCleaning: 0,
});

const recentRepairs = ref([]);
const recentComplaints = ref([]);

const loadStatistics = async () => {
  loading.value = true;
  try {
    const { data } = await getDashboardStats();
    statistics.value = data;
  } catch (error) {
    console.error("加载统计数据失败:", error);
  } finally {
    loading.value = false;
  }
};

const loadRecentRepairs = async () => {
  repairLoading.value = true;
  try {
    const { data } = await getRepairPage({ pageNum: 1, pageSize: 5 });
    recentRepairs.value = data.records || [];
  } catch (error) {
    console.error("加载报修数据失败:", error);
  } finally {
    repairLoading.value = false;
  }
};

const loadRecentComplaints = async () => {
  complaintLoading.value = true;
  try {
    const { data } = await getComplaintPage({ pageNum: 1, pageSize: 5 });
    recentComplaints.value = data.records || [];
  } catch (error) {
    console.error("加载投诉数据失败:", error);
  } finally {
    complaintLoading.value = false;
  }
};

const getStatusType = (status) => {
  const types = { 0: "warning", 1: "primary", 2: "success", 3: "info" };
  return types[status] || "info";
};

const getStatusText = (status) => {
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

const goToRepair = () => {
  router.push("/repair");
};

const goToComplaint = () => {
  router.push("/complaint");
};

onMounted(() => {
  loadStatistics();
  loadRecentRepairs();
  loadRecentComplaints();
});
</script>

<style lang="scss" scoped>
.dashboard {
  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;

      .stat-icon {
        width: 70px;
        height: 70px;
        border-radius: 10px;
        display: flex;
        justify-content: center;
        align-items: center;
        color: #fff;

        &.user-icon {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }

        &.repair-icon {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }

        &.fee-icon {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }

        &.complaint-icon {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }
      }

      .stat-info {
        margin-left: 20px;

        .stat-value {
          font-size: 28px;
          font-weight: bold;
          color: #303133;
        }

        .stat-label {
          font-size: 14px;
          color: #909399;
          margin-top: 5px;
        }
      }
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>