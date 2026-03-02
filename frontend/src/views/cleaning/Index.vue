<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span>清洁管理</span>
      </template>
      <el-button type="success" class="mb-20" @click="handleAdd"
        >新增清洁任务</el-button
      >
      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{
              getStatusName(row.status)
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="success"
              link
              @click="handleProcess(row)"
              v-if="row.status === 0"
              >处理</el-button
            >
            <el-button type="danger" link @click="handleDelete(row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";

const loading = ref(false);

const tableData = ref([
  {
    id: 1,
    location: "1号楼大厅",
    description: "地面脏污需要清洁",
    status: 0,
    createTime: "2024-01-15 08:00",
  },
  {
    id: 2,
    location: "2号楼电梯",
    description: "电梯内有污渍",
    status: 1,
    createTime: "2024-01-14 14:30",
  },
  {
    id: 3,
    location: "小区花园",
    description: "落叶需要清扫",
    status: 2,
    createTime: "2024-01-13 10:00",
  },
]);

const getStatusType = (status) => {
  const types = { 0: "warning", 1: "primary", 2: "success" };
  return types[status] || "info";
};

const getStatusName = (status) => {
  const names = { 0: "待处理", 1: "处理中", 2: "已完成" };
  return names[status] || "未知";
};

const handleAdd = () => {
  ElMessage.info("新增功能开发中");
};

const handleProcess = (row) => {
  ElMessage.info("处理功能开发中");
};

const handleDelete = (row) => {
  ElMessageBox.confirm("确定要删除该清洁任务吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      ElMessage.success("删除成功");
    })
    .catch(() => {});
};

onMounted(() => {});
</script>
