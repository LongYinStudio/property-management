<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span>物业费管理</span>
      </template>
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
          >
            <el-option label="未支付" :value="0" />
            <el-option label="已支付" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="type" label="费用类型">
          <template #default="{ row }">
            <el-tag>{{ getTypeName(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额">
          <template #default="{ row }">
            <span style="color: #f56c6c">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="year" label="年份" />
        <el-table-column prop="month" label="月份" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? "已支付" : "未支付" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              type="success"
              link
              @click="handlePay(row)"
              v-if="row.status === 0"
              >支付</el-button
            >
            <el-button type="primary" link @click="handleView(row)"
              >查看</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";

const loading = ref(false);

const queryParams = reactive({
  status: null,
});

const tableData = ref([
  {
    id: 1,
    type: 1,
    amount: 200.0,
    year: 2024,
    month: 1,
    status: 0,
    createTime: "2024-01-01 00:00:00",
  },
  {
    id: 2,
    type: 3,
    amount: 50.0,
    year: 2024,
    month: 1,
    status: 1,
    createTime: "2024-01-02 10:30:00",
  },
  {
    id: 3,
    type: 4,
    amount: 120.0,
    year: 2024,
    month: 1,
    status: 1,
    createTime: "2024-01-03 14:20:00",
  },
]);

const getTypeName = (type) => {
  const names = { 1: "物业费", 2: "停车费", 3: "水费", 4: "电费" };
  return names[type] || "未知";
};

const handleSearch = () => {};

const handleReset = () => {
  queryParams.status = null;
};

const handlePay = (row) => {
  ElMessage.info("支付功能开发中");
};

const handleView = (row) => {
  ElMessage.info("查看功能开发中");
};

onMounted(() => {});
</script>

<style lang="scss" scoped>
.search-form {
  margin-bottom: 20px;
}
</style>
