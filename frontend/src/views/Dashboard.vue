<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
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
        <el-card class="stat-card">
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
        <el-card class="stat-card">
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
        <el-card class="stat-card">
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
            <span>最近报修</span>
          </template>
          <el-table :data="recentRepairs" style="width: 100%">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{
                  getStatusText(row.status)
                }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="提交时间" width="180" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最近投诉</span>
          </template>
          <el-table :data="recentComplaints" style="width: 100%">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="getComplaintStatusType(row.status)">{{
                  getComplaintStatusText(row.status)
                }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="提交时间" width="180" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";

const statistics = ref({
  userCount: 128,
  pendingRepair: 5,
  unpaidFee: 12,
  pendingComplaint: 3,
});

const recentRepairs = ref([
  { id: 1, title: "水管漏水", status: 0, createTime: "2024-01-15 10:30" },
  { id: 2, title: "门锁损坏", status: 1, createTime: "2024-01-14 15:20" },
  { id: 3, title: "电路故障", status: 2, createTime: "2024-01-13 09:00" },
]);

const recentComplaints = ref([
  { id: 1, title: "噪音扰民", status: 0, createTime: "2024-01-15 08:00" },
  { id: 2, title: "停车秩序", status: 1, createTime: "2024-01-14 11:30" },
]);

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

onMounted(() => {
  // TODO: 加载数据
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
}
</style>
